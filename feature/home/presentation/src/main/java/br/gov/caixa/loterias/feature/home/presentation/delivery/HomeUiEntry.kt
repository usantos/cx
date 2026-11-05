package br.gov.caixa.loterias.feature.home.presentation.delivery

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign as HomeCampaign
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.contract.account.model.AccountNotification
import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeActions
import br.gov.caixa.loterias.feature.home.presentation.screen.HomePresentation
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState

interface HomeUiEntry : FeatureUiEntry {
    @Composable
    fun HomeContent(
        homeState: HomeUiState,
        homeCampaign: HomeCampaign?,
        homeLotteries: List<HomeLottery>,
        resultsState: ResultsUiState,
        requestedModalityId: Int?,
        presentation: HomePresentation,
        actions: HomeActions,
        onQuickAccessContentHeightChanged: (Dp) -> Unit,
        onCampaignContentHeightChanged: (Dp) -> Unit,
        onHeaderActionsRevealed: () -> Unit,
    )

    @Composable
    fun HomeV2Content(
        lotteries: List<HomeLottery>,
        loading: Boolean,
        results: Map<String, DrawResult>,
        cardOrder: List<String>,
        onMoveModality: (String, String) -> Unit,
        actions: HomeV2Presentation.Actions,
        campaign: LoteriasCaixaDSCampaign,
        megaSenaCampaign: LoteriasCaixaDSCampaign,
    )

    @Composable
    fun HomeDrawer(
        features: HomeFeatureConfiguration,
        preferenceState: br.gov.caixa.loterias.feature.home.presentation.state.HomePreferencesUiState?,
        drawerOpen: Boolean,
        userName: String?,
        environmentLabel: String?,
        campaign: br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign?,
        actions: HomeDrawerActions,
    )

    @Composable
    fun HomePaymentNotice(onOpenPurchases: () -> Unit)

    @Composable
    fun HomeNotificationPopup(notification: AccountNotification, onClose: () -> Unit, onOpenLink: () -> Unit)

    @Composable
    fun PresentationHome(
        presentation: HomeV2Presentation,
        modalidades: List<HomeLottery>,
        loading: Boolean,
        results: Map<String, DrawResult>,
    )

    @Composable
    fun HomeLoadingState(loading: Boolean, errorMessage: String?, onRetry: () -> Unit)
}
