package br.gov.caixa.loterias.feature.home.presentation.route

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.home.presentation.mapper.homeBettingAction
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeContent
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import kotlinx.coroutines.launch

@Composable
internal fun HomeRouteLayout(
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
