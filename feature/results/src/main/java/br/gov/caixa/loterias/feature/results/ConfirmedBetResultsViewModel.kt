package br.gov.caixa.loterias.feature.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.*
import br.gov.caixa.loterias.core.domain.results.*
import br.gov.caixa.loterias.core.domain.service.*
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConfirmedBetResultsState(
    val results: List<DrawResult> = emptyList(),
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val serviceFailure: ServiceFailure? = null,
)

class ConfirmedBetResultsViewModel(
    private val bet: ConfirmedBet,
    private val getResults: GetConfirmedBetResultsUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = MutableStateFlow(ConfirmedBetResultsState())
    val state = mutableState.asStateFlow()
    init { retry() }
    fun retry() {
        // The screen represents one immutable bet. Deduplicate before scheduling on IO so
        // taps in the same frame cannot race a second result query.
        if (state.value.loading) return
        mutableState.value = ConfirmedBetResultsState(loading = true)
        viewModelScope.launch(dispatchers.io) {
        mutableState.value = when (val result = getResults(bet)) {
            is ServiceResult.Success -> ConfirmedBetResultsState(results = result.value)
            is ServiceResult.Rejected -> ConfirmedBetResultsState(serviceFailure = result.failure)
            is ServiceResult.Failed -> ConfirmedBetResultsState(errorMessage = when (val error = result.error) {
                is AppError.Validation -> error.reason
                AppError.Network -> "Não foi possível acessar o serviço. Verifique sua conexão."
                AppError.Unexpected -> "Não foi possível consultar o resultado. Tente novamente."
            })
        }
    }
    }
}
