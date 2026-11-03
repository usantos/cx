package br.gov.caixa.loterias.feature.betting.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

interface BettingBettingLotogolContentEntry {
    @Composable
    fun LotogolBetContent(state: BettingGameUiState, actions: BettingGameActions)
}
