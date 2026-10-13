package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet
import br.gov.caixa.loterias.core.domain.tickets.Purchase
import java.math.BigDecimal
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PurchaseDetailRulesTest {
    private val basePurchase = Purchase(
        id = 9,
        date = "04/09/2026",
        status = "Cancelada",
        paymentMethod = "Pix",
        total = BigDecimal.TEN,
        statusId = 5,
        paymentMethodId = 6,
    )

    @Test fun `cancelled pix uses exact legacy copy and actions`() {
        val dialog = requireNotNull(basePurchase.statusDialog(hasPoolShare = false))
        assertEquals("Atenção", dialog.title)
        assertTrue(dialog.message.startsWith("Não recebemos seu Pix"))
        assertEquals(
            listOf(PurchaseDialogAction.SAVE_FAVORITE, PurchaseDialogAction.HELP, PurchaseDialogAction.DISMISS),
            dialog.actions.map { it.first },
        )
        assertNull(basePurchase.statusDialog(hasPoolShare = true))
    }

    @Test fun `purchase negative status rules cover card pix refund and timeout`() {
        val card = basePurchase.copy(paymentMethod = "Mercado Pago", paymentMethodId = 1)
        assertTrue(requireNotNull(card.statusDialog(false)).message.startsWith("Não identificamos"))
        assertTrue(requireNotNull(basePurchase.copy(statusId = 4).statusDialog(false)).message.contains("Pix será devolvido"))
        assertTrue(requireNotNull(basePurchase.copy(statusId = 12).statusDialog(false)).message.contains("estornado"))
        assertEquals(
            PurchaseDialogAction.NEW_PIX,
            basePurchase.copy(statusId = 3).statusDialog(false)?.actions?.first()?.first,
        )
        assertEquals(
            PurchaseDialogAction.SAVE_FAVORITE,
            card.copy(statusId = 3).statusDialog(false)?.actions?.first()?.first,
        )
        assertNull(basePurchase.copy(statusId = 8, status = "Finalizada").statusDialog(false))
    }

    @Test fun `legacy textual statuses remain supported when backend omits ids`() {
        assertTrue(basePurchase.copy(statusId = 0, status = "CANCELADA").statusDialog(false) != null)
        assertTrue(basePurchase.copy(statusId = 0, status = "ESTORNADA").statusDialog(false) != null)
        assertTrue(basePurchase.copy(statusId = 0, status = "DEBITO_NAO_AUTORIZADO").statusDialog(false) != null)
    }

    @Test fun `local purchase filters intersect contest status and modality`() {
        val bet = ConfirmedBet(
            id = 1,
            modality = "Mega-Sena",
            contest = 2900,
            date = "",
            status = "Paga",
            numbers = emptyList(),
            modalityId = 2,
            statusId = 7,
        )
        assertTrue(PurchaseBetFilters().matches(bet))
        assertTrue(PurchaseBetFilters(2900, 7, 2).matches(bet))
        assertEquals(3, PurchaseBetFilters(2900, 7, 2).activeCount)
        assertEquals(false, PurchaseBetFilters(contest = 2899).matches(bet))
        assertEquals(false, PurchaseBetFilters(statusId = 8).matches(bet))
        assertEquals(false, PurchaseBetFilters(modalityId = 3).matches(bet))
    }

    @Test fun `Pix countdown treats UTC and different offsets as the same expiration instant`() {
        val now = LocalDateTime.of(2026, 9, 4, 14, 0)
        for (expiration in listOf("2026-09-04T17:10:00Z", "2026-09-04T19:10:00+02:00", "2026-09-04T12:10:00-05:00")) {
            assertEquals(expiration, 600, basePurchase.copy(paymentExpiresAt = expiration).paymentSecondsRemaining(now))
        }
    }

    @Test fun `pix countdown parses offset and local backend dates`() {
        val now = LocalDateTime.of(2026, 9, 4, 14, 0)
        assertEquals(600, basePurchase.copy(paymentExpiresAt = "2026-09-04T14:10:00").paymentSecondsRemaining(now))
        assertEquals(600, basePurchase.copy(paymentExpiresAt = "2026-09-04T14:10:00-03:00").paymentSecondsRemaining(now))
        assertEquals(0, basePurchase.copy(paymentExpiresAt = "2026-09-04T13:59:00").paymentSecondsRemaining(now))
        assertEquals(0, basePurchase.copy(paymentExpiresAt = "inválida").paymentSecondsRemaining(now))
    }
}
