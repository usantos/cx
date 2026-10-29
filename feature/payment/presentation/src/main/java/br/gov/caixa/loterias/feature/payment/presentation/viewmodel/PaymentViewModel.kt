package br.gov.caixa.loterias.feature.payment.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.domain.usecase.DeleteSavedCardUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GeneratePixChargeUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase
import br.gov.caixa.loterias.feature.payment.presentation.coordinator.PaymentMethodsLoading
import br.gov.caixa.loterias.feature.payment.presentation.coordinator.PaymentNotices
import br.gov.caixa.loterias.feature.payment.presentation.coordinator.PaymentPixOperations
import br.gov.caixa.loterias.feature.payment.presentation.coordinator.PaymentPrePurchase
import br.gov.caixa.loterias.feature.payment.presentation.coordinator.PaymentRedirects
import br.gov.caixa.loterias.feature.payment.presentation.coordinator.PaymentSavedCards
import br.gov.caixa.loterias.feature.payment.presentation.coordinator.SavedPixRestoration
import br.gov.caixa.loterias.feature.payment.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PurchaseNoticeUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentMode
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentState
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.launch

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
    private val mutableState = reduxStateStore<PaymentUiState, PaymentAction>(
        pixRestoration?.restore() ?: PaymentUiState(loading = autoLoad),
        ::reducePaymentState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state
    private val generatingPix = AtomicBoolean(false)
    private val methods = PaymentMethodsLoading(mutableState, getMethods, getCards, getCurrentCart, ::applyFailure)
    private val cards = PaymentSavedCards(
        mutableState, viewModelScope, dispatchers, selectedPaymentMethodId,
        getMethods, getCards, deleteCard, isSessionCurrent, ::applyFailure
    )
    private val prePurchase = PaymentPrePurchase(mutableState, viewModelScope, dispatchers, validateDam, ::applyFailure)
    private val pix = PaymentPixOperations(
        mutableState, viewModelScope, dispatchers, pixRestoration,
        generatePixCharge, getPurchaseProgress, recordRecentPurchase, clock, ::applyFailure
    )
    private val notices = PaymentNotices(mutableState)
    private val redirects = PaymentRedirects(mutableState)

    init {
        if (autoLoad) load()
    }

    fun load() = viewModelScope.launch(dispatchers.io) {
        if (!isSessionCurrent()) return@launch
        if (state.value.redirect != null || state.value.redirectPaused || state.value.registrationPending ||
            state.value.notice != null
        ) {
            return@launch
        }
        // Uma cobrança restaurada é reutilizada; um POST ambíguo persistido exige recuperação da compra.
        // Chamadas sem hospedeiro de estado salvo preservam o comportamento existente de repetição explícita.
        if (mode == PaymentMode.PIX &&
            (
                state.value.pix != null || state.value.pixRecoveryRequired || state.value.pixSessionMismatch ||
                    !generatingPix.compareAndSet(false, true)
                )
        ) {
            return@launch
        }
        try {
            mutableState.dispatch(PaymentAction.LoadingStarted)
            when (mode) {
                PaymentMode.METHODS -> methods.loadMethods(withCart = true)
                PaymentMode.CARDS -> cards.loadCards()
                PaymentMode.PIX -> pix.generatePix()
            }
        } finally {
            if (mode == PaymentMode.PIX) generatingPix.set(false)
        }
    }

    fun acceptPrePurchase() = prePurchase.acceptPrePurchase()

    fun dismissError() = mutableState.dispatch(PaymentAction.ErrorDismissed)

    fun consumeNotice(expected: PurchaseNoticeUiState, moreBets: Boolean): Boolean = notices.consumeNotice(expected, moreBets)

    fun consumeRegistrationNotice(expected: PurchaseNoticeUiState): Boolean = notices.consumeRegistrationNotice(expected)

    fun registrationFinished() = notices.registrationFinished()

    fun consumeRedirect(expected: PurchaseRedirect): Boolean = redirects.consumeRedirect(expected)

    fun resumeAfterRedirect() = redirects.resumeAfterRedirect()

    fun dismissDamMessage() = mutableState.dispatch(PaymentAction.DamMessageDismissed)

    fun dismissPixStatus() = mutableState.dispatch(PaymentAction.PixStatusDismissed)

    fun requestDelete(card: SavedPaymentCard) = mutableState.dispatch(PaymentAction.CardDeletionRequested(card))

    fun cancelDelete() = mutableState.dispatch(PaymentAction.CardDeletionCancelled)

    fun requestAddCard(methodId: Long) = mutableState.dispatch(PaymentAction.CardAdditionRequested(methodId))

    fun cancelAddCard() = mutableState.dispatch(PaymentAction.CardAdditionCancelled)

    fun confirmDelete() = cards.confirmDelete()

    fun checkPixStatus() = pix.checkPixStatus()

    private fun applyFailure(error: AppError) = mutableState.dispatch(PaymentAction.Failed(error))
}
