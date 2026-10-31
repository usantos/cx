// Massas numéricas descrevem seleções de loteria, identificadores de concursos e limites de esquema.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting.recovery

import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingGameRoute
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel
import br.gov.caixa.loterias.feature.betting.testCheckpointFactory
import java.math.BigDecimal
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BettingRecoveryReviewUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun `obsolete draft shows legacy notice and Entendi returns without losing the draft`() {
        val draft = BettingCheckpoint(2, "NORMAL", 99, numbers = setOf(4, 1), numberQuantity = 6)
        val session = testCheckpointFactory().open()
        runBlocking { session.save(draft) }
        val game = LotteryGameParameters(
            LotteryModality(2, "Mega-Sena", "MEGA_SENA"), 100, "", 60, 6, 20, 10,
            listOf(0), listOf(BetPrice(6, 0, BigDecimal.ONE))
        )
        val model = BettingGameViewModel(
            2,
            GetBettingParametersUseCase {
                AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 50, listOf(game)))
            },
            AddBetToCartUseCase { error("Não deve enviar aposta") },
            checkpointSession = session
        )
        var exits = 0
        compose.setContent { CaixaDSTheme { BettingGameRoute(model, { exits++ }, {}) } }
        compose.waitUntil(10_000) { model.state.value.recoveryReview != null }
        compose.onNodeWithText("Atenção").assertIsDisplayed()
        compose.onNodeWithText("O registro de apostas individuais para o concurso 99 foi encerrado.").assertIsDisplayed()
        compose.onNodeWithText("Rascunho preservado para conferência").assertDoesNotExist()
        compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
        compose.onNodeWithText("Adicionar ao carrinho").assertDoesNotExist()
        compose.onNodeWithText("Entendi").performClick()
        compose.waitUntil(10_000) { exits == 1 }
        assertEquals(draft, runBlocking { session.read()?.checkpoint })
    }

    @Test fun `temporary recovery failure allows toolbar back and retry restores the same draft`() {
        val draft = BettingCheckpoint(2, "NORMAL", 100, numbers = setOf(4, 1), numberQuantity = 6)
        val session = testCheckpointFactory().open()
        runBlocking { session.save(draft) }
        val game = LotteryGameParameters(
            LotteryModality(2, "Mega-Sena", "MEGA_SENA"), 100, "", 60, 6, 20, 10,
            listOf(0), listOf(BetPrice(6, 0, BigDecimal.ONE))
        )
        val loads = java.util.concurrent.atomic.AtomicInteger()
        val model = BettingGameViewModel(
            2,
            GetBettingParametersUseCase {
                if (loads.incrementAndGet() == 1) {
                    AppResult.Failure(AppError.Network)
                } else {
                    AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 50, listOf(game)))
                }
            },
            AddBetToCartUseCase { error("Não deve enviar aposta") },
            checkpointSession = session
        )
        var exits = 0
        var backDispatcher: OnBackPressedDispatcher? = null
        compose.setContent {
            backDispatcher = requireNotNull(LocalOnBackPressedDispatcherOwner.current).onBackPressedDispatcher
            CaixaDSTheme { BettingGameRoute(model, { exits++ }, {}, fallbackModalityId = 2) }
        }
        compose.waitUntil(10_000) { model.state.value.errorMessage != null }
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.waitUntil(10_000) { exits == 1 }
        compose.runOnIdle { requireNotNull(backDispatcher).onBackPressed() }
        compose.waitUntil(10_000) { exits == 2 }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.waitUntil(10_000) { loads.get() >= 2 }
        compose.waitForIdle()
        compose.waitUntil(10_000) { model.state.value.game != null }
        assertEquals(game, model.state.value.game)
        assertEquals(draft.numbers, model.state.value.selectedNumbers)
        assertEquals(null, model.state.value.errorMessage)
        assertEquals(draft, runBlocking { session.read()?.checkpoint })
        assertEquals(2, loads.get())
    }

    @Test fun `same closed contest reopens the normal cartela with the preserved selection`() {
        val draft = BettingCheckpoint(2, "NORMAL", 100, numbers = (1..6).toSet(), numberQuantity = 6)
        val session = testCheckpointFactory().open()
        runBlocking { session.save(draft) }
        val game = LotteryGameParameters(
            LotteryModality(2, "Mega-Sena", "MEGA_SENA"), 100, "", 60, 6, 20, 10,
            listOf(0), listOf(BetPrice(6, 0, BigDecimal.ONE)), open = false
        )
        val model = BettingGameViewModel(
            2,
            GetBettingParametersUseCase {
                AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 50, listOf(game)))
            },
            AddBetToCartUseCase { error("Não deve enviar aposta") },
            checkpointSession = session
        )
        compose.setContent { CaixaDSTheme { BettingGameRoute(model, {}, {}) } }
        compose.waitUntil(10_000) { model.state.value.game != null || model.state.value.recoveryReview != null }
        assertEquals(draft.numbers, model.state.value.selectedNumbers)
        compose.onNodeWithText("Rascunho preservado para conferência").assertDoesNotExist()
        compose.onNodeWithText("Entendi").assertDoesNotExist()
        compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
        compose.onNodeWithText("Adicionar ao carrinho").assertIsNotEnabled()
    }
}
