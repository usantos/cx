package br.gov.caixa.loterias.core.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Looper
import android.util.Base64
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import net.openid.appauth.GrantTypeValues
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.EndSessionResponse
import net.openid.appauth.TokenResponse
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal fun rotatedBiometricCredential(request: RegularSessionRefreshRequest, response: RegularSessionRefreshResponse,
    enabled: Boolean, previous: BiometricCredential?): BiometricCredential? {
    if (!enabled || previous == null || request.context.mode != RegularSessionIdentityMode.OIDC ||
        previous.ownerKey != request.ownerKey || response.ownerKey != request.ownerKey ||
        previous.nonce != request.context.originalNonce || response.refreshToken.isBlank()) return null
    return BiometricCredential(response.refreshToken, previous.ownerKey, previous.nonce)
}

class AppAuthAuthenticationGateway(
    context: Context,
    private val configuration: OidcConfiguration,
    private val tokenStore: SessionTokenStore,
    private val requestPolicy: AuthorizationRequestPolicy,
) : AuthenticationGateway, EndSessionGateway, BiometricAuthenticationGateway, RegularSessionRefreshExecutor {
    private val appContext = context.applicationContext
    private val biometricCredentials = EncryptedBiometricCredentialStore(appContext,
        "${configuration.sessionNamespace}|${configuration.expectedIssuer}|${configuration.clientId}")
    private val biometricAttempts = BiometricAttemptRegistry(tokenStore)
    override val biometricEnabled: Boolean get() = biometricCredentials.enabled
    // USE_BIOMETRIC is declared by the app; library lint cannot see its manifest.
    // The runtime permission guard below also fails closed when another host omits it.
    @get:android.annotation.SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    override val biometricAvailable: Boolean get() {
        val keyguard = appContext.getSystemService(android.app.KeyguardManager::class.java)
        if (android.os.Build.VERSION.SDK_INT < 29) return keyguard.isDeviceSecure
        if (appContext.checkSelfPermission(android.Manifest.permission.USE_BIOMETRIC) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED) return false
        val manager = appContext.getSystemService(android.hardware.biometrics.BiometricManager::class.java)
        return if (android.os.Build.VERSION.SDK_INT >= 30) manager.canAuthenticate(
            android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_WEAK or
                android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL,
        ) == android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS
        else keyguard.isDeviceSecure || manager.canAuthenticate() == android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS
    }
    override val biometricCredentialAvailable: Boolean get() = synchronized(this) { biometricCredentials.read() != null }

    @Synchronized override fun setBiometricEnabled(enabled: Boolean) {
        if (enabled) check(biometricAvailable) { "Cadastre a biometria ou o bloqueio de tela nas configurações do aparelho." }
        if (!enabled) biometricAttempts.cancel()
        biometricCredentials.setEnabled(enabled)
    }

    @Synchronized override fun createBiometricAuthorizationIntent(): Intent {
        check(biometricAvailable && biometricEnabled) { "Biometria indisponível" }
        val credential = checkNotNull(biometricCredentials.read()) { "Entre pelo navegador para habilitar a biometria" }
        attempts.cancel()
        endSessions.clear()
        logoutIdToken = null
        val state = java.util.UUID.randomUUID().toString()
        biometricAttempts.begin(state, credential.ownerKey)
        @Suppress("DEPRECATION")
        val challenge = if (android.os.Build.VERSION.SDK_INT < 29) checkNotNull(
            appContext.getSystemService(android.app.KeyguardManager::class.java)
                .createConfirmDeviceCredentialIntent("Loterias Caixa", "Desbloqueie seu celular"),
        ) else Intent()
        return challenge.putExtra(BIOMETRIC_STATE, state)
    }

    override suspend fun completeBiometricAuthorization(result: Intent?) {
        val pending = checkNotNull(biometricAttempts.consume(result?.getStringExtra(BIOMETRIC_STATE))) {
            "Confirmação biométrica inválida ou encerrada"
        }
        try {
            val credential = synchronized(this) {
                check(biometricEnabled)
                checkNotNull(biometricCredentials.read()).also { check(it.ownerKey == pending.request) }
            }
            val exchange = TokenRequest.Builder(AuthorizationServiceConfiguration(Uri.parse(configuration.authorizationEndpoint),
                Uri.parse(configuration.tokenEndpoint)), configuration.clientId)
                .setGrantType(GrantTypeValues.REFRESH_TOKEN).setRefreshToken(credential.refreshToken).build()
            val tokens = suspendCancellableCoroutine<TokenResponse> { continuation ->
                continuation.invokeOnCancellation { tokenStore.cancelAuthorization(pending.ticket) }
                service.performTokenRequest(exchange) { response, error ->
                    if (!continuation.isActive) return@performTokenRequest
                    if (error != null) continuation.resumeWithException(error)
                    else if (response?.accessToken.isNullOrBlank()) continuation.resumeWithException(IllegalStateException("Token de acesso ausente"))
                    else continuation.resume(response)
                }
            }
            currentCoroutineContext().ensureActive()
            val owner = endpointIdentity.refreshedOwnerKey(checkNotNull(tokens.idToken), configuration,
                credential.nonce, credential.ownerKey, System.currentTimeMillis() / 1000)
            currentCoroutineContext().ensureActive()
            synchronized(this) {
                commitBiometricTokens(tokens = tokens, pending = pending, owner = owner,
                    refresh = tokens.refreshToken ?: credential.refreshToken, nonce = credential.nonce)
            }
        } catch (failure: AuthorizationException) {
            if (failure.type == AuthorizationException.TokenRequestErrors.INVALID_GRANT.type &&
                failure.code == AuthorizationException.TokenRequestErrors.INVALID_GRANT.code) {
                synchronized(this) {
                    // A revoked/expired remembered credential must not trap the user in repeated refreshes.
                    if (tokenStore.isAuthorizationCurrent(pending.ticket)) biometricCredentials.clearCredential()
                }
            }
            throw failure
        } finally { tokenStore.cancelAuthorization(pending.ticket) }
    }

    private fun commitBiometricTokens(tokens: TokenResponse, pending: PendingAuthorization<String>, owner: String,
        refresh: String, nonce: String) {
        check(biometricEnabled)
        check(tokenStore.completeAuthorization(pending.ticket, tokens.accessToken!!, tokens.idToken, owner,
            tokens.refreshToken ?: refresh, RegularSessionRefreshContext(configuration, nonce, RegularSessionIdentityMode.OIDC))) {
            "Tentativa de autenticação encerrada"
        }
        try { biometricCredentials.write(BiometricCredential(refresh, owner, nonce)) }
        catch (failure: Exception) { logout(); throw failure }
    }

    private val service = AuthorizationService(context.applicationContext)
    private val attempts = AuthorizationAttemptRegistry<AuthorizationRequest>(tokenStore)
    private val endSessions = EndSessionRegistry<EndSessionRequest>()
    private var logoutIdToken: String? = null
    private val endpointIdentity = TokenEndpointIdentity { Base64.decode(it, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING) }

    // Every gateway mutation already takes this monitor. Acquire it before the store monitor
    // so disable/logout/new login cannot interleave with post-CAS credential persistence.
    @Synchronized override fun withCommitGuard(commit: () -> RegularSessionRefreshResult): RegularSessionRefreshResult = commit()

    override fun afterCommit(request: RegularSessionRefreshRequest, response: RegularSessionRefreshResponse) {
        check(Thread.holdsLock(this) && Thread.holdsLock(tokenStore)) { "Renovação sem proteção de sessão" }
        val credential = rotatedBiometricCredential(request, response, biometricEnabled, biometricCredentials.read()) ?: return
        try { biometricCredentials.write(credential) }
        catch (_: Exception) {
            // Session refresh has already committed. Remove stale remembered credentials so
            // storage failure falls back to browser login without undoing the active session.
            biometricCredentials.clearCredential()
        }
    }

    /** OkHttp's worker calls this; AppAuth delivers its callback independently on the main loop. */
    override fun execute(request: RegularSessionRefreshRequest): RegularSessionRefreshResponse {
        check(Looper.myLooper() != Looper.getMainLooper()) { "Renovação requer uma thread de trabalho" }
        check(request.context.configuration == configuration && request.refreshToken.isNotBlank()) {
            "Contexto de renovação inválido"
        }
        val exchange = TokenRequest.Builder(AuthorizationServiceConfiguration(Uri.parse(configuration.authorizationEndpoint),
            Uri.parse(configuration.tokenEndpoint)), configuration.clientId)
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
        val completed = try { finished.await(30, TimeUnit.SECONDS) }
        catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IllegalStateException("Renovação interrompida")
        }
        check(completed && failure.get() == null) { "Não foi possível renovar a sessão" }
        val tokens = checkNotNull(response.get()) { "Resposta de renovação inválida" }
        // No session/biometric mutation here: coordinator commits only if its lease is current.
        return endpointIdentity.regularRefreshResponse(tokens.idToken, tokens.accessToken, tokens.refreshToken,
            request, System.currentTimeMillis() / 1000)
    }

    @Synchronized override fun createAuthorizationIntent(): Intent {
        biometricAttempts.cancel()
        logoutIdToken = null
        endSessions.clear()
        val options = requestPolicy.capture()
        // A new builder generates fresh state, nonce and PKCE verifier for every attempt.
        val request = AuthorizationRequest.Builder(
            AuthorizationServiceConfiguration(
                Uri.parse(configuration.authorizationEndpoint),
                Uri.parse(configuration.tokenEndpoint),
            ),
            configuration.clientId,
            ResponseTypeValues.CODE,
            Uri.parse(configuration.redirectUri),
        ).setScope(options.scope).setAdditionalParameters(options.additionalParameters).build()
        attempts.begin(requireNotNull(request.state), request)
        return service.getAuthorizationRequestIntent(request)
    }

    override suspend fun completeAuthorization(result: Intent?) {
        requireNotNull(result) { "Autenticação cancelada" }
        AuthorizationException.fromIntent(result)?.let { throw it }
        val response = requireNotNull(AuthorizationResponse.fromIntent(result)) {
            "Resposta de autenticação inválida"
        }
        val pending = checkNotNull(attempts.consume(response.state)) { "Tentativa de autenticação inválida ou expirada" }
        try {
            // Bind the exchange to our request/configuration, not response.request from the intent.
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
                            IllegalStateException("Token de acesso ausente"),
                        )
                        else -> continuation.resume(tokenResponse)
                    }
                }
            }
            currentCoroutineContext().ensureActive()
            val ownerKey = endpointIdentity.authorizationOwnerKey(tokens.idToken, tokens.accessToken, pending.request.scope,
                configuration, checkNotNull(pending.request.nonce), System.currentTimeMillis() / 1000)
            currentCoroutineContext().ensureActive()
            synchronized(this) {
                val mode = if (tokens.idToken != null) RegularSessionIdentityMode.OIDC else RegularSessionIdentityMode.OAUTH_ACCESS
                check(tokenStore.completeAuthorization(pending.ticket, tokens.accessToken!!, tokens.idToken, ownerKey,
                    tokens.refreshToken, RegularSessionRefreshContext(configuration, checkNotNull(pending.request.nonce), mode))) {
                    "Tentativa de autenticação encerrada"
                }
                try {
                    if (biometricEnabled && tokens.idToken != null && !tokens.refreshToken.isNullOrBlank()) biometricCredentials.write(
                        BiometricCredential(tokens.refreshToken!!, ownerKey, checkNotNull(pending.request.nonce)))
                    else biometricCredentials.clearCredential()
                } catch (failure: Exception) { logout(); throw failure }
            }
        } finally {
            // A previous callback must not cancel a newer attempt or clear a committed session.
            tokenStore.cancelAuthorization(pending.ticket)
        }
    }

    @Synchronized override fun cancelAuthorization() {
        biometricAttempts.cancel()
        attempts.cancel()
    }
    @Synchronized override fun logout() {
        // A second local clear (e.g. blocked-account acknowledgment) must not lose the hint.
        biometricAttempts.cancel()
        // A storage failure must leave the current identity available for an explicit retry.
        biometricCredentials.clearCredential()
        logoutIdToken = attempts.logout() ?: logoutIdToken
    }

    @Synchronized override fun prepare(): EndSessionLaunch {
        val hint = logoutIdToken
        logoutIdToken = null
        endSessions.clear()
        val endpoint = Uri.parse(configuration.endSessionEndpoint)
        require(endpoint.scheme == "https" && !endpoint.host.isNullOrBlank())
        check(tokenStore.token() == null) { "Encerre a sessão local antes do logout SSO" }
        val serviceConfiguration = AuthorizationServiceConfiguration(
            Uri.parse(configuration.authorizationEndpoint), Uri.parse(configuration.tokenEndpoint), null, endpoint,
        )
        val request = EndSessionRequest.Builder(serviceConfiguration)
            .setIdTokenHint(hint)
            .setPostLogoutRedirectUri(Uri.parse(configuration.postLogoutRedirectUri))
            .setAdditionalParameters(mapOf("client_id" to configuration.clientId))
            .build()
        return endSessions.prepare(requireNotNull(request.state), request)
    }

    override fun intentFor(launch: EndSessionLaunch): Intent = service.getEndSessionRequestIntent(
        checkNotNull(endSessions.takeRequest(launch)) { "Solicitação de logout encerrada" },
    )

    override fun complete(launch: EndSessionLaunch, result: Intent?): EndSessionOutcome {
        val error = result?.let(AuthorizationException::fromIntent)
        val cancelled = result == null || error?.let {
            it.type == AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW.type &&
                it.code == AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW.code
        } == true
        val state = if (error == null) result?.let(EndSessionResponse::fromIntent)?.state else null
        return endSessions.complete(launch, state, cancelled)
    }

    override fun discard(launch: EndSessionLaunch) = endSessions.discard(launch)
}
