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
