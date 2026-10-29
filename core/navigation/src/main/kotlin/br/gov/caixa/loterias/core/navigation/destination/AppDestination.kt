package br.gov.caixa.loterias.core.navigation.destination

/** Contratos estáveis de rotas. As features nunca dependem do módulo do aplicativo. */
enum class AppDestination(val route: String) {
    MARKETPLACE_FILTER("marketplace/filter"),
    SUPPORT("support"),
}
