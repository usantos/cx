package br.gov.caixa.loterias.apostas.navigation.session

import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.AccountCompletionViewModel

internal fun resetLoggedNavigationState(
    completion: AccountCompletionViewModel,
    navigation: AppNavigationState,
    state: AppSessionState,
    navController: androidx.navigation.NavHostController,
) {
    completion.reset()
    navigation.requestedHomeModalityId = null
    navigation.details.clear()
    state.pendingRestoredRoute = null
    state.visitorLoginRequest = null
    state.visitorReturnRoute = null
    state.visitorReturnOwnerKey = null
    state.resumedHomeAction = null
    state.accountCompletionRequired = false
    state.foregroundExpiryMarker = null
    navController.navigate("account/login") {
        popUpTo(navController.graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

internal fun navigateAuthenticatedDestination(
    sessionDependencies: AppSessionDependencies,
    navController: androidx.navigation.NavHostController,
    navigation: AppNavigationState,
    state: AppSessionState,
    destination: String,
) {
    val owner = sessionDependencies.account.authenticatedSessionIdentity.captureIdentity()?.ownerKey
    val target = state.authenticated(destination, owner, navigation.details)
    val resumeFeature = destination == "home" && target != "home"
    navController.navigate(if (resumeFeature) "home" else target) {
        // Destinos privados devem carregar novamente com a conta validada.
        popUpTo(navController.graph.id) { inclusive = true }
        launchSingleTop = true
    }
    if (resumeFeature) {
        // Constrói a pilha de retorno de forma síncrona; somente a feature solicitada é renderizada.
        navController.navigate(target) { launchSingleTop = true }
    }
}
