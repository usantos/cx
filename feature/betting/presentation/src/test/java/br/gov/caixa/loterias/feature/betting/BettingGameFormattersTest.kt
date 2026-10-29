package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.feature.betting.presentation.mapper.bettingHeaderDrawDate
import br.gov.caixa.loterias.feature.betting.presentation.mapper.fallbackBettingTitle
import br.gov.caixa.loterias.feature.betting.presentation.mapper.recurringLabel
import br.gov.caixa.loterias.feature.betting.presentation.mapper.specialHeaderTitle
import org.junit.Assert.assertEquals
import org.junit.Test

class BettingGameFormattersTest {
    @Test fun `recurrence retains singular plural and zero labels`() {
        assertEquals("Nenhum concurso", recurringLabel(0))
        assertEquals("1 concurso", recurringLabel(1))
        assertEquals("4 concursos", recurringLabel(4))
    }

    @Test fun `fallback titles retain the existing modality identifiers`() {
        val expected = mapOf(
            2 to "Mega-Sena", 3 to "Quina", 5 to "Lotomania", 7 to "Super Sete",
            8 to "Lotofácil", 9 to "+Milionária", 11 to "Dia de Sorte", 14 to "Lotogol",
            18 to "Dupla Sena", 19 to "Loteca", 20 to "Timemania"
        )
        expected.forEach { (id, title) -> assertEquals(title, fallbackBettingTitle(id)) }
        assertEquals("Simular aposta", fallbackBettingTitle(0))
        assertEquals("Simular aposta", fallbackBettingTitle(999))
    }

    @Test fun `special headers retain their literal line breaks regardless of backend title`() {
        assertEquals("mega da \nvirada", specialHeaderTitle(2, "Outro nome"))
        assertEquals("quina de \nsão joão", specialHeaderTitle(3, ""))
        assertEquals("lotofácil da \nindependência", specialHeaderTitle(8, ""))
        assertEquals("dupla de \npáscoa", specialHeaderTitle(18, ""))
    }

    @Test fun `other special headers lowercase the backend name and keep surrounding spaces`() {
        assertEquals("  concurso especial  ", specialHeaderTitle(999, "  Concurso Especial  "))
        assertEquals("", specialHeaderTitle(999, "   "))
        assertEquals("", specialHeaderTitle(999, ""))
    }

    @Test fun `draw date retains the anchored prefix without trimming or new validation`() {
        assertEquals("10/09", bettingHeaderDrawDate("10/09/2026 20:00:00"))
        assertEquals("10/09", bettingHeaderDrawDate("10/09/2026"))
        assertEquals("99/99", bettingHeaderDrawDate("99/99"))
        assertEquals("", bettingHeaderDrawDate(" 10/09/2026"))
        assertEquals("", bettingHeaderDrawDate("2026-09-10"))
        assertEquals("", bettingHeaderDrawDate(""))
    }
}
