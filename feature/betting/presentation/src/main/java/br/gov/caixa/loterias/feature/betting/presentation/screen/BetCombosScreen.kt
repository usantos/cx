package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingUiEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogUiState
import java.math.BigDecimal

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
) {
    FeatureModuleContent(
        module = AppModule.BETTING,
        entryType = BettingUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.BetCombosScreen(state, onBack, onSelect, cartTotal, cartBetCount, onCart, onRetry, onHelp)
    }
}
