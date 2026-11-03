package br.gov.caixa.loterias.feature.notifications.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry
import br.gov.caixa.loterias.feature.notifications.domain.model.InboxNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.presentation.screen.NotificationMessageActions
import br.gov.caixa.loterias.feature.notifications.presentation.state.CentralNotificationsState
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState

interface NotificationsUiEntry : FeatureUiEntry {
    @Composable
    fun CentralNotificationsScreen(
        state: CentralNotificationsState,
        onBack: () -> Unit,
        onSelectUnread: (Boolean) -> Unit,
        onMarkAll: () -> Unit,
        onClear: () -> Unit,
        onRemove: (InboxNotification) -> Unit,
        onRetry: () -> Unit,
    )

    @Composable
    fun MessagesScreen(state: NotificationsUiState, actions: NotificationMessageActions, onBack: () -> Unit)

    @Composable
    fun PreferencesScreen(
        state: NotificationsUiState,
        onTogglePreference: (NotificationCategory?, Boolean) -> Unit,
        onBack: () -> Unit,
        onHelp: () -> Unit,
    )

    @Composable
    fun NotificationDialogs(state: NotificationsUiState, onConfirm: () -> Unit, onDismiss: () -> Unit)

    @Composable
    fun CentralNotificationsDialogs(state: CentralNotificationsState, onConfirm: () -> Unit, onDismiss: () -> Unit)
}
