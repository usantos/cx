package br.gov.caixa.loterias.feature.payment.presentation.coordinator

import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNotice
import br.gov.caixa.loterias.feature.payment.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PurchaseNoticeUiState
import br.gov.caixa.loterias.feature.payment.presentation.mapper.purchaseNoticeUi
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardPurchaseContext
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentCardState

internal class PaymentCardNoticeCoordinator(
    private val mutableState: ReduxStateStore<PaymentCardUiState, PaymentCardAction>,
    private val purchase: PaymentCardPurchaseContext,
    private val clock: java.time.Clock,
) {
    fun consumeNotice(expected: PurchaseNoticeUiState, moreBets: Boolean): Boolean {
        if (moreBets && !expected.offerMoreBets) return false
        return completeNotice(expected, moreBets, false)
    }

    fun consumeRegistrationNotice(expected: PurchaseNoticeUiState): Boolean =
        expected.offerRegistration && completeNotice(expected, false, true)

    fun registrationFinished() = mutableState.dispatch(PaymentCardAction.RegistrationFinished)

    private fun completeNotice(expected: PurchaseNoticeUiState, moreBets: Boolean, registration: Boolean): Boolean {
        synchronized(mutableState) {
            if (mutableState.value.notice !== expected) return false
            mutableState.dispatch(PaymentCardAction.NoticeConsumed(purchase.trackedPurchase != null, moreBets, registration))
            return true
        }
    }

    fun requireNotice(notice: PurchaseNotice) {
        val presentation = purchaseNoticeUi(notice, java.time.LocalDateTime.now(clock))
        mutableState.dispatch(PaymentCardAction.NoticeRequired(presentation))
    }
}
