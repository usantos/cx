package br.gov.caixa.loterias.apostas

/** Immutable configuration shared by services and authentication for one process lifetime. */
data class EffectiveEnvironment(
    val apiBaseUrl: String,
    val ssoAuthUrl: String,
    val ssoTokenUrl: String,
    val ssoLogoutUrl: String,
    val ssoAccountUrl: String,
    val ssoClientId: String,
    val ssoRedirectUri: String,
    val mercadoPagoBaseUrl: String,
    val recargaPayBaseUrl: String,
    val queueAccount: String,
    val queueSystem: String,
) {
    val sessionNamespace: String get() = apiBaseUrl
}

internal class EnvironmentSelection(
    flavor: String,
    saved: String?,
    defaults: EffectiveEnvironment,
    private val persist: (String) -> Unit,
) {
    val selected = LegacyTestEnvironment.entries.firstOrNull { it.name == saved }
        ?: LegacyTestEnvironment.EXTERNO_ESTEIRA
    val effective = if (flavor == "tqs") selected.configuration() else defaults
    val selectorVisible = flavor != "prd"

    /** Persistence affects the next launch; existing clients retain their original configuration. */
    fun select(environment: LegacyTestEnvironment): Boolean {
        if (!selectorVisible || environment == selected) return false
        persist(environment.name)
        return true
    }
}

private fun LegacyTestEnvironment.configuration(): EffectiveEnvironment {
    // CX uses the same public SSO and payment endpoints for all TQS environments.
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
        queueSystem = if (this == LegacyTestEnvironment.PILOTO) "loteriasplt" else "loteriasdes",
    )
}

/** Invalidate volatile authorization before persisting; keep durable account/cart/biometric data. */
internal suspend fun applyEnvironmentChange(
    selection: EnvironmentSelection,
    environment: LegacyTestEnvironment,
    clearSession: suspend () -> Unit,
): Boolean {
    if (!selection.selectorVisible || environment == selection.selected) return false
    clearSession()
    return selection.select(environment)
}

internal fun reconcilePendingEnvironment(pending: String?, selected: LegacyTestEnvironment): String? =
    pending?.takeUnless { it == selected.name }
