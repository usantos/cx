package br.gov.caixa.loterias.feature.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.cart.GetCurrentCartUseCase
import br.gov.caixa.loterias.core.domain.cart.RecordRecentPurchaseUseCase
import java.math.BigDecimal
import br.gov.caixa.loterias.core.domain.payment.CardPaymentRequest
import br.gov.caixa.loterias.core.domain.payment.CardSubmissionOutcome
import br.gov.caixa.loterias.core.domain.payment.PurchaseRedirect
import br.gov.caixa.loterias.core.domain.payment.PurchaseNotice
import br.gov.caixa.loterias.core.domain.payment.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.core.domain.payment.GetSavedCardsUseCase
import br.gov.caixa.loterias.core.domain.payment.PaymentCardDraft
import br.gov.caixa.loterias.core.domain.payment.PaymentCardSelection
import br.gov.caixa.loterias.core.domain.payment.SavedPaymentCard
import br.gov.caixa.loterias.core.domain.payment.SubmitCardPaymentUseCase
import br.gov.caixa.loterias.core.domain.payment.PurchaseSubmission
import br.gov.caixa.loterias.core.domain.payment.CardPurchasePolling
import br.gov.caixa.loterias.core.domain.payment.cardBrand
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Year

enum class PaymentCardStage { LOADING, FORM, PREVIEW, CVV, CONFIRM, PROCESSING, FAILURE, SUCCESS, REVIEW_REQUIRED, REDIRECT_REQUIRED, NOTICE_REQUIRED }

data class PaymentCardUiState(
    val paymentMethodId: Long = 0,
    val minimumExpirationYear: Int = 2000,
    val stage: PaymentCardStage = PaymentCardStage.LOADING,
    val savedCard: SavedPaymentCard? = null,
    val number: String = "",
    val holderName: String = "",
    val month: String = "",
    val year: String = "",
    val save: Boolean = false,
    val cvv: String = "",
    val errorMessage: String? = null,
    val trackingPurchase: Boolean = false,
    val purchaseReview: CardSubmissionOutcome.ReviewRequired? = null,
    val redirect: PurchaseRedirect? = null,
    val redirectPurchaseId: Long? = null,
    val redirectPaused: Boolean = false,
    val notice: PurchaseNoticeUiState? = null,
    val registrationPending: Boolean = false,
) {
    val normalizedNumber: String get() = number.filter(Char::isDigit)
    val brand: String get() = savedCard?.brand?.uppercase().orEmpty().ifBlank {
        if (normalizedNumber.length >= 14) cardBrand(normalizedNumber) else ""
    }
    val lastDigits: String get() = savedCard?.lastDigits ?: normalizedNumber.takeLast(4)
    val cvvLength: Int get() = savedCard?.securityCodeLength ?: if (brand == "AMERICANEXPRESS") 4 else 3
    val providerName: String get() = if (paymentMethodId == 5L) "RecargaPay" else "Mercado Pago"
    val acceptedBrands: List<String> get() = if (paymentMethodId == 5L) {
        listOf("ELO", "MASTERCARD", "VISA")
    } else {
        listOf("AMERICANEXPRESS", "ELO", "HIPERCARD", "MASTERCARD", "VISA")
    }
    val expirationYears: List<Int> get() = (minimumExpirationYear..minimumExpirationYear + 20).toList()
    val formValid: Boolean get() =
        normalizedNumber.length in 14..16 &&
            holderName.isNotBlank() &&
            month.toIntOrNull() in 1..12 &&
            year.toIntOrNull() in expirationYears
}

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
) : ViewModel() {
    private val mutableState = MutableStateFlow(
        PaymentCardUiState(paymentMethodId = paymentMethodId, minimumExpirationYear = currentYear),
    )
    val state = mutableState.asStateFlow()

    private data class TrackedPurchase(val poll: CardPurchasePolling, val amount: BigDecimal)
    private var trackedPurchase: TrackedPurchase? = null
    private var reviewHandled = false

    init { load() }

    fun load() = viewModelScope.launch(dispatchers.io) {
        if (state.value.stage == PaymentCardStage.PROCESSING || state.value.redirect != null || state.value.redirectPaused || state.value.registrationPending || state.value.notice != null || state.value.purchaseReview != null || trackedPurchase != null) return@launch
        if (savedCardId.isNullOrBlank()) {
            if (initialStage == PaymentCardStage.FORM) {
                mutableState.update { it.copy(stage = PaymentCardStage.FORM, errorMessage = null) }
                return@launch
            }
            loadFirstSavedCardForLegacyEntryPoint()
            return@launch
        }
        when (val result = getCards(paymentMethodId)) {
            is AppResult.Failure -> fail(result.error)
            is AppResult.Success -> {
                val card = result.value.firstOrNull { it.id == savedCardId }
                if (card == null) fail(AppError.Validation("Cartão salvo não encontrado"))
                else mutableState.update { it.copy(stage = PaymentCardStage.PREVIEW, savedCard = card, errorMessage = null) }
            }
        }
    }

    /**
     * Some legacy activities were independently addressable even though they are now states of
     * one Compose flow. Their compatibility routes restore the first real saved card instead of
     * rendering local example card data.
     */
    private suspend fun loadFirstSavedCardForLegacyEntryPoint() {
        when (val result = getCards(paymentMethodId)) {
            is AppResult.Failure -> fail(result.error)
            is AppResult.Success -> {
                val card = result.value.firstOrNull()
                if (card == null) {
                    mutableState.update {
                        it.copy(
                            stage = PaymentCardStage.FORM,
                            errorMessage = "Cadastre um cartão para continuar.",
                        )
                    }
                } else {
                    mutableState.update {
                        it.copy(stage = initialStage, savedCard = card, errorMessage = null)
                    }
                }
            }
        }
    }

    fun changeNumber(value: String) = mutableState.update { it.copy(number = value.filter(Char::isDigit).take(16), cvv = "") }
    fun changeHolderName(value: String) = mutableState.update { it.copy(holderName = value.uppercase().take(40)) }
    fun changeMonth(value: String) = mutableState.update { it.copy(month = value.filter(Char::isDigit).take(2)) }
    fun changeYear(value: String) = mutableState.update { it.copy(year = value.filter(Char::isDigit).take(4)) }
    fun toggleSave() = mutableState.update { it.copy(save = !it.save) }
    fun continueFromCard() {
        if (state.value.stage !in setOf(PaymentCardStage.FORM, PaymentCardStage.PREVIEW)) return
        if (state.value.savedCard != null || state.value.formValid) mutableState.update { it.copy(stage = PaymentCardStage.CVV, errorMessage = null) }
        else mutableState.update { it.copy(errorMessage = "Preencha corretamente os dados do cartão.") }
    }
    fun changeCvv(value: String) = mutableState.update { current ->
        current.copy(cvv = value.filter(Char::isDigit).take(current.cvvLength))
    }
    fun continueFromCvv() {
        if (state.value.stage != PaymentCardStage.CVV) return
        if (state.value.cvv.length == state.value.cvvLength) mutableState.update { it.copy(stage = PaymentCardStage.CONFIRM, errorMessage = null) }
        else mutableState.update { it.copy(errorMessage = "Código de segurança inválido") }
    }
    fun dismissError() = mutableState.update { it.copy(errorMessage = null) }

    fun consumeNotice(expected: PurchaseNoticeUiState, moreBets: Boolean): Boolean {
        if (moreBets && !expected.offerMoreBets) return false
        return completeNotice(expected, moreBets, false)
    }
    fun consumeRegistrationNotice(expected: PurchaseNoticeUiState): Boolean =
        expected.offerRegistration && completeNotice(expected, false, true)

    fun registrationFinished() = mutableState.update { it.copy(registrationPending = false) }

    private fun completeNotice(expected: PurchaseNoticeUiState, moreBets: Boolean, registration: Boolean): Boolean {
        if (state.value.notice !== expected) return false
        mutableState.update {
            it.copy(
                notice = null,
                stage = if (trackedPurchase == null) PaymentCardStage.CONFIRM else PaymentCardStage.FAILURE,
                errorMessage = if (trackedPurchase != null) "Não foi possível consultar sua compra." else null,
                redirectPaused = moreBets,
                registrationPending = registration,
            )
        }
        return true
    }

    /** Consumed by the legacy Entendi action before removing this route from the back stack. */
    fun acknowledgePurchaseReview(): Boolean {
        if (state.value.stage != PaymentCardStage.REVIEW_REQUIRED || state.value.redirectPaused || reviewHandled) return false
        reviewHandled = true
        return true
    }

    /** Dispatch once. Returning from another screen never triggers a card POST automatically. */
    fun consumeRedirect(expected: PurchaseRedirect): Boolean {
        val current = state.value
        if (current.redirect != expected) return false
        mutableState.update {
            when {
                trackedPurchase != null -> it.copy(stage = PaymentCardStage.FAILURE, redirect = null, redirectPurchaseId = null, redirectPaused = true)
                current.redirectPurchaseId != null -> it.copy(
                    stage = PaymentCardStage.REVIEW_REQUIRED, redirect = null, redirectPurchaseId = null, redirectPaused = true,
                    purchaseReview = CardSubmissionOutcome.ReviewRequired(current.redirectPurchaseId), cvv = "",
                )
                else -> it.copy(stage = PaymentCardStage.CONFIRM, redirect = null, redirectPurchaseId = null, redirectPaused = true)
            }
        }
        return true
    }
    fun resumeAfterRedirect() = mutableState.update { it.copy(redirectPaused = false) }
    fun retry() {
        if (state.value.stage != PaymentCardStage.FAILURE || state.value.redirectPaused || state.value.registrationPending) return
        val tracked = trackedPurchase
        if (tracked == null) {
            mutableState.update { it.copy(stage = PaymentCardStage.CONFIRM, errorMessage = null) }
        } else {
            mutableState.update { it.copy(stage = PaymentCardStage.PROCESSING, errorMessage = null) }
            viewModelScope.launch(dispatchers.io) { monitorPurchase(tracked) }
        }
    }

    fun submit() {
        if (state.value.stage != PaymentCardStage.CONFIRM || state.value.redirectPaused || state.value.registrationPending || state.value.purchaseReview != null || trackedPurchase != null) return
        val current = state.value
        mutableState.update { it.copy(stage = PaymentCardStage.PROCESSING, errorMessage = null) }
        viewModelScope.launch(dispatchers.io) {
            val amount = when (val cart = getCurrentCart()) {
                is AppResult.Failure -> { fail(cart.error); return@launch }
                is AppResult.Success -> cart.value.total
            }
            if (amount.signum() <= 0) {
                fail(AppError.Validation("Valor do carrinho inválido"))
                return@launch
            }
            val selection = current.savedCard?.let(PaymentCardSelection::Saved) ?: PaymentCardSelection.New(
                PaymentCardDraft(
                    number = current.normalizedNumber,
                    holderName = current.holderName,
                    expirationMonth = current.month.toIntOrNull() ?: 0,
                    expirationYear = current.year.toIntOrNull() ?: 0,
                    save = current.save,
                ),
            )
            when (val result = submitCardPayment(CardPaymentRequest(paymentMethodId, selection, current.cvv))) {
                is AppResult.Failure -> fail(result.error)
                is AppResult.Success -> when (val outcome = result.value) {
                    is CardSubmissionOutcome.Submitted -> beginMonitoring(outcome.purchase, amount)
                    is CardSubmissionOutcome.ReviewRequired -> requirePurchaseReview(outcome)
                    is CardSubmissionOutcome.RedirectRequired -> requireRedirect(outcome.destination, outcome.purchaseId)
                    is CardSubmissionOutcome.NoticeRequired -> requireNotice(outcome.notice)
                }
            }
        }
    }

    private suspend fun beginMonitoring(submission: PurchaseSubmission, amount: BigDecimal) {
        if (submission.purchaseId <= 0) return requirePurchaseReview(CardSubmissionOutcome.ReviewRequired())
        val tracked = TrackedPurchase(CardPurchasePolling(
            submission.purchaseId, submission.pollingIntervalMillis.coerceAtLeast(0),
            submission.pollingAttempts.coerceAtLeast(0),
        ), amount)
        trackedPurchase = tracked
        mutableState.update { it.copy(trackingPurchase = true, cvv = "") }
        monitorPurchase(tracked)
    }

    private fun requirePurchaseReview(outcome: CardSubmissionOutcome.ReviewRequired) = mutableState.update {
        it.copy(stage = PaymentCardStage.REVIEW_REQUIRED, purchaseReview = outcome, cvv = "", errorMessage = null)
    }

    private fun requireRedirect(destination: PurchaseRedirect, purchaseId: Long?) = mutableState.update {
        it.copy(stage = PaymentCardStage.REDIRECT_REQUIRED, redirect = destination, redirectPurchaseId = purchaseId, errorMessage = null)
    }

    private fun requireNotice(notice: PurchaseNotice) {
        val presentation = purchaseNoticeUi(notice, java.time.LocalDateTime.now(clock))
        mutableState.update { it.copy(stage = PaymentCardStage.NOTICE_REQUIRED, notice = presentation, errorMessage = null) }
    }

    private suspend fun monitorPurchase(initial: TrackedPurchase) {
        var tracked = initial
        while (tracked.poll.hasNext) {
            delay(tracked.poll.intervalMillis)
            when (val progress = getPurchaseProgress(tracked.poll.purchaseId)) {
                is AppResult.Failure -> return fail(progress.error)
                is AppResult.Success -> {
                    progress.value.redirect?.let {
                        trackedPurchase = tracked.copy(poll = tracked.poll.copy(purchaseId = progress.value.purchaseId))
                        return requireRedirect(it, progress.value.purchaseId)
                    }
                    progress.value.notice?.let {
                        trackedPurchase = tracked.copy(poll = tracked.poll.copy(purchaseId = progress.value.purchaseId))
                        return requireNotice(it)
                    }
                    val next = tracked.poll.after(progress.value) ?: return completePurchase(tracked.amount)
                    tracked = tracked.copy(poll = next)
                    trackedPurchase = tracked
                }
            }
        }
        completePurchase(tracked.amount)
    }

    private suspend fun completePurchase(amount: BigDecimal) {
        // A local-history failure must not present an accepted purchase as failed or invite resubmission.
        recordRecentPurchase(amount)
        mutableState.update { it.copy(stage = PaymentCardStage.SUCCESS) }
    }

    private fun fail(error: AppError) = mutableState.update {
        it.copy(
            stage = PaymentCardStage.FAILURE,
            errorMessage = when (error) {
                AppError.Network -> if (it.trackingPurchase) "Não foi possível consultar sua compra. Verifique sua internet."
                    else "Não foi possível processar o pagamento. Verifique sua internet."
                is AppError.Validation -> error.reason.ifBlank { "Não foi possível processar o pagamento." }
                AppError.Unexpected -> if (it.trackingPurchase) "Não foi possível consultar sua compra."
                    else "Ocorreu um erro inesperado ao processar o pagamento."
            },
        )
    }
}
