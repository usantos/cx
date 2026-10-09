package br.gov.caixa.loterias.apostas.view.homev2

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class HomeV2FormattingTest {
    @Test fun prizesUseBrazilianUnitsWithoutRoundingAwayFraction() {
        assertEquals("R$ 100 milhões", HomeV2Formatting.prize(BigDecimal("100000000")))
        assertEquals("R$ 3,3 milhões", HomeV2Formatting.prize(BigDecimal("3300000")))
        assertEquals("R$ 600 mil", HomeV2Formatting.prize(BigDecimal("600000")))
        assertEquals("R$ 1 milhão", HomeV2Formatting.prize(BigDecimal("1000000")))
        assertEquals("Prêmio a confirmar", HomeV2Formatting.prize(null))
    }
    @Test fun datesKeepMinutesAndDoNotInventTimeForDateOnly() {
        assertEquals("sex. • 21h30", HomeV2Formatting.date("09/10/2026 21:30:00"))
        assertEquals("09/10", HomeV2Formatting.date("09/10/2026"))
        assertEquals("Sorteio a confirmar", HomeV2Formatting.date(null))
        assertEquals("Sorteio a confirmar", HomeV2Formatting.date("invalid"))
    }
}
