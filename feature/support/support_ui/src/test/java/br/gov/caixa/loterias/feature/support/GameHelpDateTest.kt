package br.gov.caixa.loterias.feature.support

import br.gov.caixa.loterias.feature.support.presentation.components.gameHelpDate
import org.junit.Assert.assertEquals
import org.junit.Test

class GameHelpDateTest {
    @Test fun `formats backend timestamps using the lottery timezone and compact legacy layout`() {
        assertEquals("16/10", gameHelpDate("16/10/2026 21:00:00"))
        assertEquals("16/10 | 20h30", gameHelpDate("16/10/2026 20:30:00", includeTime = true))
        assertEquals("16/10 | 20h30", gameHelpDate("2026-10-16T23:30:00Z", includeTime = true))
        assertEquals("16/10", gameHelpDate("16/10/2026"))
        assertEquals("16/10", gameHelpDate("2026-10-16"))
    }

    @Test fun `missing or unrecognized dates never become fixed example dates`() {
        assertEquals("Não informado", gameHelpDate("  "))
        assertEquals("A confirmar", gameHelpDate("A confirmar", includeTime = true))
    }
}
