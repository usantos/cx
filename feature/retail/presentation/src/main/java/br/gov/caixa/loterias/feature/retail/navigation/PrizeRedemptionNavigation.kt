package br.gov.caixa.loterias.feature.retail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions

internal fun NavGraphBuilder.registerPrizeRedemptionRoute(
    appContainer: RetailNavigationDependencies,
    navController: NavHostController,
    session: SessionNavigationActions,
    actions: AppNavigationActions,
) {
    composable(
        route = "retail/prize/{betId}",
        arguments = listOf(navArgument("betId") { type = NavType.LongType })
    ) { entry ->
        val betId = requireNotNull(entry.arguments).getLong("betId")
        PrizeRedemptionDestination(
            appContainer = appContainer,
            betId = betId,
            keySuffix = betId.toString(),
            onForegroundHostChanged = { session.state.prizeChannelHost = it },
            onRedirect = actions.onPrizeRedirect,
            onBetEntry = actions.onPrizeBetEntry,
            onBack = navController::popBackStack,
            onHelp = { navController.navigate("support/terms?tab=FAQ") },
            onHome = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                }
            },
            onMyBets = {
                navController.navigate("tickets/my-bets") {
                    popUpTo("tickets/my-bets") { inclusive = false }
                    launchSingleTop = true
                }
            }
        )
    }
}
