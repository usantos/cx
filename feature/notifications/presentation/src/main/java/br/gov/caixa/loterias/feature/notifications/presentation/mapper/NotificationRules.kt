package br.gov.caixa.loterias.feature.notifications.presentation.mapper

import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory

internal fun filterMessages(
    messages: List<AppNotification>,
    query: String,
    unreadOnly: Boolean,
    retainedReadIds: Set<Long> = emptySet(),
): List<AppNotification> {
    val normalizedQuery = query.trim()
    return messages.filter { message ->
        (!unreadOnly || !message.isRead || message.id in retainedReadIds) &&
            (
                normalizedQuery.isEmpty() || message.title.contains(normalizedQuery, ignoreCase = true) ||
                    message.content.contains(normalizedQuery, ignoreCase = true)
                )
    }
}

internal fun NotificationCategory.primaryAction(): Int? = when (this) {
    NotificationCategory.LUCK_REMINDERS -> R.string.notifications_apostar_agora
    NotificationCategory.RESULTS_AVAILABLE -> R.string.notifications_conferir_agora
    else -> null
}

internal fun NotificationCategory.canShare(): Boolean = when (this) {
    NotificationCategory.LUCK_REMINDERS,
    NotificationCategory.TIPS_NEWS,
    -> true

    else -> false
}
