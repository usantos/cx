package br.gov.caixa.loterias.feature.tickets.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetFiltersDialogDelivered
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseBetFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseBetFiltersDialogDelivered
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseFiltersDialogDelivered
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsFilterDialogsEntry
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

internal class TicketsFilterDialogsRenderer : TicketsFilterDialogsEntry {
    @Composable
    override fun BetFiltersDialog(state: TicketsUiState, actions: BetFiltersActions) {
        BetFiltersDialogDelivered(state, actions)
    }

    @Composable
    override fun PurchaseFiltersDialog(state: TicketsUiState, actions: PurchaseFiltersActions) {
        PurchaseFiltersDialogDelivered(state, actions)
    }

    @Composable
    override fun PurchaseBetFiltersDialog(state: TicketsUiState, actions: PurchaseBetFiltersActions) {
        PurchaseBetFiltersDialogDelivered(state, actions)
    }
}
