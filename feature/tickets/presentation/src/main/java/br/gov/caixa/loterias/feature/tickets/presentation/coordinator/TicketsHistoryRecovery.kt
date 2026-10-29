package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

internal class TicketsHistoryRecovery(
    private val mutableState: ReduxStateStore<TicketsUiState, TicketsStateAction>,
    private val bets: TicketsHistoryBets,
    private val purchases: TicketsHistoryPurchases,
) {
    fun retryBets() = if (mutableState.value.betConfiguration == null) bets.loadBets() else bets.applyBetFilters()

    fun retryPurchases() = if (mutableState.value.purchaseMonths.isEmpty()) purchases.loadPurchases() else purchases.reloadPurchases()
}
