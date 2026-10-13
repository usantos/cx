package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class BettingLoadingRecoveryTest {
    @get:Rule val compose = createComposeRule()

    @Test fun loadingNetworkFailureAndRetryUseControlledServiceWithoutCartSubmission() {
        val firstResult = CompletableDeferred<AppResult<BettingParameters>>()
        val loads = AtomicInteger(0)
        val sends = AtomicInteger(0)
        val backs = AtomicInteger(0)
        val game = LotteryGameParameters(LotteryModality(2,"Mega-Sena","MEGA_SENA"),2900,"10/09/2026",60,6,6,3,listOf(0),listOf(BetPrice(6,0,BigDecimal("5.00"))))
        val parameters = BettingParameters(BigDecimal("500"),BigDecimal("30"),50,listOf(game))
        val vm = BettingGameViewModel(2, GetBettingParametersUseCase {
            if (loads.incrementAndGet() == 1) firstResult.await() else AppResult.Success(parameters)
        }, object : AddBetToCartUseCase {
            override suspend fun invoke(draft: BetDraft): AppResult<ShoppingCart> { sends.incrementAndGet(); return AppResult.Success(ShoppingCart(1,BigDecimal.ZERO,emptyList())) }
        })
        compose.setContent { CaixaDSTheme(darkTheme = false) { BettingGameRoute(vm, { backs.incrementAndGet() }, {}) } }
        compose.mainClock.advanceTimeBy(750) // Original LoadingView delay500 + fade200.
        compose.onNode(isDialog()).assertExists()
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        compose.runOnIdle { assertTrue(vm.state.value.loading) }
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
        compose.runOnIdle { assertTrue(vm.state.value.loading); assertEquals(0, backs.get()) }
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
