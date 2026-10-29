package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.feature.betting.presentation.mapper.legacyBettingHelpTitle
import org.junit.Assert.assertEquals
import org.junit.Test

class BettingInstalledHelpTitleTest {
    @Test fun runtimeIdSelectsInstalledEnumNameRegardlessOfBackendCapitalizationOrMegaSpacing() {
        val cases = listOf(
            Triple(1, "QUINA", "Como Jogar: quina"),
            Triple(2, "Mega Sena", "Como Jogar: mega-sena"),
            Triple(5, "Lotomania", "Como Jogar: lotomania"),
            Triple(7, "SUPER SETE", "Como Jogar: super sete"),
            Triple(8, "Lotofacil", "Como Jogar: lotofácil"),
            Triple(9, "Mais Milionária", "Como Jogar: +milionária"),
            Triple(11, "Dia de Sorte", "Como Jogar: dia de sorte"),
            Triple(14, "Lotogol", "Como Jogar: lotogol"),
            Triple(18, "Dupla Sena", "Como Jogar: dupla sena"),
            Triple(19, "Loteca", "Como Jogar: loteca"),
            Triple(20, "Timemania", "Como Jogar: timemania")
        )
        cases.forEach { (id, backendName, expected) -> assertEquals(expected, legacyBettingHelpTitle(id, backendName)) }
    }

    @Test fun unknownModalityRetainsItsProvidedName() {
        assertEquals("Como Jogar: Nome público", legacyBettingHelpTitle(999, "Nome público"))
    }
}
