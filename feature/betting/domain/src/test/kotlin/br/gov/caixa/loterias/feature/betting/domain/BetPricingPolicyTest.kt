package br.gov.caixa.loterias.feature.betting.domain

import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.rule.BetPricing
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BetPricingPolicyTest {
    private val game = LotteryGameParameters(
        LotteryModality(9, "Mais Milionária", "MAISMILIONARIA"),
        1, "", 50, 6, 12, 10, emptyList(), listOf(BetPrice(6, 2, BigDecimal.TEN), BetPrice(6, 3, BigDecimal("30")))
    )

    private val cloverPriceCases = listOf(2 to "10", 3 to "30", 4 to "10")
    private val totalCases = listOf(Triple(0, 0, "10"), Triple(2, 3, "60"), Triple(-1, -1, "10"))

    @Test fun exactPriceAndLegacyNumericFallbackRemainExplicit() {
        cloverPriceCases.forEach { (clovers, expected) ->
            assertEquals(BigDecimal(expected), BetPricing.numericPrice(game, PRICED_NUMBER_COUNT, clovers)?.amount)
        }
        assertNull(BetPricing.numericPrice(game, UNPRICED_NUMBER_COUNT, 2))
    }

    @Test fun lotecaDoesNotInventPriceForAnUnsupportedCombination() {
        val loteca = game.copy(prices = listOf(BetPrice(MINIMUM_LOTECA_PROGNOSES, 0, BigDecimal.TEN, doubleQuantity = 1)))
        assertEquals(BigDecimal.TEN, BetPricing.lotecaPrice(loteca, 1, 0)?.amount)
        assertNull(BetPricing.lotecaPrice(loteca, 0, 1))
    }

    @Test fun recurringAndQuantityAreAppliedOnceWithLegacyFloors() {
        totalCases.forEach { (recurring, quantity, expected) ->
            assertEquals(BigDecimal(expected), BetPricing.total(BigDecimal.TEN, recurring, quantity))
        }
    }

    @Test fun mirrorMultipliesTheCalculatedTotalExactlyOnce() {
        assertEquals(BigDecimal("120"), BetPricing.total(BigDecimal.TEN, 2, BET_QUANTITY, mirror = true))
        assertEquals(BigDecimal("60"), BetPricing.total(BigDecimal.TEN, 2, BET_QUANTITY, mirror = false))
    }
}

private const val PRICED_NUMBER_COUNT = 6
private const val UNPRICED_NUMBER_COUNT = 7
private const val MINIMUM_LOTECA_PROGNOSES = 15
private const val BET_QUANTITY = 3
