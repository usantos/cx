package br.gov.caixa.loterias.core.navigation.workflow

// A procedência referencial da concessão sobrevive à restauração de configuração somente neste processo.
// Chaves fracas evitam reter sessões expiradas; um processo reiniciado não pode validar um marcador antigo.
private val privateEntryLeaseMarkers = java.util.WeakHashMap<br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity, String>()
private val privateVisitorMarker = java.util.UUID.randomUUID().toString()

/** Retém o estado carregado somente quando o marcador restaurado pertence exatamente à concessão atual. */
@androidx.compose.runtime.Composable
fun rememberPrivateEntryKey(identity: br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity?): String {
    val leaseMarker = synchronized(privateEntryLeaseMarkers) {
        if (identity == null) {
            privateVisitorMarker
        } else {
            privateEntryLeaseMarkers.getOrPut(identity) { java.util.UUID.randomUUID().toString() }
        }
    }
    val saver = androidx.compose.runtime.remember(leaseMarker) {
        androidx.compose.runtime.saveable.Saver<String, Any>(
            save = { listOf(it, leaseMarker) },
            restore = { saved ->
                (saved as? List<*>)?.takeIf { it.size == 2 && it[1] == leaseMarker }?.get(0) as? String
            }
        )
    }
    return androidx.compose.runtime.saveable.rememberSaveable(identity, saver = saver) {
        java.util.UUID.randomUUID().toString()
    }
}

fun openVisitorDrawerDestination(route: String, navController: androidx.navigation.NavHostController, requestLogin: (String) -> Unit) {
    if (opensVisitorDestinationBeforeLogin(route)) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo("home") { inclusive = false }
        }
    }
    requestLogin(route)
}

fun openResponsibleAssessment(navController: androidx.navigation.NavHostController) {
    navController.navigate("responsible/form") {
        popUpTo("responsible/start") { inclusive = true }
        launchSingleTop = true
    }
}

fun requiresVisitorLogin(route: String): Boolean = route.substringBefore('?') in setOf(
    "account/personal-data", "tickets/my-bets", "tickets/purchases", "betting/favorites",
    "cart/favorite-carts", "payment/cards", "responsible/form",
    "betting/rapid", "betting/rapid-list", "betting/rapid-config", "betting/favorite-retailers"
)

/** As Activities privadas legadas abrem seus controles públicos iniciais sob a solicitação de login. */
fun opensVisitorDestinationBeforeLogin(route: String): Boolean =
    requiresVisitorLogin(route) && route.substringBefore('?') != "betting/favorite-retailers"

/** As informações de suspensão são públicas, mas enviar uma suspensão altera a conta autenticada. */
fun requiresVisitorLoginOnSubmit(route: String): Boolean = route.substringBefore('?') == "responsible/suspension"

/** As ações do diálogo compartilham a requisição pendente do app e o controlador real de navegação. */
class VisitorLoginCallbacks(
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
        navController.navigate(LoginNavigation.SILENT)
    }

    fun onNegative() {
        if (pendingRequest() != route) return
        clearRequest()
        if (opensVisitorDestinationBeforeLogin(route) &&
            navController.currentDestination?.route?.substringBefore('?') == route.substringBefore('?')
        ) {
            navController.popBackStack()
        }
    }
}
