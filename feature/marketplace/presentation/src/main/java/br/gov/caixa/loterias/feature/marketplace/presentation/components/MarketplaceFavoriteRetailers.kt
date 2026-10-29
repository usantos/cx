package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFavoriteRetailer
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceEmptyState
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceFavoriteRetailerName
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer

@Composable
internal fun FavoriteRetailersContent(state: MarketplaceUiState, onOpen: (Retailer) -> Unit, onRemove: (Long) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    when {
        state.favoriteRetailersLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CaixaDS.colors.primaryButton)
        }

        state.favoriteRetailersError != null -> MarketplaceEmptyState(
            uiStrings.getString(LocalizedR.string.ui_voce_ainda_nao_possui_nenhuma_loteri_4047bc)
        )

        state.favoriteRetailers.isEmpty() -> MarketplaceEmptyState(
            uiStrings.getString(LocalizedR.string.ui_voce_ainda_nao_possui_nenhuma_loteri_4047bc)
        )

        else -> LazyColumn((Modifier.fillMaxSize()).uiId("pool.0080")) {
            items(state.favoriteRetailers, key = Retailer::id) { retailer ->
                CaixaDSMarketplaceFavoriteRetailer(
                    marketplaceFavoriteRetailerName(retailer.fantasyName),
                    "${retailer.city} - ${retailer.state}",
                    onOpen = { onOpen(retailer) },
                    onRemove = { onRemove(retailer.id) }
                )
            }
        }
    }
}
