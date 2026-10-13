package br.gov.caixa.loterias.feature.betting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.betting.GenerateRapidBetsUseCase
import br.gov.caixa.loterias.core.domain.cart.CartItem
import br.gov.caixa.loterias.core.domain.cart.RemoveCartItemUseCase
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import br.gov.caixa.loterias.core.domain.cart.ValidateCartUseCase
import br.gov.caixa.loterias.core.domain.cart.CartValidationResult
import br.gov.caixa.loterias.core.common.NetworkAvailability
import br.gov.caixa.loterias.core.common.ConnectionAvailability
import br.gov.caixa.loterias.core.presentation.ServiceFailurePresentation
import br.gov.caixa.loterias.core.presentation.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.ServiceRedirectEvent
import br.gov.caixa.loterias.core.presentation.serviceFailurePresentation
import java.time.Clock
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch

data class RapidListUiState(
    val loading: Boolean = true,
    val cart: ShoppingCart? = null,
    val readyForPayment: Boolean = false,
    val errorMessage: String? = null,
    val validationFailure: ServiceFailurePresentation = ServiceFailurePresentation(),
)

class RapidListViewModel(
    private val generateRapidBets: GenerateRapidBetsUseCase,
    private val removeCartItem: RemoveCartItemUseCase,
    private val validateCart: ValidateCartUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val networkAvailability: NetworkAvailability = NetworkAvailability { ConnectionAvailability.UNKNOWN },
    private val localClock: Clock = Clock.systemDefaultZone(),
) : ViewModel() {
    private val mutableState = MutableStateFlow(RapidListUiState())
    val state = mutableState.asStateFlow()

    init { generate() }

    fun generate(quantity: Int = 0) = viewModelScope.launch(dispatchers.io) {
        mutableState.update { it.copy(loading = true, errorMessage = null, readyForPayment = false) }
        when (val result = generateRapidBets(quantity)) {
            is AppResult.Success -> mutableState.update { it.copy(loading = false, cart = result.value) }
            is AppResult.Failure -> applyFailure(result.error)
        }
    }

    fun remove(item: CartItem) = viewModelScope.launch(dispatchers.io) {
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        when (val result = removeCartItem(item)) {
            is AppResult.Success -> mutableState.update { it.copy(loading = false, cart = result.value) }
            is AppResult.Failure -> applyFailure(result.error)
        }
    }

    fun advance() {
        if (state.value.loading || state.value.readyForPayment || state.value.validationFailure.pending) return
        if (state.value.cart?.items.isNullOrEmpty()) {
            mutableState.update { it.copy(errorMessage = "Seu carrinho está vazio.") }
            return
        }
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        viewModelScope.launch(dispatchers.io) {
            when (val result = validateCart()) {
                is AppResult.Success -> when (val validation = result.value) {
                    CartValidationResult.Accepted -> mutableState.update { it.copy(loading = false, readyForPayment = true) }
                    is CartValidationResult.Rejected -> mutableState.update { it.copy(loading = false, readyForPayment = false,
                        validationFailure = serviceFailurePresentation(validation.failure, networkAvailability, LocalDateTime.now(localClock))) }
                }
                is AppResult.Failure -> applyFailure(result.error)
            }
        }
    }

    fun dismissError() = mutableState.update { it.copy(errorMessage = null) }
    fun consumePaymentReady() = mutableState.update { it.copy(readyForPayment = false) }
    fun dismissValidationNotice(expected: ServiceNoticeUiState) = mutableState.update {
        it.copy(validationFailure = it.validationFailure.dismiss(expected))
    }
    fun acceptValidationNotice(expected: ServiceNoticeUiState) = mutableState.update {
        it.copy(validationFailure = it.validationFailure.accept(expected, networkAvailability))
    }
    fun consumeValidationRedirect(expected: ServiceRedirectEvent): Boolean {
        val previous = mutableState.getAndUpdate {
            if (it.validationFailure.redirect !== expected) it else it.copy(validationFailure = it.validationFailure.copy(redirect = null))
        }
        return previous.validationFailure.redirect === expected
    }

    private fun applyFailure(error: AppError) = mutableState.update {
        it.copy(loading = false, errorMessage = when (error) {
            AppError.Network -> "Não foi possível atualizar as apostas do Rapidão. Verifique sua internet."
            is AppError.Validation -> error.reason.ifBlank { "Não foi possível atualizar o Rapidão." }
            AppError.Unexpected -> "Ocorreu um erro inesperado no Rapidão."
        })
    }
}
