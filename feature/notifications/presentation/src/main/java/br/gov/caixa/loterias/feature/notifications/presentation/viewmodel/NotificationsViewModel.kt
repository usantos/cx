package br.gov.caixa.loterias.feature.notifications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.event.SingleConsumerEventQueue
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.domain.usecase.DeleteAllNotificationsUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.DeleteNotificationUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.GetNotificationPreferencesUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.GetNotificationsUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.MarkAllNotificationsReadUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.MarkNotificationReadUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.SetNotificationPreferenceUseCase
import br.gov.caixa.loterias.feature.notifications.presentation.coordinator.NotificationPreferenceOperations
import br.gov.caixa.loterias.feature.notifications.presentation.coordinator.NotificationsActions
import br.gov.caixa.loterias.feature.notifications.presentation.coordinator.NotificationsLoading
import br.gov.caixa.loterias.feature.notifications.presentation.coordinator.NotificationsMutations
import br.gov.caixa.loterias.feature.notifications.presentation.coordinator.NotificationsPreferences
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.NotificationsProjection
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationEffect
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationFilter
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsAction
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState
import br.gov.caixa.loterias.feature.notifications.presentation.state.reduceNotificationsState

class NotificationsViewModel(
    private val getNotifications: GetNotificationsUseCase,
    private val markRead: MarkNotificationReadUseCase,
    private val deleteNotification: DeleteNotificationUseCase,
    private val deleteAll: DeleteAllNotificationsUseCase,
    private val markAllRead: MarkAllNotificationsReadUseCase,
    private val getPreferences: GetNotificationPreferencesUseCase,
    private val setPreference: SetNotificationPreferenceUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = reduxStateStore<NotificationsUiState, NotificationsAction>(
        NotificationsUiState(),
        ::reduceNotificationsState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state

    private val mutableEffects = SingleConsumerEventQueue<NotificationEffect>()
    val effects = mutableEffects.events

    private val preferenceOperations = NotificationPreferenceOperations()
    private val loading =
        NotificationsLoading(mutableState, viewModelScope, dispatchers, getNotifications, getPreferences, preferenceOperations)
    private val mutations =
        NotificationsMutations(mutableState, viewModelScope, dispatchers, markRead, deleteNotification, deleteAll, markAllRead)
    private val projection = NotificationsProjection(mutableState, mutations)
    private val preferences =
        NotificationsPreferences(mutableState, viewModelScope, dispatchers, getPreferences, setPreference, preferenceOperations)
    private val actions = NotificationsActions(mutableState, viewModelScope, mutableEffects)

    init {
        load()
    }

    fun load() = loading.load()

    fun updateQuery(query: String) = projection.updateQuery(query)

    fun selectFilter(filter: NotificationFilter) = projection.selectFilter(filter)

    fun toggleExpanded(message: AppNotification) = projection.toggleExpanded(message)

    fun requestDeleteAll() = actions.requestDeleteAll()

    fun requestMarkAllRead() = actions.requestMarkAllRead()

    fun requestDelete(id: Long) = actions.requestDelete(id)

    fun requestMarkRead(id: Long) = actions.requestMarkRead(id)

    fun dismissDialog() = actions.dismissDialog()

    fun confirmDialog() = mutations.confirmDialog()

    fun share(message: AppNotification) = actions.share(message)

    fun performCategoryAction(message: AppNotification) = actions.performCategoryAction(message)

    fun togglePreference(category: NotificationCategory?, enabled: Boolean) = preferences.togglePreference(category, enabled)
}
