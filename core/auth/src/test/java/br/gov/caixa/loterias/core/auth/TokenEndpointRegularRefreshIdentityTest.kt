package br.gov.caixa.loterias.core.auth

import com.google.gson.JsonParser
import java.util.Base64
import org.junit.Assert.*
import org.junit.Test

class TokenEndpointRegularRefreshIdentityTest {
    private val reader = TokenEndpointIdentity { Base64.getUrlDecoder().decode(it) }
    private val issuer = "https://login.example.test/auth/realms/internet"
    private val config = OidcConfiguration("$issuer/protocol/openid-connect/auth", "$issuer/protocol/openid-connect/token",
        "client", "app:/callback", "$issuer/protocol/openid-connect/logout", "app:/callback", "https://api.example.test/v1/")
    private val owner = "549f50c2ff90b343ecb196ff27c21c91124558f7804599277f604a71cf0ccac3"
    private val oauth = RegularSessionRefreshContext(config, "nonce", RegularSessionIdentityMode.OAUTH_ACCESS)
    private val oidc = RegularSessionRefreshContext(config, "nonce", RegularSessionIdentityMode.OIDC)
    private fun claims() = JsonParser.parseString("""{"iss":"$issuer","sub":"subject","aud":"resource","azp":"client","iat":1990,"exp":2500}""").asJsonObject
    private fun encode(value: String) = Base64.getUrlEncoder().withoutPadding().encodeToString(value.toByteArray(Charsets.UTF_8))
    private fun token(value: String = claims().toString()) = "${encode("""{"alg":"RS256"}""")}.${encode(value)}.AQID"
    private fun id() = token(claims().apply { addProperty("aud", "client") }.toString())
    // Previously validated ID may be expired by the time access is refreshed.
    private fun originalId() = token(claims().apply { addProperty("aud", "client"); addProperty("nonce", "nonce"); addProperty("iat", 990); addProperty("exp", 1500) }.toString())
    private fun refreshed(access: String? = token(), id: String? = null, context: RegularSessionRefreshContext = oauth,
        previousId: String? = null, expectedOwner: String = owner) =
        reader.regularRefreshOwnerKey(id, access, context, previousId, expectedOwner, 2000)
    private fun reject(access: String? = token(), id: String? = null, context: RegularSessionRefreshContext = oauth,
        previousId: String? = null, expectedOwner: String = owner) {
        val failure = runCatching { refreshed(access, id, context, previousId, expectedOwner) }.exceptionOrNull()
        assertNotNull("Invalid refreshed identity must fail", failure)
        assertEquals("Identidade da autenticação inválida", failure!!.message)
        assertNull(failure.cause)
    }

    @Test fun `regular OAuth refresh validates new access owner with no ID token`() {
        assertEquals(owner, refreshed())
        assertEquals(owner, refreshed(access = token(claims().apply { addProperty("nonce", "nonce") }.toString())))
    }

    @Test fun `regular OAuth refresh never changes owner client issuer nonce or validity`() {
        for ((field, value) in listOf("sub" to "other", "azp" to "other", "iss" to "https://other.test", "nonce" to "wrong")) {
            reject(access = token(claims().apply { addProperty(field, value) }.toString()))
        }
        reject(access = token(claims().apply { addProperty("exp", 2000) }.toString()))
        reject(access = token(claims().apply { addProperty("iat", 1000) }.toString()))
        reject(expectedOwner = "b".repeat(64))
        reject(access = "opaque")
    }

    @Test fun `OIDC regular refresh may omit ID while preserving prior validated identity`() {
        assertEquals(owner, refreshed(context = oidc, previousId = originalId()))
        assertEquals(owner, refreshed(access = "opaque-endpoint-access", context = oidc, previousId = originalId()))
        reject(context = oidc)
        reject(context = oidc, previousId = "")
        reject(context = oidc, previousId = originalId(), expectedOwner = "")
    }

    @Test fun `new ID in either mode must match existing owner and original nonce when supplied`() {
        for (context in listOf(oauth, oidc)) {
            assertEquals(owner, refreshed(id = id(), context = context, previousId = originalId()))
            assertEquals(owner, refreshed(id = token(claims().apply { addProperty("aud", "client"); addProperty("nonce", "nonce") }.toString()), context = context, previousId = originalId()))
            reject(id = "", context = context, previousId = originalId())
            reject(id = "invalid", context = context, previousId = originalId())
            reject(id = token(claims().apply { addProperty("aud", "client"); addProperty("sub", "other") }.toString()), context = context, previousId = originalId())
            reject(id = token(claims().apply { addProperty("aud", "client"); addProperty("nonce", "wrong") }.toString()), context = context, previousId = originalId())
        }
    }

    @Test fun `OAuth mode still validates access JWT even when new ID is valid`() {
        reject(access = token(claims().apply { addProperty("sub", "other") }.toString()), id = id())
        reject(access = "opaque", id = id())
        assertEquals(owner, refreshed(id = id()))
    }

    @Test fun `regular response requires new access and refresh but preserves optional ID semantics`() {
        val request = RegularSessionRefreshRequest("original-refresh", owner, oidc, originalId())
        val response = reader.regularRefreshResponse(null, "fresh-access", "fresh-refresh", request, 2000)
        assertEquals("fresh-access", response.accessToken)
        assertEquals("fresh-refresh", response.refreshToken)
        assertEquals(owner, response.ownerKey)
        assertNull(response.idToken) // Store preserves previous ID when response omits it.
        assertSame(oidc, request.context)
        for (refresh in listOf(null, "", " ")) {
            val failure = runCatching { reader.regularRefreshResponse(null, "fresh-access", refresh, request, 2000) }.exceptionOrNull()
            assertNotNull("A missing refreshed credential must be rejected", failure)
            assertEquals("Resposta de renovação inválida", failure!!.message)
            assertNull(failure.cause)
        }
    }

    @Test fun `all modes reject missing access and invalid original context`() {
        for (context in listOf(oauth, oidc)) {
            for (access in listOf(null, "", " ")) reject(access = access, id = id(), context = context, previousId = originalId())
            reject(context = RegularSessionRefreshContext(config, "", context.mode), previousId = originalId())
            reject(context = RegularSessionRefreshContext(config.copy(tokenEndpoint = "https://other.test/token"), "nonce", context.mode), previousId = originalId())
        }
    }
}
