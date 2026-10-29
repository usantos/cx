package br.gov.caixa.loterias.feature.notifications

import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationFilter
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsAction
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState
import br.gov.caixa.loterias.feature.notifications.presentation.state.reduceNotificationsState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationsReducerTest {
    private val message = AppNotification(1, NotificationCategory.entries.first(), "Title", "Content", "Today", false)

    @Test
    fun `read completion retains expanded unread item only for original projection`() {
        val initial = NotificationsUiState(messages = listOf(message), filter = NotificationFilter.UNREAD, expandedMessageId = 1)
        val same = reduceNotificationsState(initial, NotificationsAction.MarkedRead(1, true, initial.projectionRevision))
        assertEquals(setOf(1L), same.readWhileVisible)
        assertTrue(same.messages.single().isRead)
        val changed = reduceNotificationsState(initial, NotificationsAction.QueryChanged("Title"))
        val stale = reduceNotificationsState(changed, NotificationsAction.MarkedRead(1, true, initial.projectionRevision))
        assertTrue(stale.readWhileVisible.isEmpty())
        assertTrue(stale.messages.single().isRead)
        assertFalse(initial.messages.single().isRead)
    }

    @Test
    fun `delete clears expanded item and retained read membership atomically`() {
        val initial = NotificationsUiState(messages = listOf(message), expandedMessageId = 1, readWhileVisible = setOf(1))
        val deleted = reduceNotificationsState(initial, NotificationsAction.Deleted(1))
        assertTrue(deleted.messages.isEmpty())
        assertTrue(deleted.readWhileVisible.isEmpty())
        assertEquals(null, deleted.expandedMessageId)
    }
}
