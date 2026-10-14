package br.gov.caixa.loterias.core.domain.push

import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentityProvider
import java.security.MessageDigest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun interface PushRegistrationRepository {
    suspend fun register(identity: AuthenticatedSessionIdentity, cpf: Long, token: String): Boolean
}

interface PushRegistrationStore {
    fun contains(marker: String): Boolean
    fun remember(marker: String)
}

/** Registers the device for the captured login. Failed requests remain retryable on the next resume. */
class RegisterPushDevice(
    private val sessions: AuthenticatedSessionIdentityProvider,
    private val cpfFor: (AuthenticatedSessionIdentity) -> String?,
    private val repository: PushRegistrationRepository,
    private val store: PushRegistrationStore,
) {
    private val mutex = Mutex()

    suspend operator fun invoke(token: String): Boolean {
        val identity = sessions.captureIdentity() ?: return false
        if (token.isBlank()) return false
        return mutex.withLock {
            if (!sessions.isCurrent(identity)) return@withLock false
            val cpf = cpfFor(identity)?.takeIf { it.matches(Regex("[0-9]{11}")) } ?: return@withLock false
            if (!sessions.isCurrent(identity)) return@withLock false
            // Full fingerprint avoids legacy suffix collisions; no CPF or device token is persisted.
            val marker = MessageDigest.getInstance("SHA-256").digest("$cpf\u0000$token".toByteArray(Charsets.UTF_8))
                .joinToString("") { "%02x".format(it.toInt() and 255) }
            if (store.contains(marker)) return@withLock true
            val confirmed = try { repository.register(identity, cpf.toLong(), token) }
                catch (cancelled: CancellationException) { throw cancelled }
                catch (_: Exception) { false }
            if (!confirmed || !sessions.isCurrent(identity)) return@withLock false
            store.remember(marker)
            true
        }
    }
}
