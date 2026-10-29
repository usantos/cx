package br.gov.caixa.loterias.feature.notifications.di

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationRepository
import br.gov.caixa.loterias.feature.notifications.domain.usecase.DeleteAllNotifications
import br.gov.caixa.loterias.feature.notifications.domain.usecase.DeleteNotification
import br.gov.caixa.loterias.feature.notifications.domain.usecase.GetNotificationPreferences
import br.gov.caixa.loterias.feature.notifications.domain.usecase.GetNotifications
import br.gov.caixa.loterias.feature.notifications.domain.usecase.MarkAllNotificationsRead
import br.gov.caixa.loterias.feature.notifications.domain.usecase.MarkNotificationRead
import br.gov.caixa.loterias.feature.notifications.domain.usecase.SetNotificationPreference
import br.gov.caixa.loterias.feature.notifications.presentation.viewmodel.NotificationsViewModel
import javax.inject.Inject
import javax.inject.Provider

/** Raiz de composição: casos de uso de um destino compartilham um repositório local, nunca estado global do aplicativo. */
class NotificationViewModelFactory @Inject constructor(
    private val repositories: Provider<NotificationRepository>,
    private val dispatchers: CoroutineDispatchers
) : br.gov.caixa.loterias.feature.notifications.navigation.NotificationsViewModelProvider {
    override fun create(): NotificationsViewModel {
        val repository = repositories.get()
        return NotificationsViewModel(
            GetNotifications(repository),
            MarkNotificationRead(repository),
            DeleteNotification(repository),
            DeleteAllNotifications(repository),
            MarkAllNotificationsRead(repository),
            GetNotificationPreferences(repository),
            SetNotificationPreference(repository),
            dispatchers
        )
    }
}
