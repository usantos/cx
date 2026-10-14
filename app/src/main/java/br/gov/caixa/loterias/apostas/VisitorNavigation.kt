package br.gov.caixa.loterias.apostas

/** Retain a loaded screen across restoration, while a different validated lease gets fresh private state. */
@androidx.compose.runtime.Composable
internal fun rememberPrivateEntryKey(identity: br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity?): String =
    androidx.compose.runtime.saveable.rememberSaveable(identity) { java.util.UUID.randomUUID().toString() }

internal fun openVisitorDrawerDestination(
    route: String,
    navController: androidx.navigation.NavHostController,
    requestLogin: (String) -> Unit,
) {
    if (opensVisitorDestinationBeforeLogin(route)) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo("home") { inclusive = false }
        }
    }
    requestLogin(route)
}

internal fun openResponsibleAssessment(navController: androidx.navigation.NavHostController) {
    navController.navigate("responsible/form") {
        popUpTo("responsible/start") { inclusive = true }
        launchSingleTop = true
    }
}

internal fun requiresVisitorLogin(route: String): Boolean = route.substringBefore('?') in setOf(
    "account/personal-data", "tickets/my-bets", "tickets/purchases", "betting/favorites",
    "cart/favorite-carts", "payment/cards", "responsible/form",
)

/** Legacy private activities open their initial public controls underneath the login prompt. */
internal fun opensVisitorDestinationBeforeLogin(route: String): Boolean =
    requiresVisitorLogin(route)

/** Suspension information is public, but submitting a suspension changes the signed-in account. */
internal fun requiresVisitorLoginOnSubmit(route: String): Boolean =
    route.substringBefore('?') == "responsible/suspension"

/** Dialog actions share the app's pending request and real navigation controller. */
internal class VisitorLoginCallbacks(
    private val route: String,
    private val navController: androidx.navigation.NavHostController,
    private val pendingRequest: () -> String?,
    private val clearRequest: () -> Unit,
    private val continueAfterLogin: (String) -> Unit,
) {
    fun onPositive() {
        if (pendingRequest() != route) return
        clearRequest()
        continueAfterLogin(route)
        navController.navigate(LoginNavigation.authorize)
    }

    fun onNegative() {
        if (pendingRequest() != route) return
        clearRequest()
        if (opensVisitorDestinationBeforeLogin(route) &&
            navController.currentDestination?.route?.substringBefore('?') == route.substringBefore('?')) {
            navController.popBackStack()
        }
    }
}
