package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.ClearCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.presentation.coordinator.CartContentsCoordinator
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartContentsAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartReservationAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import br.gov.caixa.loterias.feature.cart.presentation.state.reduceCartState
import java.math.BigDecimal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartClearFailureSessionTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    @Test fun `failed clear response cannot empty a replacement session cart`() = runTest(dispatcherRule.dispatcher) {
        val original = snapshot(5)
        val replacement = snapshot(9)
        val state = reduxStateStore<CartUiState, CartAction>(CartUiState(loading = false, cart = original), ::reduceCartState)
        var currentSession = true
        val response = CompletableDeferred<Unit>()
        val contents = CartContentsCoordinator(
            state, backgroundScope, FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            GetCurrentCartUseCase { error("Clear must not reload") },
            RemoveCartItemUseCase { error("Clear must not remove individual items") },
            ClearCartUseCase {
                response.await()
                AppResult.Failure(AppError.Network)
            },
            { currentSession }, { false }, {}, { "Network failure" }
        )
        contents.requestClear()
        contents.confirmClear()
        dispatcherRule.dispatcher.scheduler.runCurrent()
        currentSession = false
        state.dispatch(CartReservationAction.SessionInvalidated)
        state.dispatch(CartContentsAction.Loaded(replacement))
        response.complete(Unit)
        dispatcherRule.dispatcher.scheduler.runCurrent()
        assertEquals(replacement, state.value.cart)
        assertNull(state.value.errorMessage)
        state.close()
    }

    @Test fun `cancelled clear propagates cancellation without clearing cart or publishing failure`() = runTest(dispatcherRule.dispatcher) {
        val original = snapshot(5)
        val state = reduxStateStore<CartUiState, CartAction>(CartUiState(loading = false, cart = original), ::reduceCartState)
        var requestJob: Job? = null
        val contents = CartContentsCoordinator(
            state, backgroundScope, FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            GetCurrentCartUseCase { error("Clear must not reload") },
            RemoveCartItemUseCase { error("Clear must not remove individual items") },
            ClearCartUseCase {
                requestJob = kotlin.coroutines.coroutineContext[Job]
                throw CancellationException("Session ended")
            },
            { true }, { false }, {}, { "Unexpected failure" }
        )
        contents.requestClear()
        contents.confirmClear()
        dispatcherRule.dispatcher.scheduler.runCurrent()
        assertTrue(requestJob!!.isCancelled)
        assertEquals(original, state.value.cart)
        assertNull(state.value.errorMessage)
        state.close()
    }

    private fun snapshot(id: Long) = ShoppingCart(
        id,
        BigDecimal.TEN,
        listOf(
            CartItem(12, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "01 02 03", BigDecimal.TEN)
        )
    )
}
