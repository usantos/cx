package br.gov.caixa.loterias.feature.retail.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import kotlinx.coroutines.flow.first

internal fun NavGraphBuilder.registerMockPrizeRoutes(
    appContainer: RetailNavigationDependencies,
    navController: NavHostController,
    actions: AppNavigationActions,
) {
    mapOf(
        "retail/prize" to Triple<PrizeRedemptionChannel?, PrizeRedemptionChannel?, String>(null, null, "options"),
        "retail/prize-guidance" to Triple(PrizeRedemptionChannel.LOTTERY, null, "guidance"),
        "retail/prize-lottery" to Triple(PrizeRedemptionChannel.LOTTERY, null, "lottery"),
        "retail/prize-agency" to Triple(PrizeRedemptionChannel.AGENCY, null, "agency"),
        "retail/prize-pix" to Triple(PrizeRedemptionChannel.PIX, null, "pix"),
        "retail/prize-confirmation" to Triple(null, PrizeRedemptionChannel.PIX, "pix-success"),
        "retail/prize-mercado-pago" to Triple(PrizeRedemptionChannel.MERCADO_PAGO, null, "mp"),
        "retail/prize-mercado-pago-success" to Triple(null, PrizeRedemptionChannel.MERCADO_PAGO, "mp-success")
    ).forEach { (route, setup) ->
        composable(route) {
            if (!appContainer.mockBackendEnabled) {
                LaunchedEffect(Unit) {
                    navController.navigate("tickets/my-bets") {
                        popUpTo(route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                return@composable
            }
            PrizeRedemptionDestination(
                appContainer = appContainer,
                betId = MOCK_PRIZE_BET_ID,
                keySuffix = "alias-${setup.third}",
                onRedirect = actions.onPrizeRedirect,
                onBetEntry = actions.onPrizeBetEntry,
                initialChannel = setup.first,
                previewSuccessChannel = setup.second,
                onBack = navController::popBackStack,
                onHelp = { navController.navigate("support/terms?tab=FAQ") },
                onHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onMyBets = {
                    navController.navigate("tickets/my-bets") { launchSingleTop = true }
                }
            )
        }
    }
}

private const val MOCK_PRIZE_BET_ID = 100L
