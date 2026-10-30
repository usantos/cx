// Literais numéricos descrevem seleções de loteria, identificadores e massas de teste de limites.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingGameRoute
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
@org.junit.runner.RunWith(org.robolectric.RobolectricTestRunner::class)
@org.robolectric.annotation.Config(sdk = [35])
class BettingLocalLoadingRecoveryTest {
    @get:Rule val compose = createComposeRule()

    @Test fun lotomaniaRandomButtonCompletesLocalSimulationWhenSalesAreClosed() {
        val game = LotteryGameParameters(
            LotteryModality(16, "Lotomania", "LOTOMANIA"),
            2985, "", 100, 50, 50, 3, listOf(0), listOf(BetPrice(50, 0, BigDecimal("3.00"))),
            open = false
        )
        val session = testCheckpointFactory().open()
        val vm = BettingGameViewModel(
            16,
            GetBettingParametersUseCase {
                AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 50, listOf(game)))
            },
            AddBetToCartUseCase { error("Simulação local não deve enviar aposta") },
            checkpointSession = session
        )
        compose.setContent { CaixaDSTheme(darkTheme = false) { BettingGameRoute(vm, {}, {}) } }
        compose.waitUntil(10_000) { vm.state.value.game != null }
        compose.onNodeWithText("Aposta aleatória").assertIsEnabled().performClick()
        compose.waitUntil(10_000) { vm.state.value.selectedNumbers.size == 50 }
        compose.runOnIdle {
            assertEquals(50, vm.state.value.selectedNumbers.size)
            assertFalse(vm.state.value.canAddToCart)
            vm.clearSelection()
        }
        compose.waitUntil(10_000) { vm.state.value.selectedNumbers.isEmpty() }
        compose.runOnIdle { vm.toggleNumber(1) }
        compose.waitUntil(10_000) { vm.state.value.selectedNumbers == setOf(1) }
        compose.onNodeWithText("Completar jogo").assertIsEnabled().performClick()
        compose.waitUntil(10_000) { vm.state.value.selectedNumbers.size == 50 }
        compose.runOnIdle {
            assertTrue(1 in vm.state.value.selectedNumbers)
            assertFalse(vm.state.value.canAddToCart)
        }
        assertEquals(vm.state.value.selectedNumbers, kotlinx.coroutines.runBlocking { session.read()?.checkpoint?.numbers })
    }

    @Test fun loadingNetworkFailureAndRetryUseControlledServiceWithoutCartSubmission() {
        val firstResult = CompletableDeferred<AppResult<BettingParameters>>()
        val loads = AtomicInteger(0)
        val sends = AtomicInteger(0)
        val backs = AtomicInteger(0)
        val game =
            LotteryGameParameters(
                LotteryModality(
                    2,
                    "Mega-Sena",
                    "MEGA_SENA"
                ),
                2900, "10/09/2026", 60, 6, 6, 3, listOf(0), listOf(BetPrice(6, 0, BigDecimal("5.00")))
            )
        val parameters = BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, listOf(game))
        val vm = BettingGameViewModel(
            2,
            GetBettingParametersUseCase {
                if (loads.incrementAndGet() == 1) firstResult.await() else AppResult.Success(parameters)
            },
            object : AddBetToCartUseCase {
                override suspend fun invoke(draft: BetDraft): AppResult<ShoppingCart> {
                    sends.incrementAndGet()
                    return AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
                }
            }
        )
        compose.setContent { CaixaDSTheme(darkTheme = false) { BettingGameRoute(vm, { backs.incrementAndGet() }, {}) } }
        compose.mainClock.advanceTimeBy(750) // LoadingView original: atraso de 500 e transição de opacidade de 200.
        compose.onNode(isDialog()).assertExists()
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        compose.runOnIdle { assertTrue(vm.state.value.loading) }
        compose.runOnIdle {
            val dialog = requireNotNull(org.robolectric.shadows.ShadowDialog.getLatestDialog())
            assertTrue(dialog.isShowing)
            dialog.dispatchKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_BACK))
            dialog.dispatchKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_UP, android.view.KeyEvent.KEYCODE_BACK))
            assertTrue(dialog.isShowing)
        }
        compose.runOnIdle {
            assertTrue(vm.state.value.loading)
            assertEquals(0, backs.get())
        }
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        firstResult.complete(AppResult.Failure(AppError.Network))
        compose.waitUntil(10_000) { !vm.state.value.loading }
        compose.onNodeWithText("Não foi possível carregar a aposta. Verifique sua internet.").assertIsDisplayed()
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.waitUntil(10_000) { vm.state.value.game != null }
        compose.onNodeWithText("mega-sena").assertIsDisplayed()
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
        assertFalse(vm.state.value.loading)
        assertEquals(2, loads.get())
        assertEquals(0, sends.get())
    }
}
