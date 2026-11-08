package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText
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
    private val pagination = TicketsHistoryBetPagination(
        mutableState, scope, dispatchers, getConfirmedBets, isSessionCurrent, fail
    )

    fun loadBets() {
        if (!isSessionCurrent() || mutableState.value.isLoading) return
        val generation = pagination.beginQuery()
        mutableState.dispatch(TicketsStateAction.BetsLoading)
        scope.launch(dispatchers.io) {
            if (!pagination.isCurrent(generation)) return@launch
            val configurationResult = getBetsConfiguration()
            if (!pagination.isCurrent(generation)) return@launch
            val configuration = when (val result = configurationResult) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return@launch fail(messageText("nao_foi_possivel_carregar_os_fil_0b7129"))
            }
            val modalitiesResult = getModalities()
            if (!pagination.isCurrent(generation)) return@launch
            val modalities = when (val result = modalitiesResult) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return@launch fail(messageText("nao_foi_possivel_carregar_as_mod_bf4e8c"))
            }
            pagination.ifCurrent(generation) {
                mutableState.dispatch(TicketsStateAction.BetsConfigurationReceived(configuration, modalities))
            }
            pagination.loadBetsWith(mutableState.value.betFilters, generation)
        }
    }

    fun applyBetFilters() {
        if (!isSessionCurrent()) return
        val generation = pagination.beginQuery()
        mutableState.dispatch(TicketsStateAction.BetFiltersApplied)
        scope.launch(dispatchers.io) { pagination.loadBetsWith(mutableState.value.betFilters, generation) }
    }

    fun loadMoreBets() = pagination.loadMoreBets()
}
