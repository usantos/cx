package br.gov.caixa.loterias.feature.betting.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.ServiceRedirectEvent
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.core.presentation.state.serviceFailurePresentation
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSession
import br.gov.caixa.loterias.feature.betting.domain.usecase.GenerateRapidBetsUseCase
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.mapper.rapidServiceFailurePresentation
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingMutationBarrier
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingRecoveryMessage
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingRecoveryMessages
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidListAction
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidListUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceRapidListState
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import java.time.Clock
import java.time.LocalDateTime
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
class RapidListViewModel(
    private val generateRapidBets: GenerateRapidBetsUseCase,
    private val removeCartItem: RemoveCartItemUseCase,
    private val validateCart: ValidateCartUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val networkAvailability: NetworkAvailability = NetworkAvailability { ConnectionAvailability.UNKNOWN },
    private val localClock: Clock = Clock.systemDefaultZone(),
    autoLoad: Boolean = true,
    private val isSessionCurrent: () -> Boolean = { true },
    private val recoveryMessages: BettingRecoveryMessages = BettingRecoveryMessages.Identifiers,
    private val checkpointSession: BettingCheckpointSession? = null,
    private val recoveryMode: Boolean = false,
    private val getCurrentCart: GetCurrentCartUseCase? = null,
) : ViewModel() {
    private val mutableState = reduxStateStore<RapidListUiState, RapidListAction>(
        RapidListUiState(loading = autoLoad),
        ::reduceRapidListState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state

    private val mutation = BettingMutationBarrier(checkpointSession)
    init {
        if (recoveryMode) {
            reconcile()
        } else if (autoLoad) {
            generate()
        }
    }

    private fun reconcile() = viewModelScope.launch(dispatchers.io) {
        if (!isSessionCurrent() || checkpointSession?.isCurrent() == false) return@launch
        val read = getCurrentCart ?: return@launch
        when (val result = read()) {
            is AppResult.Success -> if (isSessionCurrent() && checkpointSession?.isCurrent() != false) {
                mutableState.dispatch(RapidListAction.CartReloaded(result.value))
            }

            is AppResult.Failure -> if (isSessionCurrent()) applyFailure(result.error)
        }
    }

    private suspend fun <T> guarded(kind: String, confirmed: (T) -> Boolean, block: suspend () -> T): T? = try {
        mutation.run(kind, confirmed, block)
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: Exception) {
        if (isSessionCurrent() && checkpointSession?.isCurrent() != false) {
            mutableState.dispatch(RapidListAction.RecoveryRequired(recoveryMessages.text(BettingRecoveryMessage.RECONCILE_REQUIRED)))
        }
        null
    }

    fun generate(quantity: Int = 0) = viewModelScope.launch(dispatchers.io) {
        if (!isSessionCurrent()) return@launch
        mutableState.dispatch(RapidListAction.CartOperationStarted)
        val result =
            guarded("GENERATE_RAPID", { it: ServiceResult<ShoppingCart> ->
                it is ServiceResult.Success
            }) {
                generateRapidBets(quantity)
            } ?: return@launch
        if (!isSessionCurrent()) return@launch
        when (result) {
            is ServiceResult.Success -> mutableState.dispatch(RapidListAction.CartLoaded(result.value))

            is ServiceResult.Failed -> applyFailure(result.error)

            is ServiceResult.Rejected -> mutableState.dispatch(
                RapidListAction.ServiceFailureReceived(
                    rapidServiceFailurePresentation(result.failure, networkAvailability, LocalDateTime.now(localClock))
                )
            )
        }
    }

    fun remove(item: CartItem) = viewModelScope.launch(dispatchers.io) {
        if (!isSessionCurrent()) return@launch
        mutableState.dispatch(RapidListAction.LoadingStarted)
        val result =
            guarded("REMOVE_CART", { it: AppResult<ShoppingCart> ->
                it is AppResult.Success
            }) {
                removeCartItem(item)
            } ?: return@launch
        if (!isSessionCurrent()) return@launch
        when (result) {
            is AppResult.Success -> mutableState.dispatch(RapidListAction.CartLoaded(result.value))
            is AppResult.Failure -> applyFailure(result.error)
        }
    }

    fun advance() {
        synchronized(mutableState) {
            if (!isSessionCurrent()) return
            if (state.value.loading || state.value.readyForPayment || state.value.validationFailure.pending) return
            if (state.value.cart?.items.isNullOrEmpty()) {
                mutableState.dispatch(RapidListAction.EmptyCartRejected())
                return
            }
            mutableState.dispatch(RapidListAction.LoadingStarted)
            viewModelScope.launch(dispatchers.io) {
                if (!isSessionCurrent()) return@launch
                val result = validateCart()
                if (!isSessionCurrent()) return@launch
                when (result) {
                    is AppResult.Success -> when (val validation = result.value) {
                        CartValidationResult.Accepted -> mutableState.dispatch(RapidListAction.PaymentReady)

                        is CartValidationResult.Rejected -> mutableState.dispatch(
                            RapidListAction.ValidationRejected(
                                serviceFailurePresentation(validation.failure, networkAvailability, LocalDateTime.now(localClock))
                            )
                        )
                    }

                    is AppResult.Failure -> applyFailure(result.error)
                }
            }
        }
    }

    fun dismissServiceNotice(expected: ServiceNoticeUiState) = mutableState.dispatch(RapidListAction.ServiceNoticeDismissed(expected))
    fun acceptServiceNotice(expected: ServiceNoticeUiState) = mutableState.dispatch(
        RapidListAction.ServiceNoticeAccepted(expected, mutableState.value.serviceFailure.accept(expected, networkAvailability))
    )
    fun consumeServiceRedirect(expected: ServiceRedirectEvent): Boolean {
        synchronized(mutableState) {
            if (mutableState.value.serviceFailure.redirect !== expected) return false
            mutableState.dispatch(RapidListAction.ServiceRedirectConsumed(expected))
            return true
        }
    }

    fun dismissError() = mutableState.dispatch(RapidListAction.ErrorDismissed)
    fun consumePaymentReady() = mutableState.dispatch(RapidListAction.PaymentReadyConsumed)
    fun dismissValidationNotice(expected: ServiceNoticeUiState) = mutableState.dispatch(RapidListAction.ValidationNoticeDismissed(expected))
    fun acceptValidationNotice(expected: ServiceNoticeUiState) = mutableState.dispatch(
        RapidListAction.ValidationNoticeAccepted(expected, mutableState.value.validationFailure.accept(expected, networkAvailability))
    )
    fun consumeValidationRedirect(expected: ServiceRedirectEvent): Boolean {
        synchronized(mutableState) {
            if (mutableState.value.validationFailure.redirect !== expected) return false
            mutableState.dispatch(RapidListAction.ValidationRedirectConsumed(expected))
            return true
        }
    }

    private fun applyFailure(error: AppError) = mutableState.dispatch(RapidListAction.Failed(error))
}
