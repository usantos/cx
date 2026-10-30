// Literais numéricos descrevem seleções de loteria, identificadores e massas de teste de limites.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.presentation.mapper.shakeComplete
import br.gov.caixa.loterias.feature.betting.presentation.mapper.withShake
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.DIA_DE_SORTE_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTOGOL_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.SUPER_SEVEN_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.TIMEMANIA_ID
import java.math.BigDecimal
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ShakeSelectionBranchesTest {
    private val team = SportsTeam(1, "Time A")
    private val month = LuckyMonth(1, "Janeiro")

    @Test fun missingGameAndEveryBlockedStateKeepCurrentSelections() {
        val input = state().copy(selectedNumbers = setOf(1))
        val blocked = listOf(
            input.copy(game = null),
            input.copy(loading = true),
            input.copy(addedToCart = true),
            input.copy(duplicateConfirmation = true),
            input.copy(mode = BetEntryMode.SURPRISE),
            input.copy(game = game(LOTOGOL_ID))
        )
        blocked.forEach { assertSame(it, it.withShake(true, Random(3))) }
    }

    @Test fun completionKeepsExistingNumbersAndClearsMirrorAndError() {
        val result = state().copy(selectedNumbers = setOf(2), generateMirror = true, errorMessage = "Anterior").withShake(false, Random(4))
        assertEquals(6, result.selectedNumbers.size)
        assertTrue(2 in result.selectedNumbers)
        assertTrue(result.selectedNumbers.all { it in 1..60 })
        assertFalse(result.generateMirror)
        assertEquals(null, result.errorMessage)
        assertTrue(result.shakeComplete())
    }

    @Test fun renewalChangesACompleteSelectionEvenWhenInitialShuffleRepeatsIt() {
        val input = state().copy(game = game().copy(maximumNumber = 7), selectedNumbers = (2..7).toSet())
        val result = input.withShake(true, zeroRandom)
        assertEquals(6, result.selectedNumbers.size)
        assertNotEquals(input.selectedNumbers, result.selectedNumbers)
        assertTrue(1 in result.selectedNumbers)
    }

    @Test fun impossibleNumberCountPreservesPreviousNumbersAndFullRangeCanRemainUnchanged() {
        val old = setOf(1, 2)
        listOf(0, 61).forEach { count ->
            assertEquals(old, state().copy(numberQuantity = count, selectedNumbers = old).withShake(true, Random(2)).selectedNumbers)
        }
        val full = state().copy(game = game().copy(maximumNumber = 6), selectedNumbers = (1..6).toSet())
        assertEquals(full.selectedNumbers, full.withShake(true, Random(2)).selectedNumbers)
    }

    @Test fun superSevenCompletionPreservesColumnsAndFillsRequestedQuantityWithinColumnLimit() {
        val input = state(SUPER_SEVEN_ID).copy(numberQuantity = 10, superSevenColumns = List(7) { if (it == 2) setOf(8) else emptySet() })
        val result = input.withShake(false, Random(9))
        assertEquals(10, result.superSevenColumns.sumOf { it.size })
        assertTrue(result.superSevenColumns.all { it.isNotEmpty() && it.size <= 2 && it.all { digit -> digit in 0..9 } })
        assertTrue(8 in result.superSevenColumns[2])
        assertTrue(result.shakeComplete())
    }

    @Test fun superSevenRenewalChangesIdenticalRegeneratedColumns() {
        val input = state(SUPER_SEVEN_ID).copy(numberQuantity = 7, superSevenColumns = List(7) { setOf(0) })
        val result = input.withShake(true, zeroRandom)
        assertNotEquals(input.superSevenColumns, result.superSevenColumns)
        assertEquals(7, result.superSevenColumns.sumOf { it.size })
        assertTrue(result.superSevenColumns.all { it.isNotEmpty() })
    }

    @Test fun teamComplementChoosesDifferentTeamAndUpdatesSearchTextOnRenewal() {
        val next = SportsTeam(2, "Time B")
        val input = state(
            TIMEMANIA_ID
        ).copy(game = game(TIMEMANIA_ID).copy(teams = listOf(team, next)), selectionStep = BetSelectionStep.COMPLEMENT, selectedTeam = team)
        val result = input.withShake(true, Random(3))
        assertEquals(next, result.selectedTeam)
        assertEquals(next.name, result.teamQuery)
        assertTrue(result.shakeComplete())
        assertFalse(result.copy(selectedTeam = null).shakeComplete())
    }

    @Test fun teamComplementWithNoReplacementKeepsSelectedTeamAndQuery() {
        val input = state(
            TIMEMANIA_ID
        ).copy(
            game = game(TIMEMANIA_ID).copy(teams = listOf(team)),
            selectionStep = BetSelectionStep.COMPLEMENT,
            selectedTeam = team,
            teamQuery = "Time A"
        )
        assertEquals(team, input.withShake(true, Random(2)).selectedTeam)
        assertEquals("Time A", input.withShake(true, Random(2)).teamQuery)
    }

    @Test fun monthComplementRenewsMonthButPreservesChoiceWhenNoOtherMonthExists() {
        val next = LuckyMonth(2, "Fevereiro")
        val input = state(
            DIA_DE_SORTE_ID
        ).copy(
            game = game(DIA_DE_SORTE_ID).copy(luckyMonths = listOf(month, next)),
            selectionStep = BetSelectionStep.COMPLEMENT,
            selectedLuckyMonth = month
        )
        val result = input.withShake(true, Random(5))
        assertEquals(next, result.selectedLuckyMonth)
        assertTrue(result.shakeComplete())
        assertFalse(result.copy(selectedLuckyMonth = null).shakeComplete())
        assertEquals(month, input.copy(game = input.game!!.copy(luckyMonths = listOf(month))).withShake(true, Random(5)).selectedLuckyMonth)
    }

    @Test fun cloverComplementFillsOnlyMissingCloversAndReportsCompletion() {
        val input = state(
            MAIS_MILIONARIA_ID
        ).copy(
            game = game(MAIS_MILIONARIA_ID).copy(maximumCloverNumber = 6),
            selectionStep = BetSelectionStep.COMPLEMENT,
            manualCloverQuantity = 2,
            selectedClovers = setOf(3)
        )
        assertFalse(input.shakeComplete())
        val result = input.withShake(false, Random(3))
        assertEquals(2, result.selectedClovers.size)
        assertTrue(3 in result.selectedClovers)
        assertTrue(result.selectedClovers.all { it in 1..6 })
        assertTrue(result.shakeComplete())
    }

    @Test fun ordinaryComplementDoesNotChangeNumbersWhenNotRenewing() {
        val input = state().copy(selectionStep = BetSelectionStep.COMPLEMENT, selectedNumbers = setOf(1))
        assertEquals(input, input.withShake(false, Random(2)))
        assertFalse(input.shakeComplete())
        assertTrue(input.copy(selectedNumbers = (1..6).toSet()).shakeComplete())
    }

    @Test fun closedSalesStillAllowSuperSevenAndComplementSimulation() {
        val inputs = listOf(
            state(SUPER_SEVEN_ID).copy(numberQuantity = 7),
            state(TIMEMANIA_ID).copy(
                game = game(TIMEMANIA_ID).copy(teams = listOf(team)),
                selectionStep = BetSelectionStep.COMPLEMENT
            ),
            state(DIA_DE_SORTE_ID).copy(
                game = game(DIA_DE_SORTE_ID).copy(luckyMonths = listOf(month)),
                selectionStep = BetSelectionStep.COMPLEMENT
            ),
            state(MAIS_MILIONARIA_ID).copy(
                game = game(MAIS_MILIONARIA_ID).copy(maximumCloverNumber = 6),
                selectionStep = BetSelectionStep.COMPLEMENT,
                manualCloverQuantity = 2
            )
        )
        for (input in inputs) {
            val closed = input.copy(game = requireNotNull(input.game).copy(open = false))
            val completed = closed.withShake(false, Random(3))
            assertTrue(completed.shakeComplete())
            assertFalse(completed.canAddToCart)
        }
    }

    private fun state(id: Int = 2) = BettingGameUiState(loading = false, game = game(id), numberQuantity = 6)

    private fun game(id: Int = 2) = LotteryGameParameters(
        LotteryModality(
            id,
            "Jogo",
            "JOGO"
        ),
        123, "22/10/2026", 60, 6, 15, 10, emptyList(), listOf(BetPrice(6, 0, BigDecimal.TEN))
    )

    private val zeroRandom = object : Random() {
        override fun nextBits(bitCount: Int): Int = 0
    }
}
