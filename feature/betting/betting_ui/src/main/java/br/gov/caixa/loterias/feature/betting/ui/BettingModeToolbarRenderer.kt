package br.gov.caixa.loterias.feature.betting.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingModeToolbarDelivered
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingModeToolbarEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

internal class BettingModeToolbarRenderer : BettingModeToolbarEntry {
    @Composable
    override fun BettingModeToolbar(
        state: BettingGameUiState,
        onManual: () -> Unit,
        onSurprise: () -> Unit,
        onNumberQuantity: () -> Unit,
        onRecurring: () -> Unit,
        onTutorial: (() -> Unit)?,
    ) {
        BettingModeToolbarDelivered(state, onManual, onSurprise, onNumberQuantity, onRecurring, onTutorial)
    }
}
