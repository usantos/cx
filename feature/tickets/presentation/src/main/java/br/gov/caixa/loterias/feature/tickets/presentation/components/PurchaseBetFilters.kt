package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsUiEntry
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
internal fun PurchaseBetFiltersDialog(state: TicketsUiState, actions: PurchaseBetFiltersActions) {
    FeatureModuleContent(
        module = AppModule.TICKETS,
        entryType = TicketsUiEntry::class.java,
        onBack = actions.closePurchaseBetFilters,
        embedded = true,
        preserveActive = true
    ) { entry -> entry.PurchaseBetFiltersDialog(state, actions) }
}

data class PurchaseBetFiltersActions(
    val closePurchaseBetFilters: () -> Unit,
    val selectPurchaseBetContest: (Int?) -> Unit,
    val selectPurchaseBetStatus: (Long?) -> Unit,
    val selectPurchaseBetModality: (Int?) -> Unit,
    val applyPurchaseBetFilters: () -> Unit,
    val clearPurchaseBetFilters: () -> Unit,
)
