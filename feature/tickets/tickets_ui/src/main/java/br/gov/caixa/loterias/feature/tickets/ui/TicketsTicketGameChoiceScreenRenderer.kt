package br.gov.caixa.loterias.feature.tickets.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsTicketGameChoiceScreenEntry
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

internal class TicketsTicketGameChoiceScreenRenderer : TicketsTicketGameChoiceScreenEntry {
    @Composable
    override fun TicketGameChoiceScreen(
        state: TicketsUiState,
        onBack: () -> Unit,
        onHelp: () -> Unit,
        onSave: (Set<Int>) -> Unit,
        onToggleModality: (Int) -> Unit,
        onRetry: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.tickets.presentation.screen.TicketGameChoiceScreenDelivered(
            state,
            onBack,
            onHelp,
            onSave,
            onToggleModality,
            onRetry
        )
    }
}
