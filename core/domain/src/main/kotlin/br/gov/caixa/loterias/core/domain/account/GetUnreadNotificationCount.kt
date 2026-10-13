package br.gov.caixa.loterias.core.domain.account

import java.io.IOException

fun interface UnreadNotificationCountRepository {
    suspend fun getCount(identity: AuthenticatedSessionIdentity): Int
}

/** Expected service failure; deliberately contains no notification content or credentials. */
class UnreadNotificationCountUnavailableException : Exception("Contagem de notificações indisponível")

class GetUnreadNotificationCountUseCase(
    private val repository: UnreadNotificationCountRepository,
    private val sessions: AuthenticatedSessionIdentityProvider,
) {
    suspend operator fun invoke(): Int {
        val identity = sessions.captureIdentity() ?: return 0
        if (!sessions.isCurrent(identity)) return 0
        val count = try {
            repository.getCount(identity)
        } catch (_: IOException) {
            return 0
        } catch (_: UnreadNotificationCountUnavailableException) {
            return 0
        }
        return if (sessions.isCurrent(identity)) count.coerceAtLeast(0) else 0
    }
}
