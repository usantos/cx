package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingUiEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.FavoriteBetsUiState

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
) {
    FeatureModuleContent(
        module = AppModule.BETTING,
        entryType = BettingUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.FavoriteBetsContent(state, onBack, onTerms, onCreateFavorite, privateDataEnabled, onFilter, onAdd, onDelete, onLoadMore)
    }
}
