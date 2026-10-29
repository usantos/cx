package br.gov.caixa.loterias.core.navigation.state

import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect

val accountCompletionRedirects = setOf(ServiceRedirect.LOGIN, ServiceRedirect.REGISTER_ACCOUNT, ServiceRedirect.ACCEPT_TERMS)

/** Retorna a uma nova consulta, nunca diretamente a uma tela de envio de transação. */
fun loggedContinuation(source: String, betId: Long? = null): String = when {
    source.startsWith("payment/") || source.startsWith("cart/") -> "cart/current"
    source.startsWith("retail/prize") && betId != null && betId > 0 -> "retail/prize/$betId"
    source.startsWith("retail/prize") -> "tickets/my-bets"
    else -> "home"
}

fun postLoginDestinationRoute(destination: br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination): String = when (destination) {
    br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination.HOME -> "home"
    br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination.REGISTRATION -> "account/register"
    br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination.TERMS -> "support/terms-acceptance"
    br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination.SELF_ASSESSMENT -> "responsible/start/required"
}

fun restoredLoggedRoute(pattern: String, argument: (String) -> String?): String {
    val restored = Regex("\\{([^}]+)\\}").replace(pattern) { match ->
        java.net.URLEncoder.encode(argument(match.groupValues[1]).orEmpty(), "UTF-8").replace("+", "%20")
    }
    // Telas financeiras sempre recarregam sua fonte após autenticação interativa.
    return if (restored.startsWith("payment/")) "cart/current" else restored
}

fun shouldUseLoggedVisualFixture(debug: Boolean, mockBackend: Boolean, requested: Boolean): Boolean = debug && mockBackend && requested

/** O login nativo do Combo retorna à Home; somente o login do bolão preserva o catálogo solicitado. */
fun authenticatedContinuation(route: String?, originatingOwner: String?, authenticatedOwner: String?): String? =
    if (route == "home/combo") null
    else if (route?.startsWith("retail/prize/") == true &&
        (originatingOwner.isNullOrBlank() || originatingOwner != authenticatedOwner)) null else route

/** Recarrega a lista autenticada; os detalhes selecionados pertencem ao titular anterior à autenticação. */
fun ticketQueryContinuation(): String = "tickets/my-bets"
