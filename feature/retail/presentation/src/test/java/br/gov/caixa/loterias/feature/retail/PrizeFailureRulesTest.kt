package br.gov.caixa.loterias.feature.retail

import org.junit.Assert.*
import br.gov.caixa.loterias.core.domain.service.model.ServiceNotice
import br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_INVALID_IDENTIFICATION
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_LOGIN_REQUIRED
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_OPERATION_FAILED
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_REQUEST_FAILED
import java.time.LocalDateTime
import org.junit.Test
import br.gov.caixa.loterias.feature.retail.presentation.mapper.prizeNoticeUi

class PrizeFailureRulesTest {
    private val now = LocalDateTime.of(2026, 9, 7, 12, 30)

    @Test fun `each notice preserves labels diagnostics and fixed local timestamp`() {
        for (reason in ServiceNoticeReason.entries) {
            val state = prizeNoticeUi(ServiceNotice(reason, "Mensagem", "E1"), now)
            when (reason) {
                ServiceNoticeReason.LOGIN_REQUIRED -> {
                    assertEquals(LEGACY_LOGIN_REQUIRED, state.message)
                    assertEquals(ServiceRedirect.LOGIN, state.destination)
                    assertEquals("Entendi", state.positiveLabel); assertNull(state.negativeLabel)
                }
                ServiceNoticeReason.MORE_BETS_REQUIRED -> {
                    assertEquals("Mensagem", state.message)
                    assertEquals(ServiceRedirect.HOME, state.destination)
                    assertEquals("Apostar mais", state.positiveLabel); assertEquals("Entendi", state.negativeLabel)
                }
                else -> {
                    assertNull(state.destination); assertNull(state.negativeLabel); assertEquals("Entendi", state.positiveLabel)
                    val expected = when (reason) {
                        ServiceNoticeReason.INVALID_IDENTIFICATION -> LEGACY_INVALID_IDENTIFICATION
                        ServiceNoticeReason.OPERATION_FAILED -> LEGACY_OPERATION_FAILED
                        ServiceNoticeReason.SERVER_FAILURE -> "$LEGACY_REQUEST_FAILED\n\nE1\n07/09/2026 12:30:00"
                        else -> "Mensagem\n\nE1\n07/09/2026 12:30:00"
                    }
                    assertEquals(expected, state.message)
                }
            }
        }
    }

    @Test fun `empty business message falls back but empty type one server message is retained`() {
        for (message in listOf(null, "")) {
            assertEquals(LEGACY_OPERATION_FAILED, prizeNoticeUi(ServiceNotice(ServiceNoticeReason.MORE_BETS_REQUIRED, message), now).message)
            assertEquals("$LEGACY_OPERATION_FAILED\n\n07/09/2026 12:30:00", prizeNoticeUi(ServiceNotice(ServiceNoticeReason.BUSINESS_RULE, message), now).message)
        }
        assertEquals("\n\n\n07/09/2026 12:30:00", prizeNoticeUi(ServiceNotice(ServiceNoticeReason.SERVER_MESSAGE, "", ""), now).message)
        assertEquals("$LEGACY_REQUEST_FAILED\n\n07/09/2026 12:30:00", prizeNoticeUi(ServiceNotice(ServiceNoticeReason.SERVER_MESSAGE), now).message)
    }
}
