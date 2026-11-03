package br.gov.caixa.loterias.feature.tickets.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase

fun NavGraphBuilder.ticketsGraph(
    appContainer: TicketsNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
    session: SessionNavigationActions,
    actions: AppNavigationActions,
) {
    ticketBetDetailNavigation(appContainer, navController, navigation, actions, session)
    ticketHistoryNavigation(appContainer, navController, navigation, session, actions)
    ticketPurchasesNavigation(appContainer, navController, navigation, session)
    ticketScannerNavigation(appContainer, navController, navigation)
    ticketManualNavigation(appContainer, navController, navigation)
}

fun visualTestPurchase(mockBackendEnabled: Boolean): Purchase? = if (mockBackendEnabled) {
    Purchase(
        id = 99,
        date = "04/09/2026",
        status = "Aprovada",
        paymentMethod = "Pix",
        total = java.math.BigDecimal("35.00")
    )
} else {
    null
}
