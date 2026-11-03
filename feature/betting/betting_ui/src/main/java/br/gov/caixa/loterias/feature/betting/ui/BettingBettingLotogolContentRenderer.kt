package br.gov.caixa.loterias.feature.betting.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingBettingLotogolContentEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

internal class BettingBettingLotogolContentRenderer : BettingBettingLotogolContentEntry {
    @Composable
    override fun LotogolBetContent(state: BettingGameUiState, actions: BettingGameActions) {
        br.gov.caixa.loterias.feature.betting.presentation.screen.LotogolBetContentDelivered(state, actions)
    }
}
