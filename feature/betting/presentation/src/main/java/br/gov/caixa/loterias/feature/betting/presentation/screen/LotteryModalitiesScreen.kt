package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingUiEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogUiState

@Composable
fun LotteryModalitiesScreen(state: BettingCatalogUiState, onBack: () -> Unit, onSelect: (Int) -> Unit, onRetry: () -> Unit) {
    FeatureModuleContent(
        module = AppModule.BETTING,
        entryType = BettingUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.LotteryModalitiesScreen(state, onBack, onSelect, onRetry)
    }
}
