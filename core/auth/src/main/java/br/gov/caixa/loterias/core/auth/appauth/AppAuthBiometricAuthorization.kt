package br.gov.caixa.loterias.core.auth.appauth

import android.content.Intent
import androidx.core.net.toUri
import br.gov.caixa.loterias.core.auth.biometric.BIOMETRIC_STATE
import br.gov.caixa.loterias.core.auth.gateway.sameBiometricCredential
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.GrantTypeValues
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse

internal class AppAuthBiometricAuthorization(private val session: AppAuthSession) {
    fun setBiometricEnabled(enabled: Boolean) {
        with(session) {
            if (enabled) check(biometricAvailable) { "Cadastre a biometria ou o bloqueio de tela nas configurações do aparelho." }
            if (!enabled) { biometricAttempts.cancel(); biometricChallengeCredential = null }
            biometricCredentials.setEnabled(enabled)
        }
    }

    fun createBiometricAuthorizationIntent(): Intent {
        with(session) {
            check(biometricAvailable && biometricEnabled) { "Biometria indisponível" }
            val credential = checkNotNull(biometricCredentials.read()) { "Entre pelo navegador para habilitar a biometria" }
            attempts.cancel()
            endSessions.clear()
            logoutIdToken = null
            val state = java.util.UUID.randomUUID().toString()
            biometricAttempts.begin(state, credential.ownerKey)
            biometricChallengeCredential = credential
            @Suppress("DEPRECATION")
            val challenge = if (android.os.Build.VERSION.SDK_INT < 29) checkNotNull(
                appContext.getSystemService(android.app.KeyguardManager::class.java)
                    .createConfirmDeviceCredentialIntent("Loterias Caixa", "Desbloqueie seu celular"),
            ) else Intent()
            return challenge.putExtra(BIOMETRIC_STATE, state)
        }
    }

    suspend fun completeBiometricAuthorization(result: Intent?) {
        with(session) {
            val (pending, challengeCredential) = synchronized(monitor) {
                val pending = checkNotNull(biometricAttempts.consume(result?.getStringExtra(BIOMETRIC_STATE))) {
                    "Confirmação biométrica inválida ou encerrada"
                }
                pending to checkNotNull(biometricChallengeCredential).also { biometricChallengeCredential = null }
            }
            try {
                val credential = synchronized(monitor) {
                    check(biometricEnabled)
                    checkNotNull(biometricCredentials.read()).also {
                        check(it.ownerKey == pending.request && sameBiometricCredential(challengeCredential, it))
                    }
                }
                val exchange = TokenRequest.Builder(
                    AuthorizationServiceConfiguration(
                        configuration.authorizationEndpoint.toUri(),
                        configuration.tokenEndpoint.toUri()
                    ),
                    configuration.clientId
                )
                    .setGrantType(GrantTypeValues.REFRESH_TOKEN).setRefreshToken(credential.refreshToken).build()
                val tokens = suspendCancellableCoroutine<TokenResponse> { continuation ->
                    continuation.invokeOnCancellation { tokenStore.cancelAuthorization(pending.ticket) }
                    service.performTokenRequest(exchange) { response, error ->
                        if (!continuation.isActive) return@performTokenRequest
                        if (error != null) {
                            continuation.resumeWithException(error)
                        } else if (response?.accessToken.isNullOrBlank() || response.refreshToken.isNullOrBlank()) {
                            continuation.resumeWithException(IllegalStateException("Resposta de renovação inválida"))
                        } else {
                            continuation.resume(response)
                        }
                    }
                }
                currentCoroutineContext().ensureActive()
                val owner = endpointIdentity.biometricRefreshOwnerKey(
                    tokens.idToken,
                    tokens.accessToken,
                    configuration,
                    credential.nonce,
                    credential.ownerKey,
                    System.currentTimeMillis() / 1000
                )
                currentCoroutineContext().ensureActive()
                synchronized(monitor) {
                    tokenUpdates.commitBiometricTokens(
                        tokens = tokens,
                        pending = pending,
                        owner = owner,
                        refresh = checkNotNull(tokens.refreshToken),
                        credential = credential
                    )
                }
            } catch (failure: AuthorizationException) {
                if (failure.type == AuthorizationException.TokenRequestErrors.INVALID_GRANT.type &&
                    failure.code == AuthorizationException.TokenRequestErrors.INVALID_GRANT.code
                ) {
                    synchronized(monitor) {
                        // Uma credencial lembrada revogada ou expirada não deve prender o usuário em renovações repetidas.
                        if (tokenStore.isAuthorizationCurrent(pending.ticket) &&
                            sameBiometricCredential(challengeCredential, biometricCredentials.read())
                        ) {
                            biometricCredentials.clearCredential()
                        }
                    }
                }
                throw failure
            } finally {
                tokenStore.cancelAuthorization(pending.ticket)
            }
        }
    }
}
