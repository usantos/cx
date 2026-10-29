package br.gov.caixa.loterias.core.auth.appauth

import android.content.Intent
import androidx.core.net.toUri
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionLaunch
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionOutcome
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.EndSessionResponse

internal class AppAuthEndSession(private val session: AppAuthSession) {
    fun cancelAuthorization() {
        with(session) {
            biometricAttempts.cancel()
            biometricChallengeCredential = null
            attempts.cancel()
        }
    }

    fun logout() {
        with(session) {
            // Uma segunda limpeza local (como a confirmação de conta bloqueada) não deve perder a indicação.
            biometricAttempts.cancel()
            biometricChallengeCredential = null
            // Uma falha de armazenamento deve manter a identidade atual disponível para uma nova tentativa explícita.
            biometricCredentials.clearCredential()
            logoutIdToken = attempts.logout() ?: logoutIdToken
        }
    }

    fun prepare(): EndSessionLaunch {
        with(session) {
            val hint = logoutIdToken
            logoutIdToken = null
            endSessions.clear()
            val endpoint = configuration.endSessionEndpoint.toUri()
            require(endpoint.scheme == "https" && !endpoint.host.isNullOrBlank())
            check(tokenStore.token() == null) { "Encerre a sessão local antes do logout SSO" }
            val serviceConfiguration = AuthorizationServiceConfiguration(
                configuration.authorizationEndpoint.toUri(),
                configuration.tokenEndpoint.toUri(),
                null,
                endpoint
            )
            val request = EndSessionRequest.Builder(serviceConfiguration)
                .setIdTokenHint(hint)
                .setPostLogoutRedirectUri(configuration.postLogoutRedirectUri.toUri())
                .setAdditionalParameters(mapOf("client_id" to configuration.clientId))
                .build()
            return endSessions.prepare(requireNotNull(request.state), request)
        }
    }

    fun intentFor(launch: EndSessionLaunch): Intent = with(session) {
        service.getEndSessionRequestIntent(
            checkNotNull(endSessions.takeRequest(launch)) { "Solicitação de logout encerrada" }
        )
    }

    fun complete(launch: EndSessionLaunch, result: Intent?): EndSessionOutcome {
        with(session) {
            val error = result?.let(AuthorizationException::fromIntent)
            val cancelled = result == null || error?.let {
                it.type == AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW.type &&
                    it.code == AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW.code
            } == true
            val state = if (error == null) result?.let(EndSessionResponse::fromIntent)?.state else null
            return endSessions.complete(launch, state, cancelled)
        }
    }

    fun discard(launch: EndSessionLaunch) = with(session) { endSessions.discard(launch) }
}
