package br.gov.caixa.loterias.apostas

// Referential lease provenance survives configuration restoration only in this process.
// Weak keys avoid retaining expired sessions; a restarted process cannot validate an old marker.
private val privateEntryLeaseMarkers = java.util.WeakHashMap<br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity, String>()
private val privateVisitorMarker = java.util.UUID.randomUUID().toString()

/** Retain loaded state only when the restored marker belongs to the exact current lease. */
@androidx.compose.runtime.Composable
internal fun rememberPrivateEntryKey(identity: br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity?): String {
    val leaseMarker = synchronized(privateEntryLeaseMarkers) {
        if (identity == null) privateVisitorMarker
        else privateEntryLeaseMarkers.getOrPut(identity) { java.util.UUID.randomUUID().toString() }
    }
    val saver = androidx.compose.runtime.remember(leaseMarker) {
        androidx.compose.runtime.saveable.Saver<String, Any>(
            save = { listOf(it, leaseMarker) },
            restore = { saved ->
                (saved as? List<*>)?.takeIf { it.size == 2 && it[1] == leaseMarker }?.get(0) as? String
            },
        )
    }
    return androidx.compose.runtime.saveable.rememberSaveable(identity, saver = saver) {
        java.util.UUID.randomUUID().toString()
    }
}

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
