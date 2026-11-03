package br.gov.caixa.loterias.feature.betting.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

interface BettingBettingEntryContentEntry {
    @Composable
    fun ManualBetContent(state: BettingGameUiState, actions: BettingGameActions, teamContent: @Composable () -> Unit)

    @Composable
    fun SurpriseBetContent(state: BettingGameUiState, actions: BettingGameActions, teamContent: @Composable () -> Unit)
}
