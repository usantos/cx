package br.gov.caixa.loterias.feature.retail.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleAdmission
import br.gov.caixa.loterias.core.presentation.delivery.rememberFeatureWritePermission
import br.gov.caixa.loterias.feature.retail.R
import br.gov.caixa.loterias.feature.retail.presentation.route.LocationGateRoute
import br.gov.caixa.loterias.feature.retail.presentation.route.RetailSearchRoute
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.LocationGateViewModel
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.RetailSearchViewModel

internal fun NavGraphBuilder.registerRetailLocationRoute(appContainer: RetailNavigationDependencies, navController: NavHostController) {
    composable("retail/location") {
        FeatureModuleAdmission(
            module = AppModule.RETAIL,
            onBack = navController::popBackStack
        ) {
            val locationViewModel = viewModel { LocationGateViewModel(appContainer.checkRetailLocation) }
            LocationGateRoute(
                viewModel = locationViewModel,
                versionName = appContainer.appVersionName,
                lotteryIconResourceId = R.drawable.icon_loteria,
                mockInsideBrazil = appContainer.mockBackendEnabled,
                onAllowed = {
                    navController.navigate("retail/search") {
                        popUpTo("retail/location") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

internal fun NavGraphBuilder.registerRetailSearchRoute(appContainer: RetailNavigationDependencies, navController: NavHostController) {
    composable("retail/search") {
        FeatureModuleAdmission(
            module = AppModule.RETAIL,
            onBack = navController::popBackStack
        ) {
            val allowNewEntry = rememberFeatureWritePermission(AppModule.RETAIL)
            val retailSearchViewModel = viewModel {
                RetailSearchViewModel(
                    searchRetailers = appContainer.searchRetailers,
                    setFavorite = appContainer.setRetailerFavorite,
                    dispatchers = appContainer.dispatchers,
                    isEntryEnabled = allowNewEntry
                )
            }
            RetailSearchRoute(retailSearchViewModel, navController::popBackStack, onHelp = {
                navController.navigate("support/terms?tab=FAQ")
            })
        }
    }
}
