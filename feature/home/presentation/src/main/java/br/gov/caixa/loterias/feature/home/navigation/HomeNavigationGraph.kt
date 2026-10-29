package br.gov.caixa.loterias.feature.home.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.destination.PoolTutorialRoute
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.authenticatedContinuation
import br.gov.caixa.loterias.core.navigation.workflow.openVisitorDrawerDestination
import br.gov.caixa.loterias.core.navigation.workflow.requiresVisitorLogin
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.route.HomeRoute
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomeViewModel
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter

fun NavGraphBuilder.homeGraph(
    appContainer: HomeNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
    session: SessionNavigationActions,
    actions: AppNavigationActions,
) {
    composable("home") { HomeDestination(appContainer, navController, navigation, session, actions) }
}

@androidx.compose.runtime.Composable
private fun HomeDestination(
    appContainer: HomeNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
    session: SessionNavigationActions,
    actions: AppNavigationActions,
) {
    val externalUriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    val ownerKey = session.identity?.ownerKey
    val preferencesViewModel = viewModel(key = "home-preferences-${ownerKey ?: "visitante"}") {
        br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomePreferencesViewModel(
            appContainer.homePreferences,
            appContainer.dispatchers,
            ownerKey
        )
    }
    if (!homePreferencesReady(preferencesViewModel)) return
    val homeViewModel = rememberHomeViewModel(appContainer)
    HomeReturnEffects(appContainer, navController, session, homeViewModel)
    val openTicketScanner = rememberHomeTicketScannerEntry { navController.navigate("tickets/read") }
    val handlers =
        HomeDestinationHandlers(
            appContainer,
            navController,
            navigation,
            session,
            actions,
            homeViewModel,
            externalUriHandler,
            openTicketScanner
        )
    HomeDestinationRoute(appContainer, navigation, session, homeViewModel, preferencesViewModel, handlers)
}

@androidx.compose.runtime.Composable
private fun homePreferencesReady(
    preferencesViewModel: br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomePreferencesViewModel,
): Boolean {
    val preferenceState by preferencesViewModel.state.collectAsStateWithLifecycle()
    if (!preferenceState.loaded) {
        preferenceState.errorMessage?.let { message ->
            br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog(
                title = stringResource(
                    LocalizedR.string.ui_atencao_b0f8a4
                ),
                message = message,
                positiveLabel = stringResource(LocalizedR.string.ui_tentar_novamente_b9e106),
                onPositive = { preferencesViewModel.load() }
            )
        }
        return false
    }
    if (!preferenceState.shakeTutorialCompleted) {
        br.gov.caixa.loterias.feature.betting.presentation.route.ShakeTutorialRoute {
            preferencesViewModel.completeShakeTutorial()
        }
        preferenceState.errorMessage?.let { message ->
            br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog(
                title = stringResource(
                    LocalizedR.string.ui_atencao_b0f8a4
                ),
                message = message,
                positiveLabel = stringResource(LocalizedR.string.ui_tentar_novamente_b9e106),
                onPositive = { preferencesViewModel.completeShakeTutorial() }
            )
        }
        return false
    }
    return true
}

@androidx.compose.runtime.Composable
private fun rememberHomeViewModel(appContainer: HomeNavigationDependencies): HomeViewModel = viewModel(key = "home") {
    HomeViewModel(
        appContainer.bettingParameters,
        appContainer.homeFeatureConfiguration,
        appContainer.betCombos,
        appContainer.socialTransfers,
        appContainer.currentCart,
        appContainer.prepareBetEntry,
        appContainer.preparePoolEntry,
        appContainer.prepareHomeBettingAction,
        appContainer.observeSessionContent,
        appContainer.readLatestNotification,
        appContainer.dispatchers,
        appContainer.networkAvailability,
        appContainer.localClock,
        getUnreadNotificationCount = appContainer.unreadNotificationCount,
        selectHomeResaleOffers = appContainer.selectHomeResaleOffers,
        getLatestResult = appContainer.latestResult
    )
}

@androidx.compose.runtime.Composable
private fun HomeReturnEffects(
    appContainer: HomeNavigationDependencies,
    navController: NavHostController,
    session: SessionNavigationActions,
    homeViewModel: HomeViewModel,
) {
    LaunchedEffect(session.state.visitorReturnRoute) {
        val requested = session.state.visitorReturnRoute ?: return@LaunchedEffect
        if (appContainer.currentUserIdentity.currentUser() != null) {
            val route =
                authenticatedContinuation(
                    requested,
                    session.state.visitorReturnOwnerKey,
                    appContainer.authenticatedSessionIdentity.captureIdentity()?.ownerKey
                )
            session.state.visitorReturnRoute = null
            session.state.visitorReturnOwnerKey = null
            if (route == null) return@LaunchedEffect
            if (route.startsWith("home/")) {
                session.state.resumedHomeAction = route
            } else {
                navController.navigate(route) {
                    launchSingleTop =
                        true
                    popUpTo("home") { inclusive = false }
                }
            }
        }
    }
    val resumeHomeState by homeViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(session.state.resumedHomeAction, resumeHomeState.loading, resumeHomeState.parametersUnavailable) {
        val route = session.state.resumedHomeAction ?: return@LaunchedEffect
        if (resumeHomeState.loading || resumeHomeState.parametersUnavailable) return@LaunchedEffect
        session.state.resumedHomeAction = null
        when {
            route == "home/rapid" -> homeViewModel.startOtherBetting(
                br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingAction.RAPID
            )

            route == "home/combo" -> homeViewModel.startOtherBetting(
                br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingAction.COMBO
            )

            route.startsWith("home/pool/") -> route.split('/').let { homeViewModel.startPools(it[2].toInt(), it[POOL_CONTEST_TYPE_INDEX]) }
        }
    }
}

private class HomeDestinationHandlers(
    private val appContainer: HomeNavigationDependencies,
    private val navController: NavHostController,
    private val navigation: AppNavigationState,
    private val session: SessionNavigationActions,
    private val actions: AppNavigationActions,
    private val homeViewModel: HomeViewModel,
    private val externalUriHandler: androidx.compose.ui.platform.UriHandler,
    private val openTicketScanner: () -> Unit,
) {
    fun bet(decision: BetEntryDecision) {
        actions.handleBetEntry(decision, false)
    }
    fun open(route: String) {
        if (route == "tickets/read") {
            openTicketScanner()
        } else if (requiresVisitorLogin(route) &&
            appContainer.currentUserIdentity.currentUser() == null
        ) {
            openVisitorDrawerDestination(route, navController, session.requestVisitorLogin)
        } else {
            navController.navigate(route)
        }
    }
    fun protect(route: String) {
        if (appContainer.currentUserIdentity.currentUser() == null) {
            if (route == "home/rapid") {
                openVisitorDrawerDestination("betting/rapid", navController, session.requestVisitorLogin)
            } else {
                session.requestVisitorLogin(route)
            }
        } else {
            when {
                route == "home/rapid" -> homeViewModel.startOtherBetting(
                    br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingAction.RAPID
                )

                route == "home/combo" -> homeViewModel.startOtherBetting(
                    br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingAction.COMBO
                )

                route.startsWith("home/pool/") -> route.split('/').let {
                    homeViewModel.startPools(it[2].toInt(), it[POOL_CONTEST_TYPE_INDEX])
                }
            }
        }
    }
    fun pool(decision: br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryDecision) {
        when (decision) {
            is br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryDecision.Ready -> {
                navigation.details.marketplaceFilter = MarketplaceFilter.forModality(
                    decision.request.modalityId,
                    if (decision.request.contestType == "ESPECIAL") 2 else 1
                )
                navigation.details.marketplaceClearFilter = navigation.details.marketplaceFilter
                navigation.details.marketplaceFilterApplied = false
                navigation.details.lastAppliedFilterRetailer = null
                navController.navigate(if (decision.showTutorial) PoolTutorialRoute.from(decision.request) else "betting/pools")
            }

            is br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryDecision.CompleteAccount ->
                actions.handleBetEntry(BetEntryDecision.CompleteAccount(decision.destination), false)
        }
    }
    fun other(decision: br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision) {
        when (decision) {
            is br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision.CompleteAccount ->
                actions.handleBetEntry(BetEntryDecision.CompleteAccount(decision.destination), false)

            is br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision.Ready -> {
                val destination = homeBettingDestination(decision.target)
                if (decision.target == br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingTarget.INSTANT) {
                    runCatching { externalUriHandler.openUri(destination) }
                        .onFailure { homeViewModel.externalBettingFailed() }
                } else {
                    navController.navigate(destination)
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun HomeDestinationRoute(
    appContainer: HomeNavigationDependencies,
    navigation: AppNavigationState,
    session: SessionNavigationActions,
    homeViewModel: HomeViewModel,
    preferencesViewModel: br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomePreferencesViewModel,
    handlers: HomeDestinationHandlers,
) {
    HomeRoute(
        preferencesViewModel = preferencesViewModel,
        networkAvailability = appContainer.networkAvailability,
        userName = appContainer.currentUserIdentity.currentUser()?.name,
        environmentLabel = appContainer.homeNavigationEnvironmentLabel,
        onOpen = handlers::open,
        onDrawerOpen = handlers::open,
        onProtectedAction = handlers::protect,
        onPoolEntry = handlers::pool,
        onLogout = session.onLogout,
        viewModel = homeViewModel,
        requestedModalityId = navigation.requestedHomeModalityId,
        onBetEntry = handlers::bet,
        onOtherBetting = handlers::other
    )
}

private const val POOL_CONTEST_TYPE_INDEX = 3
