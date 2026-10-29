package br.gov.caixa.loterias.feature.payment.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.CardPaymentRequest
import br.gov.caixa.loterias.feature.payment.domain.model.CardSubmissionOutcome
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentCardDraft
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentCardSelection
import br.gov.caixa.loterias.feature.payment.domain.usecase.SubmitCardPaymentUseCase
import br.gov.caixa.loterias.feature.payment.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardPurchaseContext
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentCardState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class PaymentCardSubmissionCoordinator(
    private val mutableState: ReduxStateStore<PaymentCardUiState, PaymentCardAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val purchase: PaymentCardPurchaseContext,
    private val paymentMethodId: Long,
    private val getCurrentCart: GetCurrentCartUseCase,
    private val submitCardPayment: SubmitCardPaymentUseCase,
    private val monitoring: PaymentCardMonitoringCoordinator,
    private val notices: PaymentCardNoticeCoordinator,
    private val redirects: PaymentCardRedirectCoordinator,
    private val fail: (AppError) -> Unit,
) {
    fun submit() {
        synchronized(mutableState) {
            if (mutableState.value.stage != PaymentCardStage.CONFIRM || mutableState.value.redirectPaused ||
                mutableState.value.registrationPending ||
                mutableState.value.purchaseReview != null ||
                purchase.trackedPurchase != null
            ) {
                return
            }
            val current = mutableState.value
            mutableState.dispatch(PaymentCardAction.ProcessingStarted)
            scope.launch(dispatchers.io) {
                val amount = when (val cart = getCurrentCart()) {
                    is AppResult.Failure -> {
                        fail(cart.error)
                        return@launch
                    }

                    is AppResult.Success -> cart.value.total
                }
                if (amount.signum() <= 0) {
                    fail(AppError.Validation(messageText("valor_do_carrinho_invalido_e1f371")))
                    return@launch
                }
                val selection = current.savedCard?.let(PaymentCardSelection::Saved) ?: PaymentCardSelection.New(
                    PaymentCardDraft(
                        number = current.normalizedNumber,
                        holderName = current.holderName,
                        expirationMonth = current.month.toIntOrNull() ?: 0,
                        expirationYear = current.year.toIntOrNull() ?: 0,
                        save = current.save
                    )
                )
                when (val result = submitCardPayment(CardPaymentRequest(paymentMethodId, selection, current.cvv))) {
                    is AppResult.Failure -> fail(result.error)

                    is AppResult.Success -> when (val outcome = result.value) {
                        is CardSubmissionOutcome.Submitted -> monitoring.beginMonitoring(outcome.purchase, amount)
                        is CardSubmissionOutcome.ReviewRequired -> redirects.requirePurchaseReview(outcome)
                        is CardSubmissionOutcome.RedirectRequired -> redirects.requireRedirect(outcome.destination, outcome.purchaseId)
                        is CardSubmissionOutcome.NoticeRequired -> notices.requireNotice(outcome.notice)
                    }
                }
            }
        }
    }
}
