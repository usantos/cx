package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingUiEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

@Composable
fun ManualBetContent(state: BettingGameUiState, actions: BettingGameActions, teamContent: @Composable () -> Unit) {
    FeatureModuleContent(
        module = AppModule.BETTING,
        entryType = BettingUiEntry::class.java,
        embedded = true,
        preserveActive = true
    ) { entry ->
        entry.ManualBetContent(state, actions, teamContent)
    }
}

@Composable
fun SurpriseBetContent(state: BettingGameUiState, actions: BettingGameActions, teamContent: @Composable () -> Unit) {
    FeatureModuleContent(
        module = AppModule.BETTING,
        entryType = BettingUiEntry::class.java,
        embedded = true,
        preserveActive = true
    ) { entry ->
        entry.SurpriseBetContent(state, actions, teamContent)
    }
}
