package br.gov.caixa.loterias.feature.tickets.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchaseDetailActions
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

interface TicketsPurchaseDetailContentEntry {
    @Composable
    fun PurchaseDetailContent(state: TicketsUiState, purchase: Purchase, onBack: () -> Unit, actions: PurchaseDetailActions)
}
