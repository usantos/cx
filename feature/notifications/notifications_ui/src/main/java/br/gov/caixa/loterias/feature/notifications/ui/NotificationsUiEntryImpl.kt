package br.gov.caixa.loterias.feature.notifications.ui

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.notifications.domain.model.InboxNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.presentation.delivery.NotificationsUiEntry
import br.gov.caixa.loterias.feature.notifications.presentation.screen.NotificationMessageActions
import br.gov.caixa.loterias.feature.notifications.presentation.state.CentralNotificationsState
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState

@Keep
class NotificationsUiEntryImpl : NotificationsUiEntry {
    @Composable
    override fun CentralNotificationsScreen(
        state: CentralNotificationsState,
        onBack: () -> Unit,
        onSelectUnread: (Boolean) -> Unit,
        onMarkAll: () -> Unit,
        onClear: () -> Unit,
        onRemove: (InboxNotification) -> Unit,
        onRetry: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.notifications.presentation.screen.DeliveredCentralNotificationsScreen(
            state,
            onBack,
            onSelectUnread,
            onMarkAll,
            onClear,
            onRemove,
            onRetry
        )
    }

    @Composable
    override fun MessagesScreen(state: NotificationsUiState, actions: NotificationMessageActions, onBack: () -> Unit) {
        br.gov.caixa.loterias.feature.notifications.presentation.screen.DeliveredMessagesScreen(state, actions, onBack)
    }

    @Composable
    override fun PreferencesScreen(
        state: NotificationsUiState,
        onTogglePreference: (NotificationCategory?, Boolean) -> Unit,
        onBack: () -> Unit,
        onHelp: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.notifications.presentation.screen.DeliveredPreferencesScreen(
            state,
            onTogglePreference,
            onBack,
            onHelp
        )
    }

    @Composable
    override fun NotificationDialogs(state: NotificationsUiState, onConfirm: () -> Unit, onDismiss: () -> Unit) {
        br.gov.caixa.loterias.feature.notifications.presentation.components.DeliveredNotificationDialogs(state, onConfirm, onDismiss)
    }

    @Composable
    override fun CentralNotificationsDialogs(state: CentralNotificationsState, onConfirm: () -> Unit, onDismiss: () -> Unit) {
        br.gov.caixa.loterias.feature.notifications.presentation.screen.DeliveredCentralNotificationsDialogs(state, onConfirm, onDismiss)
    }
}
