package br.gov.caixa.loterias.feature.betting

import org.junit.Assert.assertTrue
import org.junit.Test

/** Runtime backend IDs, not the distinct legacy modality enumeration values. */
class BettingInstalledHelpTest {
    @Test fun quinaRuntimeIdShowsItsRulesRatherThanAnEmptyDialog() {
        assertTrue(legacyBettingHelp(1).startsWith("Escolha de 5 a 15 números entre os 80 disponíveis"))
    }
    @Test fun lotomaniaRuntimeIdShowsItsMirrorRulesRatherThanAnEmptyDialog() {
        assertTrue(legacyBettingHelp(5).contains("50 números que você não escolheu"))
    }
    @Test fun lotecaRuntimeIdShowsRequiredDoubleOrTripleRatherThanAnEmptyDialog() {
        assertTrue(legacyBettingHelp(19).contains("pelo menos 1 palpite deve ser duplo"))
    }
}
