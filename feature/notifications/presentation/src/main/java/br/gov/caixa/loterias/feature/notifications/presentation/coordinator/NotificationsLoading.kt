package br.gov.caixa.loterias.feature.notifications.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.notifications.domain.usecase.GetNotificationPreferencesUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.GetNotificationsUseCase
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.notificationUserMessage
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsAction
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class NotificationsLoading(
    private val mutableState: ReduxStateStore<NotificationsUiState, NotificationsAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getNotifications: GetNotificationsUseCase,
    private val getPreferences: GetNotificationPreferencesUseCase,
    private val operations: NotificationPreferenceOperations = NotificationPreferenceOperations(),
) {

    fun load() = operations.enqueue(scope, dispatchers) {
        mutableState.dispatch(NotificationsAction.LoadStarted)
        val messages = getNotifications()
        val preferences = getPreferences()
        mutableState.dispatch(
            NotificationsAction.Loaded(
                messages = (messages as? AppResult.Success)?.value,
                preferences = (preferences as? AppResult.Success)?.value,
                error = listOf(messages, preferences).firstNotNullOfOrNull { result ->
                    (result as? AppResult.Failure)?.error?.notificationUserMessage()
                }
            )
        )
    }
}
