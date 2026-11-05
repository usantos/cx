package br.gov.caixa.loterias.feature.tickets.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.domain.contract.tickets.model.Purchase
import br.gov.caixa.loterias.core.navigation.state.PurchaseRecoverySession
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseFiltersDialog
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchasesContent
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchasesListActions
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel

@Composable
fun PurchasesDataRoute(
    viewModel: TicketsViewModel,
    onBack: () -> Unit,
    onOpen: (Purchase) -> Unit,
    privateDataEnabled: Boolean = true,
    purchaseRecovery: PurchaseRecoverySession? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel, privateDataEnabled) { if (privateDataEnabled && !state.loaded) viewModel.loadPurchases() }
    PurchasesContent(
        state = state,
        purchaseRecovery = purchaseRecovery,
        onBack = onBack,
        onOpen = onOpen,
        actions = PurchasesListActions(
            openPurchaseFilters = viewModel::openPurchaseFilters,
            selectPurchaseMonth = viewModel::selectPurchaseMonth,
            retryPurchases = viewModel::retryPurchases,
            loadMorePurchases = viewModel::loadMorePurchases
        )
    )
    if (state.showPurchaseFilters) {
        PurchaseFiltersDialog(
            state,
            PurchaseFiltersActions(
                closePurchaseFilters = viewModel::closePurchaseFilters,
                selectPaymentMethod = viewModel::selectPaymentMethod,
                selectPurchaseStatus = viewModel::selectPurchaseStatus,
                applyPurchaseFilters = viewModel::applyPurchaseFilters,
                clearPurchaseFilters = viewModel::clearPurchaseFilters
            )
        )
    }
}
