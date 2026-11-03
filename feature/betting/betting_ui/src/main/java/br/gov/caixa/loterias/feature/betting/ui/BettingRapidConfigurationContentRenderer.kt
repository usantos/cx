package br.gov.caixa.loterias.feature.betting.ui

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingRapidConfigurationContentEntry
import br.gov.caixa.loterias.feature.betting.presentation.screen.RapidConfigurationActions
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState

internal class BettingRapidConfigurationContentRenderer : BettingRapidConfigurationContentEntry {
    @Composable
    override fun RapidConfigurationContent(
        state: RapidConfigurationUiState,
        pager: PagerState,
        summaryVisited: Boolean,
        onBack: () -> Unit,
        actions: RapidConfigurationActions,
    ) {
        br.gov.caixa.loterias.feature.betting.presentation.screen.RapidConfigurationContentDelivered(
            state,
            pager,
            summaryVisited,
            onBack,
            actions
        )
    }
}
