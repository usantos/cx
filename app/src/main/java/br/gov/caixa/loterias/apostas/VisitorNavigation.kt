package br.gov.caixa.loterias.apostas

internal fun requiresVisitorLogin(route: String): Boolean = route.substringBefore('?') in setOf(
    "account/personal-data", "tickets/my-bets", "tickets/purchases", "betting/favorites",
    "cart/favorite-carts", "payment/cards", "responsible/form",
)

/** The legacy Personal Data activity shows its form underneath the login prompt. */
internal fun opensVisitorDestinationBeforeLogin(route: String): Boolean =
    route.substringBefore('?') == "account/personal-data"

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
            navController.currentDestination?.route == route.substringBefore('?')) {
            navController.popBackStack()
        }
    }
}
