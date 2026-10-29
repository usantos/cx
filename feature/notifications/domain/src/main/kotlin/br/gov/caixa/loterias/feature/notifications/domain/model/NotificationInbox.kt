package br.gov.caixa.loterias.feature.notifications.domain.model

data class InboxNotification(val id: Long, val title: String, val content: String, val read: Boolean) {
    override fun toString() = "InboxNotification(redacted)"
}

data class NotificationInbox(val all: List<InboxNotification>, val unread: List<InboxNotification>) {
    override fun toString() = "NotificationInbox(redacted)"
}
