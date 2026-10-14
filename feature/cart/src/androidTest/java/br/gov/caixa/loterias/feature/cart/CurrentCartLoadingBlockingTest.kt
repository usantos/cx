package br.gov.caixa.loterias.feature.cart

import android.graphics.Rect
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import br.gov.caixa.loterias.core.common.*
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.cart.*
import java.math.BigDecimal
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** CX blocks the window for both initial GET and parameter GET, even with cached cart content. */
class CurrentCartLoadingBlockingTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val reply = CompletableDeferred<Unit>()
    private val backCalls = AtomicInteger()
    private val addCalls = AtomicInteger()
    private val device get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @After fun finishDeferredFixture() { reply.complete(Unit) }
    @Test fun initialCartGetBlocksPhysicalBackAndAddUntilTheReply() = checkLoading(Phase.CART)
    @Test fun parameterGetWithExistingCartBlocksPhysicalBackAndAddUntilTheReply() = checkLoading(Phase.PARAMETERS)

    private fun checkLoading(phase: Phase) {
        show(phase)
        // Read the underlying Compose layout; actual pointer input targets its physical screen coordinates.
        val add = screenBounds(compose.onNodeWithText("Adicionar nova aposta"))
        val back = screenBounds(compose.onNodeWithContentDescription("Voltar"))
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        device.click(add.centerX(), add.centerY())
        device.click(back.centerX(), back.centerY())
        device.pressBack()
        compose.waitForIdle()
        assertEquals(0, backCalls.get())
        assertEquals(0, addCalls.get())
        compose.runOnIdle { reply.complete(Unit) }
        compose.waitForIdle()
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
        device.click(add.centerX(), add.centerY())
        compose.waitForIdle()
        device.pressBack()
        compose.waitForIdle()
        assertEquals(1, addCalls.get())
        assertEquals(1, backCalls.get())
    }

    private fun screenBounds(node: SemanticsNodeInteraction): Rect {
        val bounds = node.fetchSemanticsNode().boundsInRoot
        val origin = IntArray(2)
        compose.runOnIdle { compose.activity.findViewById<View>(android.R.id.content).getLocationOnScreen(origin) }
        return Rect(bounds.left.toInt() + origin[0], bounds.top.toInt() + origin[1],
            bounds.right.toInt() + origin[0], bounds.bottom.toInt() + origin[1])
    }

    private fun show(phase: Phase) {
        val item = CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "", BigDecimal.TEN,
            modalityCode = "MEGA_SENA", selectedNumbers = listOf(listOf(1, 2, 3, 4, 5, 6)))
        compose.setContent {
            val fixture = remember {
                CartViewModel(
                    getCurrentCart = {
                        if (phase == Phase.CART) reply.await()
                        AppResult.Success(ShoppingCart(1, BigDecimal.TEN, listOf(item)))
                    },
                    getBettingParameters = GetBettingParametersUseCase {
                        if (phase == Phase.PARAMETERS) reply.await()
                        AppResult.Success(BettingParameters(BigDecimal("500"), BigDecimal("2.50"), 50,
                            listOf(LotteryGameParameters(LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
                                3070, "14/10/2026", 60, 6, 20, 15, emptyList(), emptyList()))))
                    },
                    removeCartItem = { error("Read-only fixture") },
                    clearCart = { error("Read-only fixture") },
                    serverTime = ServerTimeSource { Instant.parse("2026-10-14T12:00:00Z") },
                    verifyPaymentAccount = { error("Read-only fixture") },
                    validateCart = { error("Read-only fixture") },
                    hasRecentPurchase = { error("Read-only fixture") },
                    checkCheckoutLocation = { error("Read-only fixture") },
                    recordLocationRequest = { error("Read-only fixture") },
                    dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                )
            }
            CaixaDSTheme(darkTheme = false) {
                CurrentCartRoute(fixture, { backCalls.incrementAndGet() }, { addCalls.incrementAndGet() }, {}, {}, {}, {}, {})
            }
        }
    }
    private enum class Phase { CART, PARAMETERS }
}
