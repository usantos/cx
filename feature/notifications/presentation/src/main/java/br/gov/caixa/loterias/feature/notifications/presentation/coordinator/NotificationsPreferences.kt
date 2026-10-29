package br.gov.caixa.loterias.feature.notifications.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.domain.usecase.GetNotificationPreferencesUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.SetNotificationPreferenceUseCase
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.notificationUserMessage
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsAction
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class NotificationsPreferences(
    private val mutableState: ReduxStateStore<NotificationsUiState, NotificationsAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getPreferences: GetNotificationPreferencesUseCase,
    private val setPreference: SetNotificationPreferenceUseCase,
    private val operations: NotificationPreferenceOperations = NotificationPreferenceOperations(),
) {

    fun togglePreference(category: NotificationCategory?, enabled: Boolean) = operations.enqueue(scope, dispatchers) {
        when (val result = setPreference(category, enabled)) {
            is AppResult.Success -> reloadPreferences()
            is AppResult.Failure -> mutableState.dispatch(NotificationsAction.PreferenceFailed(result.error.notificationUserMessage()))
        }
    }

    private suspend fun reloadPreferences() {
        when (val result = getPreferences()) {
            is AppResult.Success -> mutableState.dispatch(NotificationsAction.PreferencesLoaded(result.value))
            is AppResult.Failure -> mutableState.dispatch(NotificationsAction.PreferenceFailed(result.error.notificationUserMessage()))
        }
    }
}
