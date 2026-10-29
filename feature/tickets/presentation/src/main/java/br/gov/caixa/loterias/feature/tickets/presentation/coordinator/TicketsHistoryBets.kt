package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetFilters
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class TicketsHistoryBets(
    private val mutableState: ReduxStateStore<TicketsUiState, TicketsStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getConfirmedBets: GetConfirmedBetsUseCase,
    private val getBetsConfiguration: GetConfirmedBetsConfigurationUseCase,
    private val getModalities: GetLotteryModalitiesUseCase,
    private val isSessionCurrent: () -> Boolean,
    private val fail: (String) -> Unit,
) {
    fun loadBets() {
        if (!isSessionCurrent()) return
        if (mutableState.value.isLoading) return
        mutableState.dispatch(TicketsStateAction.BetsLoading)
        scope.launch(dispatchers.io) {
            if (!isSessionCurrent()) return@launch
            val configurationResult = getBetsConfiguration()
            if (!isSessionCurrent()) return@launch
            val configuration = when (val result = configurationResult) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return@launch fail(messageText("nao_foi_possivel_carregar_os_fil_0b7129"))
            }
            val modalitiesResult = getModalities()
            if (!isSessionCurrent()) return@launch
            val modalities = when (val result = modalitiesResult) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return@launch fail(messageText("nao_foi_possivel_carregar_as_mod_bf4e8c"))
            }
            mutableState.dispatch(TicketsStateAction.BetsConfigurationReceived(configuration, modalities))
            loadBetsWith(mutableState.value.betFilters)
        }
    }

    fun applyBetFilters() {
        if (mutableState.value.isLoading) return
        mutableState.dispatch(TicketsStateAction.BetFiltersApplied)
        scope.launch(dispatchers.io) { loadBetsWith(mutableState.value.betFilters) }
    }

    private suspend fun loadBetsWith(filters: BetFilters) {
        if (!isSessionCurrent()) return
        val result = getConfirmedBets.page(filters.query())
        if (!isSessionCurrent()) return
        when (result) {
            is AppResult.Success -> mutableState.dispatch(TicketsStateAction.BetsReceived(result.value))
            is AppResult.Failure -> fail(messageText("nao_foi_possivel_carregar_os_dad_bf517e"))
        }
    }
}
