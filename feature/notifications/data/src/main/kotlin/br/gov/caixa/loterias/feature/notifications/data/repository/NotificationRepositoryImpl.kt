package br.gov.caixa.loterias.feature.notifications.data.repository

import br.gov.caixa.loterias.feature.notifications.data.datasource.local.LocalNotificationMessages
import br.gov.caixa.loterias.feature.notifications.data.datasource.local.LocalNotificationPreferences
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationPreference
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationRepository

/**
 * Reproduz a caixa de entrada local de exemplo de MensagemPushModel. IDs são chaves locais, nunca IDs do servidor.
 * Crie uma instância por ViewModel de destino, como uma nova instância da tela legada.
 * O pop-up autenticado da última notificação usa um repositório e um protocolo separados.
 */
class NotificationRepositoryImpl : NotificationRepository {
    private val messages = LocalNotificationMessages()
    private val preferences = LocalNotificationPreferences()

    override suspend fun getNotifications(): List<AppNotification> = synchronized(this) { messages.snapshot() }

    override suspend fun markAsRead(id: Long) {
        synchronized(this) { messages.markAsRead(id) }
    }

    override suspend fun delete(id: Long) {
        synchronized(this) { messages.delete(id) }
    }

    override suspend fun deleteAll() {
        synchronized(this) { messages.deleteAll() }
    }

    override suspend fun markAllAsRead() {
        synchronized(this) { messages.markAllAsRead() }
    }

    override suspend fun getPreferences(): List<NotificationPreference> = synchronized(this) { preferences.snapshot() }

    override suspend fun setPreference(category: NotificationCategory?, enabled: Boolean) {
        synchronized(this) { preferences.set(category, enabled) }
    }
}
