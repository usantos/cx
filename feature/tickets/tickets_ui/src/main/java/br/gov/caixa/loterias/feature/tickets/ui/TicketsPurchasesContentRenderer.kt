package br.gov.caixa.loterias.feature.tickets.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsPurchasesContentEntry
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchasesListActions
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

internal class TicketsPurchasesContentRenderer : TicketsPurchasesContentEntry {
    @Composable
    override fun PurchasesContent(state: TicketsUiState, onBack: () -> Unit, onOpen: (Purchase) -> Unit, actions: PurchasesListActions) {
        br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchasesContentDelivered(state, onBack, onOpen, actions)
    }
}
