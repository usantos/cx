package br.gov.caixa.loterias.feature.betting.navigation

import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.destination.PoolTutorialRoute
import br.gov.caixa.loterias.core.navigation.workflow.nextDestination
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingRoute
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingFlowViewModel
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

fun NavGraphBuilder.bettingTutorialGraph(
    appContainer: BettingNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
    allScreens: List<WorkflowSpec>,
) {
    allScreens.filterNot { it.route.startsWith("responsible/") || it.route.startsWith("onboarding/") ||
        it.route.startsWith("account/") || it.route in setOf(
        "account/login",
        "account/personal-data",
        "account/register",
        "cart/current",
        "cart/included",
        "cart/pool-included",
        "cart/favorite-carts",
        "cart/favorite-detail",
        "tickets/bet-detail",
        "tickets/my-bets",
        "tickets/purchases",
        "tickets/purchase-detail",
        "tickets/manual-code",
        "tickets/read",
        "tickets/read-game-choice",
        "tickets/read-result",
        "betting/pools",
        "betting/home",
        "betting/pool-view",
        "betting/pool-detail",
        "betting/modalities",
        "betting/simulation",
        "betting/rapid",
        "betting/rapid-config",
        "betting/rapid-list",
        "betting/favorites",
        "betting/combos",
        "betting/combo-detail",
        "payment/method",
        "payment/cards",
        "payment/add-card",
        "payment/card-preview",
        "payment/confirm-card",
        "payment/cvv",
        "payment/confirmation",
        "payment/failure",
        "payment/pix-copy",
        "payment/pix-expired",
        "payment/pix-guidance",
        "payment/success",
        "results/modalities",
        "results/detail",
        "results/confirmed-bet",
        "retail/search",
        "retail/location",
        "retail/prize",
        "retail/prize-guidance",
        "retail/prize-lottery",
        "retail/prize-agency",
        "retail/prize-pix",
        "retail/prize-confirmation",
        "retail/prize-mercado-pago",
        "retail/prize-mercado-pago-success",
        "support",
        "support/terms",
        "support/terms-view",
        "support/terms-acceptance",
        "support/game-help",
        "notifications/messages",
        "notifications/preferences",
    ) }.forEach { spec ->
        composable(
            route = if (spec.route == PoolTutorialRoute.base) PoolTutorialRoute.pattern else spec.route,
            arguments = if (spec.route == PoolTutorialRoute.base) listOf(
                navArgument("modalityId") { type = NavType.IntType; defaultValue = 0 },
                navArgument("contestTypeId") { type = NavType.IntType; defaultValue = 1 },
            ) else emptyList(),
        ) { entry ->
            val onComplete = {
                if (spec.route == PoolTutorialRoute.base) {
                    val modality = entry.arguments?.getInt("modalityId") ?: 0
                    navigation.details.marketplaceFilter = if (modality > 0) MarketplaceFilter.forModality(
                        modality, entry.arguments?.getInt("contestTypeId") ?: 1,
                    ) else MarketplaceFilter.empty()
                    navigation.details.marketplaceClearFilter = navigation.details.marketplaceFilter
                    navigation.details.lastAppliedFilterRetailer = null
                    navController.navigate("betting/pools") {
                        popUpTo(PoolTutorialRoute.pattern) { inclusive = true }
                    }
                } else nextDestination(spec.route)?.let { destination ->
                    navController.navigate(destination)
                } ?: navController.popBackStack()
                Unit
            }
            when {
                spec.route.startsWith("betting/") -> {
                    val bettingViewModel = viewModel(key = "betting-flow-${spec.route}") {
                        BettingFlowViewModel(spec, appContainer.setBettingTutorialDismissed)
                    }
                    BettingRoute(bettingViewModel, navController::popBackStack, onComplete)
                }
                else -> error("Rota sem layout Compose específico: ${spec.route}")
            }
        }
    }
}
