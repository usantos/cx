package br.gov.caixa.loterias.core.navigation.action

import androidx.navigation.NavHostController

/** Uma nova aposta sempre começa nas modalidades da Home, independentemente da origem do carrinho. */
fun NavHostController.returnToModalitiesHome() {
    if (!popBackStack("home", inclusive = false)) {
        navigate("home") {
            popUpTo(graph.id) { inclusive = false }
            launchSingleTop = true
        }
    }
}
