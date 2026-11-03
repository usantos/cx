package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsUiEntry
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
fun ManualTicketContent(
    state: TicketsUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    manualCodeValid: Boolean,
    actions: ManualTicketActions,
) {
    FeatureModuleContent(
        module = AppModule.TICKETS,
        entryType = TicketsUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.ManualTicketContent(state, onBack, onHelp, manualCodeValid, actions)
    }
}
