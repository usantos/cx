package br.gov.caixa.loterias.feature.tickets.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ManualTicketActions
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

interface TicketsManualTicketContentEntry {
    @Composable
    fun ManualTicketContent(
        state: TicketsUiState,
        onBack: () -> Unit,
        onHelp: () -> Unit,
        manualCodeValid: Boolean,
        actions: ManualTicketActions,
    )
}
