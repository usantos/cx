package br.gov.caixa.loterias.feature.payment.presentation.coordinator

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentState

internal class PaymentMethodsLoading(
    private val mutableState: ReduxStateStore<PaymentUiState, PaymentAction>,
    private val getMethods: GetPaymentMethodsUseCase,
    private val getCards: GetSavedCardsUseCase,
    private val getCurrentCart: GetCurrentCartUseCase,
    private val applyFailure: (AppError) -> Unit,
) {
    suspend fun loadMethods(withCart: Boolean) {
        when (val methods = getMethods()) {
            is AppResult.Failure -> applyFailure(methods.error)

            is AppResult.Success -> {
                val cards = loadAvailableCards(methods.value)
                if (!withCart) {
                    mutableState.dispatch(PaymentAction.MethodsLoaded(methods.value, cards))
                    return
                }
                when (val cart = getCurrentCart()) {
                    is AppResult.Failure -> applyFailure(cart.error)

                    is AppResult.Success -> mutableState.dispatch(
                        PaymentAction.CheckoutMethodsLoaded(methods.value, cards, cart.value.total)
                    )
                }
            }
        }
    }

    private suspend fun loadAvailableCards(methods: List<PaymentMethod>): Map<Long, List<SavedPaymentCard>> = buildMap {
        methods.filter(PaymentMethod::isSupportedCard).forEach { method ->
            val result = getCards(method.id)
            put(method.id, (result as? AppResult.Success)?.value.orEmpty())
        }
    }
}
