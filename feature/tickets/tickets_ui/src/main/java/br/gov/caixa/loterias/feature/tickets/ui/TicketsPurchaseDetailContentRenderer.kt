package br.gov.caixa.loterias.feature.tickets.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.contract.tickets.model.Purchase
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsPurchaseDetailContentEntry
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchaseDetailActions
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

internal class TicketsPurchaseDetailContentRenderer : TicketsPurchaseDetailContentEntry {
    @Composable
    override fun PurchaseDetailContent(state: TicketsUiState, purchase: Purchase, onBack: () -> Unit, actions: PurchaseDetailActions) {
        br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchaseDetailContentDelivered(state, purchase, onBack, actions)
    }
}
