package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.ui.test.*
import android.os.SystemClock
import android.view.MotionEvent
import android.view.KeyEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalView
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePage
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.domain.repository.MarketplaceRepository
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.SearchMarketplace
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.marketplace.presentation.route.MarketplaceRoute
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.MarketplaceViewModel

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

    @Test fun initialReadUsesNativeModalAndBlocksBackUntilCompletion() {
        val response = CompletableDeferred<MarketplacePage>()
        var backs = 0
        lateinit var model: MarketplaceViewModel
        compose.runOnUiThread {
            model = MarketplaceViewModel(SearchMarketplace(MarketplaceRepository { _, _ -> response.await() }),
                MarketplaceFilter.empty(), AddMarketplacePoolToCartUseCase { error("No mutation") },
                GetFavoriteRetailersUseCase { AppResult.Success(emptyList()) }, SetRetailerFavoriteUseCase { _, _ -> error("No mutation") },
                FixedCoroutineDispatchers(Dispatchers.Main.immediate))
        }
        compose.setContent { CaixaDSTheme { route(model) { backs++ } } }
        assertBlocked { backs }
        response.complete(MarketplacePage(listOf(pool), 0, 0, 1))
        compose.waitUntil { !model.state.value.isLoading }
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
    }

    @Test fun nextPageReadBlocksBackFavoriteCartAndQuantityUntilCompletion() {
        val response = CompletableDeferred<MarketplacePage>()
        var backs = 0
        var favoriteWrites = 0
        var reservations = 0
        var cartNavigations = 0
        lateinit var rootView: android.view.View
        lateinit var model: MarketplaceViewModel
        compose.runOnUiThread {
            model = MarketplaceViewModel(SearchMarketplace(MarketplaceRepository { _, page ->
                if (page == 0) MarketplacePage(listOf(pool), 0, 1, 2) else response.await()
            }), MarketplaceFilter.empty(), AddMarketplacePoolToCartUseCase {
                reservations++; AppResult.Failure(AppError.Network)
            }, GetFavoriteRetailersUseCase { AppResult.Success(emptyList()) }, SetRetailerFavoriteUseCase { _, _ ->
                favoriteWrites++; AppResult.Success(Unit)
            }, FixedCoroutineDispatchers(Dispatchers.Main.immediate))
        }
        compose.setContent { CaixaDSTheme {
            val view = LocalView.current
            SideEffect { rootView = view }
            MarketplaceRoute(model, onBack = { backs++ }, onFilter = {}, onHelp = {},
                onRetailerSelected = {}, onPoolSelected = {}, onPoolReserved = {},
                cartTotal = java.math.BigDecimal.TEN, cartBetCount = 1, onCart = { cartNavigations++ })
        } }
        compose.waitUntil { !model.state.value.isLoading }
        val favoritePoint = compose.onNodeWithTag("carousel-favorite-B1").fetchSemanticsNode().boundsInRoot.center
        val quantityPoint = compose.onNodeWithTag("carousel-increase-B1").fetchSemanticsNode().boundsInRoot.center
        val reservePoint = compose.onNodeWithTag("carousel-add-B1").fetchSemanticsNode().boundsInRoot.center
        val cartPoint = compose.onNodeWithText("Valor atual do carrinho", substring = true).fetchSemanticsNode().boundsInRoot.center
        var screenOrigin = Offset.Zero
        compose.runOnIdle {
            val position = IntArray(2)
            rootView.getLocationOnScreen(position)
            screenOrigin = Offset(position[0].toFloat(), position[1].toFloat())
        }
        compose.runOnIdle { model.loadMore() }
        compose.runOnIdle { assertEquals(true, model.state.value.isLoadingMore) }

        assertBlocked(screenOrigin) { backs }
        listOf(favoritePoint, quantityPoint, reservePoint, cartPoint).forEach { tapScreen(it + screenOrigin) }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(0, favoriteWrites)
            assertEquals(0, reservations)
            assertEquals(0, cartNavigations)
            assertEquals(null, model.state.value.pendingFavoritePoolCode)
            assertEquals(emptyMap<String, Int>(), model.state.value.selectedShares)
        }
        response.complete(MarketplacePage(listOf(pool.copy(code = "B2")), 1, 1, 2))
        compose.waitUntil { !model.state.value.isLoadingMore }
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
        compose.onNodeWithTag("carousel-increase-B1").performClick()
        compose.runOnIdle { assertEquals(2, model.state.value.selectedShares["B1"]) }
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle { assertEquals(1, backs) }
    }

    private fun assertBlocked(screenOrigin: Offset = Offset.Zero, backs: () -> Int) {
        compose.onNodeWithText("Carregando…").assertExists()
        val point = compose.onNodeWithContentDescription("Voltar", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot.center
        tapScreen(point + screenOrigin)
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
