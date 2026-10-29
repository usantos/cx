package br.gov.caixa.loterias.feature.marketplace.presentation.route

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import androidx.compose.foundation.lazy.LazyListState
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.shouldLoadMarketplacePage

@Composable
internal fun MarketplacePaginationEffect(
    listState: LazyListState,
    state: MarketplaceUiState,
    onLoadMore: () -> Unit,
) {
    val currentLoadMore by rememberUpdatedState(onLoadMore)
    LaunchedEffect(listState, state.canLoadMore, state.isLoadingMore) {
        snapshotFlow {
            shouldLoadMarketplacePage(
                listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0,
                listState.canScrollForward, state.canLoadMore, state.isLoadingMore,
            )
        }.distinctUntilChanged().filter { it }.collect { currentLoadMore() }
    }
}
