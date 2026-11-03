package br.gov.caixa.loterias.feature.tickets.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseBetFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

interface TicketsFilterDialogsEntry {
    @Composable
    fun BetFiltersDialog(state: TicketsUiState, actions: BetFiltersActions)

    @Composable
    fun PurchaseFiltersDialog(state: TicketsUiState, actions: PurchaseFiltersActions)

    @Composable
    fun PurchaseBetFiltersDialog(state: TicketsUiState, actions: PurchaseBetFiltersActions)
}
