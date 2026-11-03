package br.gov.caixa.loterias.feature.betting.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogUiState
import java.math.BigDecimal

interface BettingBetCombosScreenEntry {
    @Composable
    fun BetCombosScreen(
        state: BettingCatalogUiState,
        onBack: () -> Unit,
        onSelect: (Int) -> Unit,
        cartTotal: BigDecimal,
        cartBetCount: Int,
        onCart: (() -> Unit)?,
        onRetry: () -> Unit,
        onHelp: () -> Unit = {},
    )
}
