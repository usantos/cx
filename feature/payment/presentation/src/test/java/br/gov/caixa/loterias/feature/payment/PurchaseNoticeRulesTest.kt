package br.gov.caixa.loterias.feature.payment

import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNotice
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNoticeReason
import java.time.LocalDateTime
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PURCHASE_INVALID_IDENTIFICATION
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PURCHASE_OPERATION_FAILED
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PURCHASE_REQUEST_FAILED
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PurchaseNoticeUiState
import br.gov.caixa.loterias.feature.payment.presentation.mapper.purchaseNoticeUi

class PurchaseNoticeRulesTest {
    @Test fun `registration offers Sim Nao without diagnostics or more bets`() {
        for (message in listOf(null, "", "Deseja iniciar seu cadastro?")) {
            assertEquals(PurchaseNoticeUiState(message?.takeIf { it.isNotEmpty() } ?: PURCHASE_OPERATION_FAILED, false, true),
                purchaseNoticeUi(PurchaseNotice(PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION, message, "018003"), LocalDateTime.of(2026, 9, 7, 12, 0)))
        }
    }
    private val now = LocalDateTime.of(2026, 9, 7, 12, 3, 4)

    @Test fun `more bets retains server message without code or timestamp and uses empty message fallback`() {
        for (message in listOf(null, "", "Valor mínimo", " ")) {
            val result = purchaseNoticeUi(PurchaseNotice(PurchaseNoticeReason.MORE_BETS_REQUIRED, message, "011010"), now)
            assertEquals(message?.takeIf { it.isNotEmpty() } ?: PURCHASE_OPERATION_FAILED, result.message)
            assertTrue(result.offerMoreBets)
        }
    }

    @Test fun `business error includes local diagnostic time and code only when present`() {
        for (code in listOf(null, "", "011999")) {
            for (message in listOf(null, "", "Regra de compra", " ")) {
                val result = purchaseNoticeUi(PurchaseNotice(PurchaseNoticeReason.BUSINESS_RULE, message, code), now)
                assertEquals(
                    (message?.takeIf { it.isNotEmpty() } ?: PURCHASE_OPERATION_FAILED) + "\n\n" +
                        (code?.let { "$it\n" } ?: "") + "07/09/2026 12:03:04",
                    result.message,
                )
                assertFalse(result.offerMoreBets)
            }
        }
    }

    @Test fun `server type one preserves an empty message but falls back for null`() {
        for (message in listOf(null, "", "Mensagem negocial")) {
            val result = purchaseNoticeUi(PurchaseNotice(PurchaseNoticeReason.SERVER_MESSAGE, message, "5001"), now)
            assertEquals((message ?: PURCHASE_REQUEST_FAILED) + "\n\n5001\n07/09/2026 12:03:04", result.message)
            assertFalse(result.offerMoreBets)
        }
    }

    @Test fun `generic server error does not display its technical message`() {
        val result = purchaseNoticeUi(PurchaseNotice(PurchaseNoticeReason.SERVER_FAILURE, "Internal technical details", "E1"), now)
        assertEquals("$PURCHASE_REQUEST_FAILED\n\nE1\n07/09/2026 12:03:04", result.message)
        assertFalse(result.offerMoreBets)
    }

    @Test fun `Mercado Pago identification error has no diagnostic suffix or additional action`() {
        assertEquals(
            PurchaseNoticeUiState(PURCHASE_INVALID_IDENTIFICATION, false),
            purchaseNoticeUi(PurchaseNotice(PurchaseNoticeReason.INVALID_IDENTIFICATION, "Ignored", "2067"), now),
        )
    }
}
