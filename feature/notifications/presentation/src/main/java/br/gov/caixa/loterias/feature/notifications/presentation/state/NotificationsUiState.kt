package br.gov.caixa.loterias.feature.notifications.presentation.state

import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationPreference
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.filterMessages

enum class NotificationFilter { ALL, UNREAD }

sealed interface NotificationConfirmation {
    data object DeleteAll : NotificationConfirmation
    data object MarkAllRead : NotificationConfirmation
    data class DeleteOne(val id: Long) : NotificationConfirmation
    data class MarkOneRead(val id: Long) : NotificationConfirmation
}

sealed interface NotificationEffect {
    data class Share(val title: String, val content: String) : NotificationEffect
}

data class NotificationsUiState(
    val loading: Boolean = true,
    val messages: List<AppNotification> = emptyList(),
    val preferences: List<NotificationPreference> = emptyList(),
    val query: String = "",
    val filter: NotificationFilter = NotificationFilter.ALL,
    val expandedMessageId: Long? = null,
    val readWhileVisible: Set<Long> = emptySet(),
    val projectionRevision: Long = 0,
    val pendingConfirmation: NotificationConfirmation? = null,
    val categoryActionMessage: NotificationText? = null,
    val errorMessage: NotificationText? = null,
) {
    val visibleMessages: List<AppNotification>
        get() = filterMessages(messages, query, filter == NotificationFilter.UNREAD, readWhileVisible)

    val emptyMessage: Int
        get() = if (filter == NotificationFilter.UNREAD) {
            R.string.notifications_voce_nao_possui_mensagens_nao_lidas
        } else {
            R.string.notifications_voce_nao_possui_mensagens
        }
}
