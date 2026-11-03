package br.gov.caixa.loterias.feature.betting.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

interface BettingModeToolbarEntry {
    @Composable
    fun BettingModeToolbar(
        state: BettingGameUiState,
        onManual: () -> Unit,
        onSurprise: () -> Unit,
        onNumberQuantity: () -> Unit,
        onRecurring: () -> Unit,
        onTutorial: (() -> Unit)?,
    )
}
