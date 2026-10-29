package br.gov.caixa.loterias.feature.tickets.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.destination.serviceRedirectRoute
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.feature.payment.navigation.PaymentRegistrationHost
import br.gov.caixa.loterias.feature.payment.navigation.paymentRegistrationViewModel
import br.gov.caixa.loterias.feature.tickets.presentation.route.PurchaseDetailDataRoute
import br.gov.caixa.loterias.feature.tickets.presentation.route.PurchasesDataRoute
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel

fun NavGraphBuilder.ticketPurchasesNavigation(
    appContainer: TicketsNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
    session: SessionNavigationActions
) {
    composable("tickets/purchases") {
        androidx.compose.runtime.key(session.privateEntryKey) {
            val ticketsViewModel = viewModel(key = "purchases-${session.privateEntryKey}") {
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
                    isSessionCurrent = session.isPrivateSessionCurrent
                )
            }
            PurchasesDataRoute(
                privateDataEnabled = session.identity != null,
                viewModel = ticketsViewModel,
                onBack = navController::popBackStack,
                onOpen = { purchase ->
                    navigation.details.selectedPurchase = purchase
                    navController.navigate("tickets/purchase-detail")
                }
            )
        }
    }
    composable("tickets/purchase-detail") {
        val purchase = navigation.details.selectedPurchase ?: visualTestPurchase(appContainer.mockBackendEnabled)
        if (purchase == null) {
            LaunchedEffect(Unit) { navController.popBackStack() }
        } else {
            val ticketsViewModel = viewModel { purchaseDetailViewModel(appContainer) }
            val registrationViewModel = paymentRegistrationViewModel(appContainer)
            val onPurchaseDetailRedirect: (br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect) -> Unit = { destination ->
                if (destination.name in setOf("LOGIN", "REGISTER_ACCOUNT", "ACCEPT_TERMS")) {
                    session.state.visitorReturnRoute = "tickets/purchase-detail"
                    session.state.visitorReturnOwnerKey = appContainer.authenticatedSessionIdentity.captureIdentity()?.ownerKey
                }
                navController.navigate(serviceRedirectRoute(destination)) { launchSingleTop = true }
            }
            PurchaseDetailDataRoute(
                ticketsViewModel,
                purchase,
                navController::popBackStack,
                onOpen = { route ->
                    if (route == "home") {
                        navController.navigate("home") { popUpTo("home") { inclusive = true } }
                    } else {
                        navController.navigate(route)
                    }
                },
                onPixRedirect = { destination ->
                    onPurchaseDetailRedirect(br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect.valueOf(destination.name))
                },
                onPixRegistration = { registrationViewModel.start() },
                onHelp = { navController.navigate(PURCHASE_HELP_ROUTE) }
            )
            PaymentRegistrationHost(
                registrationViewModel,
                ticketsViewModel::resumePixPurchaseAfterRedirect,
                onPurchaseDetailRedirect,
                simulateBrowser = appContainer.mockBackendEnabled
            )
        }
    }
}

// Registrado por supportGraph; mantém a ajuda na aba real de dúvidas frequentes, sem inventar um destino.
internal const val PURCHASE_HELP_ROUTE = "support/terms?tab=FAQ"

private fun purchaseDetailViewModel(appContainer: TicketsNavigationDependencies): TicketsViewModel = TicketsViewModel(
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
    appContainer.purchaseDetails,
    appContainer.validateFavoriteCartName,
    appContainer.savePurchaseAsFavorite,
    appContainer.validateDam,
    getPixPurchaseStatus = appContainer.purchaseProgress,
    pixNoticeClock = appContainer.localClock
)
