package br.gov.caixa.loterias.feature.marketplace

import android.os.SystemClock
import android.view.MotionEvent
import android.view.KeyEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import br.gov.caixa.loterias.core.domain.marketplace.*
import br.gov.caixa.loterias.core.domain.retail.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.core.domain.retail.SetRetailerFavoriteUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MarketplaceMutationLoadingUiTest {
    @get:Rule val compose = createComposeRule()
    private val pool = MarketplacePool("B1", "MEGA_SENA", 3070, "Lotérica", "14/10/2026", "10", 3, 2, 6, false, lotteryId = 10)

    @Test fun favoriteRequestBlocksNavigationUntilBackendSuccess() {
        val response = CompletableDeferred<AppResult<Unit>>()
        var backs = 0
        val model = model(favorite = SetRetailerFavoriteUseCase { _, _ -> response.await() })
        compose.setContent { CaixaDSTheme { route(model) { backs++ } } }
        compose.onNodeWithTag("carousel-favorite-B1").performClick()
        compose.onNodeWithText("Confirmar").performClick()
        assertBlocked { backs }
        response.complete(AppResult.Success(Unit))
        compose.waitUntil { model.state.value.updatingFavoriteLotteryIds.isEmpty() }
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle { assertEquals(1, backs) }
    }

    @Test fun reservationRequestBlocksNavigationUntilBackendFailureIsAcknowledged() {
        val response = CompletableDeferred<AppResult<ShoppingCart>>()
        var backs = 0
        val model = model(add = AddMarketplacePoolToCartUseCase { response.await() })
        compose.setContent { CaixaDSTheme { route(model) { backs++ } } }
        compose.onNodeWithTag("carousel-add-B1").performClick()
        assertBlocked { backs }
        response.complete(AppResult.Failure(AppError.Network))
        compose.waitUntil { model.state.value.addingPoolCodes.isEmpty() }
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle { assertEquals(1, backs) }
    }

    private fun assertBlocked(backs: () -> Int) {
        compose.onNodeWithText("Carregando…").assertExists()
        val point = compose.onNodeWithContentDescription("Voltar", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot.center
        tapScreen(point)
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(0, backs()) }
        compose.onNodeWithText("Carregando…").assertExists()
    }

    private fun tapScreen(point: Offset) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val now = SystemClock.uptimeMillis()
        val down = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, point.x, point.y, 0)
        val up = MotionEvent.obtain(now, now + 30, MotionEvent.ACTION_UP, point.x, point.y, 0)
        try { instrumentation.sendPointerSync(down); instrumentation.sendPointerSync(up) }
        finally { down.recycle(); up.recycle() }
    }

    private fun model(
        add: AddMarketplacePoolToCartUseCase = AddMarketplacePoolToCartUseCase { AppResult.Failure(AppError.Unexpected) },
        favorite: SetRetailerFavoriteUseCase = SetRetailerFavoriteUseCase { _, _ -> AppResult.Failure(AppError.Unexpected) },
    ): MarketplaceViewModel {
        lateinit var model: MarketplaceViewModel
        compose.runOnUiThread {
            model = MarketplaceViewModel(SearchMarketplace(MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool), 0, 0, 1) }),
                MarketplaceFilter.empty(), add, GetFavoriteRetailersUseCase { AppResult.Success(emptyList()) }, favorite,
                FixedCoroutineDispatchers(Dispatchers.Main.immediate))
        }
        return model
    }

    @androidx.compose.runtime.Composable private fun route(model: MarketplaceViewModel, onBack: () -> Unit) {
        MarketplaceRoute(model, onBack, onFilter = {}, onHelp = {}, onRetailerSelected = {}, onPoolSelected = {}, onPoolReserved = {})
    }
}
