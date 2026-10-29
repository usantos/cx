package br.gov.caixa.loterias.feature.payment.presentation.coordinator

import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentState

internal class PaymentRedirects(private val mutableState: ReduxStateStore<PaymentUiState, PaymentAction>) {

    fun consumeRedirect(expected: PurchaseRedirect): Boolean {
        synchronized(mutableState) {
            if (mutableState.value.redirect != expected) return false
            mutableState.dispatch(PaymentAction.RedirectConsumed)
            return true
        }
    }

    fun resumeAfterRedirect() = mutableState.dispatch(PaymentAction.RedirectResumed)
}
