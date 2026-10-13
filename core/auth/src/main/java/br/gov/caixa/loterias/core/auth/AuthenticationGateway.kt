package br.gov.caixa.loterias.core.auth

import android.content.Intent

interface AuthenticationGateway {
    fun createAuthorizationIntent(): Intent
    suspend fun completeAuthorization(result: Intent?)
    /** AppAuth returns browser cancellation as a non-null error Intent on some providers. */
    fun isAuthorizationCancelled(result: Intent?): Boolean {
        val failure = result?.let(net.openid.appauth.AuthorizationException::fromIntent) ?: return false
        val cancelled = net.openid.appauth.AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW
        return failure.type == cancelled.type && failure.code == cancelled.code
    }
    fun cancelAuthorization()
    fun logout()
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
