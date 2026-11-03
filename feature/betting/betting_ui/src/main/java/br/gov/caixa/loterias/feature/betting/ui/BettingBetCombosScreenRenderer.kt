package br.gov.caixa.loterias.feature.betting.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingBetCombosScreenEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogUiState
import java.math.BigDecimal

internal class BettingBetCombosScreenRenderer : BettingBetCombosScreenEntry {
    @Composable
    override fun BetCombosScreen(
        state: BettingCatalogUiState,
        onBack: () -> Unit,
        onSelect: (Int) -> Unit,
        cartTotal: BigDecimal,
        cartBetCount: Int,
        onCart: (() -> Unit)?,
        onRetry: () -> Unit,
        onHelp: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.betting.presentation.screen.BetCombosScreenDelivered(
            state,
            onBack,
            onSelect,
            cartTotal,
            cartBetCount,
            onCart,
            onRetry,
            onHelp
        )
    }
}
