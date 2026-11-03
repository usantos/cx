package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsUiEntry
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
fun PurchaseDetailContent(state: TicketsUiState, purchase: Purchase, onBack: () -> Unit, actions: PurchaseDetailActions) {
    FeatureModuleContent(
        module = AppModule.TICKETS,
        entryType = TicketsUiEntry::class.java,
        onBack = onBack,
        access = if (purchase.id > 0 && state.selectedPurchase?.id == purchase.id) {
            FeatureEntryAccess.CONTINUATION
        } else {
            FeatureEntryAccess.OPTIONAL
        },
        preserveActive = purchase.id > 0 && state.selectedPurchase?.id == purchase.id
    ) { entry ->
        entry.PurchaseDetailContent(state, purchase, onBack, actions)
    }
}
