package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.domain.notifications.*
import br.gov.caixa.loterias.feature.notifications.NotificationsViewModel
import javax.inject.Inject
import javax.inject.Provider

/** Composition root: all use cases of one destination share one local repository, never app-wide state. */
class NotificationViewModelFactory @Inject constructor(
    private val repositories: Provider<NotificationRepository>,
    private val dispatchers: CoroutineDispatchers,
) {
    fun create(): NotificationsViewModel {
        val repository = repositories.get()
        return NotificationsViewModel(
            GetNotifications(repository), MarkNotificationRead(repository), DeleteNotification(repository),
            DeleteAllNotifications(repository), MarkAllNotificationsRead(repository),
            GetNotificationPreferences(repository), SetNotificationPreference(repository), dispatchers,
        )
    }
}
