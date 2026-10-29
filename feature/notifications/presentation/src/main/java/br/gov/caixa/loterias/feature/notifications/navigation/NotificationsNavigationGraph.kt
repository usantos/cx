package br.gov.caixa.loterias.feature.notifications.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.gov.caixa.loterias.feature.notifications.presentation.route.NotificationsRoute
import br.gov.caixa.loterias.feature.notifications.presentation.screen.notificationScreens

fun NavGraphBuilder.notificationsGraph(appContainer: NotificationsNavigationDependencies, navController: NavHostController) {
    notificationScreens { "" }.forEach { spec ->
        composable(spec.route) {
            if (spec.route == "notifications/messages") {
                val central = viewModel(key = "central-notifications") { appContainer.centralNotificationsFactory.create() }
                br.gov.caixa.loterias.feature.notifications.presentation.route.CentralNotificationsRoute(
                    central,
                    navController::popBackStack
                )
                return@composable
            }
            val notificationsViewModel = viewModel(key = "notifications-${spec.route}") {
                appContainer.notificationViewModelFactory.create()
            }
            NotificationsRoute(
                route = spec.route,
                viewModel = notificationsViewModel,
                onBack = navController::popBackStack,
                onHelp = { navController.navigate("support/terms?tab=FAQ") }
            )
        }
    }
}
