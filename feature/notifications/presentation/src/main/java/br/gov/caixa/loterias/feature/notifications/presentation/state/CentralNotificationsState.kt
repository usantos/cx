package br.gov.caixa.loterias.feature.notifications.presentation.state

import br.gov.caixa.loterias.feature.notifications.domain.model.InboxNotification

sealed interface InboxConfirmation {
    data object MarkAll : InboxConfirmation
    data object Clear : InboxConfirmation
    data class Remove(val notification: InboxNotification) : InboxConfirmation
}

data class CentralNotificationsState(
    val all: List<InboxNotification> = emptyList(),
    val unread: List<InboxNotification> = emptyList(),
    val unreadOnly: Boolean = false,
    val loading: Boolean = false,
    val isAnonymous: Boolean = false,
    val pending: InboxConfirmation? = null,
    val error: NotificationText? = null,
) {
    val visible get() = if (unreadOnly) unread else all
    val canMarkAll get() = !loading && !isAnonymous && unread.isNotEmpty()
    val canDeleteAll get() = !loading && !isAnonymous && !unreadOnly && all.isNotEmpty()
    override fun toString() = "CentralNotificationsState(redacted)"
}
