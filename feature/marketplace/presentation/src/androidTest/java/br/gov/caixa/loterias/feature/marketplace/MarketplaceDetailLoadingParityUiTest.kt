package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.ui.test.*
import org.junit.Assert.*
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import android.view.KeyEvent
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceDetailUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.marketplace.presentation.route.MarketplaceDetailRoute
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.MarketplaceDetailViewModel
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceDetailScreen
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState

class MarketplaceDetailLoadingParityUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun favoriteMutationBlocksBackAndCartUntilBackendCompletes() = exercise(true)

    @Test fun poolReservationBlocksBackAndCartUntilBackendCompletes() = exercise(false)

    @Test fun initialDetailGetBlocksNativeBackAndFooterUntilPayloadWithoutDuplicatingFirstResume() {
        val result = CompletableDeferred<AppResult<MarketplacePoolDetail>>()
        var gets = 0
        var backCalls = 0
        var cartCalls = 0
        val showInitialLoad = mutableStateOf(false)
        lateinit var model: MarketplaceDetailViewModel
        compose.setContent {
            CaixaDSTheme {
                if (showInitialLoad.value) {
                    MarketplaceDetailRoute(
                        model,
                        { backCalls++ },
                        {},
                        cartTotal = BigDecimal.TEN,
                        cartBetCount = 1,
                        onCart = { cartCalls++ }
                    )
                } else {
                    // Mede o centro do rodapé real na mesma janela; durante o GET inicial o painel ainda não existe.
                    MarketplaceDetailScreen(
                        MarketplaceDetailUiState(detail = detail(), isLoading = false), {}, {}, {}, {}, {},
                        cartTotal = BigDecimal.TEN, cartBetCount = 1, onCart = {}
                    )
                }
            }
        }
        val footer = screenPoint("Carrinho de apostas")
        model = MarketplaceDetailViewModel(
            "B1",
            GetMarketplaceDetailUseCase {
                gets++
                result.await()
            },
            AddMarketplacePoolToCartUseCase { AppResult.Failure(AppError.Network) },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        )
        compose.runOnIdle { showInitialLoad.value = true }
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        tap(screenPoint("Voltar"))
        // A posição do rodapé nativo não deve receber entrada antes do conteúdo inicial.
        tap(footer)
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, gets)
            assertEquals(0, backCalls)
            assertEquals(0, cartCalls)
        }
        result.complete(AppResult.Success(detail()))
        compose.waitForIdle()
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
        compose.onNodeWithContentDescription("Carrinho de apostas").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(1, gets)
            assertEquals(1, backCalls)
            assertEquals(1, cartCalls)
        }
    }

    private fun exercise(favorite: Boolean) {
        val favoriteResult = CompletableDeferred<AppResult<Unit>>()
        val purchaseResult = CompletableDeferred<AppResult<ShoppingCart>>()
        var backCalls = 0
        var cartCalls = 0
        val model = MarketplaceDetailViewModel(
            "B1",
            GetMarketplaceDetailUseCase { AppResult.Success(detail()) },
            AddMarketplacePoolToCartUseCase { purchaseResult.await() },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate),
            SetRetailerFavoriteUseCase { _, _ -> favoriteResult.await() },
            false
        )
        compose.setContent {
            CaixaDSTheme {
                MarketplaceDetailRoute(
                    model,
                    { backCalls++ },
                    {},
                    cartTotal = BigDecimal("39.00"),
                    cartBetCount = 9,
                    onCart = { cartCalls++ }
                )
            }
        }
        val back = screenPoint("Voltar")
        val cart = screenPoint("Carrinho de apostas")
        if (favorite) {
            compose.onNodeWithContentDescription("Favoritar lotérica").performClick()
            compose.onNodeWithText("Confirmar").performClick()
        } else {
            compose.onNodeWithTag("pool-detail-add-to-cart").performClick()
        }
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        // A entrada global usa a janela Android em primeiro plano. performClick do Compose na raiz de uma
        // atividade oculta poderia ignorar o roteamento modal; use o despacho real de ponteiro e retorno aqui.
        tap(back)
        tap(cart)
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(0, backCalls)
            assertEquals(0, cartCalls)
        }
        if (favorite) {
            favoriteResult.complete(AppResult.Success(Unit))
        } else {
            purchaseResult.complete(AppResult.Failure(AppError.Network))
        }
        compose.waitForIdle()
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
        compose.onNodeWithContentDescription("Carrinho de apostas").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(1, backCalls)
            assertEquals(1, cartCalls)
        }
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
    private fun MotionEvent.useEvent(send: (MotionEvent) -> Unit) {
        try {
            send(this)
        } finally {
            recycle()
        }
    }
    private fun detail() = MarketplacePoolDetail(
        "B1", "MEGA_SENA", 3070, 10, "Lotérica", "1", "Cidade - SP",
        "14/10/2026", "1000.00", 20, 50, 2, 8, "10.00", "0.50", "10.50", emptyList(), lastSharePriceWithFeeValue = BigDecimal("10.50")
    )
}
