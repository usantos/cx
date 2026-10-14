package br.gov.caixa.loterias.feature.marketplace

import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import android.view.KeyEvent
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.common.*
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import br.gov.caixa.loterias.core.domain.marketplace.*
import br.gov.caixa.loterias.core.domain.retail.SetRetailerFavoriteUseCase
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MarketplaceDetailLoadingParityUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    @Test fun favoriteMutationBlocksBackAndCartUntilBackendCompletes() = exercise(true)
    @Test fun poolReservationBlocksBackAndCartUntilBackendCompletes() = exercise(false)

    @Test fun initialDetailGetBlocksNativeBackAndFooterUntilPayloadWithoutDuplicatingFirstResume() {
        val result = CompletableDeferred<AppResult<MarketplacePoolDetail>>()
        var gets = 0
        var backCalls = 0
        var cartCalls = 0
        val model = MarketplaceDetailViewModel("B1", GetMarketplaceDetailUseCase {
            gets++; result.await()
        }, AddMarketplacePoolToCartUseCase { AppResult.Failure(AppError.Network) },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate))
        compose.setContent { CaixaDSTheme { MarketplaceDetailRoute(model, { backCalls++ }, {},
            cartTotal = BigDecimal.TEN, cartBetCount = 1, onCart = { cartCalls++ }) } }
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        tap(screenPoint("Voltar"))
        var footer = Offset.Zero
        compose.runOnUiThread {
            val content = compose.activity.findViewById<View>(android.R.id.content)
            val origin = IntArray(2)
            content.getLocationOnScreen(origin)
            footer = Offset(origin[0] + content.width - 40f, origin[1] + content.height - 30f)
        }
        // The native footer location must not receive input before the initial payload.
        tap(footer)
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, gets); assertEquals(0, backCalls); assertEquals(0, cartCalls) }
        result.complete(AppResult.Success(detail()))
        compose.waitForIdle()
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
        compose.onNodeWithContentDescription("Carrinho de apostas").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle { assertEquals(1, gets); assertEquals(1, backCalls); assertEquals(1, cartCalls) }
    }

    private fun exercise(favorite: Boolean) {
        val favoriteResult = CompletableDeferred<AppResult<Unit>>()
        val purchaseResult = CompletableDeferred<AppResult<ShoppingCart>>()
        var backCalls = 0
        var cartCalls = 0
        val model = MarketplaceDetailViewModel("B1", GetMarketplaceDetailUseCase { AppResult.Success(detail()) },
            AddMarketplacePoolToCartUseCase { purchaseResult.await() }, FixedCoroutineDispatchers(Dispatchers.Main.immediate),
            SetRetailerFavoriteUseCase { _, _ -> favoriteResult.await() }, false)
        compose.setContent { CaixaDSTheme { MarketplaceDetailRoute(model, { backCalls++ }, {},
            cartTotal = BigDecimal("39.00"), cartBetCount = 9, onCart = { cartCalls++ }) } }
        val back = screenPoint("Voltar")
        val cart = screenPoint("Carrinho de apostas")
        if (favorite) {
            compose.onNodeWithContentDescription("Favoritar lotérica").performClick()
            compose.onNodeWithText("Confirmar").performClick()
        } else compose.onNodeWithText("Adicionar ao carrinho").performClick()
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        // Global input targets the foreground Android window. Compose performClick on a hidden
        // activity root could bypass modal window routing, so use real pointer/back dispatch here.
        tap(back); tap(cart); InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, backCalls); assertEquals(0, cartCalls) }
        if (favorite) favoriteResult.complete(AppResult.Success(Unit))
        else purchaseResult.complete(AppResult.Failure(AppError.Network))
        compose.waitForIdle()
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
        compose.onNodeWithContentDescription("Carrinho de apostas").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle { assertEquals(1, backCalls); assertEquals(1, cartCalls) }
    }

    private fun screenPoint(description: String): Offset {
        val point = compose.onNodeWithContentDescription(description).fetchSemanticsNode().boundsInRoot.center
        val origin = IntArray(2)
        compose.runOnUiThread { compose.activity.findViewById<View>(android.R.id.content).getLocationOnScreen(origin) }
        return Offset(point.x + origin[0], point.y + origin[1])
    }
    private fun tap(point: Offset) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val down = SystemClock.uptimeMillis()
        MotionEvent.obtain(down, down, MotionEvent.ACTION_DOWN, point.x, point.y, 0).useEvent(instrumentation::sendPointerSync)
        MotionEvent.obtain(down, down + 20, MotionEvent.ACTION_UP, point.x, point.y, 0).useEvent(instrumentation::sendPointerSync)
    }
    private fun MotionEvent.useEvent(send: (MotionEvent) -> Unit) { try { send(this) } finally { recycle() } }
    private fun detail() = MarketplacePoolDetail("B1", "MEGA_SENA", 3070, 10, "Lotérica", "1", "Cidade - SP",
        "14/10/2026", "1000.00", 20, 50, 2, 8, "10.00", "0.50", "10.50", emptyList(), lastSharePriceWithFeeValue = BigDecimal("10.50"))
}
