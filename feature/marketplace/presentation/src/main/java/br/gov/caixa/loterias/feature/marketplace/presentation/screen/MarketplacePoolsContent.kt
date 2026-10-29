package br.gov.caixa.loterias.feature.marketplace.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceListFooter
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceStyle
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCard as Card
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCardDefaults as CardDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.presentation.components.MarketplaceCarousel
import br.gov.caixa.loterias.feature.marketplace.presentation.components.PoolCard
import br.gov.caixa.loterias.feature.marketplace.presentation.components.PoolCardActions
import br.gov.caixa.loterias.feature.marketplace.presentation.route.MarketplacePaginationEffect
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState

@Composable
internal fun MarketplacePoolsContent(
    state: MarketplaceUiState,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onPoolSelected: (String) -> Unit,
    onToggleExpanded: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onFavorite: (String) -> Unit,
    onAddToCart: (String) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val listState = rememberLazyListState()
    MarketplacePaginationEffect(listState, state, onLoadMore)
    when {
        state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CaixaDS.colors.primaryButton)
        }

        state.errorMessage != null -> MarketplaceEmptyState(state.errorMessage)

        state.pools.isEmpty() -> MarketplaceEmptyState(
            if (state.lotteryFiltered) {
                uiStrings.getString(LocalizedR.string.ui_esta_loterica_nao_possui_boloes_disp_dd5e5c)
            } else {
                uiStrings.getString(LocalizedR.string.ui_nao_foram_encontrados_boloes_disponi_ecef7d)
            }
        )

        else -> LazyColumn(
            modifier = (Modifier.fillMaxSize().legacyAutomationId("marketplace-pools-list")).uiId("pool.0113"),
            state = listState,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = CaixaDSMarketplaceStyle.listBottomPadding)
        ) {
            item(key = "carousel") {
                MarketplaceCarousel(state, onDecrease, onIncrease, onFavorite, onPoolSelected, onAddToCart)
            }
            items(state.listPools, key = MarketplacePool::code) { pool ->
                Box(Modifier.legacyAutomationId("pool-list-${pool.code}")) {
                    PoolCard(
                        pool = pool,
                        expanded = pool.code in state.expandedPoolCodes,
                        selectedShares = state.selectedShares[pool.code] ?: 1,
                        lotteryFiltered = state.lotteryFiltered,
                        updatingFavorite = pool.lotteryId in state.updatingFavoriteLotteryIds,
                        addingToCart = pool.code in state.addingPoolCodes,
                        actions = PoolCardActions(
                            onToggleExpanded = { onToggleExpanded(pool.code) },
                            onDecrease = { onDecrease(pool.code) },
                            onIncrease = { onIncrease(pool.code) },
                            onFavorite = { onFavorite(pool.code) },
                            onDetail = { onPoolSelected(pool.code) },
                            onAddToCart = { onAddToCart(pool.code) }
                        ),
                        modalityNames = state.modalityNames,
                        specialContests = state.specialContests,
                        showModality = state.showModalityInList
                    )
                }
            }
            item {
                CaixaDSMarketplaceListFooter(state.isLoadingMore, exhausted = !state.canLoadMore && state.errorMessage == null)
            }
        }
    }
}

@Composable
internal fun MarketplaceEmptyState(message: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 20.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CaixaDSColorTokens.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 44.dp, bottom = 43.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_ops_aaf6b8),
                color = CaixaDSColorTokens.Blue700,
                style = CaixaDSMarketplaceStyle.text(24, FontWeight.Bold),
                modifier = Modifier.uiId("pool.0114")
            )
            Text(
                message,
                color = CaixaDSColorTokens.Blue700,
                style = CaixaDSMarketplaceStyle.text(16),
                textAlign = TextAlign.Center,
                modifier = Modifier.uiId("pool.0115")
            )
        }
    }
}
