package br.gov.caixa.loterias.core.presentation

import br.gov.caixa.loterias.core.domain.service.*
import java.time.LocalDateTime
import org.junit.Assert.*
import org.junit.Test

class ServiceNoticeRulesTest {
    private val now = LocalDateTime.of(2026, 9, 7, 12, 30)

    @Test fun `all shared notices preserve message actions and diagnostic snapshot`() {
        for (reason in ServiceNoticeReason.entries) {
            val ui = serviceNoticeUi(ServiceNotice(reason, "Mensagem", "E1"), now)
            val expectedMessage = when (reason) {
                ServiceNoticeReason.LOGIN_REQUIRED -> "Sua sessão expirou. É necessário realizar o login novamente para continuar utilizando o aplicativo Loterias CAIXA."
                ServiceNoticeReason.INVALID_IDENTIFICATION -> LEGACY_INVALID_IDENTIFICATION
                ServiceNoticeReason.OPERATION_FAILED -> LEGACY_OPERATION_FAILED
                ServiceNoticeReason.MORE_BETS_REQUIRED -> "Mensagem"
                ServiceNoticeReason.SERVER_FAILURE -> "$LEGACY_REQUEST_FAILED\n\nE1\n07/09/2026 12:30:00"
                else -> "Mensagem\n\nE1\n07/09/2026 12:30:00"
            }
            assertEquals(expectedMessage, ui.message)
            when (reason) {
                ServiceNoticeReason.LOGIN_REQUIRED -> {
                    assertEquals(ServiceRedirect.LOGIN, ui.destination); assertEquals("Entendi", ui.positiveLabel); assertNull(ui.negativeLabel)
                }
                ServiceNoticeReason.MORE_BETS_REQUIRED -> {
                    assertEquals(ServiceRedirect.HOME, ui.destination); assertEquals("Apostar mais", ui.positiveLabel); assertEquals("Entendi", ui.negativeLabel)
                }
                else -> { assertNull(ui.destination); assertNull(ui.negativeLabel); assertEquals("Entendi", ui.positiveLabel) }
            }
        }
    }

    @Test fun `empty server type one message differs from empty business message`() {
        for (message in listOf(null, "")) {
            assertEquals(LEGACY_OPERATION_FAILED, serviceNoticeUi(ServiceNotice(ServiceNoticeReason.MORE_BETS_REQUIRED, message), now).message)
            assertEquals("$LEGACY_OPERATION_FAILED\n\n07/09/2026 12:30:00", serviceNoticeUi(ServiceNotice(ServiceNoticeReason.BUSINESS_RULE, message), now).message)
        }
        assertEquals("\n\n\n07/09/2026 12:30:00", serviceNoticeUi(ServiceNotice(ServiceNoticeReason.SERVER_MESSAGE, "", ""), now).message)
        assertEquals("$LEGACY_REQUEST_FAILED\n\n07/09/2026 12:30:00", serviceNoticeUi(ServiceNotice(ServiceNoticeReason.SERVER_MESSAGE), now).message)
    }
}
