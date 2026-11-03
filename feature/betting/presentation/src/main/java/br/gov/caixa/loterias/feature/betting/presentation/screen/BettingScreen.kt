package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingUiEntry
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingRoute
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowUiState

/** Renderiza o estado do tutorial; ciclo de vida e navegação permanecem em BettingRoute. */

@Composable
fun BettingScreen(
    state: BettingFlowUiState,
    onPageSelected: (Int) -> Unit,
    onBack: () -> Unit,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
    onToggleDoNotShowAgain: () -> Unit,
) {
    FeatureModuleContent(
        module = AppModule.BETTING,
        entryType = BettingUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.BettingScreen(state, onPageSelected, onBack, onPrimary, onSecondary, onToggleDoNotShowAgain)
    }
}
