package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsUiEntry
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
fun TicketGameChoiceScreen(
    state: TicketsUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onSave: (Set<Int>) -> Unit,
    onToggleModality: (Int) -> Unit,
    onRetry: () -> Unit,
) {
    FeatureModuleContent(
        module = AppModule.TICKETS,
        entryType = TicketsUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.TicketGameChoiceScreen(state, onBack, onHelp, onSave, onToggleModality, onRetry)
    }
}
