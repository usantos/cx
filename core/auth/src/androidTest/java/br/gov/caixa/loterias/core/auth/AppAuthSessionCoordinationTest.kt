package br.gov.caixa.loterias.core.auth

import androidx.test.platform.app.InstrumentationRegistry
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.auth.gateway.OidcConfiguration
import br.gov.caixa.loterias.core.auth.authorization.AuthorizationDeviceInfoProvider
import br.gov.caixa.loterias.core.auth.authorization.WeakBiometricAvailability
import br.gov.caixa.loterias.core.auth.authorization.AuthorizationRequestPolicy
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionIdentityMode
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshContext
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshRequest
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshResponse
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshResult
import br.gov.caixa.loterias.core.auth.gateway.AppAuthAuthenticationGateway

class AppAuthSessionCoordinationTest {
    @Test fun logoutWaitsForRefreshCommitBeforeClearingTheSameSession() {
        val tokens = SessionTokenStore()
        tokens.completeAuthorization(tokens.beginAuthorization(), "synthetic-access", "synthetic-id", "a".repeat(64))
        val gateway = gateway(tokens)
        val entered = CountDownLatch(1)
        val release = CountDownLatch(1)
        val logoutStarted = CountDownLatch(1)
        val logoutFinished = CountDownLatch(1)
        val workers = Executors.newFixedThreadPool(2)
        try {
            val commit = workers.submit<RegularSessionRefreshResult> {
                gateway.withCommitGuard {
                    entered.countDown()
                    check(release.await(5, TimeUnit.SECONDS))
                    assertEquals("synthetic-access", tokens.token())
                    RegularSessionRefreshResult.UPDATED
                }
            }
            assertTrue(entered.await(5, TimeUnit.SECONDS))
            val logout = workers.submit {
                logoutStarted.countDown()
                gateway.logout()
                logoutFinished.countDown()
            }
            assertTrue(logoutStarted.await(5, TimeUnit.SECONDS))
            assertFalse(logoutFinished.await(200, TimeUnit.MILLISECONDS))
            assertEquals("synthetic-access", tokens.token())
            release.countDown()
            assertEquals(RegularSessionRefreshResult.UPDATED, commit.get(5, TimeUnit.SECONDS))
            logout.get(5, TimeUnit.SECONDS)
            assertNull(tokens.token())
        } finally {
            release.countDown()
            workers.shutdownNow()
        }
    }

    @Test fun refreshPersistenceRequiresBothSessionGuards() {
        val tokens = SessionTokenStore()
        val gateway = gateway(tokens)
        val request = RegularSessionRefreshRequest("refresh", "a".repeat(64),
            RegularSessionRefreshContext(configuration(), "nonce", RegularSessionIdentityMode.OIDC), null)
        val response = RegularSessionRefreshResponse("access", "rotated", request.ownerKey)
        assertTrue(runCatching { gateway.afterCommit(request, response) }.exceptionOrNull() is IllegalStateException)
        assertTrue(gateway.withCommitGuard {
            assertTrue(runCatching { gateway.afterCommit(request, response) }.exceptionOrNull() is IllegalStateException)
            synchronized(tokens) { gateway.afterCommit(request, response) }
            RegularSessionRefreshResult.UPDATED
        } == RegularSessionRefreshResult.UPDATED)
        assertNull(tokens.token())
    }

    private fun gateway(tokens: SessionTokenStore) = AppAuthAuthenticationGateway(
        InstrumentationRegistry.getInstrumentation().targetContext, configuration(), tokens,
        AuthorizationRequestPolicy(AuthorizationDeviceInfoProvider { null }, WeakBiometricAvailability { false }),
    )

    private fun configuration() = OidcConfiguration(
        authorizationEndpoint = "https://example.invalid/auth", tokenEndpoint = "https://example.invalid/token",
        clientId = "test", redirectUri = "test:/auth", endSessionEndpoint = "https://example.invalid/logout",
        postLogoutRedirectUri = "test:/logout", sessionNamespace = "coordination-${UUID.randomUUID()}",
    )
}
