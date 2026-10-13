package br.gov.caixa.loterias.feature.notifications

import br.gov.caixa.loterias.core.domain.notifications.AppNotification
import br.gov.caixa.loterias.core.domain.notifications.NotificationCategory

internal fun filterMessages(
    messages: List<AppNotification>,
    query: String,
    unreadOnly: Boolean,
    retainedReadIds: Set<Long> = emptySet(),
): List<AppNotification> {
    val normalizedQuery = query.trim()
    return messages.filter { message ->
        (!unreadOnly || !message.isRead || message.id in retainedReadIds) &&
            (normalizedQuery.isEmpty() || message.title.contains(normalizedQuery, ignoreCase = true) ||
                message.content.contains(normalizedQuery, ignoreCase = true))
    }
}

internal fun NotificationCategory.primaryAction(): String? = when (this) {
    NotificationCategory.LUCK_REMINDERS -> "Apostar agora"
    NotificationCategory.RESULTS_AVAILABLE -> "Conferir agora"
    else -> null
}

internal fun NotificationCategory.canShare(): Boolean = when (this) {
    NotificationCategory.LUCK_REMINDERS,
    NotificationCategory.TIPS_NEWS -> true
    else -> false
}
