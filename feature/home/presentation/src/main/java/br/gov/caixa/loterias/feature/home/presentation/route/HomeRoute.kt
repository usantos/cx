package br.gov.caixa.loterias.feature.home.presentation.route

import android.content.ActivityNotFoundException
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.domain.configuration.model.CampaignArea
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign as HomeCampaign
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.home.presentation.components.SimulationRecoveryPanel
import br.gov.caixa.loterias.feature.home.presentation.mapper.homeBettingAction
import br.gov.caixa.loterias.feature.home.presentation.mapper.toHomeLotteries
import br.gov.caixa.loterias.feature.home.presentation.mapper.withHomeMegaSenaCampaign
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeContent
import br.gov.caixa.loterias.feature.home.presentation.screen.HomePresentation
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomeViewModel
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import kotlinx.coroutines.launch

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
    onBetEntry: (br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision) -> Unit,
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

private data class HomeRouteContext(
    val viewModel: HomeViewModel,
    val preferencesViewModel: br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomePreferencesViewModel?,
    val userName: String?,
    val environmentLabel: String?,
    val onOpen: (String) -> Unit,
    val onProtectedAction: ((String) -> Unit)?,
    val onDrawerOpen: (String) -> Unit,
    val onLogout: () -> Unit,
    val requestedModalityId: Int?,
)

private data class HomeRoutePanels(
    val quick: androidx.compose.runtime.MutableState<Boolean>,
    val campaign: androidx.compose.runtime.MutableState<Boolean>,
    val presentation: HomePresentation,
    val onQuickHeight: (androidx.compose.ui.unit.Dp) -> Unit,
    val onCampaignHeight: (androidx.compose.ui.unit.Dp) -> Unit,
)

private data class HomeRouteScene(
    val homeState: br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState,
    val preferences: br.gov.caixa.loterias.feature.home.presentation.state.HomePreferencesUiState?,
    val lotteries: List<br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery>,
    val campaign: HomeCampaign?,
    val lifecycle: androidx.lifecycle.Lifecycle.State,
    val panels: HomeRoutePanels,
    val navigationBarInset: Float,
    val density: androidx.compose.ui.unit.Density,
    val classic: Boolean,
)

@Composable
private fun HomeRouteLayout(
    context: HomeRouteContext,
    scene: HomeRouteScene,
    drawerState: androidx.compose.material3.DrawerState,
    scope: kotlinx.coroutines.CoroutineScope,
    openWomenSupport: () -> Unit,
) {
    val homeResults = scene.homeState.results
    val resultsState = ResultsUiState(
        modality = homeResults.expandedModality,
        result = homeResults.expandedModality?.let(homeResults.cache::get),
        isLoading = homeResults.pendingPage != null,
        errorMessage = homeResults.error
    )
    HomeNavigationDrawer(
        drawerState, scene.navigationBarInset, scene.preferences, context.preferencesViewModel, scene.homeState,
        HomeDrawerIdentity(context.userName, context.environmentLabel),
        onClose = { scope.launch { drawerState.close() } },
        onExternalFailure = context.viewModel::externalBettingFailed,
        onOpen = { route ->
            scope.launch { drawerState.close() }
            val action = homeBettingAction(route)
            if (action != null &&
                context.onProtectedAction != null
            ) {
                context.onProtectedAction("home/rapid")
            } else if (action !=
                null
            ) {
                context.viewModel.startOtherBetting(action)
            } else {
                context.onDrawerOpen(route)
            }
        },
        onLogout = context.onLogout
    ) {
        if (LocalHomeRuntimeConfiguration.current.homeV2) {
            HomeV2RouteContent(
                cardOrder =
                    scene.preferences?.cardOrder
                        ?: br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomePreferencesViewModel.DEFAULT_ORDER,
                onMoveModality = { from, to -> context.preferencesViewModel?.moveModality(from, to) },
                homeState = scene.homeState,
                homeCampaign = scene.campaign,
                homeLotteries = scene.lotteries,
                density = scene.density,
                navigationBarInset = scene.navigationBarInset,
                onOpen = context.onOpen,
                onProtectedAction = context.onProtectedAction,
                viewModel = context.viewModel,
                onOpenMenu = { scope.launch { drawerState.open() } }
            )
        } else {
            HomeContent(
                homeState = scene.homeState, homeCampaign = scene.campaign, homeLotteries = scene.lotteries,
                resultsState = resultsState, requestedModalityId = context.requestedModalityId,
                onQuickAccessContentHeightChanged = scene.panels.onQuickHeight,
                onCampaignContentHeightChanged = scene.panels.onCampaignHeight,
                onHeaderActionsRevealed = context.viewModel::revealHeaderActions,
                presentation = scene.panels.presentation,
                actions = homeLegacyActions(
                    context.viewModel, scene.lotteries,
                    scene.panels.quick, scene.panels.campaign,
                    drawerState, scope, context.onOpen, context.onProtectedAction, openWomenSupport
                )
            )
        }
    }
}

@Composable
private fun HomeRouteOverlays(
    context: HomeRouteContext,
    scene: HomeRouteScene,
    failure: androidx.compose.runtime.MutableState<Boolean>,
    openWomenSupport: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val uriHandler = LocalUriHandler.current
    var womenSupportFailed by failure
    if (scene.classic && scene.homeState.results.pendingPage != null) {
        br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog()
    }
    if (scene.classic) {
        scene.homeState.results.error?.let { message ->
            CaixaDSDialog(
                title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
                message = message,
                positiveLabel = uiStrings.getString(LocalizedR.string.ui_ok_565339),
                onPositive = context.viewModel.resultActions::dismissError
            )
        }
    }
    if (womenSupportFailed) {
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_abrir_o_whatsapp_do_dbe6b0),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            onPositive = openWomenSupport,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
            onNegative = { womenSupportFailed = false }
        )
    }
    HomeRouteDialogs(
        homeState = scene.homeState,
        lifecycleState = scene.lifecycle,
        onOpen = context.onOpen,
        actions = HomeDialogActions(
            acknowledgeLocalCartReview = context.viewModel::acknowledgeLocalCartReview,
            acknowledgeUnidentifiedPayment = context.viewModel::acknowledgeUnidentifiedPayment,
            closeNotification = context.viewModel::closeNotification,
            dismissNotificationError = context.viewModel::dismissNotificationError,
            acceptNotificationNotice = context.viewModel::acceptNotificationNotice,
            dismissNotificationNotice = context.viewModel::dismissNotificationNotice,
            dismissBetEntryError = context.viewModel::dismissBetEntryError,
            openNotificationLink = {
                context.viewModel.notificationLink()?.let { link ->
                    try {
                        uriHandler.openUri(link)
                    } catch (_: Exception) {
                        context.viewModel.notificationLinkFailed()
                    }
                }
            }
        )
    )
    scene.preferences?.errorMessage?.let { message ->
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = message,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_ok_565339),
            onPositive = { context.preferencesViewModel?.dismissError() }
        )
    }
}

@Composable
private fun HomeRouteSystemBars(classicHome: Boolean, activity: android.app.Activity?) {
    val window = activity?.window
    val view = LocalView.current
    br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSStatusBarColor(
        if (classicHome) {
            br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens.White
        } else {
            br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens.Blue700
        }
    )
    SideEffect { updateHomeSystemBars(window, view) }
}

@Composable
private fun womenSupportOpener(failure: androidx.compose.runtime.MutableState<Boolean>): () -> Unit {
    val uriHandler = LocalUriHandler.current
    return {
        failure.value = try {
            uriHandler.openUri("https://api.whatsapp.com/send?phone=5561996100180&text=Disque180")
            false
        } catch (_: ActivityNotFoundException) {
            true
        } catch (_: SecurityException) {
            true
        } catch (_: IllegalArgumentException) {
            true
        }
    }
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
    val megaCampaign = br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSMegaSenaCampaign.current
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
