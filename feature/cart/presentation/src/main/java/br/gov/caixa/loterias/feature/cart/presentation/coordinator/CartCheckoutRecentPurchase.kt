package br.gov.caixa.loterias.feature.cart.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.HasRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartCheckoutAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** A confirmação se aplica somente ao par de carrinho e conta capturado. */
internal class CartCheckoutRecentPurchase(
    private val mutableState: ReduxStateStore<CartUiState, CartAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val hasRecentPurchase: HasRecentPurchaseUseCase,
    private val continuePayment: suspend (CartPaymentAccountDecision) -> Unit,
    private val requestPayment: () -> Unit,
    private val messageFor: (AppError) -> String,
) {
    private var recentPurchaseContinuation: Pair<ShoppingCart, CartPaymentAccountDecision>? = null

    suspend fun checkRecentPurchase(account: CartPaymentAccountDecision) {
        val cart = mutableState.value.cart ?: return
        when (val result = hasRecentPurchase(cart.total)) {
            is AppResult.Failure -> mutableState.dispatch(CartCheckoutAction.Failed(messageFor(result.error)))

            is AppResult.Success -> if (result.value) {
                recentPurchaseContinuation = cart to account
                mutableState.dispatch(CartCheckoutAction.RecentPurchaseDetected)
            } else {
                continuePayment(account)
            }
        }
    }

    /** Sim significa revisar o carrinho, sem autorizar uma compra duplicada. */
    fun reviewRecentPurchase() {
        recentPurchaseContinuation = null
        mutableState.dispatch(CartCheckoutAction.RecentPurchaseReviewed)
    }

    /** Não retoma somente esta tentativa; uma tentativa posterior deve consultar o histórico novamente. */
    fun continueRecentPurchase() {
        if (mutableState.value.loading) return
        val (cart, account) = recentPurchaseContinuation ?: return
        reviewRecentPurchase()
        if (mutableState.value.cart != cart) {
            requestPayment()
            return
        }
        mutableState.dispatch(CartCheckoutAction.RecentPurchaseContinued)
        scope.launch(dispatchers.io) { continuePayment(account) }
    }
}
