package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.presentation.state.FavoriteBetsUiState
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
internal fun FavoriteBetsList(
    state: FavoriteBetsUiState,
    padding: PaddingValues,
    onFilter: () -> Unit,
    onAdd: (FavoriteBet) -> Unit,
    onDelete: (FavoriteBet) -> Unit,
    onLoadMore: () -> Unit = {},
) {
    val listState = rememberLazyListState()
    LaunchedEffect(listState, state.selectedModalityId, state.favorites.size, state.canLoadMore) {
        if (state.canLoadMore) {
            snapshotFlow {
                val info = listState.layoutInfo
                info.totalItemsCount > 0 && (info.visibleItemsInfo.lastOrNull()?.index ?: -1) >= info.totalItemsCount - 1
            }.distinctUntilChanged().collect { reachedEnd -> if (reachedEnd) onLoadMore() }
        }
    }
    Column(Modifier.fillMaxSize().padding(padding).background(LoteriasCaixaDSColorTokens.White)) {
        FavoriteListHeader(state.selectedModalityName, onFilter)
        LazyColumn(
            state = listState,
            modifier = (Modifier.weight(1f).padding(top = 6.dp))
                .uiId("bet.0101"),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            itemsIndexed(state.favorites, key = { _, favorite -> favorite.id }) { index, favorite ->
                val previous = state.favorites.getOrNull(index - 1)
                FavoriteBetItem(
                    favorite = favorite,
                    showModalityHeader = previous?.modalityId != favorite.modalityId,
                    onAdd = { onAdd(favorite) },
                    onDelete = { onDelete(favorite) }
                )
            }
            if (state.loadingMore) {
                item {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = LoteriasCaixaDS.colors.brand)
                    }
                }
            }
        }
    }
}
