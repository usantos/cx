package br.gov.caixa.loterias.core.domain.push.usecase

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import java.security.MessageDigest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import br.gov.caixa.loterias.core.domain.push.repository.PushRegistrationRepository
import br.gov.caixa.loterias.core.domain.push.repository.PushRegistrationLocalRepository

/** Registra o dispositivo para o login capturado. Requisições com falha podem ser repetidas na próxima retomada. */
class RegisterPushDevice(
    private val sessions: AuthenticatedSessionIdentityProvider,
    private val cpfFor: (AuthenticatedSessionIdentity) -> String?,
    private val repository: PushRegistrationRepository,
    private val store: PushRegistrationLocalRepository,
) {
    private val mutex = Mutex()

    suspend operator fun invoke(token: String): Boolean {
        val identity = sessions.captureIdentity() ?: return false
        if (token.isBlank()) return false
        return mutex.withLock {
            if (!sessions.isCurrent(identity)) return@withLock false
            val cpf = cpfFor(identity)?.takeIf { it.matches(Regex("[0-9]{11}")) } ?: return@withLock false
            if (!sessions.isCurrent(identity)) return@withLock false
            // A impressão digital completa evita colisões de sufixos legados; nenhum CPF ou token de dispositivo é persistido.
            val marker = MessageDigest.getInstance("SHA-256").digest("$cpf\u0000$token".toByteArray(Charsets.UTF_8))
                .joinToString("") { "%02x".format(it.toInt() and 255) }
            if (store.contains(identity, marker)) return@withLock true
            val confirmed = try { repository.register(identity, cpf.toLong(), token) }
                catch (cancelled: CancellationException) { throw cancelled }
                catch (_: Exception) { false }
            if (!confirmed || !sessions.isCurrent(identity)) return@withLock false
            store.remember(identity, marker)
            true
        }
    }
}
