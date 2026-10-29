package br.gov.caixa.loterias.core.domain.payment

import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.feature.payment.domain.model.CardPurchasePolling
import br.gov.caixa.loterias.feature.payment.domain.model.PurchasePolling
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseProgress
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNotice
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNoticeReason
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect

class CardPurchasePollingTest {
    @Test fun `notice cannot accidentally end processing as a successful purchase`() {
        for (reason in PurchaseNoticeReason.entries) {
            assertThrows(IllegalArgumentException::class.java) {
                CardPurchasePolling(91, 1000, 3).after(PurchaseProgress(91, 8, notice = PurchaseNotice(reason)))
            }
        }
    }
    @Test fun `redirect cannot accidentally be interpreted as completed polling`() {
        for (destination in PurchaseRedirect.entries) {
            assertThrows(IllegalArgumentException::class.java) {
                CardPurchasePolling(91, 1000, 3).after(PurchaseProgress(91, 8, redirect = destination))
            }
        }
    }
    @Test fun `position equal to repetitions ends without an additional query`() {
        assertFalse(CardPurchasePolling(1, 0, 0).hasNext)
        assertTrue(CardPurchasePolling(1, 1000, 2, 1).hasNext)
        assertFalse(CardPurchasePolling(1, 1000, 2, 2).hasNext)
        assertFalse(CardPurchasePolling(1, 1000, 2, 3).hasNext)
    }

    @Test fun `every continuation replaces server parameters and advances existing position`() {
        val first = CardPurchasePolling(1, 1000, 3)
        val second = requireNotNull(first.after(PurchaseProgress(2, 7, PurchasePolling(4000, 7))))
        assertEquals(CardPurchasePolling(2, 4000, 7, 1), second)
        val third = requireNotNull(second.after(PurchaseProgress(3, 7, PurchasePolling(8000, 2))))
        assertEquals(CardPurchasePolling(3, 8000, 2, 2), third)
        assertFalse(third.hasNext)
    }

    @Test fun `protocol completion does not depend on the payment status classifier`() {
        val polling = CardPurchasePolling(1, 1000, 3)
        for (status in (-1..15)) assertNull(polling.after(PurchaseProgress(1, status)))
        assertNotNull(polling.after(PurchaseProgress(1, 8, PurchasePolling(1, 3))))
    }

    @Test fun `negative timing is immediate and negative repetitions end the continuation`() {
        val next = requireNotNull(CardPurchasePolling(1, 1, 2).after(PurchaseProgress(1, 7, PurchasePolling(-1, -1))))
        assertEquals(CardPurchasePolling(1, 0, 0, 1), next)
        assertFalse(next.hasNext)
    }
}
