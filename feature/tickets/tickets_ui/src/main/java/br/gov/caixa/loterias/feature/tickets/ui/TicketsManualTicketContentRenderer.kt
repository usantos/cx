package br.gov.caixa.loterias.feature.tickets.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsManualTicketContentEntry
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ManualTicketActions
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

internal class TicketsManualTicketContentRenderer : TicketsManualTicketContentEntry {
    @Composable
    override fun ManualTicketContent(
        state: TicketsUiState,
        onBack: () -> Unit,
        onHelp: () -> Unit,
        manualCodeValid: Boolean,
        actions: ManualTicketActions,
    ) {
        br.gov.caixa.loterias.feature.tickets.presentation.screen.ManualTicketContentDelivered(
            state,
            onBack,
            onHelp,
            manualCodeValid,
            actions
        )
    }
}
