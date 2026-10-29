package br.gov.caixa.loterias.feature.cart.presentation.coordinator

import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.ServiceRedirectEvent
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartCheckoutAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState

/** A identidade por referência impede que um consumidor antigo limpe um redirecionamento mais recente. */
internal class CartCheckoutRedirects(private val mutableState: ReduxStateStore<CartUiState, CartAction>) {
    fun consumePaymentReady() = mutableState.dispatch(CartCheckoutAction.PaymentReadyConsumed)

    fun consumeValidationRedirect(expected: ServiceRedirectEvent): Boolean = synchronized(mutableState) {
        if (mutableState.value.validationFailure.redirect !== expected) return@synchronized false
        mutableState.dispatch(CartCheckoutAction.ValidationRedirectConsumed(expected))
        true
    }

    fun consumePaymentAccountDecision() = mutableState.dispatch(CartCheckoutAction.AccountDecisionConsumed)
}
