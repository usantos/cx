package br.gov.caixa.loterias.feature.tickets.navigation

import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel
import br.gov.caixa.loterias.feature.tickets.presentation.route.ManualTicketDataRoute
import br.gov.caixa.loterias.feature.tickets.presentation.route.TicketResultDataRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

fun NavGraphBuilder.ticketManualNavigation(
    appContainer: TicketsNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
) {
    composable("tickets/manual-code") {
        val ticketsViewModel = viewModel(key = "manual-tickets-read") {
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
        ManualTicketDataRoute(
            ticketsViewModel,
            navController::popBackStack,
            onResult = {
                navigation.details.checkedTicketBets = it
                navController.navigate("tickets/read-result")
            },
            onHelp = { navController.navigate("support/terms?tab=FAQ") },
        )
    }
    composable("tickets/read-result") { resultEntry ->
        // Reutiliza o VM existente da rota de consulta; o DTO de consulta não é um histórico de apostas.
        // A entrada anterior global muda enquanto este destino permanece composto
        // durante sua animação de saída, por exemplo, ao abrir a ajuda ou a Home.
        val queryEntry = remember(resultEntry) {
            navController.previousBackStackEntry?.takeIf {
                it.destination.route in setOf("tickets/read", "tickets/manual-code")
            }
        }
        val queryViewModel: TicketsViewModel? = queryEntry?.let { entry ->
            viewModel<TicketsViewModel>(viewModelStoreOwner = entry,
                key = if (entry.destination.route == "tickets/read") "scanner-tickets-read" else "manual-tickets-read") {
                TicketsViewModel(
                    appContainer.confirmedBets, appContainer.purchases, appContainer.purchaseBets,
                    appContainer.checkTicket, appContainer.dispatchers, appContainer.confirmedBetsConfiguration,
                    appContainer.lotteryModalities, appContainer.purchaseMonths,
                    appContainer.purchasePaymentMethods, appContainer.purchaseStatuses,
                )
            }
        }
        val queryState = queryViewModel?.state?.collectAsStateWithLifecycle()?.value
        val ticketResult = queryState?.ticketResult
        var currentGame by remember(ticketResult) {
            mutableStateOf<br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters?>(null)
        }
        LaunchedEffect(ticketResult) {
            ticketResult?.modalityId?.let { modalityId ->
                when (val parameters = appContainer.bettingParameters()) {
                    is br.gov.caixa.loterias.core.common.result.AppResult.Success ->
                        currentGame = parameters.value.games.firstOrNull { it.modality.id == modalityId }
                    is br.gov.caixa.loterias.core.common.result.AppResult.Failure -> Unit
                }
            }
        }
        TicketResultDataRoute(
            bets = navigation.details.checkedTicketBets,
            visualData = ticketResult?.let {
                br.gov.caixa.loterias.feature.tickets.presentation.components.ticketResultVisualData(it, currentGame)
            },
            onBetNow = {
                navigation.requestedHomeModalityId = ticketResult?.modalityId
                    ?: navigation.details.checkedTicketBets.firstOrNull()?.modalityId
                navController.navigate("home") { popUpTo("home"); launchSingleTop = true }
            },
            onBack = navController::popBackStack,
            onHelp = { navController.navigate("support/terms?tab=FAQ") },
        )
    }
}
