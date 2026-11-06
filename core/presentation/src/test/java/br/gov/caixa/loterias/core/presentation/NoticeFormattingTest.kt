package br.gov.caixa.loterias.core.presentation

import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test
import br.gov.caixa.loterias.core.presentation.formatting.legacyDiagnosticMessage

class NoticeFormattingTest {
    @Test fun `diagnostic formatting distinguishes absent and empty values without sampling current time`() {
        val now = LocalDateTime.of(2026, 9, 7, 12, 30, 15)
        assertEquals("Mensagem\n\n07/09/2026 12:30:15", legacyDiagnosticMessage("Mensagem", null, now))
        assertEquals("Mensagem\n\n\n07/09/2026 12:30:15", legacyDiagnosticMessage("Mensagem", "", now))
        assertEquals("\n\nE1\n07/09/2026 12:30:15", legacyDiagnosticMessage("", "E1", now))
    }
}
