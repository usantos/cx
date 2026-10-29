package br.gov.caixa.loterias.feature.cart.presentation.coordinator

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartCheckoutAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.ensureActive

internal class CartCheckoutValidation(
    private val mutableState: ReduxStateStore<CartUiState, CartAction>,
    private val validateCart: ValidateCartUseCase,
    val notices: CartCheckoutNotices,
    private val messageFor: (AppError) -> String,
) {
    suspend fun continuePayment(account: CartPaymentAccountDecision) {
        if (account == CartPaymentAccountDecision.LoginRequired) {
            mutableState.dispatch(CartCheckoutAction.AccountDecisionReceived(account))
            return
        }
        val result = validateCart()
        // Um callback de transporte concluído não deve reativar a finalização após o cancelamento de sua sessão.
        coroutineContext.ensureActive()
        when (result) {
            is AppResult.Success -> when (val validation = result.value) {
                CartValidationResult.Accepted -> mutableState.dispatch(CartCheckoutAction.PaymentValidated)
                is CartValidationResult.Rejected -> notices.show(validation.failure)
            }

            is AppResult.Failure -> mutableState.dispatch(CartCheckoutAction.ValidationFailed(messageFor(result.error)))
        }
    }
}
