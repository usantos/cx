package br.gov.caixa.loterias.feature.cart

import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import br.gov.caixa.loterias.core.common.*
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
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

/** Real VM, deferred synthetic services, and physical input into the dialog window. */
class CurrentCartMutationBlockingTest {
    @get:Rule val compose = createComposeRule()
    private val reply = CompletableDeferred<Unit>()
    private val backCalls = AtomicInteger()
    private lateinit var model: CartViewModel
    private val device get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @After fun finishDeferredFixture() { reply.complete(Unit) }

    @Test fun deletionBlocksToolbarAndHardwareBackUntilTheReply() = checkBlocking(Operation.DELETE)
    @Test fun clearBlocksToolbarAndHardwareBackUntilTheReply() = checkBlocking(Operation.CLEAR)
    @Test fun favoriteLoadingStaysAboveTheNameDialogUntilTheReply() = checkBlocking(Operation.FAVORITE)
    @Test fun checkoutBlocksToolbarAndHardwareBackUntilTheReply() = checkBlocking(Operation.CHECKOUT)

    private fun checkBlocking(operation: Operation) {
        show()
        compose.onNodeWithText("mega-sena").assertIsDisplayed()
        val toolbar = requireNotNull(device.wait(Until.findObject(By.desc("Excluir todas as apostas")), 5_000)).visibleBounds
        when (operation) {
            Operation.DELETE -> {
                compose.onNodeWithContentDescription("Excluir").performClick()
                compose.onNodeWithText("Sim").performClick()
            }
            Operation.CLEAR -> {
                compose.onNodeWithContentDescription("Excluir todas as apostas").performClick()
                compose.onNodeWithText("Sim").performClick()
            }
            Operation.FAVORITE -> {
                compose.onNodeWithContentDescription("Salvar carrinho favorito").performClick()
                compose.runOnIdle { model.updateFavoriteName("Fixture"); model.submitFavoriteName() }
            }
            Operation.CHECKOUT -> compose.onNodeWithText("Ir para pagamento").performClick()
        }
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        device.click(toolbar.centerX(), toolbar.centerY())
        device.pressBack()
        compose.waitForIdle()
        compose.runOnIdle {
            assertTrue(model.state.value.loading)
            assertFalse(model.state.value.clearConfirmation)
            if (operation == Operation.FAVORITE) assertTrue(model.state.value.favoriteNameDialog)
        }
        assertEquals(0, backCalls.get())
        compose.runOnIdle { reply.complete(Unit) }
        compose.waitForIdle()
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
        if (operation == Operation.FAVORITE) {
            compose.onNodeWithText("Entendi").performClick()
            compose.waitForIdle()
        }
        device.pressBack()
        compose.waitForIdle()
        assertEquals("Hardware back must use the Root callback after the mutation", 1, backCalls.get())
    }

    private fun show() {
        val item = CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "", BigDecimal.TEN,
            modalityCode = "MEGA_SENA", selectedNumbers = listOf(listOf(1, 2, 3, 4, 5, 6)))
        compose.setContent {
            val fixture = remember {
                CartViewModel(
                    getCurrentCart = { AppResult.Success(ShoppingCart(1, BigDecimal.TEN, listOf(item))) },
                    removeCartItem = { reply.await(); AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList())) },
                    clearCart = { reply.await(); AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList())) },
                    serverTime = ServerTimeSource { Instant.parse("2026-10-14T12:00:00Z") },
                    verifyPaymentAccount = { reply.await(); AppResult.Success(CartPaymentAccountDecision.Allowed) },
                    validateCart = { AppResult.Success(CartValidationResult.Accepted) },
                    hasRecentPurchase = { AppResult.Success(false) },
                    checkCheckoutLocation = { AppResult.Success(CheckoutLocationStatus.IN_BRAZIL) },
                    recordLocationRequest = { AppResult.Success(Unit) },
                    saveCurrentCartFavorite = SaveCurrentCartFavoriteUseCase { reply.await(); AppResult.Success(Unit) },
                    dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                    authenticated = true,
                ).also { model = it }
            }
            CaixaDSTheme(darkTheme = false) {
                CurrentCartRoute(fixture, { backCalls.incrementAndGet() }, {}, {}, {}, {}, {}, {})
            }
        }
    }

    private enum class Operation { DELETE, CLEAR, FAVORITE, CHECKOUT }
}
