package br.gov.caixa.loterias.core.auth.appauth

import android.content.Intent
import androidx.core.net.toUri
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.GrantTypeValues
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse

internal class AppAuthBrowserAuthorization(private val session: AppAuthSession) {
    fun createAuthorizationIntent(silent: Boolean = false, switchUser: Boolean = false): Intent {
        with(session) {
            biometricAttempts.cancel()
            biometricChallengeCredential = null
            logoutIdToken = null
            endSessions.clear()
            val options = requestPolicy.capture()
            // Um novo construtor gera novos valores de state, nonce e verificador PKCE em cada tentativa.
            val request = AuthorizationRequest.Builder(
                AuthorizationServiceConfiguration(
                    configuration.authorizationEndpoint.toUri(),
                    configuration.tokenEndpoint.toUri()
                ),
                configuration.clientId,
                ResponseTypeValues.CODE,
                configuration.redirectUri.toUri()
            ).setScope(if (silent) options.scope ?: "openid" else options.scope)
                .setPrompt(
                    when {
                        switchUser -> AuthorizationRequest.Prompt.LOGIN
                        silent -> AuthorizationRequest.Prompt.NONE
                        else -> null
                    }
                )
                .setAdditionalParameters(options.additionalParameters).build()
            attempts.begin(requireNotNull(request.state), request)
            return service.getAuthorizationRequestIntent(request)
        }
    }

    suspend fun completeAuthorization(result: Intent?) {
        with(session) {
            requireNotNull(result) { "Autenticação cancelada" }
            AuthorizationException.fromIntent(result)?.let { throw it }
            val response = requireNotNull(AuthorizationResponse.fromIntent(result)) {
                "Resposta de autenticação inválida"
            }
            val pending = checkNotNull(attempts.consume(response.state)) { "Tentativa de autenticação inválida ou expirada" }
            try {
                // Vincula a troca à nossa requisição e configuração, não ao response.request vindo do Intent.
                val exchange = TokenRequest.Builder(pending.request.configuration, pending.request.clientId)
                    .setGrantType(GrantTypeValues.AUTHORIZATION_CODE)
                    .setRedirectUri(pending.request.redirectUri)
                    .setCodeVerifier(pending.request.codeVerifier)
                    .setNonce(pending.request.nonce)
                    .setAuthorizationCode(requireNotNull(response.authorizationCode))
                    .build()
                val tokens = suspendCancellableCoroutine<TokenResponse> { continuation ->
                    continuation.invokeOnCancellation { tokenStore.cancelAuthorization(pending.ticket) }
                    service.performTokenRequest(exchange) { tokenResponse, exception ->
                        if (!continuation.isActive) return@performTokenRequest
                        when {
                            exception != null -> continuation.resumeWithException(exception)

                            tokenResponse?.accessToken.isNullOrBlank() -> continuation.resumeWithException(
                                IllegalStateException("Token de acesso ausente")
                            )

                            else -> continuation.resume(tokenResponse)
                        }
                    }
                }
                currentCoroutineContext().ensureActive()
                val ownerKey = endpointIdentity.authorizationOwnerKey(
                    tokens.idToken,
                    tokens.accessToken,
                    pending.request.scope,
                    configuration,
                    checkNotNull(pending.request.nonce),
                    System.currentTimeMillis() / 1000
                )
                currentCoroutineContext().ensureActive()
                synchronized(monitor) {
                    tokenUpdates.commitAuthorization(tokens, pending, ownerKey)
                }
            } finally {
                // Uma função de retorno anterior não deve cancelar uma tentativa mais recente nem limpar uma sessão confirmada.
                tokenStore.cancelAuthorization(pending.ticket)
            }
        }
    }
}
