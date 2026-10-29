package br.gov.caixa.loterias.feature.notifications.data.repository

import br.gov.caixa.loterias.feature.notifications.data.repository.NotificationRepositoryImpl
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationRepositoryImplTest {
    @Test fun `legacy messages are unique and all unread`() = runTest {
        val repository = NotificationRepositoryImpl()
        val messages = repository.getNotifications()
        repository.markAsRead(messages.first().id)

        assertEquals(5, messages.size)
        assertEquals(messages.size, messages.map { it.id }.distinct().size)
        assertTrue(messages.none { it.isRead })
    }

    @Test fun `read changes only matching local ids and unknown ids have no side effects`() = runTest {
        val repository = NotificationRepositoryImpl()

        repository.markAsRead(1L)
        repository.markAsRead(2L)
        repository.markAsRead(99L)

        assertTrue(repository.getNotifications().first { it.id == 1L }.isRead)
        assertTrue(repository.getNotifications().first { it.id == 2L }.isRead)
        assertEquals(5, repository.getNotifications().size)
        assertTrue(repository.getNotifications().filter { it.id > 2L }.none { it.isRead })
    }

    @Test fun `delete one and delete all update the session list`() = runTest {
        val repository = NotificationRepositoryImpl()

        repository.delete(3L)
        repository.delete(99L)
        assertEquals(listOf(1L, 2L, 4L, 5L), repository.getNotifications().map { it.id })

        repository.deleteAll()
        assertTrue(repository.getNotifications().isEmpty())
    }

    @Test fun `mark all updates local messages and is idempotent`() = runTest {
        val repository = NotificationRepositoryImpl()
        repository.markAsRead(1L)

        repository.markAllAsRead()
        repository.markAllAsRead()

        assertTrue(repository.getNotifications().all { it.isRead })
        assertEquals(5, repository.getNotifications().size)
    }

    @Test fun `legacy preference switches are independent local state without fabricated subscriptions`() = runTest {
        val repository = NotificationRepositoryImpl()
        assertTrue(repository.getPreferences().none { it.enabled })
        assertEquals(6, repository.getPreferences().size)

        repository.setPreference(null, true)
        assertTrue(repository.getPreferences().first().enabled)
        assertTrue(repository.getPreferences().drop(1).none { it.enabled })

        repository.setPreference(NotificationCategory.TIPS_NEWS, true)
        repository.setPreference(NotificationCategory.RESULTS_AVAILABLE, false)
        val partlyEnabled = repository.getPreferences()
        assertTrue(partlyEnabled.first().enabled)
        assertFalse(partlyEnabled.first { it.category == NotificationCategory.RESULTS_AVAILABLE }.enabled)
        assertTrue(partlyEnabled.first { it.category == NotificationCategory.TIPS_NEWS }.enabled)

        repository.setPreference(NotificationCategory.RESULTS_AVAILABLE, true)
        assertTrue(repository.getPreferences().first().enabled)

        repository.setPreference(null, false)
        assertFalse(repository.getPreferences().first().enabled)
        assertTrue(repository.getPreferences().first { it.category == NotificationCategory.RESULTS_AVAILABLE }.enabled)
        assertTrue(NotificationRepositoryImpl().getPreferences().none { it.enabled })
    }

    @Test fun `fresh destination restores legacy local samples and does not inherit read delete or preference state`() = runTest {
        val first = NotificationRepositoryImpl()
        first.markAllAsRead()
        first.deleteAll()
        first.setPreference(null, true)
        val second = NotificationRepositoryImpl()
        assertEquals(5, second.getNotifications().size)
        assertTrue(second.getNotifications().none { it.isRead })
        assertTrue(second.getPreferences().none { it.enabled })
        assertTrue(first.getNotifications().isEmpty())
    }
}
