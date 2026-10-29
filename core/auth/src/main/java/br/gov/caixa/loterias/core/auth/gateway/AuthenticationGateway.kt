package br.gov.caixa.loterias.core.auth.gateway

import android.content.Intent

interface AuthenticationGateway : br.gov.caixa.loterias.feature.account.domain.port.SessionAuthentication {
    fun createAuthorizationIntent(): Intent

    /** Solicita nova autenticação no provedor para permitir a escolha de outra conta. */
    fun createAccountSwitchAuthorizationIntent(): Intent = createAuthorizationIntent()

    /** Retorna null quando este gateway não consegue reutilizar uma sessão do provedor sem interação do usuário. */
    fun createSilentAuthorizationIntent(): Intent? = null
    suspend fun completeAuthorization(result: Intent?)

    /** O AppAuth retorna o cancelamento do navegador como um Intent de erro não nulo em alguns provedores. */
    fun isAuthorizationCancelled(result: Intent?): Boolean {
        val failure = result?.let(net.openid.appauth.AuthorizationException::fromIntent) ?: return false
        val cancelled = net.openid.appauth.AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW
        return failure.type == cancelled.type && failure.code == cancelled.code
    }
    override fun cancelAuthorization()
    override fun logout()
}

data class OidcConfiguration(
    val authorizationEndpoint: String,
    val tokenEndpoint: String,
    val clientId: String,
    val redirectUri: String,
    val endSessionEndpoint: String,
    val postLogoutRedirectUri: String,
    val sessionNamespace: String,
    val expectedIssuer: String = tokenEndpoint.removeSuffix("/protocol/openid-connect/token"),
)
