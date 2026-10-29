package br.gov.caixa.loterias.feature.tickets.navigation

import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel
import br.gov.caixa.loterias.feature.tickets.presentation.route.TicketScannerRoute
import br.gov.caixa.loterias.feature.tickets.presentation.route.TicketGameChoiceRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

fun NavGraphBuilder.ticketScannerNavigation(
    appContainer: TicketsNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
) {
    composable("tickets/read") {
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
                )
            }
            TicketScannerRoute(
                ticketsViewModel,
                navController::popBackStack,
                onManual = { navController.navigate("tickets/manual-code") },
                onResult = {
                    navigation.details.checkedTicketBets = it
                    navController.navigate("tickets/read-result")
                },
            )
    }
    composable("tickets/read-game-choice") {
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
            )
        }
        TicketGameChoiceRoute(
            viewModel = ticketsViewModel,
            onBack = navController::popBackStack,
            onHelp = { navController.navigate("support/terms?tab=FAQ") },
            onSave = { navController.navigate("tickets/read") },
        )
    }
}
