package br.gov.caixa.loterias.feature.betting.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingBettingScreenEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowUiState

internal class BettingBettingScreenRenderer : BettingBettingScreenEntry {
    @Composable
    override fun BettingScreen(
        state: BettingFlowUiState,
        onPageSelected: (Int) -> Unit,
        onBack: () -> Unit,
        onPrimary: () -> Unit,
        onSecondary: () -> Unit,
        onToggleDoNotShowAgain: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.betting.presentation.screen.BettingScreenDelivered(
            state,
            onPageSelected,
            onBack,
            onPrimary,
            onSecondary,
            onToggleDoNotShowAgain
        )
    }
}
