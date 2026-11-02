package br.gov.caixa.loterias.feature.retail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions

fun NavGraphBuilder.retailGraph(
    appContainer: RetailNavigationDependencies,
    navController: NavHostController,
    session: SessionNavigationActions,
    actions: AppNavigationActions,
) {
    registerPrizeRedemptionRoute(appContainer, navController, session, actions)
    registerRetailLocationRoute(appContainer, navController)
    registerMockPrizeRoutes(appContainer, navController, actions)
    registerRetailSearchRoute(appContainer, navController)
}
