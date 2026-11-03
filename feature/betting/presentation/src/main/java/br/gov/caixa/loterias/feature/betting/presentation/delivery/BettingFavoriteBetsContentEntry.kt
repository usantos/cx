package br.gov.caixa.loterias.feature.betting.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.presentation.state.FavoriteBetsUiState

interface BettingFavoriteBetsContentEntry {
    @Composable
    fun FavoriteBetsContent(
        state: FavoriteBetsUiState,
        onBack: () -> Unit,
        onTerms: () -> Unit,
        onCreateFavorite: (Int) -> Unit,
        privateDataEnabled: Boolean,
        onFilter: () -> Unit,
        onAdd: (FavoriteBet) -> Unit,
        onDelete: (FavoriteBet) -> Unit,
        onLoadMore: () -> Unit = {},
    )
}
