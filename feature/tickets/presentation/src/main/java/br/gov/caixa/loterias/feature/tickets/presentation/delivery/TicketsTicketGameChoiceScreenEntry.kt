package br.gov.caixa.loterias.feature.tickets.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

interface TicketsTicketGameChoiceScreenEntry {
    @Composable
    fun TicketGameChoiceScreen(
        state: TicketsUiState,
        onBack: () -> Unit,
        onHelp: () -> Unit,
        onSave: (Set<Int>) -> Unit,
        onToggleModality: (Int) -> Unit,
        onRetry: () -> Unit,
    )
}
