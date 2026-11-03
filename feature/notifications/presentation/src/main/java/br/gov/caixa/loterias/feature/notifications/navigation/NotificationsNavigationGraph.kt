package br.gov.caixa.loterias.feature.notifications.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleAdmission
import br.gov.caixa.loterias.core.presentation.delivery.rememberFeatureWritePermission
import br.gov.caixa.loterias.feature.notifications.presentation.route.NotificationsRoute
import br.gov.caixa.loterias.feature.notifications.presentation.screen.notificationScreens

fun NavGraphBuilder.notificationsGraph(appContainer: NotificationsNavigationDependencies, navController: NavHostController) {
    notificationScreens { "" }.forEach { spec ->
        composable(spec.route) {
            FeatureModuleAdmission(
                module = AppModule.NOTIFICATIONS,
                onBack = navController::popBackStack
            ) {
                val isEntryEnabled = rememberFeatureWritePermission(AppModule.NOTIFICATIONS)
                if (spec.route == "notifications/messages") {
                    val central =
                        viewModel(key = "central-notifications") { appContainer.centralNotificationsFactory.create(isEntryEnabled) }
                    br.gov.caixa.loterias.feature.notifications.presentation.route.CentralNotificationsRoute(
                        central,
                        navController::popBackStack
                    )
                    return@FeatureModuleAdmission
                }
                val notificationsViewModel = viewModel(key = "notifications-${spec.route}") {
                    appContainer.notificationViewModelFactory.create(isEntryEnabled)
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
}
