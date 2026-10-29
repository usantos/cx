// Massas numéricas descrevem seleções de loteria, identificadores de concursos e limites de esquema.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting.recovery

import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint
import br.gov.caixa.loterias.feature.betting.presentation.recovery.restoreInto
import br.gov.caixa.loterias.feature.betting.presentation.recovery.toCheckpoint
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test
class BettingCheckpointProjectionTest {
    private val game = LotteryGameParameters(
        LotteryModality(14, "Lotogol", "LOTOGOL"), 100, "", 60,
        1, 15, 10, listOf(0, 3), listOf(BetPrice(1, 0, BigDecimal.ONE)),
        matches = listOf(SportsMatch(1, SportsTeam(1, "A"), SportsTeam(2, "B")))
    )

    @Test fun `direct projection restores partial scores mode complement and quantities without success flags`() {
        val original = BettingGameUiState(
            loading = false, game = game, mode = BetEntryMode.SURPRISE,
            selectionStep = BetSelectionStep.COMPLEMENT, lotogolScores = mapOf(1 to (3 to null)),
            numberQuantity = 1, surpriseQuantity = 4, surpriseCloverQuantity = 0, manualCloverQuantity = 0,
            teamQuery = "query", favoriteExpanded = true, favoriteName = "rascunho", addedToCart = true,
            favoriteSaved = true, favoriteOverwriteConfirmation = true, dailyLimitConfirmation = true
        )
        val draft = original.toCheckpoint()!!
        val restored = draft.restoreInto(BettingGameUiState(loading = false, game = game))
        assertEquals(original.lotogolScores, restored.lotogolScores)
        assertEquals(original.mode, restored.mode)
        assertEquals(original.selectionStep, restored.selectionStep)
        assertEquals(original.surpriseQuantity, restored.surpriseQuantity)
        assertEquals(original.teamQuery, restored.teamQuery)
        assertEquals(original.favoriteName, restored.favoriteName)
        assertFalse(restored.loading)
        assertFalse(restored.addedToCart)
        assertFalse(restored.favoriteSaved)
        assertFalse(restored.favoriteOverwriteConfirmation)
        assertFalse(restored.dailyLimitConfirmation)
    }

    @Test fun `different contest cannot silently receive old selected numbers`() {
        val draft = BettingCheckpoint(14, "NORMAL", 100, numbers = setOf(1))
        assertThrows(IllegalArgumentException::class.java) {
            draft.restoreInto(BettingGameUiState(loading = false, game = game.copy(contestNumber = 101)))
        }
    }

    @Test fun `unknown stable team identifier is not recreated from old private metadata`() {
        val draft = BettingCheckpoint(14, "NORMAL", 100, teamNumber = 88)
        assertThrows(IllegalArgumentException::class.java) { draft.restoreInto(BettingGameUiState(game = game)) }
    }
}
