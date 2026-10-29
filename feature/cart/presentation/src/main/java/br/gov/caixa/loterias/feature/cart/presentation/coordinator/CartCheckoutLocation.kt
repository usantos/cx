package br.gov.caixa.loterias.feature.cart.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationStatus
import br.gov.caixa.loterias.feature.cart.domain.usecase.CheckCheckoutLocationUseCase
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartCheckoutAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** Controla a conta que aguarda a localização e a fronteira original de cancelamento das etapas seguintes. */
internal class CartCheckoutLocation(
    private val mutableState: ReduxStateStore<CartUiState, CartAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val checkCheckoutLocation: CheckCheckoutLocationUseCase,
    private val checkRecentPurchase: suspend (CartPaymentAccountDecision) -> Unit,
) {
    private var locationAccount: CartPaymentAccountDecision? = null
    val pending: Boolean get() = locationAccount != null

    suspend fun check(account: CartPaymentAccountDecision) {
        locationAccount = account
        checkLocationAndContinue()
    }

    private suspend fun checkLocationAndContinue() {
        val account = locationAccount ?: return
        try {
            val status = when (val result = checkCheckoutLocation()) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> CheckoutLocationStatus.UNAVAILABLE
            }
            if (status == CheckoutLocationStatus.IN_BRAZIL) {
                locationAccount = null
                checkRecentPurchase(account)
            } else {
                mutableState.dispatch(CartCheckoutAction.LocationPrompted(status))
            }
        } catch (cancelled: CancellationException) {
            blockLocation()
            throw cancelled
        } finally {
            mutableState.dispatch(CartCheckoutAction.LocationCheckFinished)
        }
    }

    fun retryCheckoutLocation() {
        mutableState.dispatch(CartCheckoutAction.LocationRetryStarted)
        scope.launch(dispatchers.io) { checkLocationAndContinue() }
    }

    fun declineLocationConsent() {
        if (mutableState.value.locationPrompt != null) blockLocation()
    }

    fun dismissLocationBlock(): Boolean {
        if (mutableState.value.locationPrompt !in
            setOf(CheckoutLocationStatus.OUTSIDE_BRAZIL, CheckoutLocationStatus.UNAVAILABLE)
        ) {
            return false
        }
        locationAccount = null
        mutableState.dispatch(CartCheckoutAction.LocationBlockDismissed)
        return true
    }

    fun blockLocation() {
        locationAccount = null
        mutableState.dispatch(CartCheckoutAction.LocationBlocked)
    }
}
