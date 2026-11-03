package br.gov.caixa.loterias.feature.betting.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowUiState

interface BettingBettingScreenEntry {
    @Composable
    fun BettingScreen(
        state: BettingFlowUiState,
        onPageSelected: (Int) -> Unit,
        onBack: () -> Unit,
        onPrimary: () -> Unit,
        onSecondary: () -> Unit,
        onToggleDoNotShowAgain: () -> Unit,
    )
}
