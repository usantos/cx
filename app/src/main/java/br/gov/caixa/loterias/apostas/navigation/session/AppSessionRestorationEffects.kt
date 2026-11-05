package br.gov.caixa.loterias.apostas.navigation.session

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import br.gov.caixa.loterias.core.auth.foreground.ForegroundSessionNoticeController
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.core.navigation.state.DetailRestorationOutcome
import br.gov.caixa.loterias.core.navigation.state.restoredLoggedRoute

@Composable
internal fun AppSessionRestorationEffects(
    identity: br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity?,
    currentNavigationEntry: androidx.navigation.NavBackStackEntry?,
    foregroundNoticeController: ForegroundSessionNoticeController,
    navController: androidx.navigation.NavHostController,
    navigation: AppNavigationState,
    state: AppSessionState,
) {
    LaunchedEffect(identity, currentNavigationEntry) {
        state.foregroundExpiryMarker?.let { marker ->
            val route = currentNavigationEntry?.destination?.route?.substringBefore('?')
            if (route == "account/login" || route == "account/token" || route?.startsWith("onboarding/") == true ||
                foregroundNoticeController.restore(marker) == null
            ) {
                foregroundNoticeController.dismiss(marker)
                state.foregroundExpiryMarker = null
            }
        }
    }
    LaunchedEffect(identity, currentNavigationEntry, navigation.details.restorationOutcome) {
        val entry = currentNavigationEntry
        if (identity == null && navigation.details.restorationOutcome == DetailRestorationOutcome.Pending && entry != null &&
            entry.destination.route?.startsWith("account/login") != true
        ) {
            state.pendingRestoredRoute =
                restoredLoggedRoute(entry.destination.route.orEmpty()) { key -> entry.arguments?.get(key)?.toString() }
            navController.navigate("account/login") {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}
