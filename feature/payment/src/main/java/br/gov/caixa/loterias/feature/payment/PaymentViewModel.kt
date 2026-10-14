package br.gov.caixa.loterias.feature.payment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.cart.GetCurrentCartUseCase
import br.gov.caixa.loterias.core.domain.cart.RecordRecentPurchaseUseCase
import br.gov.caixa.loterias.core.domain.payment.DeleteSavedCardUseCase
import br.gov.caixa.loterias.core.domain.payment.GeneratePixChargeUseCase
import br.gov.caixa.loterias.core.domain.payment.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.core.domain.payment.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.core.domain.payment.GetSavedCardsUseCase
import br.gov.caixa.loterias.core.domain.payment.PaymentMethod
import br.gov.caixa.loterias.core.domain.payment.PixCharge
import br.gov.caixa.loterias.core.domain.payment.PurchaseRedirect
import br.gov.caixa.loterias.core.domain.payment.SavedPaymentCard
import br.gov.caixa.loterias.core.domain.payment.ValidateDamUseCase
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PaymentMode { METHODS, CARDS, PIX }

data class PaymentUiState(
    val loading: Boolean = true,
    val methods: List<PaymentMethod> = emptyList(),
    val cards: Map<Long, List<SavedPaymentCard>> = emptyMap(),
    val cartTotal: BigDecimal = BigDecimal.ZERO,
    val showPrePurchaseConfirmation: Boolean = false,
    val pendingAddMethodId: Long? = null,
    val deleteCandidate: SavedPaymentCard? = null,
    val pix: PixCharge? = null,
    val pixApproved: Boolean = false,
    val pixRejected: Boolean = false,
    val checkingPix: Boolean = false,
    val damMessage: String? = null,
    val errorMessage: String? = null,
    val redirect: PurchaseRedirect? = null,
    val redirectPaused: Boolean = false,
    val notice: PurchaseNoticeUiState? = null,
    val registrationPending: Boolean = false,
    val pixRecoveryRequired: Boolean = false,
    val pixSessionMismatch: Boolean = false,
)

class PaymentViewModel(
    private val mode: PaymentMode,
    private val selectedPaymentMethodId: Long? = null,
    private val getMethods: GetPaymentMethodsUseCase,
    private val getCards: GetSavedCardsUseCase,
    private val deleteCard: DeleteSavedCardUseCase,
    private val generatePixCharge: GeneratePixChargeUseCase,
    private val getPurchaseProgress: GetPurchaseProgressUseCase,
    private val validateDam: ValidateDamUseCase,
    private val getCurrentCart: GetCurrentCartUseCase,
    private val recordRecentPurchase: RecordRecentPurchaseUseCase,
    private val clock: java.time.Clock,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val ownerKey: String? = null,
    private val savedStateHandle: SavedStateHandle? = null,
    autoLoad: Boolean = true,
    private val isSessionCurrent: () -> Boolean = { true },
) : ViewModel() {
    private val pixRestoration = if (mode == PaymentMode.PIX) savedStateHandle?.let { SavedPixRestoration(it, ownerKey) } else null
    private val mutableState = MutableStateFlow(pixRestoration?.restore() ?: PaymentUiState(loading = autoLoad))
    val state = mutableState.asStateFlow()
    private val generatingPix = AtomicBoolean(false)

    init { if (autoLoad) load() }

    fun load() = viewModelScope.launch(dispatchers.io) {
        if (!isSessionCurrent()) return@launch
        if (state.value.redirect != null || state.value.redirectPaused || state.value.registrationPending || state.value.notice != null) return@launch
        // A restored charge is reused; a persisted ambiguous POST requires purchase recovery.
        // Calls without a saved-state host retain the existing explicit retry behavior.
        if (mode == PaymentMode.PIX &&
            (state.value.pix != null || state.value.pixRecoveryRequired || state.value.pixSessionMismatch ||
                !generatingPix.compareAndSet(false, true))) return@launch
        try {
            mutableState.update { it.copy(loading = true, errorMessage = null) }
            when (mode) {
                PaymentMode.METHODS -> loadMethods(withCart = true)
                PaymentMode.CARDS -> loadCards()
                PaymentMode.PIX -> generatePix()
            }
        } finally {
            if (mode == PaymentMode.PIX) generatingPix.set(false)
        }
    }

    fun acceptPrePurchase() = viewModelScope.launch(dispatchers.io) {
        mutableState.update { it.copy(showPrePurchaseConfirmation = false, loading = true, errorMessage = null) }
        when (val result = validateDam()) {
            is AppResult.Failure -> applyFailure(result.error)
            is AppResult.Success -> mutableState.update {
                it.copy(
                    loading = false,
                    damMessage = result.value.message.takeIf { result.value.blocked },
                )
            }
        }
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
        mutableState.update { it.copy(notice = null, redirectPaused = moreBets, registrationPending = registration) }
        return true
    }
    fun consumeRedirect(expected: PurchaseRedirect): Boolean {
        if (state.value.redirect != expected) return false
        mutableState.update { it.copy(redirect = null, redirectPaused = true) }
        return true
    }
    fun resumeAfterRedirect() = mutableState.update { it.copy(redirectPaused = false) }
    fun dismissDamMessage() = mutableState.update { it.copy(damMessage = null) }
    fun dismissPixStatus() = mutableState.update { it.copy(pixRejected = false) }
    fun requestDelete(card: SavedPaymentCard) = mutableState.update { it.copy(deleteCandidate = card) }
    fun cancelDelete() = mutableState.update { it.copy(deleteCandidate = null) }
    fun requestAddCard(methodId: Long) = mutableState.update { it.copy(pendingAddMethodId = methodId) }
    fun cancelAddCard() = mutableState.update { it.copy(pendingAddMethodId = null) }

    fun confirmDelete() {
        val card = state.value.deleteCandidate ?: return
        viewModelScope.launch(dispatchers.io) {
            mutableState.update { it.copy(loading = true, deleteCandidate = null) }
            when (val result = deleteCard(card.deletionIdentifier, card.paymentMethodId)) {
                is AppResult.Failure -> applyFailure(result.error)
                is AppResult.Success -> mutableState.update {
                    it.copy(loading = false, cards = it.cards + (card.paymentMethodId to result.value))
                }
            }
        }
    }

    fun checkPixStatus() {
        val pix = state.value.pix ?: return
        if (state.value.checkingPix || state.value.pixApproved || state.value.redirect != null || state.value.redirectPaused || state.value.registrationPending || state.value.notice != null) return
        mutableState.update { it.copy(checkingPix = true, errorMessage = null) }
        viewModelScope.launch(dispatchers.io) {
            when (val result = getPurchaseProgress(pix.purchaseId)) {
                is AppResult.Failure -> applyFailure(result.error)
                is AppResult.Success -> {
                    result.value.redirect?.let { destination ->
                        mutableState.update { it.copy(checkingPix = false, pixRejected = false, redirect = destination) }
                        return@launch
                    }
                    result.value.notice?.let { notice ->
                        val presentation = purchaseNoticeUi(notice, java.time.LocalDateTime.now(clock))
                        mutableState.update { it.copy(checkingPix = false, pixRejected = false, notice = presentation) }
                        return@launch
                    }
                    // Merely generating a Pix code must not create a successful-purchase record.
                    if (result.value.completed) recordRecentPurchase(pix.amount)
                    mutableState.update {
                        it.copy(
                            checkingPix = false,
                            pixApproved = result.value.completed,
                            pixRejected = result.value.rejected,
                        )
                    }
                }
            }
        }
    }

    private suspend fun loadMethods(withCart: Boolean) {
        when (val methods = getMethods()) {
            is AppResult.Failure -> applyFailure(methods.error)
            is AppResult.Success -> {
                val cards = loadAvailableCards(methods.value)
                if (!withCart) {
                    mutableState.update { it.copy(loading = false, methods = methods.value, cards = cards) }
                    return
                }
                when (val cart = getCurrentCart()) {
                    is AppResult.Failure -> applyFailure(cart.error)
                    is AppResult.Success -> mutableState.update {
                        it.copy(
                            loading = false,
                            methods = methods.value,
                            cards = cards,
                            cartTotal = cart.value.total,
                            showPrePurchaseConfirmation = true,
                        )
                    }
                }
            }
        }
    }

    private suspend fun loadCards() {
        if (!isSessionCurrent()) return
        val methods = getMethods()
        if (!isSessionCurrent()) return
        when (methods) {
            is AppResult.Failure -> applyFailure(methods.error)
            is AppResult.Success -> {
                val cardMethods = methods.value.filter { method ->
                    method.isSupportedCard && (selectedPaymentMethodId == null || method.id == selectedPaymentMethodId)
                }
                if (cardMethods.isEmpty()) {
                    return applyFailure(AppError.Validation("Meio de pagamento indisponível"))
                }
                val loaded = linkedMapOf<Long, List<SavedPaymentCard>>()
                for (method in cardMethods) {
                    if (!isSessionCurrent()) return
                    val result = getCards(method.id)
                    if (!isSessionCurrent()) return
                    when (result) {
                        is AppResult.Failure -> applyFailure(result.error)
                        is AppResult.Success -> loaded[method.id] = result.value
                    }
                }
                if (state.value.errorMessage == null) {
                    mutableState.update { it.copy(loading = false, methods = cardMethods, cards = loaded) }
                }
            }
        }
    }

    private suspend fun loadAvailableCards(methods: List<PaymentMethod>): Map<Long, List<SavedPaymentCard>> =
        buildMap {
            methods.filter(PaymentMethod::isSupportedCard).forEach { method ->
                val result = getCards(method.id)
                put(method.id, (result as? AppResult.Success)?.value.orEmpty())
            }
        }

    private suspend fun generatePix() {
        mutableState.update { it.copy(loading = true, errorMessage = null, pixApproved = false, pixRejected = false) }
        pixRestoration?.recordAttempt()
        when (val result = generatePixCharge()) {
            is AppResult.Failure -> {
                // A failed response cannot prove that the purchase POST was not accepted.
                // Saved-state hosts recover through purchases instead of sending another POST.
                if (pixRestoration != null) mutableState.update { it.copy(pixRecoveryRequired = true) }
                applyFailure(result.error)
            }
            is AppResult.Success -> {
                pixRestoration?.recordCharge(result.value)
                mutableState.update { it.copy(loading = false, pix = result.value) }
            }
        }
    }

    private fun applyFailure(error: AppError) = mutableState.update {
        it.copy(loading = false, checkingPix = false, errorMessage = when (error) {
            AppError.Network -> "Não foi possível acessar o pagamento. Verifique sua internet."
            is AppError.Validation -> error.reason.ifBlank { "Não foi possível continuar o pagamento." }
            AppError.Unexpected -> "Ocorreu um erro inesperado no pagamento."
        })
    }
}
