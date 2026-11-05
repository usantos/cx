package br.gov.caixa.loterias.feature.home.presentation.route

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.domain.configuration.model.CampaignArea
import br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.home.presentation.components.SimulationRecoveryPanel
import br.gov.caixa.loterias.feature.home.presentation.mapper.toHomeLotteries
import br.gov.caixa.loterias.feature.home.presentation.mapper.withHomeMegaSenaCampaign
import br.gov.caixa.loterias.feature.home.presentation.screen.HomePresentation
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomeViewModel
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign as HomeCampaign

@Composable
fun HomeRoute(
    userName: String?,
    networkAvailability: br.gov.caixa.loterias.core.common.network.NetworkAvailability,
    environmentLabel: String?,
    onOpen: (String) -> Unit,
    onPoolEntry: (br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryDecision) -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel,
    requestedModalityId: Int? = null,
    onProtectedAction: ((String) -> Unit)? = null,
    preferencesViewModel: br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomePreferencesViewModel? = null,
    onBetEntry: (br.gov.caixa.loterias.core.domain.contract.betting.model.BetEntryDecision) -> Unit,
    onOtherBetting: (br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision) -> Unit,
    onDrawerOpen: (String) -> Unit = onOpen,
) {
    val activity = LocalContext.current.findActivity()
    val classicHome = !LocalHomeRuntimeConfiguration.current.homeV2
    HomeRouteSystemBars(classicHome, activity)
    val quickAccessExpandedState = rememberSaveable { mutableStateOf(false) }
    var quickAccessExpanded by quickAccessExpandedState
    val womenSupportFailure = rememberSaveable { mutableStateOf(false) }
    val openWomenSupport = womenSupportOpener(womenSupportFailure)
    val preferenceState = preferencesViewModel?.state?.collectAsStateWithLifecycle()?.value
    val homeState by viewModel.state.collectAsStateWithLifecycle()
    if (homeModuleUnavailable(homeState, viewModel, networkAvailability) { activity?.finish() }) return

    val homeCampaign = homeState.features.campaigns.active(CampaignArea.HOME)
    val octoberRoseExpandedState = rememberSaveable(homeCampaign) { mutableStateOf(false) }
    var octoberRoseExpanded by octoberRoseExpandedState
    val octoberRosePanelVisible = octoberRoseExpanded && homeCampaign == HomeCampaign.OUTUBRO_ROSA
    val lifecycleState by androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    HomeRouteEffects(
        homeState,
        lifecycleState,
        viewModel,
        onOpen,
        onBetEntry,
        onOtherBetting,
        onPoolEntry,
        refreshHomeOnResume = !LocalHomeRuntimeConfiguration.current.homeV2
    )
    val homeLotteries = rememberHomeLotteries(homeState)
    HomeVisibilityEffect(viewModel, homeLotteries, classicHome, lifecycleState)
    val homeResults = homeState.results
    val density = LocalDensity.current
    val navigationBarInset = WindowInsets.navigationBars.getBottom(density).toFloat()
    var quickAccessContentHeight by remember { mutableStateOf(0.dp) }
    var campaignContentHeight by remember { mutableStateOf(0.dp) }
    val (carouselOffset, cloverRotation, quickAccessToggleEnabled, campaignRotation, footerScrimAlpha) = homePanelAnimations(
        quickAccessExpanded,
        octoberRosePanelVisible,
        quickAccessContentHeight + 16.dp,
        campaignContentHeight
    )
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    HomeShakeGuidance(preferenceState, homeState, homeLotteries, classicHome, lifecycleState, drawerState)
    val scope = rememberCoroutineScope()
    val drawerOpen = drawerState.isOpen
    HomeExitConfirmation(enabled = !drawerOpen) { activity?.finish() }
    if (homeRecoveryGate(homeState, viewModel, networkAvailability)) return
    val context = HomeRouteContext(
        viewModel, preferencesViewModel, userName, environmentLabel, onOpen,
        onProtectedAction, onDrawerOpen, onLogout, requestedModalityId
    )
    val panels = HomeRoutePanels(
        quickAccessExpandedState,
        octoberRoseExpandedState,
        HomePresentation(
            quickAccessExpanded,
            homeResults.expandedPage,
            octoberRosePanelVisible,
            carouselOffset,
            cloverRotation,
            quickAccessToggleEnabled,
            campaignRotation,
            footerScrimAlpha
        ),
        { quickAccessContentHeight = it },
        { campaignContentHeight = it }
    )
    val scene = HomeRouteScene(
        homeState, preferenceState, homeLotteries, homeCampaign, lifecycleState,
        panels, navigationBarInset, density, classicHome
    )
    HomeRouteLayout(context, scene, drawerState, scope, openWomenSupport)
    HomeRouteOverlays(context, scene, womenSupportFailure, openWomenSupport)
}

@Composable
private fun rememberHomeLotteries(
    homeState: br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState,
): List<br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery> {
    val baseHomeLotteries =
        remember(
            homeState.parameters,
            homeState.resaleOffers,
            homeState.loading,
            homeState.combos,
            homeState.features.specialContests,
            homeState.features.poolSalesEnabled
        ) {
            homeState.parameters.toHomeLotteries(
                hasCombos = homeState.combos.isNotEmpty(),
                specialContests = homeState.features.specialContests,
                poolSalesEnabled = homeState.features.poolSalesEnabled,
                resaleOffers = homeState.resaleOffers
            )
        }
    val megaCampaign = br.gov.caixa.loterias.core.designsystem.theme.LocalLoteriasCaixaDSMegaSenaCampaign.current
    return remember(baseHomeLotteries, megaCampaign) {
        baseHomeLotteries.withHomeMegaSenaCampaign(megaCampaign)
    }
}

@Composable
private fun HomeVisibilityEffect(
    viewModel: HomeViewModel,
    homeLotteries: List<br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery>,
    classicHome: Boolean,
    lifecycleState: androidx.lifecycle.Lifecycle.State,
) {
    val serverTime = LocalHomeRuntimeConfiguration.current.serverTime
    val resaleDeadlines = homeLotteries.map { it.poolResaleClosingDateTime }
    val visible = classicHome && lifecycleState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)
    androidx.compose.runtime.DisposableEffect(viewModel, visible, resaleDeadlines, serverTime) {
        viewModel.visibility.update(visible, resaleDeadlines, serverTime)
        onDispose { viewModel.visibility.update(false, emptyList(), serverTime) }
    }
}

@Composable
private fun HomeShakeGuidance(
    preferenceState: br.gov.caixa.loterias.feature.home.presentation.state.HomePreferencesUiState?,
    homeState: br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState,
    homeLotteries: List<br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery>,
    classicHome: Boolean,
    lifecycleState: androidx.lifecycle.Lifecycle.State,
    drawerState: androidx.compose.material3.DrawerState,
) {
    HomeShakeDrawerEffect(
        pending = preferenceState?.loaded == true && preferenceState.shakeTooltipPending,
        homeReady = !homeState.loading && !homeState.loadingCart && !homeState.loadingSocialTransfers &&
            !homeState.parametersUnavailable && homeLotteries.isNotEmpty() &&
            (!classicHome || homeState.headerActionsRevealed) &&
            lifecycleState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED),
        openDrawer = { drawerState.open() }
    )
}

@Composable
private fun homeRecoveryGate(
    homeState: br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState,
    viewModel: HomeViewModel,
    networkAvailability: br.gov.caixa.loterias.core.common.network.NetworkAvailability,
): Boolean {
    if (homeState.parametersUnavailable) {
        SimulationRecoveryPanel(
            loading = homeState.loading,
            message = homeState.parameterRetryMessage,
            onRetry = viewModel::load,
            onBack = viewModel::dismissParametersUnavailable,
            onDismissMessage = viewModel::dismissParameterRetryMessage,
            networkAvailability = networkAvailability
        )
        return true
    }
    return false
}

@Composable
private fun homeModuleUnavailable(
    homeState: br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState,
    viewModel: HomeViewModel,
    networkAvailability: br.gov.caixa.loterias.core.common.network.NetworkAvailability,
    onExit: () -> Unit,
): Boolean {
    if (br.gov.caixa.loterias.core.presentation.delivery.featureModuleEnabled(
            br.gov.caixa.loterias.core.domain.configuration.model.AppModule.HOME
        )
    ) {
        return false
    }
    if (!homeRecoveryGate(homeState, viewModel, networkAvailability)) {
        br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent(
            module = br.gov.caixa.loterias.core.domain.configuration.model.AppModule.HOME,
            entryType = br.gov.caixa.loterias.feature.home.presentation.delivery.HomeUiEntry::class.java,
            onBack = onExit
        ) { }
    }
    return true
}
