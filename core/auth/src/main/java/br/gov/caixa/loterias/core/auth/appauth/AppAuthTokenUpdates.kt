package br.gov.caixa.loterias.core.auth.appauth

import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.TokenResponse
import br.gov.caixa.loterias.core.auth.gateway.sameBiometricCredential
import br.gov.caixa.loterias.core.auth.biometric.BiometricCredential
import br.gov.caixa.loterias.core.auth.authorization.PendingAuthorization
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionIdentityMode
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshContext
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshRequest
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshResponse
import br.gov.caixa.loterias.core.auth.gateway.biometricRefreshContext
import br.gov.caixa.loterias.core.auth.gateway.rotatedBiometricCredential

internal class AppAuthTokenUpdates(private val session: AppAuthSession) {
    fun commitBiometricTokens(tokens: TokenResponse, pending: PendingAuthorization<String>, owner: String,
        refresh: String, credential: BiometricCredential,
    ) {
        with(session) {
            check(biometricEnabled && sameBiometricCredential(credential, biometricCredentials.read()))
            val nonce = credential.nonce
            val context = biometricRefreshContext(configuration, nonce, tokens.idToken)
            check(
                tokenStore.completeAuthorization(
                    pending.ticket,
                    tokens.accessToken!!,
                    tokens.idToken,
                    owner,
                    refresh,
                    context
                )
            ) {
                "Tentativa de autenticação encerrada"
            }
            try {
                biometricCredentials.write(BiometricCredential(refresh, owner, nonce, credential.firstName))
            } catch (failure: Exception) {
                logout()
                throw failure
            }
        }
    }

    fun commitAuthorization(tokens: TokenResponse, pending: PendingAuthorization<AuthorizationRequest>, ownerKey: String) {
        with(session) {
            val remembered = biometricEnabled && !tokens.refreshToken.isNullOrBlank()
            val mode = if (tokens.idToken != null) RegularSessionIdentityMode.OIDC else RegularSessionIdentityMode.OAUTH_ACCESS
            check(
                tokenStore.completeAuthorization(
                    pending.ticket,
                    tokens.accessToken!!,
                    tokens.idToken,
                    ownerKey,
                    tokens.refreshToken,
                    RegularSessionRefreshContext(
                        configuration,
                        checkNotNull(pending.request.nonce),
                        mode,
                        rememberedBiometric = remembered
                    )
                )
            ) {
                "Tentativa de autenticação encerrada"
            }
            try {
                // A identidade já foi validada pelo endpoint TLS, inclusive no fluxo OAuth sem token de ID.
                if (remembered) {
                    biometricCredentials.write(
                        BiometricCredential(
                            tokens.refreshToken!!,
                            ownerKey,
                            checkNotNull(pending.request.nonce),
                            br.gov.caixa.loterias.core.auth.session.JwtCurrentUserIdentityProvider(tokenStore).currentUser()?.name
                                ?.trim()?.split(Regex("\\s+"))?.firstOrNull()?.takeIf { it.isNotBlank() }
                                ?.lowercase(java.util.Locale.forLanguageTag("pt-BR"))
                                ?.replaceFirstChar { it.titlecase(java.util.Locale.forLanguageTag("pt-BR")) }
                        )
                    )
                } else {
                    biometricCredentials.clearCredential()
                }
            } catch (failure: Exception) {
                logout()
                throw failure
            }
        }
    }

    fun afterCommit(request: RegularSessionRefreshRequest, response: RegularSessionRefreshResponse) {
        with(session) {
            check(Thread.holdsLock(monitor) && Thread.holdsLock(tokenStore)) { "Renovação sem proteção de sessão" }
            val credential = rotatedBiometricCredential(request, response, biometricEnabled, biometricCredentials.read()) ?: return
            try {
                biometricCredentials.write(credential)
            } catch (_: Exception) {
                // A renovação da sessão já foi confirmada. Remove credenciais lembradas obsoletas para que
                // uma falha de armazenamento leve ao login pelo navegador sem desfazer a sessão ativa.
                biometricCredentials.clearCredential()
            }
        }
    }
}
