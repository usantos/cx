package br.gov.caixa.loterias.feature.notifications.presentation.coordinator

import br.gov.caixa.loterias.core.common.event.SingleConsumerEventQueue
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.canShare
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.primaryAction
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationConfirmation
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationEffect
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationText
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsAction
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class NotificationsActions(
    private val mutableState: ReduxStateStore<NotificationsUiState, NotificationsAction>,
    private val scope: CoroutineScope,
    private val mutableEffects: SingleConsumerEventQueue<NotificationEffect>,
) {

    fun requestDeleteAll() {
        mutableState.dispatch(NotificationsAction.ConfirmationRequested(NotificationConfirmation.DeleteAll))
    }

    fun requestMarkAllRead() {
        mutableState.dispatch(NotificationsAction.ConfirmationRequested(NotificationConfirmation.MarkAllRead))
    }

    fun requestDelete(id: Long) = mutableState.dispatch(NotificationsAction.ConfirmationRequested(NotificationConfirmation.DeleteOne(id)))

    fun requestMarkRead(id: Long) =
        mutableState.dispatch(NotificationsAction.ConfirmationRequested(NotificationConfirmation.MarkOneRead(id)))

    fun dismissDialog() = mutableState.dispatch(NotificationsAction.DialogDismissed)

    fun share(message: AppNotification) {
        if (!message.category.canShare()) return
        scope.launch { mutableEffects.emit(NotificationEffect.Share(message.title, message.content)) }
    }

    fun performCategoryAction(message: AppNotification) {
        if (message.category.primaryAction() != null) {
            mutableState.dispatch(
                NotificationsAction.CategoryActionPerformed(NotificationText.Resource(R.string.notifications_realiza_acao_da_categoria))
            )
        }
    }
}
