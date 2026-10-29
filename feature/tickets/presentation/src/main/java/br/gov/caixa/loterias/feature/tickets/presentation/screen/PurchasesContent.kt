package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth
import br.gov.caixa.loterias.feature.tickets.presentation.components.ErrorData
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyEmptyPurchases
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyPurchaseIntro
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyPurchaseRow
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyTicketPeriodSelector
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyTicketsFilterAction
import br.gov.caixa.loterias.feature.tickets.presentation.components.LoadingData
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
internal fun PurchasesContent(state: TicketsUiState, onBack: () -> Unit, onOpen: (Purchase) -> Unit, actions: PurchasesListActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_minhas_compras_c1e245),
        onBack = onBack,
        topBarAction = { LegacyTicketsFilterAction(state.purchaseFilters.activeCount, actions.openPurchaseFilters) },
        containerColor = CaixaDSColorTokens.White,
        modifier = Modifier.uiId("tickets.0239")
    ) { padding ->
        androidx.compose.foundation.lazy.LazyColumn((Modifier.fillMaxSize().padding(padding)).uiId("tickets.0240")) {
            item { LegacyPurchaseIntro() }
            item {
                LegacyTicketPeriodSelector(
                    uiStrings.getString(LocalizedR.string.final_purchase_month_year),
                    state.purchaseFilters.month?.description.orEmpty(),
                    state.purchaseMonths.map { it.description },
                    (Modifier.padding(start = 8.dp, end = 8.dp, top = 24.dp)).uiId("tickets.0241")
                ) { index -> state.purchaseMonths.getOrNull(index)?.let(actions.selectPurchaseMonth) }
            }
            item {
                Box(
                    Modifier.fillMaxWidth().padding(horizontal = 17.dp).height(1.dp).background(CaixaDSColorTokens.TicketDisabledBackground)
                )
            }
            when {
                state.isLoading -> item { LoadingData((Modifier.height(100.dp)).uiId("tickets.0242")) }

                state.errorMessage != null -> item {
                    ErrorData(
                        state.errorMessage.orEmpty(),
                        actions.retryPurchases,
                        modifier = Modifier
                            .uiId("tickets.0243")
                    )
                }

                state.loaded && state.purchases.isEmpty() -> item { LegacyEmptyPurchases() }

                else -> items(state.purchases.size, key = { state.purchases[it].id }) { index ->
                    val purchase = state.purchases[index]
                    LegacyPurchaseRow(purchase) { onOpen(purchase) }
                }
            }
            if (state.canLoadMore || state.isLoadingMore) {
                item {
                    LaunchedEffect(state.purchases.size) { actions.loadMorePurchases() }
                    if (state.isLoadingMore) CircularProgressIndicator(Modifier.padding(5.dp))
                }
            }
        }
    }
}

internal data class PurchasesListActions(
    val openPurchaseFilters: () -> Unit,
    val selectPurchaseMonth: (PurchaseMonth) -> Unit,
    val retryPurchases: () -> Unit,
    val loadMorePurchases: () -> Unit,
)
