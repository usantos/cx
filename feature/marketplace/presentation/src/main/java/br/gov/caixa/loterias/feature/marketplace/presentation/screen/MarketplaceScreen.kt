package br.gov.caixa.loterias.feature.marketplace.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceBack
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceStyle
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceToolbarIcon
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.caixaDSMarketplaceCanvas
import br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartSummary
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.presentation.components.FavoriteRetailersContent
import br.gov.caixa.loterias.feature.marketplace.presentation.components.MarketplaceContestHeader
import br.gov.caixa.loterias.feature.marketplace.presentation.components.MarketplaceTabs
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceTab
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import java.math.BigDecimal

@Composable
fun MarketplaceScreen(
    state: MarketplaceUiState,
    onBack: () -> Unit,
    onFilter: () -> Unit,
    onHelp: () -> Unit,
    onRetailerSelected: (Retailer) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onPoolSelected: (String) -> Unit,
    onToggleExpanded: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onFavorite: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    onSelectTab: (MarketplaceTab) -> Unit,
    onRemoveRetailer: (Long) -> Unit,
    retailerView: Boolean,
    cartTotal: BigDecimal = BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
    onRetailerSearch: () -> Unit = {},
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = if (retailerView) {
            uiStrings.getString(
                LocalizedR.string.ui_loterica_favorita_6ab38b
            )
        } else {
            uiStrings.getString(LocalizedR.string.ui_bolao_caixa_4efca1)
        },
        onBack = onBack,
        gradientTopBar = false,
        topBarStartColor = CaixaDSColorTokens.Blue700,

        bottomBar = { CaixaDSCartSummary(cartTotal, cartBetCount, onClick = onCart, modifier = Modifier.uiId("pool.0116")) },
        topBarActionWidth = if (retailerView) 48.dp else 96.dp,
        topBarAction = {
            MarketplaceToolbar(state.selectedTab, retailerView, onFilter, onRetailerSearch, onHelp)
        },
        modifier = Modifier.uiId("pool.0121")
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).caixaDSMarketplaceCanvas(cartTotal.signum() > 0)
        ) {
            if (!retailerView) {
                MarketplaceTabs(state.selectedTab, onSelectTab)
            }
            if (state.selectedTab == MarketplaceTab.POOLS && !state.lotteryFiltered) {
                state.contestHeader?.let { MarketplaceContestHeader(it, state.contestSpecialName) }
            }
            when {
                !retailerView && state.selectedTab == MarketplaceTab.FAVORITE_RETAILERS -> FavoriteRetailersContent(
                    state = state,
                    onOpen = onRetailerSelected,
                    onRemove = onRemoveRetailer
                )

                else -> MarketplacePoolsContent(
                    state = state,
                    onRetry = onRetry,
                    onLoadMore = onLoadMore,
                    onPoolSelected = onPoolSelected,
                    onToggleExpanded = onToggleExpanded,
                    onDecrease = onDecrease,
                    onIncrease = onIncrease,
                    onFavorite = onFavorite,
                    onAddToCart = onAddToCart
                )
            }
        }
    }
}

@Composable
private fun MarketplaceToolbar(
    selectedTab: MarketplaceTab,
    retailerView: Boolean,
    onFilter: () -> Unit,
    onRetailerSearch: () -> Unit,
    onHelp: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxSize().padding(end = CaixaDSMarketplaceStyle.toolbarEndInset),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!retailerView) {
            IconButton(
                onClick = if (selectedTab == MarketplaceTab.POOLS) onFilter else onRetailerSearch,
                modifier = (Modifier.width(CaixaDSMarketplaceStyle.toolbarActionWidth)).uiId("pool.0117")
            ) {
                CaixaDSMarketplaceToolbarIcon(
                    help = false,
                    search = selectedTab == MarketplaceTab.FAVORITE_RETAILERS,
                    description = if (selectedTab == MarketplaceTab.POOLS) {
                        uiStrings.getString(
                            LocalizedR.string.ui_filtrar_boloes_bd6f30
                        )
                    } else {
                        uiStrings.getString(
                            LocalizedR.string.ui_pesquisar_loterica_742000
                        )
                    },
                    modifier = Modifier.uiId("pool.0118")
                )
            }
        }
        IconButton(onClick = onHelp, modifier = (Modifier.width(CaixaDSMarketplaceStyle.toolbarActionWidth)).uiId("pool.0119")) {
            CaixaDSMarketplaceToolbarIcon(
                help = true,
                description = uiStrings.getString(LocalizedR.string.ui_ajuda_fe0add),
                modifier = Modifier.uiId("pool.0120")
            )
        }
    }
}
