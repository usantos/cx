package br.gov.caixa.loterias.feature.betting.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingLotteryModalitiesScreenEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogUiState

internal class BettingLotteryModalitiesScreenRenderer : BettingLotteryModalitiesScreenEntry {
    @Composable
    override fun LotteryModalitiesScreen(state: BettingCatalogUiState, onBack: () -> Unit, onSelect: (Int) -> Unit, onRetry: () -> Unit) {
        br.gov.caixa.loterias.feature.betting.presentation.screen.LotteryModalitiesScreenDelivered(state, onBack, onSelect, onRetry)
    }
}
