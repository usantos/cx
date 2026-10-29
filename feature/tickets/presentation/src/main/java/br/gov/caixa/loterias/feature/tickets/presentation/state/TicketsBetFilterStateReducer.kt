package br.gov.caixa.loterias.feature.tickets.presentation.state

import br.gov.caixa.loterias.feature.tickets.domain.model.TicketPeriod

internal sealed interface TicketsBetFilterAction : TicketsStateAction {
    data object Opened : TicketsBetFilterAction
    data object Closed : TicketsBetFilterAction
    data class PeriodSelected(val value: TicketPeriod) : TicketsBetFilterAction
    data class StatusSelected(val value: Long) : TicketsBetFilterAction
    data class OrderingSelected(val value: Long) : TicketsBetFilterAction
    data class BetTypeSelected(val value: Long) : TicketsBetFilterAction
    data class ContestTypeSelected(val value: Long) : TicketsBetFilterAction
    data class ModalitySelected(val value: Int) : TicketsBetFilterAction
    data object SurpriseToggled : TicketsBetFilterAction
    data object RecurringToggled : TicketsBetFilterAction
    data object ComboToggled : TicketsBetFilterAction
    data object Cleared : TicketsBetFilterAction
}

internal fun reduceTicketsBetFilterState(state: TicketsUiState, action: TicketsBetFilterAction): TicketsUiState = when (action) {
    TicketsBetFilterAction.Opened -> state.copy(showBetFilters = true)
    TicketsBetFilterAction.Closed -> state.copy(showBetFilters = false)
    is TicketsBetFilterAction.PeriodSelected -> state.copy(betFilters = state.betFilters.copy(period = action.value))
    is TicketsBetFilterAction.StatusSelected -> state.copy(betFilters = state.betFilters.copy(status = action.value))
    is TicketsBetFilterAction.OrderingSelected -> state.copy(betFilters = state.betFilters.copy(ordering = action.value))
    is TicketsBetFilterAction.BetTypeSelected -> state.copy(betFilters = state.betFilters.copy(betType = action.value))
    is TicketsBetFilterAction.ContestTypeSelected -> state.copy(betFilters = state.betFilters.copy(contestType = action.value))
    is TicketsBetFilterAction.ModalitySelected -> state.copy(betFilters = state.betFilters.copy(modality = action.value))
    TicketsBetFilterAction.SurpriseToggled -> state.copy(betFilters = state.betFilters.copy(surprise = !state.betFilters.surprise))
    TicketsBetFilterAction.RecurringToggled -> state.copy(betFilters = state.betFilters.copy(recurring = !state.betFilters.recurring))
    TicketsBetFilterAction.ComboToggled -> state.copy(betFilters = state.betFilters.copy(combo = !state.betFilters.combo))
    TicketsBetFilterAction.Cleared -> clearTicketsBetFilters(state)
}

private fun clearTicketsBetFilters(state: TicketsUiState): TicketsUiState = state.copy(
    betFilters = BetFilters(
        period = state.betFilters.period,
        status = state.betConfiguration?.defaultStatus ?: 1,
        ordering = state.betConfiguration?.defaultOrdering ?: DEFAULT_BET_ORDERING,
        betType = state.betConfiguration?.betTypes?.firstOrNull()?.value ?: 1,
        contestType = state.betConfiguration?.contestTypes?.firstOrNull()?.value ?: 0
    ),
    showBetFilters = false
)

private const val DEFAULT_BET_ORDERING = 3L
