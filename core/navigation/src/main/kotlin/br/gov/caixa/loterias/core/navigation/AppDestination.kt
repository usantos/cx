package br.gov.caixa.loterias.core.navigation

/** Stable route contracts. Features never depend on the application module. */
enum class AppDestination(val route: String) {
    MARKETPLACE_FILTER("marketplace/filter"),
    SUPPORT("support"),
}
