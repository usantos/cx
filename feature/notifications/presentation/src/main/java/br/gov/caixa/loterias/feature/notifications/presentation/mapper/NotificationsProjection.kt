package br.gov.caixa.loterias.feature.notifications.presentation.mapper

import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.presentation.coordinator.NotificationsMutations
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationFilter
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsAction
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState

internal class NotificationsProjection(
    private val mutableState: ReduxStateStore<NotificationsUiState, NotificationsAction>,
    private val mutations: NotificationsMutations
) {

    fun updateQuery(query: String) = mutableState.dispatch(NotificationsAction.QueryChanged(query))

    fun selectFilter(filter: NotificationFilter) = mutableState.dispatch(NotificationsAction.FilterSelected(filter))

    fun toggleExpanded(message: AppNotification) {
        val current = mutableState.value.visibleMessages.firstOrNull { it.id == message.id } ?: return
        val willExpand = mutableState.value.expandedMessageId != message.id
        mutableState.dispatch(NotificationsAction.ExpansionChanged(if (willExpand) message.id else null))
        if (willExpand && !current.isRead) mutations.markOneAsRead(message.id, keepExpanded = true)
    }
}
