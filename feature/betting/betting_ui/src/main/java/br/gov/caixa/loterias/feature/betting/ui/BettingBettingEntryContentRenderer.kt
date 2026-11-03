package br.gov.caixa.loterias.feature.betting.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingBettingEntryContentEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

internal class BettingBettingEntryContentRenderer : BettingBettingEntryContentEntry {
    @Composable
    override fun ManualBetContent(state: BettingGameUiState, actions: BettingGameActions, teamContent: @Composable () -> Unit) {
        br.gov.caixa.loterias.feature.betting.presentation.screen.ManualBetContentDelivered(state, actions, teamContent)
    }

    @Composable
    override fun SurpriseBetContent(state: BettingGameUiState, actions: BettingGameActions, teamContent: @Composable () -> Unit) {
        br.gov.caixa.loterias.feature.betting.presentation.screen.SurpriseBetContentDelivered(state, actions, teamContent)
    }
}
