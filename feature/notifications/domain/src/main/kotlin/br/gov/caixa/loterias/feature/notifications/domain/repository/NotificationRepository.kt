package br.gov.caixa.loterias.feature.notifications.domain.repository

import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationPreference

interface NotificationRepository {
    suspend fun getNotifications(): List<AppNotification>
    suspend fun markAsRead(id: Long)
    suspend fun delete(id: Long)
    suspend fun deleteAll()
    suspend fun markAllAsRead()
    suspend fun getPreferences(): List<NotificationPreference>
    suspend fun setPreference(category: NotificationCategory?, enabled: Boolean)
}
