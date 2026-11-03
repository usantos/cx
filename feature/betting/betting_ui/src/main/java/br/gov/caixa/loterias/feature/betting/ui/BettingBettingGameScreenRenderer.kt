package br.gov.caixa.loterias.feature.betting.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingBettingGameScreenEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import java.math.BigDecimal

internal class BettingBettingGameScreenRenderer : BettingBettingGameScreenEntry {
    @Composable
    override fun BettingGameScreen(
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
    ) {
        br.gov.caixa.loterias.feature.betting.presentation.screen.BettingGameScreenDelivered(
            state,
            actions,
            cartTotal,
            cartBetCount,
            onCart,
            onClear,
            onBack,
            onHelp,
            fallbackModalityId,
            onRetry,
            content
        )
    }
}
