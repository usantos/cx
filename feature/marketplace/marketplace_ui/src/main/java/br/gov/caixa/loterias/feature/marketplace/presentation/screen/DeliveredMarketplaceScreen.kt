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
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceStyle
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceToolbarIcon
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.cart.LoteriasCaixaDSCartSummary
import br.gov.caixa.loterias.core.designsystem.components.loteriasCaixaDSMarketplaceCanvas
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.presentation.components.FavoriteRetailersContent
import br.gov.caixa.loterias.feature.marketplace.presentation.components.MarketplaceContestHeader
import br.gov.caixa.loterias.feature.marketplace.presentation.components.MarketplaceTabs
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceScreenActions
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceScreenNavigation
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceTab
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import br.gov.caixa.loterias.feature.marketplace.ui.R as DeliveryR
import java.math.BigDecimal

@Composable
fun MarketplaceScreenDelivered(
    state: MarketplaceUiState,
    navigation: MarketplaceScreenNavigation,
    actions: MarketplaceScreenActions,
    retailerView: Boolean,
    cartTotal: BigDecimal = BigDecimal.ZERO,
    cartBetCount: Int = 0,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = if (retailerView) {
            uiStrings.getString(
                DeliveryR.string.ui_loterica_favorita_6ab38b
            )
        } else {
            uiStrings.getString(DeliveryR.string.ui_bolao_caixa_4efca1)
        },
        onBack = navigation.onBack,
        gradientTopBar = false,
        topBarStartColor = LoteriasCaixaDSColorTokens.Blue700,

        bottomBar = {
            LoteriasCaixaDSCartSummary(cartTotal, cartBetCount, onClick = navigation.onCart, modifier = Modifier.uiId("pool.0116"))
        },
        topBarActionWidth = if (retailerView) 48.dp else 96.dp,
        topBarAction = {
            MarketplaceToolbar(state.selectedTab, retailerView, navigation.onFilter, navigation.onRetailerSearch, navigation.onHelp)
        },
        modifier = Modifier.uiId("pool.0121")
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).loteriasCaixaDSMarketplaceCanvas(cartTotal.signum() > 0)
        ) {
            if (!retailerView) {
                MarketplaceTabs(state.selectedTab, actions.onSelectTab)
            }
            if (state.selectedTab == MarketplaceTab.POOLS && !state.lotteryFiltered) {
                state.contestHeader?.let { MarketplaceContestHeader(it, state.contestSpecialName) }
            }
            when {
                !retailerView && state.selectedTab == MarketplaceTab.FAVORITE_RETAILERS -> FavoriteRetailersContent(
                    state = state,
                    onOpen = navigation.onRetailerSelected,
                    onRemove = actions.onRemoveRetailer
                )

                else -> MarketplacePoolsContentDelivered(
                    state = state,
                    onRetry = actions.onRetry,
                    onLoadMore = actions.onLoadMore,
                    onPoolSelected = navigation.onPoolSelected,
                    onToggleExpanded = actions.onToggleExpanded,
                    onDecrease = actions.onDecrease,
                    onIncrease = actions.onIncrease,
                    onFavorite = actions.onFavorite,
                    onAddToCart = actions.onAddToCart
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
        Modifier.fillMaxSize().padding(end = LoteriasCaixaDSMarketplaceStyle.toolbarEndInset),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!retailerView) {
            IconButton(
                onClick = if (selectedTab == MarketplaceTab.POOLS) onFilter else onRetailerSearch,
                modifier = (Modifier.width(LoteriasCaixaDSMarketplaceStyle.toolbarActionWidth)).uiId("pool.0117")
            ) {
                LoteriasCaixaDSMarketplaceToolbarIcon(
                    help = false,
                    search = selectedTab == MarketplaceTab.FAVORITE_RETAILERS,
                    description = if (selectedTab == MarketplaceTab.POOLS) {
                        uiStrings.getString(
                            DeliveryR.string.ui_filtrar_boloes_bd6f30
                        )
                    } else {
                        uiStrings.getString(
                            DeliveryR.string.ui_pesquisar_loterica_742000
                        )
                    },
                    modifier = Modifier.uiId("pool.0118")
                )
            }
        }
        IconButton(onClick = onHelp, modifier = (Modifier.width(LoteriasCaixaDSMarketplaceStyle.toolbarActionWidth)).uiId("pool.0119")) {
            LoteriasCaixaDSMarketplaceToolbarIcon(
                help = true,
                description = uiStrings.getString(DeliveryR.string.ui_ajuda_fe0add),
                modifier = Modifier.uiId("pool.0120")
            )
        }
    }
}
