package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.core.domain.retail.*
import br.gov.caixa.loterias.core.presentation.*
import java.time.LocalDateTime
import org.junit.Assert.*
import org.junit.Test

class PrizeFailureRulesTest {
    private val now = LocalDateTime.of(2026, 9, 7, 12, 30)

    @Test fun `each notice preserves labels diagnostics and fixed local timestamp`() {
        for (reason in PrizeNoticeReason.entries) {
            val state = prizeNoticeUi(PrizeNotice(reason, "Mensagem", "E1"), now)
            when (reason) {
                PrizeNoticeReason.LOGIN_REQUIRED -> {
                    assertEquals(LEGACY_LOGIN_REQUIRED, state.message)
                    assertEquals(PrizeRedirect.LOGIN, state.destination)
                    assertEquals("Entendi", state.positiveLabel); assertNull(state.negativeLabel)
                }
                PrizeNoticeReason.MORE_BETS_REQUIRED -> {
                    assertEquals("Mensagem", state.message)
                    assertEquals(PrizeRedirect.HOME, state.destination)
                    assertEquals("Apostar mais", state.positiveLabel); assertEquals("Entendi", state.negativeLabel)
                }
                else -> {
                    assertNull(state.destination); assertNull(state.negativeLabel); assertEquals("Entendi", state.positiveLabel)
                    val expected = when (reason) {
                        PrizeNoticeReason.INVALID_IDENTIFICATION -> LEGACY_INVALID_IDENTIFICATION
                        PrizeNoticeReason.OPERATION_FAILED -> LEGACY_OPERATION_FAILED
                        PrizeNoticeReason.SERVER_FAILURE -> "$LEGACY_REQUEST_FAILED\n\nE1\n07/09/2026 12:30:00"
                        else -> "Mensagem\n\nE1\n07/09/2026 12:30:00"
                    }
                    assertEquals(expected, state.message)
                }
            }
        }
    }

    @Test fun `empty business message falls back but empty type one server message is retained`() {
        for (message in listOf(null, "")) {
            assertEquals(LEGACY_OPERATION_FAILED, prizeNoticeUi(PrizeNotice(PrizeNoticeReason.MORE_BETS_REQUIRED, message), now).message)
            assertEquals("$LEGACY_OPERATION_FAILED\n\n07/09/2026 12:30:00", prizeNoticeUi(PrizeNotice(PrizeNoticeReason.BUSINESS_RULE, message), now).message)
        }
        assertEquals("\n\n\n07/09/2026 12:30:00", prizeNoticeUi(PrizeNotice(PrizeNoticeReason.SERVER_MESSAGE, "", ""), now).message)
        assertEquals("$LEGACY_REQUEST_FAILED\n\n07/09/2026 12:30:00", prizeNoticeUi(PrizeNotice(PrizeNoticeReason.SERVER_MESSAGE), now).message)
    }
}
