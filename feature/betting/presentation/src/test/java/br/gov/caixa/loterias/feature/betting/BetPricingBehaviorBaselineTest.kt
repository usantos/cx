package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.presentation.mapper.calculateBetTotal
import br.gov.caixa.loterias.feature.betting.presentation.mapper.resolveBetPrice
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test

class BetPricingBehaviorBaselineTest {
    private val game = LotteryGameParameters(
        LotteryModality(9, "Mais Milionária", "MAISMILIONARIA"),
        1, "27/10/2026", 50, 6, 12, 10, emptyList(), listOf(BetPrice(6, 2, BigDecimal.TEN), BetPrice(6, 3, BigDecimal("30")))
    )

    private val cloverPriceCases = listOf(2 to "10", 3 to "30", 4 to "10")
    private val totalCases = listOf(Triple(0, 0, "10"), Triple(2, 3, "60"), Triple(-1, -1, "10"))

    @Test fun exactCloverPriceWinsAndMissingCloverFallsBackToNumberPrice() {
        cloverPriceCases.forEach { (clovers, expected) ->
            assertEquals(BigDecimal(expected), resolveBetPrice(game, PRICED_NUMBER_COUNT, clovers))
        }
    }

    @Test fun missingNumberPriceIsZeroForPresentation() {
        assertEquals(BigDecimal.ZERO, resolveBetPrice(game, UNPRICED_NUMBER_COUNT, 2))
    }

    @Test fun recurringAndQuantityEachHaveMinimumOne() {
        totalCases.forEach { (recurring, quantity, expected) ->
            assertEquals(BigDecimal(expected), calculateBetTotal(BigDecimal.TEN, recurring, quantity))
        }
    }
}

private const val PRICED_NUMBER_COUNT = 6
private const val UNPRICED_NUMBER_COUNT = 7
private const val MINIMUM_LOTECA_PROGNOSES = 15
private const val BET_QUANTITY = 3
