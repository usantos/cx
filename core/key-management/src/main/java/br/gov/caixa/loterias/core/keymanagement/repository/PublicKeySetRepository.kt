package br.gov.caixa.loterias.core.keymanagement.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import br.gov.caixa.loterias.core.keymanagement.model.KeySetVerificationResult
import br.gov.caixa.loterias.core.keymanagement.model.KeySetSource
import br.gov.caixa.loterias.core.keymanagement.model.ResolvedPublicKeySet
import br.gov.caixa.loterias.core.keymanagement.model.RemotePublicKeySetSource
import br.gov.caixa.loterias.core.keymanagement.model.SignedKeySetStore
import br.gov.caixa.loterias.core.keymanagement.verification.SignedKeySetVerifier

class PublicKeySetRepository(
    private val remote: RemotePublicKeySetSource,
    private val store: SignedKeySetStore,
    private val verifier: SignedKeySetVerifier,
    private val localFallback: PublicKeySet,
) {
    private val stateLock = Any()
    private val refreshMutex = Mutex()
    private var highestAcceptedVersion = localFallback.version
    private var resolved = resolveCachedOrFallback()

    fun current(): ResolvedPublicKeySet = synchronized(stateLock) {
        if (resolved.source != KeySetSource.LOCAL_FALLBACK && !verifier.isTimeWindowValid(resolved.keySet)) {
            resolved = fallback()
        }
        resolved
    }

    suspend fun refresh(): ResolvedPublicKeySet = refreshMutex.withLock {
        val envelope = try {
            remote.fetch()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            null
        } ?: return@withLock current()

        val minimumVersion = synchronized(stateLock) { highestAcceptedVersion }
        when (val verification = verifier.verify(envelope, minimumVersion)) {
            is KeySetVerificationResult.Accepted -> {
                if (verification.keySet.version > minimumVersion) {
                    try {
                        store.write(envelope)
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (_: Exception) {
                        // Uma atualização verificada pode ser usada em memória se a persistência estiver indisponível.
                    }
                    synchronized(stateLock) {
                        highestAcceptedVersion = verification.keySet.version
                        resolved = ResolvedPublicKeySet(verification.keySet, KeySetSource.REMOTE)
                    }
                }
                current()
            }
            is KeySetVerificationResult.Rejected -> current()
        }
    }

    private fun resolveCachedOrFallback(): ResolvedPublicKeySet {
        val cached = runCatching { store.read() }.getOrNull() ?: return fallback()
        // Um cache assinado expirado ainda preserva a versão aceita após uma reinicialização.
        highestAcceptedVersion = maxOf(highestAcceptedVersion, verifier.authenticatedVersion(cached) ?: 0)
        return when (val verification = verifier.verify(cached, highestAcceptedVersion)) {
            is KeySetVerificationResult.Accepted -> ResolvedPublicKeySet(verification.keySet, KeySetSource.CACHE)
            is KeySetVerificationResult.Rejected -> fallback()
        }
    }

    private fun fallback() = ResolvedPublicKeySet(localFallback, KeySetSource.LOCAL_FALLBACK)
}
