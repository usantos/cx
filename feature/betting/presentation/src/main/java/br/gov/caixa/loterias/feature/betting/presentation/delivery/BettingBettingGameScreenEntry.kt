package br.gov.caixa.loterias.feature.betting.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

interface BettingBettingGameScreenEntry {
    @Composable
    fun BettingGameScreen(
        state: BettingGameUiState,
        actions: BettingGameActions,
        cartTotal: java.math.BigDecimal,
        cartBetCount: Int,
        onCart: (() -> Unit)?,
        onClear: () -> Unit,
        onBack: () -> Unit,
        onHelp: () -> Unit,
        fallbackModalityId: Int?,
        onRetry: () -> Unit,
        content: @Composable () -> Unit,
    )
}
