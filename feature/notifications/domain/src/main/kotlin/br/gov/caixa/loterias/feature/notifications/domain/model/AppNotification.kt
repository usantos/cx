package br.gov.caixa.loterias.feature.notifications.domain.model

data class AppNotification(
    val id: Long,
    val category: NotificationCategory,
    val title: String,
    val content: String,
    val dateLabel: String,
    val isRead: Boolean
)
