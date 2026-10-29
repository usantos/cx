package br.gov.caixa.loterias.feature.notifications

import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.canShare
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.filterMessages
import br.gov.caixa.loterias.feature.notifications.presentation.mapper.primaryAction
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationRulesTest {
    private val messages = listOf(
        message(1, NotificationCategory.RESULTS_AVAILABLE, "Resultado disponível", "Confira sua aposta", false),
        message(2, NotificationCategory.PAYMENTS_REDEMPTIONS, "Pagamento aprovado", "Compra confirmada", true)
    )

    @Test fun `blank query returns all messages`() {
        assertEquals(messages, filterMessages(messages, "  ", unreadOnly = false))
    }

    @Test fun `unread filter keeps only unread messages`() {
        assertEquals(listOf(messages.first()), filterMessages(messages, "", unreadOnly = true))
    }

    @Test fun `query matches title or content ignoring case`() {
        assertEquals(listOf(messages.first()), filterMessages(messages, "RESULTADO", unreadOnly = false))
        assertEquals(listOf(messages.last()), filterMessages(messages, "confirmada", unreadOnly = false))
        assertEquals(emptyList<AppNotification>(), filterMessages(messages, "ausente", unreadOnly = false))
    }

    @Test fun `category actions match the legacy behavior`() {
        assertEquals(R.string.notifications_apostar_agora, NotificationCategory.LUCK_REMINDERS.primaryAction())
        assertEquals(R.string.notifications_conferir_agora, NotificationCategory.RESULTS_AVAILABLE.primaryAction())
        assertNull(NotificationCategory.PAYMENTS_REDEMPTIONS.primaryAction())
        assertNull(NotificationCategory.TIPS_NEWS.primaryAction())
        assertNull(NotificationCategory.SECURITY_ALERTS.primaryAction())
    }

    @Test fun `only luck reminders and news can be shared`() {
        assertTrue(NotificationCategory.LUCK_REMINDERS.canShare())
        assertTrue(NotificationCategory.TIPS_NEWS.canShare())
        assertFalse(NotificationCategory.RESULTS_AVAILABLE.canShare())
        assertFalse(NotificationCategory.PAYMENTS_REDEMPTIONS.canShare())
        assertFalse(NotificationCategory.SECURITY_ALERTS.canShare())
    }
}

internal fun message(
    id: Long,
    category: NotificationCategory,
    title: String = "Título",
    content: String = "Conteúdo",
    read: Boolean = false,
) = AppNotification(id, category, title, content, "hoje", read)
