package br.gov.caixa.loterias.feature.results.navigation

import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import br.gov.caixa.loterias.feature.results.presentation.route.ResultsRoute
import br.gov.caixa.loterias.feature.results.presentation.viewmodel.ResultsViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

fun NavGraphBuilder.resultsGraph(
    appContainer: ResultsNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
    actions: AppNavigationActions,
) {
    composable("results/confirmed-bet") {
        val bet = navigation.details.selectedBet ?: if (appContainer.mockBackendEnabled) {
            appContainer.visualConfirmedBet("").copy(id = 100L)
        } else null
        if (bet == null || bet.id <= 0) {
            LaunchedEffect(Unit) {
                if (!navController.popBackStack()) {
                    navController.navigate("tickets/my-bets") {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        } else {
            val resultModel = viewModel(key = "confirmed-results-${bet.id}") {
                br.gov.caixa.loterias.feature.results.presentation.viewmodel.ConfirmedBetResultsViewModel(
                    bet, appContainer.confirmedBetResults, appContainer.dispatchers,
                )
            }
            br.gov.caixa.loterias.feature.results.presentation.route.ConfirmedBetResultRoute(
                bet = bet, viewModel = resultModel, onBack = navController::popBackStack,
                onComplete = { navController.navigate("tickets/my-bets") { launchSingleTop = true } },
                onServiceRedirect = actions.onTicketServiceRedirect,
                onRedeemPrize = { navController.navigate("retail/prize/${bet.id}") },
            )
        }
    }
    composable(
        "results/home/{modality}",
        arguments = listOf(navArgument("modality") { type = NavType.StringType }),
    ) { entry ->
        val modality = entry.arguments?.getString("modality").orEmpty()
        val model = viewModel(key = "home-result-$modality") {
            ResultsViewModel(appContainer.latestResult, appContainer.resultByContest, appContainer.dispatchers)
        }
        LaunchedEffect(modality) { model.selectModality(modality) }
        ResultsRoute(model, navController::popBackStack, showDetail = true)
    }
    listOf("results/modalities", "results/detail").forEach { route ->
        composable(route) {
            val resultsViewModel = viewModel(key = "results-$route") {
                ResultsViewModel(
                    appContainer.latestResult,
                    appContainer.resultByContest,
                    appContainer.dispatchers,
                )
            }
            ResultsRoute(
                resultsViewModel,
                navController::popBackStack,
                showDetail = route == "results/detail",
            )
        }
    }
}
