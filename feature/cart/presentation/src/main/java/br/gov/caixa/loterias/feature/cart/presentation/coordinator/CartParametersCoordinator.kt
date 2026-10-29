package br.gov.caixa.loterias.feature.cart.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.cart.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartParametersAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

internal class CartParametersCoordinator(
    private val mutableState: ReduxStateStore<CartUiState, CartAction>,
    private val viewModelScope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getBettingParameters: GetBettingParametersUseCase?,
    private val reservationSessionCurrent: () -> Boolean,
    private val invalidateReservationSession: () -> Unit,
) {
    private companion object {
        val PARAMETER_RETRY_FAILURE get() = messageText("nao_foi_possivel_concluir_esta_o_3c7db3")
    }

    fun retryParameters() = loadParameters()

    fun dismissParameterRetryMessage() = mutableState.dispatch(CartParametersAction.RetryMessageDismissed)

    fun dismissParametersUnavailable() {
        if (!mutableState.value.loadingParameters) mutableState.dispatch(CartParametersAction.UnavailableDismissed)
    }

    fun loadParameters() {
        val getParameters = getBettingParameters ?: return
        if (mutableState.value.loadingParameters) return
        val recovering = mutableState.value.parametersUnavailable
        mutableState.dispatch(CartParametersAction.LoadStarted)
        viewModelScope.launch(dispatchers.io) {
            try {
                val result = getParameters()
                coroutineContext.ensureActive()
                if (!reservationSessionCurrent()) {
                    invalidateReservationSession()
                    return@launch
                }
                when (result) {
                    is AppResult.Success -> mutableState.dispatch(
                        if (result.value.games.isEmpty()) {
                            CartParametersAction.Unavailable(if (recovering) PARAMETER_RETRY_FAILURE else null)
                        } else {
                            CartParametersAction.Loaded(result.value.minimumCartAmount, result.value.maximumCartBets)
                        }
                    )

                    // O carrinho legado mantém seu conteúdo atual quando ocorre a falha inicial de transporte.
                    is AppResult.Failure -> if (recovering) mutableState.dispatch(CartParametersAction.RetryFailed(PARAMETER_RETRY_FAILURE))
                }
            } finally {
                mutableState.dispatch(CartParametersAction.LoadFinished)
            }
        }
    }
}
