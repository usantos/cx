package br.gov.caixa.loterias.core.auth

import java.util.Base64
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.auth.token.TokenEndpointIdentity
import br.gov.caixa.loterias.core.auth.gateway.OidcConfiguration
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.auth.biometric.BiometricCredential
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionIdentityMode
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshRequest
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshResponse
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshExecutor
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshResult
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshCoordinator
import br.gov.caixa.loterias.core.auth.gateway.biometricRefreshContext
import br.gov.caixa.loterias.core.auth.gateway.rotatedBiometricCredential

class IdlessBiometricRefreshRotationTest {
    private val issuer = "https://login.example.test/auth/realms/internet"
    private val configuration = OidcConfiguration("$issuer/protocol/openid-connect/auth", "$issuer/protocol/openid-connect/token",
        "client", "app:/callback", "$issuer/protocol/openid-connect/logout", "app:/callback", "https://api.example.test/v1/")
    private val owner = "549f50c2ff90b343ecb196ff27c21c91124558f7804599277f604a71cf0ccac3"
    private val policy = TokenEndpointIdentity { Base64.getUrlDecoder().decode(it) }
    private fun encode(value: String) = Base64.getUrlEncoder().withoutPadding().encodeToString(value.toByteArray())
    private fun access() = "${encode("""{"alg":"RS256"}""")}.${encode("""{"iss":"$issuer","sub":"subject","azp":"client","iat":1990,"exp":2500}""")}.AQID"

    @Test fun `accepted ID-less biometric session keeps remembered credential current across regular refresh`() {
        val verifiedOwner = policy.biometricRefreshOwnerKey(null, access(), configuration, "nonce", owner, 2000)
        val context = biometricRefreshContext(configuration, "nonce", null)
        assertEquals(RegularSessionIdentityMode.OAUTH_ACCESS, context.mode)
        val store = SessionTokenStore()
        assertTrue(store.completeAuthorization(store.beginAuthorization(), access(), null, verifiedOwner, "biometric-refresh", context))
        var remembered = BiometricCredential("biometric-refresh", owner, "nonce")
        val identity = store.captureIdentity()
        var serial = 0
        val executor = object : RegularSessionRefreshExecutor {
            override fun execute(request: RegularSessionRefreshRequest): RegularSessionRefreshResponse {
                assertEquals(remembered.refreshToken, request.refreshToken)
                return policy.regularRefreshResponse(null, access(), "regular-refresh-${++serial}", request, 2000)
            }
            override fun afterCommit(request: RegularSessionRefreshRequest, response: RegularSessionRefreshResponse) {
                assertTrue(Thread.holdsLock(store))
                remembered = rotatedBiometricCredential(request, response, true, remembered) ?: remembered
            }
        }
        val coordinator = RegularSessionRefreshCoordinator(store, executor) { 2000 }
        repeat(2) {
            assertEquals(RegularSessionRefreshResult.UPDATED, coordinator.refreshAfterUnauthorized(store.captureRefreshTicket()!!))
            assertEquals("regular-refresh-${it + 1}", remembered.refreshToken)
            assertEquals("nonce", remembered.nonce)
            assertEquals(owner, remembered.ownerKey)
            assertSame(identity, store.captureIdentity())
            assertEquals(remembered.refreshToken, store.refreshRequest(store.captureRefreshTicket()!!)!!.refreshToken)
        }
    }

    @Test fun `replacement session blocks regular refresh from rotating remembered biometric credential`() {
        val store = SessionTokenStore()
        store.completeAuthorization(store.beginAuthorization(), access(), null, owner, "biometric-refresh",
            biometricRefreshContext(configuration, "nonce", null))
        val remembered = BiometricCredential("biometric-refresh", owner, "nonce")
        var persisted = false
        val coordinator = RegularSessionRefreshCoordinator(store, object : RegularSessionRefreshExecutor {
            override fun execute(request: RegularSessionRefreshRequest): RegularSessionRefreshResponse {
                val response = policy.regularRefreshResponse(null, access(), "late-refresh", request, 2000)
                store.completeAuthorization(store.beginAuthorization(), "replacement-access", null, "b".repeat(64))
                return response
            }
            override fun afterCommit(request: RegularSessionRefreshRequest, response: RegularSessionRefreshResponse) {
                persisted = true
                rotatedBiometricCredential(request, response, true, remembered)
            }
        }) { 2000 }
        assertEquals(RegularSessionRefreshResult.STALE, coordinator.refreshAfterUnauthorized(store.captureRefreshTicket()!!))
        assertFalse(persisted)
        assertEquals("biometric-refresh", remembered.refreshToken)
        assertEquals("replacement-access", store.token())
    }
}
