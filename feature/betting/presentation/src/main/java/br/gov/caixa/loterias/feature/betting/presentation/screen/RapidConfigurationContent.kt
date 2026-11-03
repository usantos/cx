package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingUiEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState

@Composable
fun RapidConfigurationContent(
    state: RapidConfigurationUiState,
    pager: PagerState,
    summaryVisited: Boolean,
    onBack: () -> Unit,
    actions: RapidConfigurationActions,
) {
    FeatureModuleContent(
        module = AppModule.BETTING,
        entryType = BettingUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.RapidConfigurationContent(state, pager, summaryVisited, onBack, actions)
    }
}
