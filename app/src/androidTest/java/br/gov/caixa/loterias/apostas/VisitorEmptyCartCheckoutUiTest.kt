package br.gov.caixa.loterias.apostas

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationStatus
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.*
import br.gov.caixa.loterias.feature.cart.presentation.route.CurrentCartRoute
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.CartViewModel
import java.math.BigDecimal
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** O carrinho vazio do visitante usa a rota e as barreiras reais de finalização com simulações exclusivamente locais. */
class VisitorEmptyCartCheckoutUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun emptyVisitorCartHidesPaymentWithoutStartingCheckout() {
        val events = CopyOnWriteArrayList<String>()
        val vm = model(events, CheckoutLocationStatus.IN_BRAZIL)
        show(vm, events)
        compose.waitUntil { vm.state.value.cart != null }
        compose.onNodeWithText("Ir para pagamento").assertDoesNotExist()
        compose.onNodeWithContentDescription("Carrinho").assertDoesNotExist()
        compose.onNodeWithText("Seu carrinho está vazio").assertExists()
        assertEquals(emptyList<String>(), events.toList())
    }

    @Test fun emptyVisitorCartDoesNotCheckLocationOrRequestLogin() {
        val events = CopyOnWriteArrayList<String>()
        val vm = model(events, CheckoutLocationStatus.UNAVAILABLE)
        show(vm, events)
        compose.waitUntil { vm.state.value.cart != null }
        compose.onNodeWithText("Ir para pagamento").assertDoesNotExist()
        compose.onNodeWithContentDescription("Carrinho").assertDoesNotExist()
        compose.onNodeWithText("Seu carrinho está vazio").assertExists()
        assertEquals(emptyList<String>(), events.toList())
    }

    private fun show(vm: CartViewModel, events: MutableList<String>) {
        compose.setContent {
            CaixaDSTheme {
                CurrentCartRoute(vm, onBack = {}, onContinueBetting = {},
                    onPayment = { error("A guest cart must never open payment") },
                    onLogin = { events += "login" },
                    onAccountRequired = { error("Unexpected account completion") },
                    onLocationBlocked = { events += "blocked" },
                    onValidationRedirect = { error("Unexpected server redirect") })
            }
        }
    }

    private fun model(events: MutableList<String>, location: CheckoutLocationStatus) = CartViewModel(
        getCurrentCart = GetCurrentCartUseCase { AppResult.Success(ShoppingCart(0, BigDecimal.ZERO, emptyList())) },
        removeCartItem = RemoveCartItemUseCase { error("Unexpected removal") },
        clearCart = ClearCartUseCase { error("Unexpected clear") },
        serverTime = br.gov.caixa.loterias.core.common.time.ServerTimeSource { Instant.parse("2026-10-21T12:00:00Z") },
        verifyPaymentAccount = VerifyCartPaymentAccountUseCase {
            events += "account"; AppResult.Success(CartPaymentAccountDecision.LoginRequired)
        },
        validateCart = ValidateCartUseCase { error("Guest checkout must not invoke remote validation") },
        hasRecentPurchase = HasRecentPurchaseUseCase { events += "recent"; AppResult.Success(false) },
        checkCheckoutLocation = CheckCheckoutLocationUseCase { events += "location"; AppResult.Success(location) },
        recordLocationRequest = RecordCheckoutLocationRequestUseCase { error("No permission request expected") },
    )
}
