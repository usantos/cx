package br.gov.caixa.loterias.core.auth

import org.junit.Assert.*
import org.junit.Test

class RegularBiometricCredentialRotationTest {
    private val owner = "a".repeat(64)
    private val config = OidcConfiguration("https://s/auth", "https://s/token", "client", "app:/callback",
        "https://s/logout", "app:/callback", "https://session")
    private val context = RegularSessionRefreshContext(config, "original-nonce", RegularSessionIdentityMode.OIDC)
    private val request = RegularSessionRefreshRequest("original-refresh", owner, context, "validated-id")
    private val response = RegularSessionRefreshResponse("fresh-access", "fresh-refresh", owner)
    private val previous = BiometricCredential("original-refresh", owner, "original-nonce")

    @Test fun `remembered enabled OIDC owner rotates refresh and preserves original nonce`() {
        val credential = rotatedBiometricCredential(request, response, true, previous)
        assertNotNull("Enabled remembered owner must follow the committed refresh rotation", credential)
        assertEquals("fresh-refresh", credential!!.refreshToken)
        assertEquals(owner, credential.ownerKey)
        assertEquals("original-nonce", credential.nonce)
        assertEquals("original-refresh", previous.refreshToken)
    }

    @Test fun `disabled biometric and absent credential never create remembered login`() {
        assertNull(rotatedBiometricCredential(request, response, false, previous))
        assertNull(rotatedBiometricCredential(request, response, true, null))
    }

    @Test fun `OAuth access only never creates or rotates biometric credential`() {
        val oauth = RegularSessionRefreshRequest("original-refresh", owner,
            RegularSessionRefreshContext(config, "original-nonce", RegularSessionIdentityMode.OAUTH_ACCESS), null)
        assertNull(rotatedBiometricCredential(oauth, response, true, previous))
    }

    @Test fun `bound biometric OAuth refresh rotates only complete matching credential`() {
        val bound = RegularSessionRefreshRequest("original-refresh", owner,
            RegularSessionRefreshContext(config, "original-nonce", RegularSessionIdentityMode.OAUTH_ACCESS,
                rememberedBiometric = true), null)
        val rotated = rotatedBiometricCredential(bound, response, true, previous)
        assertNotNull(rotated)
        assertEquals("fresh-refresh", rotated!!.refreshToken)
        assertEquals("original-nonce", rotated.nonce)
        assertNull(rotatedBiometricCredential(bound, response, false, previous))
        assertNull(rotatedBiometricCredential(bound, response, true, null))
        assertNull(rotatedBiometricCredential(bound, response, true, BiometricCredential("stale-refresh", owner, "original-nonce")))
        assertNull(rotatedBiometricCredential(bound, response, true, BiometricCredential("original-refresh", owner, "different-nonce")))
    }

    @Test fun `OIDC cannot overwrite remembered credential changed since refresh snapshot`() {
        assertNull(rotatedBiometricCredential(request, response, true,
            BiometricCredential("another-refresh", owner, "original-nonce")))
    }

    @Test fun `foreign owner mismatched original nonce and malformed response cannot rotate credential`() {
        assertNull(rotatedBiometricCredential(request, response, true, BiometricCredential("old", "b".repeat(64), "original-nonce")))
        assertNull(rotatedBiometricCredential(request, response, true, BiometricCredential("old", owner, "different-nonce")))
        assertNull(rotatedBiometricCredential(request, RegularSessionRefreshResponse("access", "refresh", "b".repeat(64)), true, previous))
        assertNull(rotatedBiometricCredential(request, RegularSessionRefreshResponse("access", "", owner), true, previous))
    }
}
