package br.gov.caixa.loterias.core.auth.gateway

import br.gov.caixa.loterias.feature.account.domain.model.EndSessionLaunch
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionOutcome
import android.content.Context
import android.content.Intent
import br.gov.caixa.loterias.core.auth.appauth.AppAuthRefresh
import br.gov.caixa.loterias.core.auth.authorization.AuthorizationRequestPolicy
import br.gov.caixa.loterias.core.auth.appauth.AppAuthBiometricAuthorization
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.auth.appauth.AppAuthEndSession
import br.gov.caixa.loterias.core.auth.biometric.BiometricCredential
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionIdentityMode
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshContext
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshRequest
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshResponse
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshExecutor
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshResult
import br.gov.caixa.loterias.core.auth.appauth.AppAuthSession
import br.gov.caixa.loterias.core.auth.appauth.AppAuthBrowserAuthorization

internal fun biometricRefreshContext(configuration: OidcConfiguration, nonce: String, idToken: String?): RegularSessionRefreshContext =
    RegularSessionRefreshContext(configuration, nonce,
        if (idToken != null) RegularSessionIdentityMode.OIDC else RegularSessionIdentityMode.OAUTH_ACCESS,
        rememberedBiometric = true)

internal fun rotatedBiometricCredential(request: RegularSessionRefreshRequest, response: RegularSessionRefreshResponse,
    enabled: Boolean, previous: BiometricCredential?,
): BiometricCredential? {
    if (!enabled || previous == null ||
        (request.context.mode != RegularSessionIdentityMode.OIDC && !request.context.rememberedBiometric) ||
        previous.ownerKey != request.ownerKey || response.ownerKey != request.ownerKey ||
        previous.nonce != request.context.originalNonce || previous.refreshToken != request.refreshToken ||
        response.refreshToken.isBlank()
    ) {
        return null
    }
    return BiometricCredential(response.refreshToken, previous.ownerKey, previous.nonce, previous.firstName)
}

class AppAuthAuthenticationGateway(
    context: Context,
    private val configuration: OidcConfiguration,
    private val tokenStore: SessionTokenStore,
    private val requestPolicy: AuthorizationRequestPolicy,
) : AuthenticationGateway,
    EndSessionGateway,
    BiometricAuthenticationGateway,
    RegularSessionRefreshExecutor {
    private val session = AppAuthSession(context, configuration, tokenStore, requestPolicy, this, ::logout)
    private val browser = AppAuthBrowserAuthorization(session)
    private val biometric = AppAuthBiometricAuthorization(session)
    private val refresh = AppAuthRefresh(session)
    private val ending = AppAuthEndSession(session)
    override val biometricEnabled: Boolean get() = session.biometricEnabled

    @get:android.annotation.SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    override val biometricAvailable: Boolean get() = session.biometricAvailable

    @get:android.annotation.SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    override val biometricHardwareAvailable: Boolean get() = session.biometricHardwareAvailable
    override val biometricCredentialAvailable: Boolean get() = synchronized(this) { session.biometricCredentials.read() != null }

    override val biometricFirstName: String? get() = synchronized(this) { session.biometricCredentials.read()?.firstName }

    @Synchronized override fun setBiometricEnabled(enabled: Boolean) = biometric.setBiometricEnabled(enabled)

    @Synchronized override fun createBiometricAuthorizationIntent(): Intent = biometric.createBiometricAuthorizationIntent()
    override suspend fun completeBiometricAuthorization(result: Intent?) = biometric.completeBiometricAuthorization(result)

    @Synchronized override fun withCommitGuard(commit: () -> RegularSessionRefreshResult): RegularSessionRefreshResult = commit()
    override fun afterCommit(request: RegularSessionRefreshRequest, response: RegularSessionRefreshResponse) =
        session.tokenUpdates.afterCommit(request, response)
    override fun execute(request: RegularSessionRefreshRequest): RegularSessionRefreshResponse = refresh.execute(request)

    @Synchronized override fun createAuthorizationIntent(): Intent = browser.createAuthorizationIntent()

    @Synchronized override fun createAccountSwitchAuthorizationIntent(): Intent = browser.createAuthorizationIntent(switchUser = true)

    @Synchronized override fun createSilentAuthorizationIntent(): Intent = browser.createAuthorizationIntent(silent = true)
    override suspend fun completeAuthorization(result: Intent?) = browser.completeAuthorization(result)

    @Synchronized override fun cancelAuthorization() = ending.cancelAuthorization()

    @Synchronized override fun logout(): Unit = ending.logout()

    @Synchronized override fun prepare(): EndSessionLaunch = ending.prepare()
    override fun intentFor(launch: EndSessionLaunch): Intent = ending.intentFor(launch)
    override fun complete(launch: EndSessionLaunch, result: Intent?): EndSessionOutcome = ending.complete(launch, result)
    override fun discard(launch: EndSessionLaunch) = ending.discard(launch)
}
