package br.gov.caixa.loterias.core.auth.appauth

import android.os.Looper
import androidx.core.net.toUri
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshRequest
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshResponse
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.GrantTypeValues
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse

internal class AppAuthRefresh(private val session: AppAuthSession) {
    fun execute(request: RegularSessionRefreshRequest): RegularSessionRefreshResponse {
        with(session) {
            check(Looper.myLooper() != Looper.getMainLooper()) { "Renovação requer uma thread de trabalho" }
            check(request.context.configuration == configuration && request.refreshToken.isNotBlank()) {
                "Contexto de renovação inválido"
            }
            val exchange = TokenRequest.Builder(
                AuthorizationServiceConfiguration(
                    configuration.authorizationEndpoint.toUri(),
                    configuration.tokenEndpoint.toUri()
                ),
                configuration.clientId
            )
                .setGrantType(GrantTypeValues.REFRESH_TOKEN).setRefreshToken(request.refreshToken)
                .setNonce(request.context.originalNonce).build()
            val finished = CountDownLatch(1)
            val response = AtomicReference<TokenResponse?>()
            val failure = AtomicReference<AuthorizationException?>()
            service.performTokenRequest(exchange) { tokens, error ->
                response.set(tokens)
                failure.set(error)
                finished.countDown()
            }
            val completed = try {
                finished.await(30, TimeUnit.SECONDS)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
                throw IllegalStateException("Renovação interrompida")
            }
            check(completed && failure.get() == null) { "Não foi possível renovar a sessão" }
            val tokens = checkNotNull(response.get()) { "Resposta de renovação inválida" }
            // Nenhuma alteração de sessão ou biometria aqui: o coordenador só confirma se sua concessão ainda for atual.
            return endpointIdentity.regularRefreshResponse(
                tokens.idToken,
                tokens.accessToken,
                tokens.refreshToken,
                request,
                System.currentTimeMillis() / 1000
            )
        }
    }
}
