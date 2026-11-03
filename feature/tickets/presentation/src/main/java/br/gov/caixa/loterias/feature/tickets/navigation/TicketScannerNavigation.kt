package br.gov.caixa.loterias.feature.tickets.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleAdmission
import br.gov.caixa.loterias.core.presentation.delivery.rememberFeatureWritePermission
import br.gov.caixa.loterias.feature.tickets.presentation.route.TicketGameChoiceRoute
import br.gov.caixa.loterias.feature.tickets.presentation.route.TicketScannerRoute
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel

fun NavGraphBuilder.ticketScannerNavigation(
    appContainer: TicketsNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
) {
    composable("tickets/read") {
        FeatureModuleAdmission(AppModule.TICKETS, { navController.popBackStack() }) {
            val canStartTicketsOperation = rememberFeatureWritePermission(AppModule.TICKETS)
            val ticketsViewModel = viewModel(key = "scanner-tickets-read") {
                TicketsViewModel(
                    appContainer.confirmedBets,
                    appContainer.purchases,
                    appContainer.purchaseBets,
                    appContainer.checkTicket,
                    appContainer.dispatchers,
                    appContainer.confirmedBetsConfiguration,
                    appContainer.lotteryModalities,
                    appContainer.purchaseMonths,
                    appContainer.purchasePaymentMethods,
                    appContainer.purchaseStatuses,
                    canStartOperation = canStartTicketsOperation
                )
            }
            TicketScannerRoute(
                ticketsViewModel,
                navController::popBackStack,
                onManual = { navController.navigate("tickets/manual-code") },
                onResult = {
                    navigation.details.checkedTicketBets = it
                    navController.navigate("tickets/read-result")
                }
            )
        }
    }
    composable("tickets/read-game-choice") {
        FeatureModuleAdmission(AppModule.TICKETS, { navController.popBackStack() }) {
            val canStartTicketsOperation = rememberFeatureWritePermission(AppModule.TICKETS)
            val ticketsViewModel = viewModel(key = "ticket-game-choice") {
                TicketsViewModel(
                    appContainer.confirmedBets,
                    appContainer.purchases,
                    appContainer.purchaseBets,
                    appContainer.checkTicket,
                    appContainer.dispatchers,
                    appContainer.confirmedBetsConfiguration,
                    appContainer.lotteryModalities,
                    appContainer.purchaseMonths,
                    appContainer.purchasePaymentMethods,
                    appContainer.purchaseStatuses,
                    canStartOperation = canStartTicketsOperation
                )
            }
            TicketGameChoiceRoute(
                viewModel = ticketsViewModel,
                onBack = navController::popBackStack,
                onHelp = { navController.navigate("support/terms?tab=FAQ") },
                onSave = { navController.navigate("tickets/read") }
            )
        }
    }
}
