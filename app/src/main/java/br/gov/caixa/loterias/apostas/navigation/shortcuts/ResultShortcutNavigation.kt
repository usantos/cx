package br.gov.caixa.loterias.apostas.navigation.shortcuts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import br.gov.caixa.loterias.apostas.runtime.shortcuts.ResultShortcutRequest
import br.gov.caixa.loterias.apostas.runtime.shortcuts.ResultShortcutRequests

internal val LocalResultShortcutRequests = staticCompositionLocalOf<ResultShortcutRequests?> { null }

@Composable
internal fun ResultShortcutNavigation(navController: NavHostController) {
    val requests = LocalResultShortcutRequests.current
    val request = requests?.pending
    LaunchedEffect(request) {
        if (request != null) {
            navigateToResultShortcut(navController, request)
            requests.consume(request)
        }
    }
}

internal fun navigateToResultShortcut(navController: NavHostController, request: ResultShortcutRequest) {
    if (request.coldStart) {
        navController.navigate("home") {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }
    val current = navController.currentDestination
    navController.navigate(request.destination.route) {
        if (current?.route?.startsWith("results/") == true) {
            popUpTo(current.id) { inclusive = true }
        }
    }
}
