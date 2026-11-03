package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.navigation.state.PurchaseRecoverySession
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsUiEntry
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
fun PurchasesContent(
    state: TicketsUiState,
    onBack: () -> Unit,
    onOpen: (Purchase) -> Unit,
    actions: PurchasesListActions,
    purchaseRecovery: PurchaseRecoverySession? = null,
) {
    val recovering = purchaseRecovery?.current() != null
    FeatureModuleContent(
        module = AppModule.TICKETS,
        entryType = TicketsUiEntry::class.java,
        onBack = onBack,
        access = if (recovering) FeatureEntryAccess.CONTINUATION else FeatureEntryAccess.OPTIONAL,
        preserveActive = recovering
    ) { entry ->
        entry.PurchasesContent(state, onBack, onOpen, actions)
    }
}
