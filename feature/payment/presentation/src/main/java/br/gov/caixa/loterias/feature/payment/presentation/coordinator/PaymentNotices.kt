package br.gov.caixa.loterias.feature.payment.presentation.coordinator

import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PurchaseNoticeUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentState

internal class PaymentNotices(private val mutableState: ReduxStateStore<PaymentUiState, PaymentAction>) {

    fun consumeNotice(expected: PurchaseNoticeUiState, moreBets: Boolean): Boolean {
        if (moreBets && !expected.offerMoreBets) return false
        return completeNotice(expected, moreBets, false)
    }

    fun consumeRegistrationNotice(expected: PurchaseNoticeUiState): Boolean =
        expected.offerRegistration && completeNotice(expected, false, true)

    fun registrationFinished() = mutableState.dispatch(PaymentAction.RegistrationFinished)

    private fun completeNotice(expected: PurchaseNoticeUiState, moreBets: Boolean, registration: Boolean): Boolean {
        synchronized(mutableState) {
            if (mutableState.value.notice !== expected) return false
            mutableState.dispatch(PaymentAction.NoticeConsumed(moreBets, registration))
            return true
        }
    }
}
