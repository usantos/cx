package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketPeriod
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsBetFilterAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

internal class TicketsHistoryBetFilters(
    private val mutableState: ReduxStateStore<TicketsUiState, TicketsStateAction>,
    private val applyBetFilters: () -> Unit
) {
    fun openBetFilters() = mutableState.dispatch(TicketsBetFilterAction.Opened)
    fun closeBetFilters() = mutableState.dispatch(TicketsBetFilterAction.Closed)
    fun selectBetPeriod(value: TicketPeriod) = mutableState.dispatch(TicketsBetFilterAction.PeriodSelected(value))
    fun selectBetStatus(value: Long) = mutableState.dispatch(TicketsBetFilterAction.StatusSelected(value))
    fun selectBetOrdering(value: Long) = mutableState.dispatch(TicketsBetFilterAction.OrderingSelected(value))
    fun selectBetType(value: Long) = mutableState.dispatch(TicketsBetFilterAction.BetTypeSelected(value))
    fun selectContestType(value: Long) = mutableState.dispatch(TicketsBetFilterAction.ContestTypeSelected(value))
    fun selectModality(value: Int) = mutableState.dispatch(TicketsBetFilterAction.ModalitySelected(value))
    fun toggleSurprise() = mutableState.dispatch(TicketsBetFilterAction.SurpriseToggled)
    fun toggleRecurring() = mutableState.dispatch(TicketsBetFilterAction.RecurringToggled)
    fun toggleCombo() = mutableState.dispatch(TicketsBetFilterAction.ComboToggled)

    fun clearBetFilters() {
        // O período é selecionado fora deste diálogo na tela legada de histórico.
        mutableState.dispatch(TicketsBetFilterAction.Cleared)
        applyBetFilters()
    }
}
