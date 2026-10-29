package br.gov.caixa.loterias.feature.betting.recovery

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.presentation.coordinator.BettingGameFavorites
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingDraftCommands
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceBettingGameState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BettingFavoriteQueueOrderingTest {
    private val game = LotteryGameParameters(
        LotteryModality(2, "Mega", "MEGA_SENA"), 1, "", 2, 1, 2, 1, listOf(0), emptyList()
    )

    @Test fun `favorite completion checks revision after an earlier queued selection`() = runTest {
        val initial = BettingGameUiState(
            loading = false,
            game = game,
            selectedNumbers = setOf(1),
            numberQuantity = 1,
            favoriteName = "Meu jogo"
        )
        val working = reduxStateStore<BettingGameUiState, BettingGameAction>(initial, ::reduceBettingGameState)
        val saving = CompletableDeferred<Unit>()
        val completeSave = CompletableDeferred<Unit>()
        lateinit var favorites: BettingGameFavorites
        val commands = BettingDraftCommands(
            this,
            initial,
            persist = {},
            isCurrent = { true },
            working = working,
            onSelectionChange = { before, after -> favorites.recordSelectionChange(before, after) }
        )
        favorites = BettingGameFavorites(
            working,
            this,
            FixedCoroutineDispatchers(UnconfinedTestDispatcher(testScheduler)),
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase {
                saving.complete(Unit)
                completeSave.await()
                AppResult.Success(Unit)
            },
            stateWriter = commands::edit,
            enqueueCommand = commands::execute
        )
        try {
            favorites.submitFavorite()
            assertTrue(saving.isCompleted)
            // O ator ainda não iniciou: esta edição deve executar antes da confirmação remota.
            commands.execute { favorites.dispatch(BettingGameAction.NumberToggled(2, warn = true)) }
            assertEquals(setOf(1), working.value.selectedNumbers)
            completeSave.complete(Unit)
            advanceUntilIdle()
            assertEquals(setOf(1, 2), commands.state.value.selectedNumbers)
            assertFalse(commands.state.value.favoriteSaved)
        } finally {
            commands.close()
        }
    }
}
