package br.gov.caixa.loterias.feature.notifications.data.datasource.local

import br.gov.caixa.loterias.feature.notifications.data.repository.NotificationRepositoryImpl
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification

/** Estado do destino; acessado somente sob o monitor de NotificationRepositoryImpl. */
internal class LocalNotificationMessages {
    private val messages = legacyNotificationInboxSamples().toMutableList()

    fun snapshot(): List<AppNotification> = messages.toList()

    fun markAsRead(id: Long) {
        val index = messages.indexOfFirst { it.id == id }
        if (index >= 0) messages[index] = messages[index].copy(isRead = true)
    }

    fun delete(id: Long) {
        messages.removeAll { it.id == id }
    }

    fun deleteAll() {
        messages.clear()
    }

    fun markAllAsRead() {
        messages.replaceAll { it.copy(isRead = true) }
    }
}
