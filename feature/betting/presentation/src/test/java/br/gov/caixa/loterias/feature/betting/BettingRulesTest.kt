package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.presentation.mapper.availableNumberQuantities
import br.gov.caixa.loterias.feature.betting.presentation.mapper.calculateBetTotal
import br.gov.caixa.loterias.feature.betting.presentation.mapper.canSubmitNumericBet
import br.gov.caixa.loterias.feature.betting.presentation.mapper.formatBetAmount
import br.gov.caixa.loterias.feature.betting.presentation.mapper.resolveBetPrice
import br.gov.caixa.loterias.feature.betting.presentation.mapper.toggleSelectedNumber
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BettingRulesTest {
    @Test fun `currency labels use Brazilian separators and render missing amounts as zero`() {
        assertEquals("R$ 1.234,50", formatBetAmount(BigDecimal("1234.5")))
        assertEquals("R$ 0,00", formatBetAmount(null))
    }

    @Test fun `toggle adds and removes an allowed number`() {
        assertEquals(setOf(7), toggleSelectedNumber(emptySet(), 7, 1..60, 20))
        assertEquals(emptySet<Int>(), toggleSelectedNumber(setOf(7), 7, 1..60, 20))
    }

    @Test fun `toggle rejects invalid number and selection above limit`() {
        assertEquals(setOf(1), toggleSelectedNumber(setOf(1), 61, 1..60, 20))
        assertEquals(setOf(1, 2), toggleSelectedNumber(setOf(1, 2), 3, 1..60, 2))
        assertEquals(emptySet<Int>(), toggleSelectedNumber(emptySet(), 1, 1..60, -1))
    }

    @Test fun `numeric bet respects inclusive selection limits`() {
        assertTrue(canSubmitNumericBet((1..6).toSet(), 6, 20))
        assertTrue(canSubmitNumericBet((1..20).toSet(), 6, 20))
        assertFalse(canSubmitNumericBet((1..5).toSet(), 6, 20))
        assertFalse(canSubmitNumericBet((1..21).toSet(), 6, 20))
    }

    @Test fun `number quantities use valid backend prices and fallback to configured range`() {
        val priced = game(prices = listOf(price(8, "40"), price(6, "5"), price(8, "45"), price(30, "1")))
        assertEquals(listOf(6, 8), availableNumberQuantities(priced))
        assertEquals((6..10).toList(), availableNumberQuantities(game(prices = emptyList())))
        assertTrue(availableNumberQuantities(game(minimum = 10, maximum = 6, prices = emptyList())).isEmpty())
    }

    @Test fun `price prefers exact clover count then number quantity and zero fallback`() {
        val game = game(prices = listOf(price(6, "5", 0), price(6, "8", 2)))
        assertEquals(BigDecimal("8"), resolveBetPrice(game, 6, 2))
        assertEquals(BigDecimal("5"), resolveBetPrice(game, 6, 1))
        assertEquals(BigDecimal.ZERO, resolveBetPrice(game, 7, 0))
    }

    @Test fun `total uses at least one draw and one bet`() {
        assertEquals(BigDecimal("10"), calculateBetTotal(BigDecimal.TEN, 0, 0))
        assertEquals(BigDecimal("60"), calculateBetTotal(BigDecimal.TEN, 3, 2))
    }

    private fun game(minimum: Int = 6, maximum: Int = 10, prices: List<BetPrice>) = LotteryGameParameters(
        modality = LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
        contestNumber = 1,
        drawDate = "hoje",
        maximumNumber = 60,
        minimumSelection = minimum,
        maximumSelection = maximum,
        maximumSurprises = 15,
        recurringOptions = listOf(0, 2),
        prices = prices
    )

    private fun price(numbers: Int, value: String, clovers: Int = 0) = BetPrice(numbers, clovers, BigDecimal(value))
}
