package br.gov.caixa.loterias.feature.payment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.domain.contract.cart.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.core.domain.contract.payment.model.PurchaseRedirect
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.SubmitCardPaymentUseCase
import br.gov.caixa.loterias.feature.payment.presentation.coordinator.PaymentCardFormCoordinator
import br.gov.caixa.loterias.feature.payment.presentation.coordinator.PaymentCardMonitoringCoordinator
import br.gov.caixa.loterias.feature.payment.presentation.coordinator.PaymentCardNoticeCoordinator
import br.gov.caixa.loterias.feature.payment.presentation.coordinator.PaymentCardRedirectCoordinator
import br.gov.caixa.loterias.feature.payment.presentation.coordinator.PaymentCardSavedCardsCoordinator
import br.gov.caixa.loterias.feature.payment.presentation.coordinator.PaymentCardSubmissionCoordinator
import br.gov.caixa.loterias.feature.payment.presentation.coordinator.PaymentCardSubmissionServices
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PurchaseNoticeUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardPurchaseContext
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentCardState
import java.time.Year

class PaymentCardViewModel(
    private val paymentMethodId: Long,
    private val savedCardId: String?,
    private val initialStage: PaymentCardStage = PaymentCardStage.FORM,
    private val getCards: GetSavedCardsUseCase,
    private val submitCardPayment: SubmitCardPaymentUseCase,
    private val getPurchaseProgress: GetPurchaseProgressUseCase,
    private val getCurrentCart: GetCurrentCartUseCase,
    private val recordRecentPurchase: RecordRecentPurchaseUseCase,
    private val clock: java.time.Clock,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    currentYear: Int = Year.now().value,
    private val canStartOperation: () -> Boolean = { true },
) : ViewModel() {
    private val mutableState = reduxStateStore<PaymentCardUiState, PaymentCardAction>(
        PaymentCardUiState(paymentMethodId = paymentMethodId, minimumExpirationYear = currentYear),
        ::reducePaymentCardState
    ).also { addCloseable(it) }
    val state = mutableState.state

    private val purchase = PaymentCardPurchaseContext()
    private val form = PaymentCardFormCoordinator(mutableState)
    private val notices = PaymentCardNoticeCoordinator(mutableState, purchase, clock)
    private val redirects = PaymentCardRedirectCoordinator(mutableState, purchase)
    private val savedCards = PaymentCardSavedCardsCoordinator(
        mutableState, viewModelScope, dispatchers, paymentMethodId, savedCardId, initialStage, getCards, purchase, ::fail
    )
    private val monitoring = PaymentCardMonitoringCoordinator(
        mutableState, viewModelScope, dispatchers, purchase, getPurchaseProgress, recordRecentPurchase, notices, redirects, ::fail
    )
    private val submission = PaymentCardSubmissionCoordinator(
        mutableState, viewModelScope, dispatchers, purchase, paymentMethodId,
        PaymentCardSubmissionServices(getCurrentCart, submitCardPayment, canStartOperation), monitoring, notices, redirects, ::fail
    )

    init {
        load()
    }

    /** Identificador recebido do backend; nunca contém dados do cartão ou o código de segurança. */
    val recoveryPurchaseId: Long?
        get() = purchase.trackedPurchase?.poll?.purchaseId?.takeIf { it > 0 }
            ?: state.value.purchaseReview?.purchaseId?.takeIf { it > 0 }
            ?: state.value.redirectPurchaseId?.takeIf { it > 0 }

    fun load(): kotlinx.coroutines.Job = if (canStartOperation()) {
        savedCards.load()
    } else {
        kotlinx.coroutines.Job().apply { complete() }
    }

    fun changeNumber(value: String) = form.changeNumber(value)
    fun changeHolderName(value: String) = form.changeHolderName(value)
    fun changeMonth(value: String) = form.changeMonth(value)
    fun changeYear(value: String) = form.changeYear(value)
    fun toggleSave() = form.toggleSave()
    fun continueFromCard() = form.continueFromCard()
    fun changeCvv(value: String) = form.changeCvv(value)
    fun continueFromCvv() = form.continueFromCvv()
    fun dismissError() = mutableState.dispatch(PaymentCardAction.ErrorDismissed)

    fun consumeNotice(expected: PurchaseNoticeUiState, moreBets: Boolean): Boolean = notices.consumeNotice(expected, moreBets)
    fun consumeRegistrationNotice(expected: PurchaseNoticeUiState): Boolean = notices.consumeRegistrationNotice(expected)

    fun registrationFinished() = notices.registrationFinished()

    /** Consumido pela ação legada Entendi antes de remover esta rota da pilha de navegação. */
    fun acknowledgePurchaseReview(): Boolean = redirects.acknowledgePurchaseReview()

    /** Despacha uma vez. Retornar de outra tela nunca dispara automaticamente um POST de cartão. */
    fun consumeRedirect(expected: PurchaseRedirect): Boolean = redirects.consumeRedirect(expected)
    fun resumeAfterRedirect() = redirects.resumeAfterRedirect()
    fun retry() = monitoring.retry()

    /** Reconhece a conclusão antes de entregar a navegação ao hospedeiro. */
    fun consumeSuccessNavigation(): Boolean = synchronized(mutableState) {
        val state = mutableState.value
        if (state.stage != PaymentCardStage.SUCCESS || state.successNavigationConsumed) return false
        mutableState.dispatch(PaymentCardAction.SuccessNavigationConsumed)
        true
    }

    fun submit() {
        if (canStartOperation()) submission.submit()
    }

    private fun fail(error: AppError) = mutableState.dispatch(PaymentCardAction.Failed(error))
}
