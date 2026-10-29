package br.gov.caixa.loterias.core.domain.betting

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.rule.LotterySelectionPolicy

class LotterySelectionPolicyTest {
    @Test fun `Super Sete adds another digit per column only after each seven-number boundary`() {
        for (quantity in 0..7) assertEquals(1, LotterySelectionPolicy.superSevenMaximumPerColumn(quantity))
        for (quantity in 8..14) assertEquals(2, LotterySelectionPolicy.superSevenMaximumPerColumn(quantity))
        for (quantity in 15..21) assertEquals(3, LotterySelectionPolicy.superSevenMaximumPerColumn(quantity))
    }

    @Test fun `Loteca accepts only exact backend priced double and triple counts`() {
        val prices = listOf(
            BetPrice(15, 0, BigDecimal.TEN, doubleQuantity = 1),
            BetPrice(16, 0, BigDecimal.ZERO, tripleQuantity = 1),
        )
        assertTrue(LotterySelectionPolicy.hasLotecaPrice(prices, doubles = 1, triples = 0))
        assertTrue(LotterySelectionPolicy.hasLotecaPrice(prices, doubles = 0, triples = 1))
        assertFalse(LotterySelectionPolicy.hasLotecaPrice(prices, doubles = 1, triples = 1))
        assertFalse(LotterySelectionPolicy.hasLotecaPrice(prices, doubles = 0, triples = 0))
        assertFalse(LotterySelectionPolicy.hasLotecaPrice(emptyList(), doubles = 1, triples = 0))
    }
}
