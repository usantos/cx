package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketPeriod
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsUiEntry
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
internal fun BetFiltersDialog(state: TicketsUiState, actions: BetFiltersActions) {
    FeatureModuleContent(
        module = AppModule.TICKETS,
        entryType = TicketsUiEntry::class.java,
        onBack = actions.closeBetFilters,
        embedded = true,
        preserveActive = true
    ) { entry -> entry.BetFiltersDialog(state, actions) }
}

data class BetFiltersActions(
    val closeBetFilters: () -> Unit,
    val selectBetPeriod: (TicketPeriod) -> Unit,
    val selectBetStatus: (Long) -> Unit,
    val selectBetOrdering: (Long) -> Unit,
    val selectBetType: (Long) -> Unit,
    val selectContestType: (Long) -> Unit,
    val selectModality: (Int) -> Unit,
    val toggleSurprise: () -> Unit,
    val toggleRecurring: () -> Unit,
    val toggleCombo: () -> Unit,
    val applyBetFilters: () -> Unit,
    val clearBetFilters: () -> Unit,
)
