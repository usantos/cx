package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.service.ServiceRedirect

internal val accountCompletionRedirects = setOf(ServiceRedirect.LOGIN, ServiceRedirect.REGISTER_ACCOUNT, ServiceRedirect.ACCEPT_TERMS)

/** Returns to a fresh query, never directly to a transaction submission screen. */
internal fun loggedContinuation(source: String, betId: Long? = null): String = when {
    source.startsWith("payment/") || source.startsWith("cart/") -> "cart/current"
    source.startsWith("retail/prize") && betId != null && betId > 0 -> "retail/prize/$betId"
    source.startsWith("retail/prize") -> "tickets/my-bets"
    else -> "home"
}

internal fun postLoginDestinationRoute(destination: br.gov.caixa.loterias.core.domain.account.PostLoginDestination): String = when (destination) {
    br.gov.caixa.loterias.core.domain.account.PostLoginDestination.HOME -> "home"
    br.gov.caixa.loterias.core.domain.account.PostLoginDestination.REGISTRATION -> "account/register"
    br.gov.caixa.loterias.core.domain.account.PostLoginDestination.TERMS -> "support/terms-acceptance"
    br.gov.caixa.loterias.core.domain.account.PostLoginDestination.SELF_ASSESSMENT -> "responsible/start/required"
}

internal fun restoredLoggedRoute(pattern: String, argument: (String) -> String?): String {
    val restored = Regex("\\{([^}]+)\\}").replace(pattern) { match ->
        java.net.URLEncoder.encode(argument(match.groupValues[1]).orEmpty(), "UTF-8").replace("+", "%20")
    }
    // Financial screens always reload their source after interactive authentication.
    return if (restored.startsWith("payment/")) "cart/current" else restored
}

internal fun shouldUseLoggedVisualFixture(debug: Boolean, mockBackend: Boolean, requested: Boolean): Boolean = debug && mockBackend && requested

/** An owned prize query may resume only for its originating account. Cart queries load the new account. */
internal fun authenticatedContinuation(route: String?, originatingOwner: String?, authenticatedOwner: String?): String? =
    if (route?.startsWith("retail/prize/") == true &&
        (originatingOwner.isNullOrBlank() || originatingOwner != authenticatedOwner)) null else route

/** Reload the authenticated list; selected details belong to the pre-authentication owner. */
internal fun ticketQueryContinuation(): String = "tickets/my-bets"
