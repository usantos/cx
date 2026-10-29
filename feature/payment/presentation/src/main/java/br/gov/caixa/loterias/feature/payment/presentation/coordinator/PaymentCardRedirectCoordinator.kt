package br.gov.caixa.loterias.feature.payment.presentation.coordinator

import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.payment.domain.model.CardSubmissionOutcome
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardPurchaseContext
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentCardState

internal class PaymentCardRedirectCoordinator(
    private val mutableState: ReduxStateStore<PaymentCardUiState, PaymentCardAction>,
    private val purchase: PaymentCardPurchaseContext,
) {
    private var reviewHandled = false

    fun acknowledgePurchaseReview(): Boolean {
        synchronized(mutableState) {
            if (mutableState.value.stage != PaymentCardStage.REVIEW_REQUIRED || mutableState.value.redirectPaused ||
                reviewHandled
            ) {
                return false
            }
            reviewHandled = true
            return true
        }
    }

    fun consumeRedirect(expected: PurchaseRedirect): Boolean {
        synchronized(mutableState) {
            if (mutableState.value.redirect != expected) return false
            mutableState.dispatch(PaymentCardAction.RedirectConsumed(purchase.trackedPurchase != null))
            return true
        }
    }

    fun resumeAfterRedirect() = mutableState.dispatch(PaymentCardAction.RedirectResumed)

    fun requirePurchaseReview(outcome: CardSubmissionOutcome.ReviewRequired) =
        mutableState.dispatch(PaymentCardAction.PurchaseReviewRequired(outcome))

    fun requireRedirect(destination: PurchaseRedirect, purchaseId: Long?) =
        mutableState.dispatch(PaymentCardAction.RedirectRequired(destination, purchaseId))
}
