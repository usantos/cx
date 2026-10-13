package br.gov.caixa.loterias.core.keymanagement

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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
                        // A verified update may be used in memory if persistence is unavailable.
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
        // An expired signed cache still preserves the accepted version across a restart.
        highestAcceptedVersion = maxOf(highestAcceptedVersion, verifier.authenticatedVersion(cached) ?: 0)
        return when (val verification = verifier.verify(cached, highestAcceptedVersion)) {
            is KeySetVerificationResult.Accepted -> ResolvedPublicKeySet(verification.keySet, KeySetSource.CACHE)
            is KeySetVerificationResult.Rejected -> fallback()
        }
    }

    private fun fallback() = ResolvedPublicKeySet(localFallback, KeySetSource.LOCAL_FALLBACK)
}
