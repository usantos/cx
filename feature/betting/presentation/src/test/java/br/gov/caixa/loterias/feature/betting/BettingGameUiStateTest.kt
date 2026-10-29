package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BettingGameUiStateTest {
    @Test fun `manual exit confirmation recognizes each kind of partially edited bet`() {
        val empty = BettingGameUiState(loading = false, game = game())
        assertFalse(empty.hasPendingEdits)
        assertFalse(empty.copy(lotecaOutcomes = mapOf(1 to emptySet()), lotogolScores = mapOf(1 to (null to null))).hasPendingEdits)
        val edits = listOf(
            empty.copy(selectedNumbers = setOf(1)),
            empty.copy(superSevenColumns = listOf(setOf(0)) + List(6) { emptySet() }),
            empty.copy(lotecaOutcomes = mapOf(1 to setOf(LotecaOutcome.HOME))),
            empty.copy(lotogolScores = mapOf(1 to (0 to null))),
            empty.copy(lotogolScores = mapOf(1 to (null to 5)))
        )
        edits.forEach {
            assertTrue(it.hasPendingEdits)
            assertFalse(it.copy(addedToCart = true).hasPendingEdits)
        }
    }

    @Test fun `surprise exit confirmation recognizes changed quantities and mirror`() {
        val basic = BettingGameUiState(
            loading = false,
            game = game(),
            mode = BetEntryMode.SURPRISE,
            numberQuantity = 6,
            surpriseCloverQuantity = 2
        )
        assertFalse(basic.hasPendingEdits)
        assertFalse(basic.copy(game = null).hasPendingEdits)
        listOf(
            basic.copy(surpriseQuantity = 2),
            basic.copy(numberQuantity = 7),
            basic.copy(surpriseCloverQuantity = 3),
            basic.copy(generateMirror = true)
        ).forEach {
            assertTrue(it.hasPendingEdits)
            assertFalse(it.copy(addedToCart = true).hasPendingEdits)
        }
    }

    @Test fun `clover picker exposes priced distinct quantities only for the current number count`() {
        val state = BettingGameUiState(
            game = game().copy(
                prices = listOf(
                    BetPrice(6, 4, BigDecimal("36")),
                    BetPrice(6, 2, BigDecimal("6")),
                    BetPrice(6, 4, BigDecimal("36")),
                    BetPrice(7, 3, BigDecimal("126")),
                    BetPrice(6, 0, BigDecimal("1"))
                )
            ),
            numberQuantity = 6
        )
        assertEquals(listOf(2, 4), state.cloverQuantityOptions)
        assertEquals(listOf(3), state.copy(numberQuantity = 7).cloverQuantityOptions)
        assertTrue(state.copy(game = null).cloverQuantityOptions.isEmpty())
    }

    @Test fun `price preview multiplies recurring draws and surprise bets before mirror`() {
        val manual = BettingGameUiState(
            loading = false,
            game = game(),
            numberQuantity = 6,
            manualCloverQuantity = 2,
            selectedClovers = setOf(1, 2),
            recurringDraws = 4,
            surpriseQuantity = 3
        )
        assertEquals(BigDecimal("24.00"), manual.totalPrice)
        assertEquals(BigDecimal("72.00"), manual.copy(mode = BetEntryMode.SURPRISE, surpriseCloverQuantity = 2).totalPrice)
        assertEquals(
            BigDecimal("144.00"),
            manual.copy(mode = BetEntryMode.SURPRISE, surpriseCloverQuantity = 2, generateMirror = true).totalPrice
        )
    }

    private fun game() = LotteryGameParameters(
        modality = LotteryModality(MAIS_MILIONARIA_ID, "+Milionária", "MAIS_MILIONARIA"),
        contestNumber = 1, drawDate = "13/10/2026", maximumNumber = 50,
        minimumSelection = 6, maximumSelection = 7, maximumSurprises = 3,
        recurringOptions = listOf(2, 4), minimumClovers = 2, maximumClovers = 4,
        prices = listOf(BetPrice(6, 2, BigDecimal("6.00")))
    )
}
