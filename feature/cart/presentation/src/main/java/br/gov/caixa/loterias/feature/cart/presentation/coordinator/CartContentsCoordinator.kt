package br.gov.caixa.loterias.feature.cart.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.ClearCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartContentsAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

internal class CartContentsCoordinator(
    private val mutableState: ReduxStateStore<CartUiState, CartAction>,
    private val viewModelScope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getCurrentCart: GetCurrentCartUseCase,
    private val removeCartItem: RemoveCartItemUseCase,
    private val clearCart: ClearCartUseCase,
    private val reservationSessionCurrent: () -> Boolean,
    private val isCartRequestRunning: () -> Boolean,
    private val setCartRequestRunning: (Boolean) -> Unit,
    private val errorMessage: (AppError) -> String
) {
    private var cartRequestRunning: Boolean
        get() = isCartRequestRunning()
        set(value) = setCartRequestRunning(value)
    private fun AppError.userMessage(): String = errorMessage(this)

    fun load() {
        if (cartRequestRunning) return
        cartRequestRunning = true
        mutableState.dispatch(CartContentsAction.LoadStarted)
        viewModelScope.launch(dispatchers.io) {
            try {
                applyResult(getCurrentCart())
            } finally {
                cartRequestRunning = false
                mutableState.dispatch(CartContentsAction.LoadFinished)
            }
        }
    }

    fun requestRemoval(item: CartItem) = mutableState.dispatch(CartContentsAction.RemovalRequested(item))

    fun requestClear() = mutableState.dispatch(CartContentsAction.ClearRequested)

    fun dismissConfirmation() = mutableState.dispatch(CartContentsAction.ConfirmationDismissed)

    fun confirmRemoval() {
        if (mutableState.value.loading) return
        val item = mutableState.value.pendingRemoval ?: return
        cartRequestRunning = true
        mutableState.dispatch(CartContentsAction.RemovalStarted)
        viewModelScope.launch(dispatchers.io) {
            try {
                applyResult(removeCartItem(item))
            } finally {
                cartRequestRunning = false
            }
        }
    }

    fun confirmClear() {
        if (mutableState.value.loading) return
        cartRequestRunning = true
        mutableState.dispatch(CartContentsAction.ClearStarted)
        viewModelScope.launch(dispatchers.io) {
            try {
                val result = clearCart()
                coroutineContext.ensureActive()
                if (!reservationSessionCurrent()) return@launch
                when (result) {
                    is AppResult.Success -> mutableState.dispatch(CartContentsAction.Cleared(result.value))

                    // A CX atual limpa seu carrinho local e exibido mesmo quando a limpeza remota falha.
                    is AppResult.Failure -> mutableState.dispatch(CartContentsAction.ClearFailed(result.error.userMessage()))
                }
            } finally {
                cartRequestRunning = false
            }
        }
    }

    fun applyResult(result: AppResult<ShoppingCart>) {
        if (!reservationSessionCurrent()) return
        when (result) {
            is AppResult.Success -> mutableState.dispatch(CartContentsAction.Loaded(result.value))
            is AppResult.Failure -> mutableState.dispatch(CartContentsAction.LoadFailed(result.error.userMessage()))
        }
    }
}
