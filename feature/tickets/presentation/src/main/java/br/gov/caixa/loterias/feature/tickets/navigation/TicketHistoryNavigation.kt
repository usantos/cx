package br.gov.caixa.loterias.feature.tickets.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.activeCartSummary
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleAdmission
import br.gov.caixa.loterias.core.presentation.delivery.rememberFeatureWritePermission
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetRepeatActionsOverlay
import br.gov.caixa.loterias.feature.tickets.presentation.route.ConfirmedBetsDataRoute
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel
import kotlinx.coroutines.flow.first

fun NavGraphBuilder.ticketHistoryNavigation(
    appContainer: TicketsNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
    session: SessionNavigationActions,
    actions: AppNavigationActions,
) {
    composable("tickets/my-bets") {
        FeatureModuleAdmission(AppModule.TICKETS, { navController.popBackStack() }) {
            val canStartTicketsOperation = rememberFeatureWritePermission(AppModule.TICKETS)
            androidx.compose.runtime.key(session.privateEntryKey) {
                var repeatBet by remember { mutableStateOf<ConfirmedBet?>(null) }
                var repeatRequest by remember { mutableStateOf(0) }
                val ticketsViewModel = viewModel(key = "my-bets-${session.privateEntryKey}") {
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
                        isSessionCurrent = session.isPrivateSessionCurrent,
                        canStartOperation = canStartTicketsOperation
                    )
                }
                val repeatViewModel = repeatBet?.let { bet ->
                    val repeatViewModel = viewModel(key = "list-repeat-${bet.id}-$repeatRequest") {
                        createTicketBetDetailViewModel(bet, appContainer, canStartTicketsOperation)
                    }
                    LaunchedEffect(repeatViewModel) {
                        // A recuperação deve terminar antes da repetição para não apagar seus diálogos.
                        repeatViewModel.state.first { !it.loading }
                        if (repeatViewModel.state.value.detailsReady) repeatViewModel.addCurrentBetToCart().join()
                    }
                    repeatViewModel
                }
                androidx.compose.foundation.layout.Box {
                    var cartRevision by remember { androidx.compose.runtime.mutableIntStateOf(0) }
                    val cartSummary = activeCartSummary(appContainer.currentCart, cartRevision)
                    ConfirmedBetsDataRoute(
                        privateDataEnabled = session.identity != null,
                        viewModel = ticketsViewModel,
                        cartTotal = cartSummary?.total ?: java.math.BigDecimal.ZERO,
                        cartBetCount = cartSummary?.betCount ?: 0,
                        onCart = { navController.navigate("cart/current") },
                        onHelp = { navController.navigate("support/terms?tab=FAQ") },
                        onAddToCart = { bet ->
                            repeatRequest += 1
                            repeatBet = bet
                        },
                        onBetNow = {
                            navController.navigate("home") {
                                popUpTo("home")
                                launchSingleTop = true
                            }
                        },
                        onBack = navController::popBackStack,
                        onOpen = { bet ->
                            navigation.details.selectedBet = bet
                            navController.navigate(
                                "tickets/bet-detail?original=${bet.originalRecurringBet}&resgate=${bet.redeemedPrize}"
                            )
                        }
                    )
                    repeatViewModel?.let {
                        BetRepeatActionsOverlay(
                            it,
                            onServiceRedirect = actions.onTicketServiceRedirect,
                            onCartChanged = { cartRevision++ }
                        )
                    }
                }
            }
        }
    }
}
