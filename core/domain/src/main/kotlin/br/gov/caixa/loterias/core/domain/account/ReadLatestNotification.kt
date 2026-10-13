package br.gov.caixa.loterias.core.domain.account

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf
import br.gov.caixa.loterias.core.domain.service.ServiceFailure

sealed interface NotificationReadResult {
    data object Confirmed : NotificationReadResult
    data class Rejected(val failure: ServiceFailure) : NotificationReadResult
}

interface LatestNotificationSession {
    fun capture(notification: AccountNotification): AuthenticatedSessionIdentity?
    /** Clear only the same instance, without overwriting a newer notification/cart/session. */
    fun clear(identity: AuthenticatedSessionIdentity, notification: AccountNotification): Boolean
}

fun interface LatestNotificationReadRepository {
    suspend fun markRead(identity: AuthenticatedSessionIdentity, notificationId: Long): NotificationReadResult
}

fun interface ReadLatestNotificationUseCase {
    suspend operator fun invoke(notification: AccountNotification): AppResult<NotificationReadResult>
}

class ReadLatestNotification(
    private val session: LatestNotificationSession,
    private val remote: LatestNotificationReadRepository,
) : ReadLatestNotificationUseCase {
    override suspend fun invoke(notification: AccountNotification) = appResultOf {
        val identity = checkNotNull(session.capture(notification)) { "Notificação de outra sessão" }
        // Legacy dismisses locally but makes no request/clear when the notification has no id.
        notification.id?.let { id ->
            require(id > 0) { "Identificação de notificação inválida" }
            val result = remote.markRead(identity, id)
            if (result is NotificationReadResult.Rejected) return@appResultOf result
            check(session.clear(identity, notification)) { "Notificação ou sessão substituída" }
        }
        NotificationReadResult.Confirmed
    }
}
