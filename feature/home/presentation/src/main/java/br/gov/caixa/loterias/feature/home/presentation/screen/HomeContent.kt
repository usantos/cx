package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign as HomeCampaign
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.home.presentation.delivery.HomeUiEntry
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState

@Composable
fun HomeContent(
    homeState: HomeUiState,
    homeCampaign: HomeCampaign?,
    homeLotteries: List<HomeLottery>,
    resultsState: ResultsUiState,
    requestedModalityId: Int?,
    presentation: HomePresentation,
    actions: HomeActions,
    onQuickAccessContentHeightChanged: (Dp) -> Unit = {},
    onCampaignContentHeightChanged: (Dp) -> Unit = {},
    onHeaderActionsRevealed: () -> Unit = {},
) {
    FeatureModuleContent(
        module = AppModule.HOME,
        entryType = HomeUiEntry::class.java,
        onBack = {},
        access = FeatureEntryAccess.OPTIONAL,
        preserveActive = false
    ) { entry ->
        entry.HomeContent(
            homeState,
            homeCampaign,
            homeLotteries,
            resultsState,
            requestedModalityId,
            presentation,
            actions,
            onQuickAccessContentHeightChanged,
            onCampaignContentHeightChanged,
            onHeaderActionsRevealed
        )
    }
}
