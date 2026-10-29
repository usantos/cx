package br.gov.caixa.loterias.feature.notifications.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.notifications.domain.usecase.DeleteAllNotificationsUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.DeleteNotificationUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.MarkAllNotificationsReadUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.MarkNotificationReadUseCase
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.notificationUserMessage
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationConfirmation
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsAction
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class NotificationsMutations(
    private val mutableState: ReduxStateStore<NotificationsUiState, NotificationsAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val markRead: MarkNotificationReadUseCase,
    private val deleteNotification: DeleteNotificationUseCase,
    private val deleteAll: DeleteAllNotificationsUseCase,
    private val markAllRead: MarkAllNotificationsReadUseCase,
) {

    fun confirmDialog() {
        when (val confirmation = mutableState.value.pendingConfirmation) {
            NotificationConfirmation.DeleteAll -> mutate({ deleteAll() }, NotificationsAction.AllDeleted)

            NotificationConfirmation.MarkAllRead -> mutate({ markAllRead() }, NotificationsAction.AllMarkedRead)

            is NotificationConfirmation.DeleteOne -> mutate({
                deleteNotification(confirmation.id)
            }, NotificationsAction.Deleted(confirmation.id))

            is NotificationConfirmation.MarkOneRead -> markOneAsRead(confirmation.id, keepExpanded = false)

            null -> Unit
        }
    }

    fun markOneAsRead(id: Long, keepExpanded: Boolean) {
        val projection = mutableState.value.projectionRevision
        mutate({ markRead(id) }, NotificationsAction.MarkedRead(id, keepExpanded, projection))
    }

    private fun mutate(operation: suspend () -> AppResult<Unit>, success: NotificationsAction) = scope.launch(dispatchers.io) {
        when (val result = operation()) {
            is AppResult.Success -> mutableState.dispatch(success)
            is AppResult.Failure -> mutableState.dispatch(NotificationsAction.MutationFailed(result.error.notificationUserMessage()))
        }
    }
}
