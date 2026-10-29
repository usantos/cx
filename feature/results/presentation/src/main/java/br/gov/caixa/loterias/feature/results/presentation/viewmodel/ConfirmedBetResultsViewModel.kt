package br.gov.caixa.loterias.feature.results.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.results.domain.usecase.GetConfirmedBetResultsUseCase
import br.gov.caixa.loterias.feature.results.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.results.presentation.state.ConfirmedBetResultsAction
import br.gov.caixa.loterias.feature.results.presentation.state.ConfirmedBetResultsState
import br.gov.caixa.loterias.feature.results.presentation.state.reduceConfirmedBetResultsState
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import kotlinx.coroutines.launch

class ConfirmedBetResultsViewModel(
    private val bet: ConfirmedBet,
    private val getResults: GetConfirmedBetResultsUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = reduxStateStore<ConfirmedBetResultsState, ConfirmedBetResultsAction>(
        ConfirmedBetResultsState(),
        ::reduceConfirmedBetResultsState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state
    init {
        retry()
    }
    fun retry() {
        // A tela representa uma aposta imutável. Remove duplicações antes de agendar em IO para que
        // toques no mesmo quadro não disputem uma segunda consulta de resultado.
        if (state.value.loading) return
        mutableState.dispatch(ConfirmedBetResultsAction.LoadStarted)
        viewModelScope.launch(dispatchers.io) {
            mutableState.dispatch(
                when (val result = getResults(bet)) {
                    is ServiceResult.Success -> ConfirmedBetResultsAction.Loaded(result.value)

                    is ServiceResult.Rejected -> ConfirmedBetResultsAction.Rejected(result.failure)

                    is ServiceResult.Failed -> ConfirmedBetResultsAction.Failed(
                        when (val error = result.error) {
                            is AppError.Validation -> error.reason
                            AppError.Network -> messageText("nao_foi_possivel_acessar_o_servi_856170")
                            AppError.Unexpected -> messageText("nao_foi_possivel_consultar_o_res_465180")
                        }
                    )
                }
            )
        }
    }
}
