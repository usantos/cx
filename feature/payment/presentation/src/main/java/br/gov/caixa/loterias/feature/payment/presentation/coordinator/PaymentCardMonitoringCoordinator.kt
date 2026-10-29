package br.gov.caixa.loterias.feature.payment.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.CardPurchasePolling
import br.gov.caixa.loterias.feature.payment.domain.model.CardSubmissionOutcome
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseSubmission
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardPurchaseContext
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardTrackedPurchase
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentCardState
import java.math.BigDecimal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class PaymentCardMonitoringCoordinator(
    private val mutableState: ReduxStateStore<PaymentCardUiState, PaymentCardAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val purchase: PaymentCardPurchaseContext,
    private val getPurchaseProgress: GetPurchaseProgressUseCase,
    private val recordRecentPurchase: RecordRecentPurchaseUseCase,
    private val notices: PaymentCardNoticeCoordinator,
    private val redirects: PaymentCardRedirectCoordinator,
    private val fail: (AppError) -> Unit,
) {
    fun retry() {
        if (mutableState.value.stage != PaymentCardStage.FAILURE || mutableState.value.redirectPaused ||
            mutableState.value.registrationPending
        ) {
            return
        }
        val tracked = purchase.trackedPurchase
        if (tracked == null) {
            mutableState.dispatch(PaymentCardAction.ConfirmationStarted)
        } else {
            mutableState.dispatch(PaymentCardAction.ProcessingStarted)
            scope.launch(dispatchers.io) { monitorPurchase(tracked) }
        }
    }

    suspend fun beginMonitoring(submission: PurchaseSubmission, amount: BigDecimal) {
        if (submission.purchaseId <= 0) return redirects.requirePurchaseReview(CardSubmissionOutcome.ReviewRequired())
        val tracked = PaymentCardTrackedPurchase(
            CardPurchasePolling(
                submission.purchaseId,
                submission.pollingIntervalMillis.coerceAtLeast(0),
                submission.pollingAttempts.coerceAtLeast(0)
            ),
            amount
        )
        purchase.trackedPurchase = tracked
        mutableState.dispatch(PaymentCardAction.PurchaseTrackingStarted)
        monitorPurchase(tracked)
    }

    private suspend fun monitorPurchase(initial: PaymentCardTrackedPurchase) {
        var tracked = initial
        while (tracked.poll.hasNext) {
            delay(tracked.poll.intervalMillis)
            when (val progress = getPurchaseProgress(tracked.poll.purchaseId)) {
                is AppResult.Failure -> return fail(progress.error)

                is AppResult.Success -> {
                    progress.value.redirect?.let {
                        purchase.trackedPurchase = tracked.copy(poll = tracked.poll.copy(purchaseId = progress.value.purchaseId))
                        return redirects.requireRedirect(it, progress.value.purchaseId)
                    }
                    progress.value.notice?.let {
                        purchase.trackedPurchase = tracked.copy(poll = tracked.poll.copy(purchaseId = progress.value.purchaseId))
                        return notices.requireNotice(it)
                    }
                    val next = tracked.poll.after(progress.value) ?: return completePurchase(tracked.amount)
                    tracked = tracked.copy(poll = next)
                    purchase.trackedPurchase = tracked
                }
            }
        }
        completePurchase(tracked.amount)
    }

    private suspend fun completePurchase(amount: BigDecimal) {
        // Uma falha no histórico local não deve apresentar uma compra aceita como falha nem incentivar novo envio.
        recordRecentPurchase(amount)
        mutableState.dispatch(PaymentCardAction.PurchaseCompleted)
    }
}
