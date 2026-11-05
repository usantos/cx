package br.gov.caixa.loterias.feature.home.ui

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign as HomeCampaign
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.contract.account.model.AccountNotification
import br.gov.caixa.loterias.feature.home.presentation.components.RenderHomeDrawer
import br.gov.caixa.loterias.feature.home.presentation.components.RenderHomeNotificationPopup
import br.gov.caixa.loterias.feature.home.presentation.components.RenderHomePaymentNotice
import br.gov.caixa.loterias.feature.home.presentation.delivery.HomeDrawerActions
import br.gov.caixa.loterias.feature.home.presentation.delivery.HomeUiEntry
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeActions
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeComposition
import br.gov.caixa.loterias.feature.home.presentation.screen.HomePresentation
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.presentation.screen.RenderHomeContent
import br.gov.caixa.loterias.feature.home.presentation.screen.RenderHomeV2Content
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState

@Keep
class HomeUiEntryImpl : HomeUiEntry {
    @Composable
    override fun HomeContent(
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
    ) {
        RenderHomeContent(
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

    @Composable
    override fun HomeV2Content(
        lotteries: List<HomeLottery>,
        loading: Boolean,
        results: Map<String, DrawResult>,
        cardOrder: List<String>,
        onMoveModality: (String, String) -> Unit,
        actions: HomeV2Presentation.Actions,
        campaign: LoteriasCaixaDSCampaign,
        megaSenaCampaign: LoteriasCaixaDSCampaign,
    ) {
        RenderHomeV2Content(lotteries, loading, results, cardOrder, onMoveModality, actions, campaign, megaSenaCampaign)
    }

    @Composable
    override fun HomeDrawer(
        features: HomeFeatureConfiguration,
        preferenceState: br.gov.caixa.loterias.feature.home.presentation.state.HomePreferencesUiState?,
        drawerOpen: Boolean,
        userName: String?,
        environmentLabel: String?,
        campaign: br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign?,
        actions: HomeDrawerActions,
    ) {
        RenderHomeDrawer(
            features,
            preferenceState,
            actions.toggleShake,
            actions.consumeShakeTooltip,
            drawerOpen,
            userName,
            environmentLabel,
            campaign,
            actions.open,
            actions.logout,
            actions.close,
            actions.externalFailure
        )
    }

    @Composable
    override fun HomePaymentNotice(onOpenPurchases: () -> Unit) {
        RenderHomePaymentNotice(onOpenPurchases)
    }

    @Composable
    override fun HomeNotificationPopup(notification: AccountNotification, onClose: () -> Unit, onOpenLink: () -> Unit) {
        RenderHomeNotificationPopup(notification, onClose, onOpenLink)
    }

    @Composable
    override fun PresentationHome(
        presentation: HomeV2Presentation,
        modalidades: List<HomeLottery>,
        loading: Boolean,
        results: Map<String, DrawResult>,
    ) {
        presentation.HomeComposition(modalidades, loading, results)
    }

    @Composable
    override fun HomeLoadingState(loading: Boolean, errorMessage: String?, onRetry: () -> Unit) {
        br.gov.caixa.loterias.feature.home.presentation.components.RenderHomeLoadingState(loading, errorMessage, onRetry)
    }
}
