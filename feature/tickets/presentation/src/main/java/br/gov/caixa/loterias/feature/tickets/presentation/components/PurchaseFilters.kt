package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsUiEntry
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
internal fun PurchaseFiltersDialog(state: TicketsUiState, actions: PurchaseFiltersActions) {
    FeatureModuleContent(
        module = AppModule.TICKETS,
        entryType = TicketsUiEntry::class.java,
        onBack = actions.closePurchaseFilters,
        embedded = true,
        preserveActive = true
    ) { entry -> entry.PurchaseFiltersDialog(state, actions) }
}

data class PurchaseFiltersActions(
    val closePurchaseFilters: () -> Unit,
    val selectPaymentMethod: (Long?) -> Unit,
    val selectPurchaseStatus: (Long?) -> Unit,
    val applyPurchaseFilters: () -> Unit,
    val clearPurchaseFilters: () -> Unit,
)
