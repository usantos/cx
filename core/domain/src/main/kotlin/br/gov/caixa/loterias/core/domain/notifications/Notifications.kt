package br.gov.caixa.loterias.core.domain.notifications

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf

enum class NotificationCategory {
    PAYMENTS_REDEMPTIONS,
    LUCK_REMINDERS,
    RESULTS_AVAILABLE,
    TIPS_NEWS,
    SECURITY_ALERTS,
}

data class AppNotification(
    val id: Long,
    val category: NotificationCategory,
    val title: String,
    val content: String,
    val dateLabel: String,
    val isRead: Boolean,
)

data class NotificationPreference(
    val category: NotificationCategory?,
    val title: String,
    val description: String,
    val enabled: Boolean,
)

interface NotificationRepository {
    suspend fun getNotifications(): List<AppNotification>
    suspend fun markAsRead(id: Long)
    suspend fun delete(id: Long)
    suspend fun deleteAll()
    suspend fun markAllAsRead()
    suspend fun getPreferences(): List<NotificationPreference>
    suspend fun setPreference(category: NotificationCategory?, enabled: Boolean)
}

fun interface GetNotificationsUseCase {
    suspend operator fun invoke(): AppResult<List<AppNotification>>
}

class GetNotifications(private val repository: NotificationRepository) : GetNotificationsUseCase {
    override suspend fun invoke() = appResultOf { repository.getNotifications() }
}

fun interface MarkNotificationReadUseCase {
    suspend operator fun invoke(id: Long): AppResult<Unit>
}

class MarkNotificationRead(private val repository: NotificationRepository) : MarkNotificationReadUseCase {
    override suspend fun invoke(id: Long) = appResultOf { repository.markAsRead(id) }
}

fun interface DeleteNotificationUseCase {
    suspend operator fun invoke(id: Long): AppResult<Unit>
}

class DeleteNotification(private val repository: NotificationRepository) : DeleteNotificationUseCase {
    override suspend fun invoke(id: Long) = appResultOf { repository.delete(id) }
}

fun interface DeleteAllNotificationsUseCase {
    suspend operator fun invoke(): AppResult<Unit>
}

class DeleteAllNotifications(private val repository: NotificationRepository) : DeleteAllNotificationsUseCase {
    override suspend fun invoke() = appResultOf { repository.deleteAll() }
}

fun interface MarkAllNotificationsReadUseCase {
    suspend operator fun invoke(): AppResult<Unit>
}

class MarkAllNotificationsRead(private val repository: NotificationRepository) : MarkAllNotificationsReadUseCase {
    override suspend fun invoke() = appResultOf { repository.markAllAsRead() }
}

fun interface GetNotificationPreferencesUseCase {
    suspend operator fun invoke(): AppResult<List<NotificationPreference>>
}

class GetNotificationPreferences(private val repository: NotificationRepository) : GetNotificationPreferencesUseCase {
    override suspend fun invoke() = appResultOf { repository.getPreferences() }
}

fun interface SetNotificationPreferenceUseCase {
    suspend operator fun invoke(category: NotificationCategory?, enabled: Boolean): AppResult<Unit>
}

class SetNotificationPreference(private val repository: NotificationRepository) : SetNotificationPreferenceUseCase {
    override suspend fun invoke(category: NotificationCategory?, enabled: Boolean) =
        appResultOf { repository.setPreference(category, enabled) }
}
