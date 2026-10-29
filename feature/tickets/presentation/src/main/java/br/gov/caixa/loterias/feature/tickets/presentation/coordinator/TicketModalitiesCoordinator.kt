package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class TicketModalitiesCoordinator(
    private val mutableState: ReduxStateStore<TicketsUiState, TicketsStateAction>,
    private val viewModelScope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val fail: (String) -> Unit,
    private val getModalities: GetLotteryModalitiesUseCase,
) {
    fun loadTicketModalities() {
        if (mutableState.value.isLoading || mutableState.value.ticketModalities.isNotEmpty()) return
        mutableState.dispatch(TicketsStateAction.TicketModalitiesLoading)
        viewModelScope.launch(dispatchers.io) {
            when (val result = getModalities()) {
                is AppResult.Failure -> fail(messageText("nao_foi_possivel_carregar_as_mod_bf4e8c"))
                is AppResult.Success -> mutableState.dispatch(TicketsStateAction.TicketModalitiesReceived(result.value))
            }
        }
    }

    fun toggleTicketModality(id: Int) {
        if (mutableState.value.ticketModalities.none { it.id == id }) return
        mutableState.dispatch(TicketsStateAction.TicketModalityToggled(id))
    }
}
