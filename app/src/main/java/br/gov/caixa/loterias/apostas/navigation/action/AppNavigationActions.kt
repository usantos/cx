package br.gov.caixa.loterias.apostas.navigation.action

import br.gov.caixa.loterias.apostas.navigation.session.AppSessionDependencies
import br.gov.caixa.loterias.apostas.navigation.session.AppSessionCoordinator
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.destination.BettingSimulationRoute
import br.gov.caixa.loterias.core.navigation.state.accountCompletionRedirects
import br.gov.caixa.loterias.core.navigation.state.loggedContinuation
import br.gov.caixa.loterias.core.navigation.destination.serviceRedirectRoute
import br.gov.caixa.loterias.core.navigation.state.ticketQueryContinuation
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision
import br.gov.caixa.loterias.feature.home.presentation.mapper.name
import br.gov.caixa.loterias.feature.home.presentation.mapper.prize
import br.gov.caixa.loterias.feature.payment.navigation.paymentRedirectRoute

internal fun appNavigationActions(
    sessionDependencies: AppSessionDependencies,
    navController: androidx.navigation.NavHostController,
    navigation: AppNavigationState,
    session: AppSessionCoordinator,
): AppNavigationActions {
    val handleBetEntry: (BetEntryDecision, Boolean) -> Unit = { decision, returnViaHome ->
        when (decision) {
            is BetEntryDecision.Ready -> {
                navigation.details.betToEdit = null
                navigation.preparedBettingParameters = decision.parameters
                if (returnViaHome) {
                    navigation.requestedHomeModalityId = decision.game.modality.id
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
                navController.navigate(BettingSimulationRoute.from(decision.game))
            }
            is BetEntryDecision.CompleteAccount -> {
                session.state.accountCompletionRequired = true
                navController.navigate(when (decision.destination) {
                PostLoginDestination.HOME -> "home"
                PostLoginDestination.REGISTRATION -> "account/register"
                PostLoginDestination.TERMS -> "support/terms-acceptance"
                PostLoginDestination.SELF_ASSESSMENT -> "responsible/start/required"
                }) { launchSingleTop = true }
            }
        }
    }
    val onPrizeBetEntry: (BetEntryDecision) -> Unit = { handleBetEntry(it, true) }
    val onPrizeRedirect: (br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect) -> Unit = { destination ->
        if (destination in accountCompletionRedirects) {
            val betId = navController.currentBackStackEntry?.arguments?.getLong("betId")?.takeIf { it > 0 } ?: navigation.details.selectedBet?.id
            session.state.visitorReturnRoute = loggedContinuation("retail/prize", betId)
            session.state.visitorReturnOwnerKey = sessionDependencies.account.authenticatedSessionIdentity.captureIdentity()?.ownerKey
        }
        navController.navigate(serviceRedirectRoute(destination)) {
            if (destination == br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect.HOME) {
                popUpTo("home") { inclusive = false }
            }
            launchSingleTop = true
        }
    }
    val onTicketServiceRedirect: (br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect) -> Unit = { destination ->
        if (destination in accountCompletionRedirects) {
            session.state.visitorReturnRoute = ticketQueryContinuation()
            session.state.visitorReturnOwnerKey = sessionDependencies.account.authenticatedSessionIdentity.captureIdentity()?.ownerKey
        }
        navController.navigate(serviceRedirectRoute(destination)) { launchSingleTop = true }
    }
    val onPaymentRedirect: (br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect) -> Unit = { destination ->
        if (destination.name in setOf("LOGIN", "REGISTER_ACCOUNT", "ACCEPT_TERMS")) session.state.visitorReturnRoute = "cart/current"
        navController.navigate(paymentRedirectRoute(destination)) { launchSingleTop = true }
    }
    val onPaymentMoreBets: () -> Unit = {
        navController.navigate("home") {
            popUpTo("home") { inclusive = false }
            launchSingleTop = true
        }
    }
    return AppNavigationActions(handleBetEntry, onPrizeBetEntry, onPrizeRedirect, onTicketServiceRedirect, onPaymentRedirect, onPaymentMoreBets)
}
