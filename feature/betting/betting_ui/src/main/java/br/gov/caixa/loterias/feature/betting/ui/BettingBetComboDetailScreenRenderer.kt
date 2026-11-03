package br.gov.caixa.loterias.feature.betting.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingBetComboDetailScreenEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogUiState
import java.math.BigDecimal

internal class BettingBetComboDetailScreenRenderer : BettingBetComboDetailScreenEntry {
    @Composable
    override fun BetComboDetailScreen(
        state: BettingCatalogUiState,
        onBack: () -> Unit,
        cartTotal: BigDecimal,
        cartBetCount: Int,
        onCart: (() -> Unit)?,
        onRetry: () -> Unit,
        onDecreaseQuantity: () -> Unit,
        onIncreaseQuantity: () -> Unit,
        onAddCombo: () -> Unit,
        onToggleSurprise: () -> Unit,
        onHelp: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.betting.presentation.screen.BetComboDetailScreenDelivered(
            state,
            onBack,
            cartTotal,
            cartBetCount,
            onCart,
            onRetry,
            onDecreaseQuantity,
            onIncreaseQuantity,
            onAddCombo,
            onToggleSurprise,
            onHelp
        )
    }
}
