package br.gov.caixa.loterias.feature.notifications.presentation.state

import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationPreference

sealed interface NotificationsAction {
    data object LoadStarted : NotificationsAction
    data class Loaded(val messages: List<AppNotification>?, val preferences: List<NotificationPreference>?, val error: NotificationText?) :
        NotificationsAction
    data class ConfirmationRequested(val confirmation: NotificationConfirmation) : NotificationsAction
    data object DialogDismissed : NotificationsAction
    data class CategoryActionPerformed(val message: NotificationText) : NotificationsAction
    data class QueryChanged(val query: String) : NotificationsAction
    data class FilterSelected(val filter: NotificationFilter) : NotificationsAction
    data class ExpansionChanged(val id: Long?) : NotificationsAction
    data object AllDeleted : NotificationsAction
    data object AllMarkedRead : NotificationsAction
    data class Deleted(val id: Long) : NotificationsAction
    data class MarkedRead(val id: Long, val keepExpanded: Boolean, val projectionRevision: Long) : NotificationsAction
    data class MutationFailed(val error: NotificationText) : NotificationsAction
    data class PreferencesLoaded(val preferences: List<NotificationPreference>) : NotificationsAction
    data class PreferenceFailed(val error: NotificationText) : NotificationsAction
}

fun reduceNotificationsState(state: NotificationsUiState, action: NotificationsAction): NotificationsUiState = when (action) {
    NotificationsAction.LoadStarted -> state.copy(loading = true, errorMessage = null)

    is NotificationsAction.Loaded -> state.copy(
        loading = false,
        messages = action.messages ?: state.messages,
        preferences = action.preferences ?: state.preferences,
        errorMessage = action.error
    )

    is NotificationsAction.ConfirmationRequested -> state.copy(pendingConfirmation = action.confirmation)

    NotificationsAction.DialogDismissed -> state.copy(pendingConfirmation = null, categoryActionMessage = null, errorMessage = null)

    is NotificationsAction.CategoryActionPerformed -> state.copy(categoryActionMessage = action.message)

    is NotificationsAction.PreferencesLoaded -> state.copy(preferences = action.preferences, errorMessage = null)

    is NotificationsAction.PreferenceFailed -> state.copy(errorMessage = action.error)

    else -> reduceNotificationMessages(state, action)
}

private fun reduceNotificationMessages(state: NotificationsUiState, action: NotificationsAction): NotificationsUiState = when (action) {
    NotificationsAction.AllDeleted -> state.copy(
        messages = emptyList(),
        expandedMessageId = null,
        readWhileVisible = emptySet(),
        pendingConfirmation = null,
        errorMessage = null
    )

    NotificationsAction.AllMarkedRead -> state.copy(
        messages = state.messages.map { it.copy(isRead = true) },
        readWhileVisible = emptySet(),
        expandedMessageId = null,
        pendingConfirmation = null,
        errorMessage = null
    )

    is NotificationsAction.Deleted -> state.copy(
        messages = state.messages.filterNot { it.id == action.id },
        readWhileVisible = state.readWhileVisible - action.id,
        expandedMessageId = state.expandedMessageId?.takeUnless { it == action.id },
        pendingConfirmation = null,
        errorMessage = null
    )

    is NotificationsAction.MarkedRead -> {
        val retain =
            action.keepExpanded && state.filter == NotificationFilter.UNREAD && action.projectionRevision == state.projectionRevision &&
                state.messages.any { it.id == action.id }
        state.copy(
            messages = state.messages.map { if (it.id == action.id) it.copy(isRead = true) else it },
            readWhileVisible = if (retain) state.readWhileVisible + action.id else state.readWhileVisible - action.id,
            expandedMessageId = if (action.keepExpanded) state.expandedMessageId else null,
            pendingConfirmation = null,
            errorMessage = null
        )
    }

    is NotificationsAction.MutationFailed -> state.copy(pendingConfirmation = null, errorMessage = action.error)

    else -> reduceNotificationsProjection(state, action)
}

private fun reduceNotificationsProjection(state: NotificationsUiState, action: NotificationsAction): NotificationsUiState = when (action) {
    is NotificationsAction.QueryChanged -> state.copy(
        query = action.query,
        readWhileVisible = if (action.query.isEmpty()) emptySet() else state.readWhileVisible,
        projectionRevision =
            state.projectionRevision + 1
    )

    is NotificationsAction.FilterSelected -> state.copy(
        filter = action.filter,
        expandedMessageId = null,
        readWhileVisible = emptySet(),
        projectionRevision = state.projectionRevision + 1
    )

    is NotificationsAction.ExpansionChanged -> state.copy(expandedMessageId = action.id)

    else -> state
}
