package br.gov.caixa.loterias.feature.tickets.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.contract.tickets.model.Purchase
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchasesListActions
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

interface TicketsPurchasesContentEntry {
    @Composable
    fun PurchasesContent(state: TicketsUiState, onBack: () -> Unit, onOpen: (Purchase) -> Unit, actions: PurchasesListActions)
}
