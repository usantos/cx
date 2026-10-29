package br.gov.caixa.loterias.feature.cart

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.feature.cart.domain.model.CartPoolShare
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationStatus
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.math.BigDecimal
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.CartViewModel
import br.gov.caixa.loterias.feature.cart.presentation.route.CurrentCartRoute

class CurrentCartCoordinationTest {
    @get:Rule val compose = createComposeRule()
    private val models = mutableListOf<CartViewModel>()
    @After fun cleanup() { models.forEach { it.viewModelScope.cancel() } }

    @Test fun replacingViewModelUpdatesItemDialogsFavoriteNameAndCheckoutCallbacks() {
        val first = Fixture()
        val second = Fixture()
        val selected = mutableIntStateOf(0)
        var payment = 0
        compose.setContent {
            val old = remember { first.create() }
            val current = remember { second.create() }
            val vm = if (selected.intValue == 0) old else current
            CaixaDSTheme { CurrentCartRoute(vm, {}, {}, { assertFalse(vm.state.value.paymentReady); payment++ }, {}, {}, {}, {}) }
        }
        compose.runOnIdle { selected.intValue = 1 }
        compose.onNodeWithContentDescription("Excluir").performClick()
        compose.runOnIdle { assertNull(first.model.state.value.pendingRemoval); assertEquals(1L, second.model.state.value.pendingRemoval?.id) }
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithContentDescription("Salvar carrinho favorito").performClick()
        compose.onNode(hasSetTextAction()).performTextReplacement("Segundo")
        compose.onNodeWithText("Salvar").performScrollTo().performClick()
        compose.onNodeWithText("Carrinho salvo nos favoritos.").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("Ir para pagamento").performClick()
        compose.runOnIdle { assertTrue(first.saved.isEmpty()); assertEquals(listOf("Segundo"), second.saved); assertEquals(1, payment) }
    }

    @Test fun loginRegistrationAndPaymentConsumeDecisionsBeforeHostNavigation() {
        val fixtures = listOf(Fixture(CartPaymentAccountDecision.LoginRequired),
            Fixture(CartPaymentAccountDecision.CompleteAccount(PostLoginDestination.REGISTRATION)), Fixture())
        val selected = mutableIntStateOf(0)
        val destinations = mutableListOf<String>()
        compose.setContent {
            val vms = remember { fixtures.map { it.create() } }
            val vm = vms[selected.intValue]
            CaixaDSTheme {
                CurrentCartRoute(vm, {}, {}, { assertFalse(vm.state.value.paymentReady); destinations.add("payment") },
                    { assertNull(vm.state.value.paymentAccountDecision); destinations.add("login") },
                    { assertNull(vm.state.value.paymentAccountDecision); destinations.add("account:$it") }, {}, {})
            }
        }
        compose.onNodeWithText("Ir para pagamento").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.runOnIdle { selected.intValue = 1 }
        compose.onNodeWithText("Ir para pagamento").performClick()
        compose.runOnIdle { selected.intValue = 2 }
        compose.onNodeWithText("Ir para pagamento").performClick()
        compose.runOnIdle { assertEquals(listOf("login", "account:REGISTRATION", "payment"), destinations) }
    }

    @Test fun reservationCounterRefreshesFromServerClockWithoutChangingTheCart() {
        val fixture = Fixture().apply {
            cart = ShoppingCart(1, BigDecimal.TEN, listOf(CartItem(9, CartItemKind.POOL_SHARE, "Bolão Mega", "", BigDecimal.TEN,
                modalityCode = "MEGA_SENA", poolShare = CartPoolShare("ABC", 2, 10, "2026-10-18T15:02:00Z"))))
        }
        compose.setContent {
            val vm = remember { fixture.create() }
            CaixaDSTheme { CurrentCartRoute(vm, {}, {}, {}, {}, {}, {}, {}) }
        }
        compose.onNodeWithText("02:00").assertIsDisplayed()
        compose.runOnIdle { fixture.now = Instant.parse("2026-10-18T15:00:01Z") }
        compose.waitUntil(timeoutMillis = 5_000) { compose.onAllNodesWithText("01:59").fetchSemanticsNodes().isNotEmpty() }
        compose.runOnIdle { assertEquals(9L, fixture.model.state.value.cart?.items?.single()?.id); assertFalse(fixture.model.state.value.expirationRemovalInFlight) }
    }

    private inner class Fixture(val account: CartPaymentAccountDecision = CartPaymentAccountDecision.Allowed) {
        var now = Instant.parse("2026-10-18T15:00:00Z")
        var cart = ShoppingCart(1, BigDecimal.TEN, listOf(CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "", BigDecimal.TEN,
            modalityCode = "MEGA_SENA", selectedNumbers = listOf(listOf(1, 2, 3, 4, 5, 6)))))
        val saved = mutableListOf<String>()
        lateinit var model: CartViewModel
        fun create() = CartViewModel(
            getCurrentCart = { AppResult.Success(cart) },
            removeCartItem = { item -> cart = cart.copy(items = cart.items.filterNot { it.id == item.id }); AppResult.Success(cart) },
            clearCart = { cart = cart.copy(items = emptyList()); AppResult.Success(cart) },
            serverTime = ServerTimeSource { now }, verifyPaymentAccount = { AppResult.Success(account) },
            validateCart = { AppResult.Success(CartValidationResult.Accepted) }, hasRecentPurchase = { AppResult.Success(false) },
            checkCheckoutLocation = { AppResult.Success(CheckoutLocationStatus.IN_BRAZIL) }, recordLocationRequest = { AppResult.Success(Unit) },
            validateFavoriteCartName = { AppResult.Success(Unit) },
            saveCurrentCartFavorite = { saved.add(it); AppResult.Success(Unit) }, authenticated = true,
            dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
        ).also { model = it; models.add(it) }
    }
}
