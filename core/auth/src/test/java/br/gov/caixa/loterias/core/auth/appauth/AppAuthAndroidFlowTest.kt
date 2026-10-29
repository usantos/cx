// Literais numéricos descrevem payloads sintéticos, níveis de API e limites esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.auth.appauth

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.util.Base64
import br.gov.caixa.loterias.core.auth.authorization.AuthorizationDeviceInfoProvider
import br.gov.caixa.loterias.core.auth.authorization.AuthorizationRequestPolicy
import br.gov.caixa.loterias.core.auth.authorization.WeakBiometricAvailability
import br.gov.caixa.loterias.core.auth.biometric.BIOMETRIC_STATE
import br.gov.caixa.loterias.core.auth.biometric.CredentialTestProvider
import br.gov.caixa.loterias.core.auth.biometric.TestCredentialKeyGenerator
import br.gov.caixa.loterias.core.auth.biometric.TestCredentialKeyStore
import br.gov.caixa.loterias.core.auth.gateway.AppAuthAuthenticationGateway
import br.gov.caixa.loterias.core.auth.gateway.OidcConfiguration
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionIdentityMode
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshContext
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshRequest
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshResponse
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshResult
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionOutcome
import java.security.Security
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.EndSessionResponse
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/** Substitui só o transporte navegador/HTTP do SDK; requisições, registros e validação de tokens continuam reais. */
@Implements(AuthorizationService::class)
class FlowAuthorizationServiceShadow {
    // O Robolectric exige o nome exato da ABI do shadow do construtor do SDK.
    @Suppress("ktlint:standard:function-naming")
    @Implementation
    fun __constructor__(context: Context) {
        require(context.packageName.isNotBlank())
    }

    @Implementation fun getAuthorizationRequestIntent(request: AuthorizationRequest): Intent {
        authorization = request
        return Intent("test.authorize")
    }

    @Implementation fun getEndSessionRequestIntent(request: EndSessionRequest): Intent {
        ending = request
        return Intent("test.end")
    }

    @Implementation fun performTokenRequest(request: TokenRequest, callback: AuthorizationService.TokenResponseCallback) {
        exchange = request
        val handler = respond
        if (handler != null) handler(request, callback) else pending = callback
    }
    companion object {
        var authorization: AuthorizationRequest? = null
        var ending: EndSessionRequest? = null
        var exchange: TokenRequest? = null
        var pending: AuthorizationService.TokenResponseCallback? = null
        var respond: ((TokenRequest, AuthorizationService.TokenResponseCallback) -> Unit)? = null
        fun reset() {
            authorization = null
            ending = null
            exchange = null
            pending = null
            respond = null
        }
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], shadows = [FlowAuthorizationServiceShadow::class, FlowBiometricManagerShadow::class])
class AppAuthAndroidFlowTest {
    private val context get() = RuntimeEnvironment.getApplication()
    private val configuration =
        OidcConfiguration(
            "https://s.example/protocol/openid-connect/auth",
            "https://s.example/protocol/openid-connect/token",
            "client",
            "app:/callback",
            "https://s.example/logout",
            "app:/logout",
            "https://api.example/v1/",
            "https://s.example"
        )
    private lateinit var session: AppAuthSession
    private lateinit var store: SessionTokenStore
    private var logoutCalls = 0

    @Before fun setup() {
        Security.removeProvider("AndroidKeyStore")
        Security.addProvider(CredentialTestProvider())
        TestCredentialKeyStore.keys.clear()
        FlowAuthorizationServiceShadow.reset()
        TestCredentialKeyGenerator.failGeneration = false
        store = SessionTokenStore()
        session =
            AppAuthSession(
                context,
                configuration,
                store,
                AuthorizationRequestPolicy(
                    AuthorizationDeviceInfoProvider {
                        "test-device"
                    },
                    WeakBiometricAvailability { false }
                ),
                Any(),
                {
                    logoutCalls++
                    store.clear()
                }
            )
    }

    @After fun cleanup() {
        Security.removeProvider("AndroidKeyStore")
        TestCredentialKeyStore.keys.clear()
    }
    private fun jwt(nonce: String?, subject: String = "test-user"): String {
        val now = System.currentTimeMillis() / 1000
        val claims = JSONObject().put(
            "iss",
            configuration.expectedIssuer
        ).put("sub", subject).put("name", "  JOÃO DA SILVA  ").put("cpf", "12345678901")
            .put("aud", "client").put("azp", "client").put("iat", now).put(
                "exp",
                now + 300
            )
        if (nonce != null) claims.put("nonce", nonce)
        fun encode(value: String) = Base64.encodeToString(value.toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        return encode("{\"alg\":\"RS256\"}") + "." + encode(claims.toString()) + ".c2ln"
    }
    private fun response(request: TokenRequest, access: String = "access", refresh: String? = "refresh", id: String? = null) =
        TokenResponse.Builder(request)
            .setTokenType("Bearer").setAccessToken(access).setRefreshToken(refresh).setIdToken(id).build()
    private fun begin(): Pair<AppAuthBrowserAuthorization, AuthorizationRequest> {
        val browser = AppAuthBrowserAuthorization(session)
        assertEquals("test.authorize", browser.createAuthorizationIntent().action)
        return browser to FlowAuthorizationServiceShadow.authorization!!
    }
    private fun result(request: AuthorizationRequest) =
        AuthorizationResponse.Builder(request).setState(request.state).setAuthorizationCode("auth-code").build().toIntent()

    @Test fun `switch account forces interactive login with fresh PKCE and correlation`() {
        session.biometricCredentials.setEnabled(true)
        session.biometricCredentials.write(
            br.gov.caixa.loterias.core.auth.biometric.BiometricCredential("saved-refresh", "a".repeat(64), "saved-nonce", "João")
        )
        val browser = AppAuthBrowserAuthorization(session)
        browser.createAuthorizationIntent()
        val previous = checkNotNull(FlowAuthorizationServiceShadow.authorization)
        browser.createAuthorizationIntent(switchUser = true)
        val current = checkNotNull(FlowAuthorizationServiceShadow.authorization)
        assertEquals("login", current.prompt)
        assertEquals("João", session.biometricCredentials.read()!!.firstName)
        assertNotEquals(previous.state, current.state)
        assertNotEquals(previous.nonce, current.nonce)
        assertNotEquals(previous.codeVerifier, current.codeVerifier)
        assertNull(store.captureIdentity())
    }

    @Test fun `silent browser request forbids interaction and keeps fresh state nonce and PKCE`() {
        val browser = AppAuthBrowserAuthorization(session)
        browser.createAuthorizationIntent(silent = true)
        val silent = checkNotNull(FlowAuthorizationServiceShadow.authorization)
        assertEquals("none", silent.prompt)
        assertEquals("openid", silent.scope)
        assertNotNull(silent.state)
        assertNotNull(silent.nonce)
        assertNotNull(silent.codeVerifier)
        browser.createAuthorizationIntent()
        val interactive = checkNotNull(FlowAuthorizationServiceShadow.authorization)
        assertNull(interactive.prompt)
        assertNotEquals(silent.state, interactive.state)
        assertNotEquals(silent.nonce, interactive.nonce)
        assertNotEquals(silent.codeVerifier, interactive.codeVerifier)
    }

    @Test fun `browser request uses fresh correlation and code exchange binds local PKCE and nonce`() = runBlocking {
        val (browser, first) = begin()
        val (_, current) = begin()
        assertNotEquals(first.state, current.state)
        assertNotEquals(first.nonce, current.nonce)
        assertNotEquals(first.codeVerifier, current.codeVerifier)
        assertTrue(runCatching { browser.completeAuthorization(result(first)) }.isFailure)
        FlowAuthorizationServiceShadow.respond =
            { request, callback -> callback.onTokenRequestCompleted(response(request, id = jwt(current.nonce)), null) }
        browser.completeAuthorization(result(current))
        assertEquals("auth-code", FlowAuthorizationServiceShadow.exchange!!.authorizationCode)
        assertEquals(current.codeVerifier, FlowAuthorizationServiceShadow.exchange!!.codeVerifier)
        assertEquals(current.nonce, FlowAuthorizationServiceShadow.exchange!!.nonce)
        assertEquals("access", store.token())
        assertNotNull(store.captureIdentity())
        assertNull(session.biometricCredentials.read())
        assertTrue(runCatching { browser.completeAuthorization(result(current)) }.isFailure)
    }

    @Test fun `browser failures cancellation and late callback cannot replace a new attempt`() = runBlocking {
        val (browser, request) = begin()
        assertTrue(runCatching { browser.completeAuthorization(null) }.isFailure)
        assertTrue(runCatching { browser.completeAuthorization(Intent()) }.isFailure)
        assertTrue(
            runCatching {
                browser.completeAuthorization(AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW.toIntent())
            }.isFailure
        )
        val pending = async(start = CoroutineStart.UNDISPATCHED) { browser.completeAuthorization(result(request)) }
        val callback = FlowAuthorizationServiceShadow.pending!!
        pending.cancel()
        pending.join()
        assertNull(store.token())
        val (_, next) = begin()
        callback.onTokenRequestCompleted(response(FlowAuthorizationServiceShadow.exchange!!, id = jwt(request.nonce)), null)
        assertNull(store.token())
        FlowAuthorizationServiceShadow.respond =
            { _, cb -> cb.onTokenRequestCompleted(null, AuthorizationException.TokenRequestErrors.INVALID_GRANT) }
        assertTrue(runCatching { browser.completeAuthorization(result(next)) }.exceptionOrNull() is AuthorizationException)
        assertNull(store.token())
        val (_, missing) = begin()
        FlowAuthorizationServiceShadow.respond =
            { tokenRequest, cb -> cb.onTokenRequestCompleted(response(tokenRequest, access = ""), null) }
        assertTrue(runCatching { browser.completeAuthorization(result(missing)) }.isFailure)
        assertNull(store.token())
    }

    @Test fun `authorization stores optional remembered credential and refresh rotates under the session guard`() = runBlocking {
        session.biometricCredentials.setEnabled(true)
        val (browser, request) = begin()
        FlowAuthorizationServiceShadow.respond =
            { exchange, callback ->
                callback.onTokenRequestCompleted(response(exchange, access = jwt(request.nonce), id = jwt(request.nonce)), null)
            }
        browser.completeAuthorization(result(request))
        val credential = session.biometricCredentials.read()!!
        assertEquals("refresh", credential.refreshToken)
        assertEquals("João", credential.firstName)
        assertEquals(request.nonce, credential.nonce)
        val refreshRequest = RegularSessionRefreshRequest(
            "refresh",
            credential.ownerKey,
            RegularSessionRefreshContext(configuration, credential.nonce, RegularSessionIdentityMode.OIDC),
            jwt(request.nonce)
        )
        assertTrue(
            runCatching {
                session.tokenUpdates.afterCommit(refreshRequest, RegularSessionRefreshResponse("new", "rotated", credential.ownerKey))
            }.isFailure
        )
        synchronized(session.monitor) {
            synchronized(store) {
                session.tokenUpdates.afterCommit(refreshRequest, RegularSessionRefreshResponse("new", "rotated", credential.ownerKey))
            }
        }
        assertEquals("rotated", session.biometricCredentials.read()!!.refreshToken)
        assertEquals(0, logoutCalls)
    }

    @Test fun `OAuth login without ID token remembers opted in identity across restart and refresh`() = runBlocking {
        session.biometricCredentials.setEnabled(true)
        val (browser, request) = begin()
        assertNull(request.scope)
        FlowAuthorizationServiceShadow.respond = { exchange, callback ->
            callback.onTokenRequestCompleted(response(exchange, access = jwt(request.nonce)), null)
        }
        browser.completeAuthorization(result(request))
        val credential = session.biometricCredentials.read()
        assertNotNull("OAuth validado também precisa preservar o acesso biométrico autorizado", credential)
        assertEquals("João", credential!!.firstName)
        assertEquals("refresh", credential.refreshToken)
        val regular = store.refreshRequest(store.captureRefreshTicket()!!)!!
        assertEquals(RegularSessionIdentityMode.OAUTH_ACCESS, regular.context.mode)
        assertTrue(regular.context.rememberedBiometric)
        synchronized(session.monitor) {
            synchronized(store) {
                session.tokenUpdates.afterCommit(regular, RegularSessionRefreshResponse("new", "rotated", credential.ownerKey))
            }
        }
        val restored = br.gov.caixa.loterias.core.auth.biometric.EncryptedBiometricCredentialStore(
            context,
            "${configuration.sessionNamespace}|${configuration.expectedIssuer}|${configuration.clientId}"
        )
        assertTrue(restored.enabled)
        assertEquals("rotated", restored.read()!!.refreshToken)
        assertEquals("João", restored.read()!!.firstName)
        assertEquals(0, logoutCalls)
    }

    @Test fun `OAuth login never enables or persists biometric access without opt in`() = runBlocking {
        val (browser, request) = begin()
        FlowAuthorizationServiceShadow.respond = { exchange, callback ->
            callback.onTokenRequestCompleted(response(exchange, access = jwt(request.nonce)), null)
        }
        browser.completeAuthorization(result(request))
        assertNotNull(store.captureIdentity())
        assertFalse(session.biometricCredentials.enabled)
        assertNull(session.biometricCredentials.read())
        assertFalse(store.refreshRequest(store.captureRefreshTicket()!!)!!.context.rememberedBiometric)
    }

    @Test fun `remembered credential storage failure rolls back authenticated access`() = runBlocking {
        session.biometricCredentials.setEnabled(true)
        val (browser, request) = begin()
        FlowAuthorizationServiceShadow.respond =
            { exchange, callback -> callback.onTokenRequestCompleted(response(exchange, id = jwt(request.nonce)), null) }
        TestCredentialKeyGenerator.failGeneration = true
        assertTrue(runCatching { browser.completeAuthorization(result(request)) }.isFailure)
        assertNull(store.token())
        assertNull(store.captureIdentity())
        assertEquals(1, logoutCalls)
        assertNull(session.biometricCredentials.read())
    }

    @Test fun `end session requires local logout and confirms only its launched state`() {
        val ending = AppAuthEndSession(session)
        store.completeAuthorization(store.beginAuthorization(), "access", "id-hint", "a".repeat(64))
        assertTrue(runCatching { ending.prepare() }.isFailure)
        ending.logout()
        assertNull(store.token())
        ending.logout()
        val launch = ending.prepare()
        assertEquals("test.end", ending.intentFor(launch).action)
        val request = FlowAuthorizationServiceShadow.ending!!
        assertEquals("id-hint", request.idTokenHint)
        assertEquals("client", request.additionalParameters["client_id"])
        assertEquals(
            EndSessionOutcome.RETURNED,
            ending.complete(launch, EndSessionResponse.Builder(request).setState(request.state).build().toIntent())
        )
        assertTrue(runCatching { ending.intentFor(launch) }.isFailure)
        val cancelled = ending.prepare()
        ending.intentFor(cancelled)
        assertEquals(EndSessionOutcome.CANCELLED, ending.complete(cancelled, null))
        val error = ending.prepare()
        ending.intentFor(error)
        assertEquals(
            EndSessionOutcome.CANCELLED,
            ending.complete(error, AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW.toIntent())
        )
        val discarded = ending.prepare()
        ending.discard(discarded)
        assertEquals(EndSessionOutcome.UNCONFIRMED, ending.complete(discarded, Intent()))
        ending.cancelAuthorization()
    }

    @Test fun `biometric refresh requires current challenge rotates credential and rejects replay`() = runBlocking {
        shadowOf(context).grantPermissions(Manifest.permission.USE_BIOMETRIC)
        FlowBiometricManagerShadow.status = BiometricManager.BIOMETRIC_SUCCESS
        val biometric = AppAuthBiometricAuthorization(session)
        biometric.setBiometricEnabled(true)
        val (browser, request) = begin()
        FlowAuthorizationServiceShadow.respond =
            { exchange, callback -> callback.onTokenRequestCompleted(response(exchange, id = jwt(request.nonce)), null) }
        browser.completeAuthorization(result(request))
        val challenge = biometric.createBiometricAuthorizationIntent()
        assertNotNull(challenge.getStringExtra(BIOMETRIC_STATE))
        FlowAuthorizationServiceShadow.respond =
            { exchange, callback ->
                callback.onTokenRequestCompleted(
                    response(exchange, access = "biometric-access", refresh = "biometric-refresh", id = jwt(request.nonce)),
                    null
                )
            }
        biometric.completeBiometricAuthorization(challenge)
        assertEquals("biometric-access", store.token())
        assertEquals("biometric-refresh", session.biometricCredentials.read()!!.refreshToken)
        assertTrue(runCatching { biometric.completeBiometricAuthorization(challenge) }.isFailure)
        val revoked = biometric.createBiometricAuthorizationIntent()
        FlowAuthorizationServiceShadow.respond =
            { _, callback -> callback.onTokenRequestCompleted(null, AuthorizationException.TokenRequestErrors.INVALID_GRANT) }
        assertTrue(runCatching { biometric.completeBiometricAuthorization(revoked) }.exceptionOrNull() is AuthorizationException)
        assertNull(session.biometricCredentials.read())
        assertTrue(runCatching { biometric.createBiometricAuthorizationIntent() }.isFailure)
        biometric.setBiometricEnabled(false)
        assertFalse(session.biometricEnabled)
    }

    @Test fun `biometric disabling and invalid exchange cannot authenticate a pending challenge`() = runBlocking {
        shadowOf(context).grantPermissions(Manifest.permission.USE_BIOMETRIC)
        FlowBiometricManagerShadow.status = BiometricManager.BIOMETRIC_SUCCESS
        val biometric = AppAuthBiometricAuthorization(session)
        biometric.setBiometricEnabled(true)
        val (browser, request) = begin()
        FlowAuthorizationServiceShadow.respond =
            { exchange, callback -> callback.onTokenRequestCompleted(response(exchange, id = jwt(request.nonce)), null) }
        browser.completeAuthorization(result(request))
        val invalid = biometric.createBiometricAuthorizationIntent()
        FlowAuthorizationServiceShadow.respond =
            { exchange, callback -> callback.onTokenRequestCompleted(response(exchange, refresh = null), null) }
        assertTrue(runCatching { biometric.completeBiometricAuthorization(invalid) }.isFailure)
        val cancelled = biometric.createBiometricAuthorizationIntent()
        FlowAuthorizationServiceShadow.respond = null
        val pending = async(start = CoroutineStart.UNDISPATCHED) { biometric.completeBiometricAuthorization(cancelled) }
        val callback = FlowAuthorizationServiceShadow.pending!!
        pending.cancel()
        pending.join()
        callback.onTokenRequestCompleted(response(FlowAuthorizationServiceShadow.exchange!!, id = jwt(request.nonce)), null)
        biometric.setBiometricEnabled(false)
        assertTrue(runCatching { biometric.completeBiometricAuthorization(cancelled) }.isFailure)
        assertNull(session.biometricCredentials.read())
    }

    @Test fun `gateway public facade delegates local cancellation logout and one shot SSO launch`() = runBlocking {
        val gateway = AppAuthAuthenticationGateway(
            context,
            configuration,
            store,
            AuthorizationRequestPolicy(AuthorizationDeviceInfoProvider { null }, WeakBiometricAvailability { false })
        )
        assertFalse(gateway.biometricEnabled)
        assertFalse(gateway.biometricCredentialAvailable)
        shadowOf(context).denyPermissions(Manifest.permission.USE_BIOMETRIC)
        assertFalse(gateway.biometricAvailable)
        assertFalse(gateway.biometricHardwareAvailable)
        assertTrue(runCatching { gateway.setBiometricEnabled(true) }.isFailure)
        assertTrue(runCatching { gateway.createBiometricAuthorizationIntent() }.isFailure)
        assertTrue(runCatching { gateway.completeBiometricAuthorization(null) }.isFailure)
        gateway.setBiometricEnabled(false)
        gateway.createAuthorizationIntent()
        val request = FlowAuthorizationServiceShadow.authorization!!
        FlowAuthorizationServiceShadow.respond =
            { exchange, callback -> callback.onTokenRequestCompleted(response(exchange, id = jwt(request.nonce)), null) }
        gateway.completeAuthorization(result(request))
        assertEquals("access", store.token())
        gateway.cancelAuthorization()
        assertTrue(gateway.isAuthorizationCancelled(AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW.toIntent()))
        assertFalse(gateway.isAuthorizationCancelled(null))
        assertFalse(gateway.isAuthorizationCancelled(Intent()))
        assertFalse(gateway.isAuthorizationCancelled(AuthorizationException.TokenRequestErrors.INVALID_GRANT.toIntent()))
        assertEquals(RegularSessionRefreshResult.UPDATED, gateway.withCommitGuard { RegularSessionRefreshResult.UPDATED })
        gateway.logout()
        assertNull(store.token())
        val launch = gateway.prepare()
        gateway.intentFor(launch)
        assertEquals(EndSessionOutcome.CANCELLED, gateway.complete(launch, null))
        val discarded = gateway.prepare()
        gateway.discard(discarded)
        assertEquals(EndSessionOutcome.UNCONFIRMED, gateway.complete(discarded, null))
        val refreshRequest =
            RegularSessionRefreshRequest(
                "refresh",
                "a".repeat(64),
                RegularSessionRefreshContext(configuration, "nonce", RegularSessionIdentityMode.OIDC),
                "id"
            )
        assertTrue(runCatching { gateway.execute(refreshRequest) }.isFailure)
        synchronized(gateway) {
            synchronized(store) { gateway.afterCommit(refreshRequest, RegularSessionRefreshResponse("access", "refresh", "a".repeat(64))) }
        }
    }

    @Test fun `refresh exchange runs on worker validates context and never commits directly`() {
        val request =
            RegularSessionRefreshRequest(
                "refresh",
                "a".repeat(64),
                RegularSessionRefreshContext(configuration, "nonce", RegularSessionIdentityMode.OIDC),
                "prior-id"
            )
        val refresh = AppAuthRefresh(session)
        assertTrue(runCatching { refresh.execute(request) }.isFailure)
        val executor = Executors.newSingleThreadExecutor()
        try {
            FlowAuthorizationServiceShadow.respond =
                { exchange, callback -> callback.onTokenRequestCompleted(response(exchange, access = "new", refresh = "rotated"), null) }
            val value = executor.submit<RegularSessionRefreshResponse> { refresh.execute(request) }.get(5, TimeUnit.SECONDS)
            assertEquals("new", value.accessToken)
            assertEquals("rotated", value.refreshToken)
            assertNull(store.token())
            assertEquals("nonce", FlowAuthorizationServiceShadow.exchange!!.nonce)
            FlowAuthorizationServiceShadow.respond =
                { _, callback -> callback.onTokenRequestCompleted(null, AuthorizationException.TokenRequestErrors.INVALID_GRANT) }
            assertTrue(
                runCatching {
                    executor.submit<RegularSessionRefreshResponse> { refresh.execute(request) }.get(5, TimeUnit.SECONDS)
                }.isFailure
            )
            FlowAuthorizationServiceShadow.respond = { _, callback -> callback.onTokenRequestCompleted(null, null) }
            assertTrue(
                runCatching {
                    executor.submit<RegularSessionRefreshResponse> { refresh.execute(request) }.get(5, TimeUnit.SECONDS)
                }.isFailure
            )
            FlowAuthorizationServiceShadow.respond = null
            assertTrue(
                executor.submit<Boolean> {
                    Thread.currentThread().interrupt()
                    val failure = runCatching { refresh.execute(request) }.exceptionOrNull()
                    val interrupted = Thread.currentThread().isInterrupted
                    Thread.interrupted()
                    failure is IllegalStateException && interrupted
                }.get(5, TimeUnit.SECONDS)
            )
            val invalid = RegularSessionRefreshRequest("", request.ownerKey, request.context, null)
            assertTrue(
                runCatching {
                    executor.submit<RegularSessionRefreshResponse> { refresh.execute(invalid) }.get(5, TimeUnit.SECONDS)
                }.isFailure
            )
        } finally {
            executor.shutdownNow()
        }
    }
}

@Implements(BiometricManager::class)
class FlowBiometricManagerShadow {
    @Implementation fun canAuthenticate() = status

    @Implementation fun canAuthenticate(authenticators: Int): Int {
        require(authenticators > 0)
        return status
    }
    companion object {
        var status = BiometricManager.BIOMETRIC_SUCCESS
    }
}
