package br.gov.caixa.loterias.feature.payment.presentation.coordinator

import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.payment.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentCardState

internal class PaymentCardFormCoordinator(private val mutableState: ReduxStateStore<PaymentCardUiState, PaymentCardAction>) {
    fun changeNumber(value: String) = mutableState.dispatch(PaymentCardAction.NumberChanged(value))

    fun changeHolderName(value: String) = mutableState.dispatch(PaymentCardAction.HolderNameChanged(value))

    fun changeMonth(value: String) = mutableState.dispatch(PaymentCardAction.MonthChanged(value))

    fun changeYear(value: String) = mutableState.dispatch(PaymentCardAction.YearChanged(value))

    fun toggleSave() = mutableState.dispatch(PaymentCardAction.SaveToggled)

    fun continueFromCard() {
        if (mutableState.value.stage !in setOf(PaymentCardStage.FORM, PaymentCardStage.PREVIEW)) return
        if (mutableState.value.savedCard != null || mutableState.value.formValid) {
            mutableState.dispatch(PaymentCardAction.CvvEntryStarted)
        } else {
            mutableState.dispatch(PaymentCardAction.InvalidCardEntered())
        }
    }

    fun changeCvv(value: String) = mutableState.dispatch(PaymentCardAction.CvvChanged(value))

    fun continueFromCvv() {
        if (mutableState.value.stage != PaymentCardStage.CVV) return
        if (mutableState.value.cvv.length == mutableState.value.cvvLength) {
            mutableState.dispatch(PaymentCardAction.ConfirmationStarted)
        } else {
            mutableState.dispatch(PaymentCardAction.InvalidCvvEntered())
        }
    }
}
