package br.gov.caixa.loterias.feature.notifications.presentation.state

import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationInbox

sealed interface CentralNotificationsAction {
    data object IdentityChanged : CentralNotificationsAction
    data object PrivateStateCleared : CentralNotificationsAction
    data object LoadStarted : CentralNotificationsAction
    data class Loaded(val inbox: NotificationInbox, val error: NotificationText?) : CentralNotificationsAction
    data class LoadFailed(val error: NotificationText) : CentralNotificationsAction
    data class UnreadSelected(val selected: Boolean) : CentralNotificationsAction
    data class ConfirmationRequested(val confirmation: InboxConfirmation) : CentralNotificationsAction
    data object DialogDismissed : CentralNotificationsAction
    data object MutationStarted : CentralNotificationsAction
}

fun reduceCentralNotificationsState(state: CentralNotificationsState, action: CentralNotificationsAction): CentralNotificationsState =
    when (action) {
        CentralNotificationsAction.IdentityChanged -> CentralNotificationsState()

        CentralNotificationsAction.PrivateStateCleared -> CentralNotificationsState(isAnonymous = true)

        CentralNotificationsAction.LoadStarted -> state.copy(loading = true, error = null, pending = null)

        is CentralNotificationsAction.Loaded -> state.copy(
            all = action.inbox.all,
            unread = action.inbox.unread,
            loading = false,
            error = action.error
        )

        is CentralNotificationsAction.LoadFailed -> state.copy(loading = false, error = action.error)

        is CentralNotificationsAction.UnreadSelected -> state.copy(unreadOnly = action.selected)

        is CentralNotificationsAction.ConfirmationRequested -> state.copy(pending = action.confirmation)

        CentralNotificationsAction.DialogDismissed -> state.copy(pending = null, error = null)

        CentralNotificationsAction.MutationStarted -> state.copy(loading = true)
    }
