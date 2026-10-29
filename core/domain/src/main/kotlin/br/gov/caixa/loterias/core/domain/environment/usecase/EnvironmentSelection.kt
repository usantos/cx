package br.gov.caixa.loterias.core.domain.environment.usecase

import br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment
import br.gov.caixa.loterias.core.domain.environment.model.EffectiveEnvironment

class EnvironmentSelection(
    flavor: String,
    saved: String?,
    defaults: EffectiveEnvironment,
    private val persist: (String) -> Unit,
) {
    val selected = TestEnvironment.entries.firstOrNull { it.name == saved }
        ?: TestEnvironment.EXTERNO_ESTEIRA
    val effective = if (flavor == "tqs") selected.configuration() else defaults
    val selectorVisible = flavor != "prd"

    /** A persistência afeta a próxima inicialização; clientes existentes mantêm sua configuração original. */
    fun select(environment: TestEnvironment): Boolean {
        if (!selectorVisible || environment == selected) return false
        persist(environment.name)
        return true
    }
}

private fun TestEnvironment.configuration(): EffectiveEnvironment {
    // O CX usa os mesmos endpoints públicos de SSO e pagamento em todos os ambientes TQS.
    val sso = "https://logindes.caixa.gov.br"
    val oidc = "$sso/auth/realms/internet/protocol/openid-connect"
    return EffectiveEnvironment(
        apiBaseUrl = apiBaseUrl,
        ssoAuthUrl = "$oidc/auth", ssoTokenUrl = "$oidc/token", ssoLogoutUrl = "$oidc/logout",
        ssoAccountUrl = "$sso/auth/realms/internet/account",
        ssoClientId = "cli-mob-lce", ssoRedirectUri = "br.gov.caixa.caixaloterias:/oauth2Callback",
        mercadoPagoBaseUrl = "https://api.mercadopago.com/",
        recargaPayBaseUrl = "https://secure-sandbox.recargapay.com/",
        queueAccount = "caixaloteriasdev",
        queueSystem = if (this == TestEnvironment.PILOTO) "loteriasplt" else "loteriasdes",
    )
}

/** Invalida a autorização volátil antes de persistir; mantém os dados duráveis de conta, carrinho e biometria. */
suspend fun applyEnvironmentChange(
    selection: EnvironmentSelection,
    environment: TestEnvironment,
    clearSession: suspend () -> Unit,
): Boolean {
    if (!selection.selectorVisible || environment == selection.selected) return false
    clearSession()
    return selection.select(environment)
}

fun reconcilePendingEnvironment(pending: String?, selected: TestEnvironment): String? =
    pending?.takeUnless { it == selected.name }
