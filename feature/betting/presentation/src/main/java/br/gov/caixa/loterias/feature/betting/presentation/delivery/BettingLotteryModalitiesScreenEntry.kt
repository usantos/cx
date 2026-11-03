package br.gov.caixa.loterias.feature.betting.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogUiState

interface BettingLotteryModalitiesScreenEntry {
    @Composable
    fun LotteryModalitiesScreen(state: BettingCatalogUiState, onBack: () -> Unit, onSelect: (Int) -> Unit, onRetry: () -> Unit)
}
