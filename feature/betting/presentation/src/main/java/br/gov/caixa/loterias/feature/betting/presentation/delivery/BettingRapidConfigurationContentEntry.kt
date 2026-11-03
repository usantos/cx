package br.gov.caixa.loterias.feature.betting.presentation.delivery

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.screen.RapidConfigurationActions
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState

interface BettingRapidConfigurationContentEntry {
    @Composable
    fun RapidConfigurationContent(
        state: RapidConfigurationUiState,
        pager: PagerState,
        summaryVisited: Boolean,
        onBack: () -> Unit,
        actions: RapidConfigurationActions,
    )
}
