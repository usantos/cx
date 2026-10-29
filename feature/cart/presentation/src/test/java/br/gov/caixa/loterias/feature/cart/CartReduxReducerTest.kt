package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.state.ServiceFailurePresentation
import br.gov.caixa.loterias.core.presentation.state.ServiceRedirectEvent
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.coordinator.CartCheckoutRedirects
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartCheckoutAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartContentsAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartLocationAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartReservationAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import br.gov.caixa.loterias.feature.cart.presentation.state.reduceCartState
import java.math.BigDecimal
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class CartReduxReducerTest {
    private val timeoutSeconds = 5L
    private val cart = ShoppingCart(
        1,
        BigDecimal.TEN,
        listOf(
            CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "01 02 03", BigDecimal.TEN)
        )
    )

    @Test
    fun `failed clear empties content while retaining expiration operation ownership`() {
        val initial = CartUiState(loading = true, expirationRemovalInFlight = true, cart = cart)
        val failed = reduceCartState(initial, CartContentsAction.ClearFailed("Network failure"))
        assertTrue(failed.loading)
        assertTrue(failed.expirationRemovalInFlight)
        assertTrue(failed.cart!!.items.isEmpty())
        assertEquals(BigDecimal.ZERO, failed.cart.total)
        assertEquals(0, failed.cart.totalBetCount)
        assertEquals("Network failure", failed.errorMessage)
        assertEquals(cart, initial.cart)
    }

    @Test
    fun `reservation identity invalidation clears all private checkout state`() {
        val initial = CartUiState(
            loading = true,
            cart = cart,
            authenticated = true,
            paymentReady = true,
            favoriteName = "Private",
            locationAction = CartLocationAction.PERMISSION_SETTINGS
        )
        assertEquals(CartUiState(loading = false), reduceCartState(initial, CartReservationAction.SessionInvalidated))
    }

    @Test
    fun `stale notice acceptance and redirect consumption preserve newer identities`() {
        val oldNotice = ServiceNoticeUiState("Older")
        val currentNotice = ServiceNoticeUiState("Current")
        val redirect = ServiceRedirectEvent(ServiceRedirect.ACCEPT_TERMS)
        val current = CartUiState(validationFailure = ServiceFailurePresentation(notice = currentNotice, redirect = redirect))
        assertSame(current, reduceCartState(current, CartCheckoutAction.ValidationNoticeAccepted(oldNotice, ServiceFailurePresentation())))
        assertSame(
            current,
            reduceCartState(
                current,
                CartCheckoutAction.ValidationRedirectConsumed(ServiceRedirectEvent(ServiceRedirect.ACCEPT_TERMS))
            )
        )
        val consumed = reduceCartState(current, CartCheckoutAction.ValidationRedirectConsumed(redirect))
        assertSame(currentNotice, consumed.validationFailure.notice)
        assertNull(consumed.validationFailure.redirect)
    }

    @Test
    fun `location launch claim cannot replace an action already awaiting Android`() {
        val initial = CartUiState(locationAction = CartLocationAction.REQUEST_PERMISSION)
        val claimed = reduceCartState(initial, CartCheckoutAction.LocationActionTaken(CartLocationAction.REQUEST_PERMISSION))
        assertNull(claimed.locationAction)
        assertEquals(CartLocationAction.REQUEST_PERMISSION, claimed.awaitingLocationAction)
        assertSame(claimed, reduceCartState(claimed, CartCheckoutAction.LocationActionTaken(CartLocationAction.REQUEST_PERMISSION)))
    }

    @Test
    fun `concurrent redirect consumers claim an event exactly once`() {
        val expected = ServiceRedirectEvent(ServiceRedirect.ACCEPT_TERMS)
        reduxStateStore<CartUiState, CartAction>(
            CartUiState(validationFailure = ServiceFailurePresentation(redirect = expected)),
            ::reduceCartState
        ).use { store ->
            val redirects = CartCheckoutRedirects(store)
            val start = CountDownLatch(1)
            val executor = Executors.newFixedThreadPool(2)
            try {
                val claims = List(2) {
                    executor.submit<Boolean> {
                        check(start.await(timeoutSeconds, TimeUnit.SECONDS))
                        redirects.consumeValidationRedirect(expected)
                    }
                }
                start.countDown()
                assertEquals(1, claims.count { it.get(timeoutSeconds, TimeUnit.SECONDS) })
                assertFalse(redirects.consumeValidationRedirect(expected))
                assertNull(store.value.validationFailure.redirect)
            } finally {
                executor.shutdownNow()
            }
        }
    }
}
