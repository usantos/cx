package br.gov.caixa.loterias.core.domain.notifications

import br.gov.caixa.loterias.feature.notifications.domain.usecase.GetNotifications
import br.gov.caixa.loterias.feature.notifications.domain.usecase.GetNotificationPreferences
import br.gov.caixa.loterias.feature.notifications.domain.usecase.MarkNotificationRead
import br.gov.caixa.loterias.feature.notifications.domain.usecase.DeleteNotification
import br.gov.caixa.loterias.feature.notifications.domain.usecase.DeleteAllNotifications
import br.gov.caixa.loterias.feature.notifications.domain.usecase.SetNotificationPreference
import br.gov.caixa.loterias.feature.notifications.domain.usecase.MarkAllNotificationsRead
import br.gov.caixa.loterias.core.common.result.AppResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationPreference
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationRepository

class NotificationUseCasesTest {
    @Test fun `all notification use cases delegate to repository`() = runTest {
        val repository = RecordingRepository()

        assertTrue(GetNotifications(repository)() is AppResult.Success)
        assertTrue(MarkNotificationRead(repository)(7L) is AppResult.Success)
        assertTrue(DeleteNotification(repository)(8L) is AppResult.Success)
        assertTrue(DeleteAllNotifications(repository)() is AppResult.Success)
        assertTrue(MarkAllNotificationsRead(repository)() is AppResult.Success)
        assertTrue(GetNotificationPreferences(repository)() is AppResult.Success)
        assertTrue(SetNotificationPreference(repository)(NotificationCategory.TIPS_NEWS, true) is AppResult.Success)

        assertEquals(
            listOf("get", "read:7", "delete:8", "deleteAll", "readAll", "preferences", "preference:TIPS_NEWS:true"),
            repository.calls,
        )
    }

    @Test fun `repository errors are converted to app failures`() = runTest {
        val repository = RecordingRepository(fail = true)

        assertTrue(GetNotifications(repository)() is AppResult.Failure)
        assertTrue(MarkNotificationRead(repository)(1L) is AppResult.Failure)
        assertTrue(DeleteNotification(repository)(1L) is AppResult.Failure)
        assertTrue(DeleteAllNotifications(repository)() is AppResult.Failure)
        assertTrue(MarkAllNotificationsRead(repository)() is AppResult.Failure)
        assertTrue(GetNotificationPreferences(repository)() is AppResult.Failure)
        assertTrue(SetNotificationPreference(repository)(null, false) is AppResult.Failure)
    }

    private class RecordingRepository(private val fail: Boolean = false) : NotificationRepository {
        val calls = mutableListOf<String>()
        private fun record(value: String) {
            if (fail) error("failure")
            calls += value
        }

        override suspend fun getNotifications(): List<AppNotification> {
            record("get")
            return listOf(AppNotification(1, NotificationCategory.TIPS_NEWS, "T", "C", "D", false))
        }

        override suspend fun markAsRead(id: Long) = record("read:$id")
        override suspend fun delete(id: Long) = record("delete:$id")
        override suspend fun deleteAll() = record("deleteAll")
        override suspend fun markAllAsRead() = record("readAll")
        override suspend fun getPreferences(): List<NotificationPreference> {
            record("preferences")
            return listOf(NotificationPreference(null, "Todas", "", false))
        }

        override suspend fun setPreference(category: NotificationCategory?, enabled: Boolean) =
            record("preference:${category?.name}:$enabled")
    }
}
