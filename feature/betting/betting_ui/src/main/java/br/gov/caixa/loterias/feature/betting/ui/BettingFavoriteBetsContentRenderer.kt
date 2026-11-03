package br.gov.caixa.loterias.feature.betting.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingFavoriteBetsContentEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.FavoriteBetsUiState

internal class BettingFavoriteBetsContentRenderer : BettingFavoriteBetsContentEntry {
    @Composable
    override fun FavoriteBetsContent(
        state: FavoriteBetsUiState,
        onBack: () -> Unit,
        onTerms: () -> Unit,
        onCreateFavorite: (Int) -> Unit,
        privateDataEnabled: Boolean,
        onFilter: () -> Unit,
        onAdd: (FavoriteBet) -> Unit,
        onDelete: (FavoriteBet) -> Unit,
        onLoadMore: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.betting.presentation.screen.FavoriteBetsContentDelivered(
            state,
            onBack,
            onTerms,
            onCreateFavorite,
            privateDataEnabled,
            onFilter,
            onAdd,
            onDelete,
            onLoadMore
        )
    }
}
