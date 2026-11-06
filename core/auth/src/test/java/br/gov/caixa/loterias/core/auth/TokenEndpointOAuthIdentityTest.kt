package br.gov.caixa.loterias.core.auth

import com.google.gson.JsonParser
import java.util.Base64
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.auth.token.TokenEndpointIdentity
import br.gov.caixa.loterias.core.auth.gateway.OidcConfiguration

/** Todos os tokens compactos aqui são sintéticos; não são usadas credenciais nem declarações de produção. */
class TokenEndpointOAuthIdentityTest {
    private val reader = TokenEndpointIdentity { Base64.getUrlDecoder().decode(it) }
    private val issuer = "https://login.example.test/auth/realms/internet"
    private val config = OidcConfiguration("$issuer/protocol/openid-connect/auth", "$issuer/protocol/openid-connect/token",
        "client", "app:/callback", "$issuer/protocol/openid-connect/logout", "app:/callback", "https://api.example.test/v1/")
    private fun accessClaims() = JsonParser.parseString("""{"iss":"$issuer","sub":"subject","aud":"api-resource","azp":"client","iat":990,"exp":1500}""").asJsonObject
    private fun encode(value: String) = Base64.getUrlEncoder().withoutPadding().encodeToString(value.toByteArray(Charsets.UTF_8))
    private fun jwt(body: String = accessClaims().toString(), header: String = """{"alg":"RS256"}""") = "${encode(header)}.${encode(body)}.AQID"
    private fun owner(access: String? = jwt(), id: String? = null, scope: String? = null,
        configuration: OidcConfiguration = config, nonce: String = "nonce", now: Long = 1000,
    ) = reader.authorizationOwnerKey(id, access, scope, configuration, nonce, now)
    private fun id() = jwt(
        accessClaims().apply {
            addProperty("aud", "client")
            addProperty("nonce", "nonce")
        }.toString()
    )
    private fun reject(
        access: String? = jwt(),
        id: String? = null,
        scope: String? = null,
        configuration: OidcConfiguration = config,
        nonce: String = "nonce",
        now: Long = 1000,
    ) {
        val failure = runCatching { owner(access, id, scope, configuration, nonce, now) }.exceptionOrNull()
        assertNotNull("Invalid endpoint identity must be rejected", failure)
        assertEquals("Identidade da autenticação inválida", failure!!.message)
        assertNull(failure.cause)
    }

    @Test fun `fresh OAuth access JWT derives same owner as OIDC ID despite resource audience and absent nonce`() {
        assertEquals("549f50c2ff90b343ecb196ff27c21c91124558f7804599277f604a71cf0ccac3", owner())
        assertEquals(reader.ownerKey(id(), config, "nonce", 1000), owner())
        assertEquals(owner(), owner(scope = "profile offline_access"))
        assertEquals(
            owner(),
            owner(
                access = jwt(
                    accessClaims().apply {
                        add("aud", JsonParser.parseString("[\"api-resource\",\"account\"]"))
                    }.toString()
                )
            )
        )
        assertEquals(owner(), owner(access = jwt(accessClaims().apply { remove("aud") }.toString())))
    }

    @Test fun `openid request never accepts an access-only response`() {
        for (scope in listOf("openid", "profile openid offline_access", " openid ")) reject(scope = scope)
        assertEquals(owner(), owner(scope = "openid-other"))
        assertEquals(owner(), owner(id = id(), scope = "openid profile"))
    }

    @Test fun `ID present remains authoritative and invalid ID never falls back to access`() {
        reject(id = "")
        reject(id = "invalid")
        reject(
            id = jwt(
                accessClaims().apply {
                    addProperty("aud", "client")
                    addProperty("nonce", "wrong")
                }.toString()
            )
        )
        reject(
            id = jwt(
                accessClaims().apply {
                    addProperty("aud", "other")
                    addProperty("nonce", "nonce")
                }.toString()
            )
        )
        assertEquals(owner(), owner(id = id()))
    }

    @Test fun `access issuer authorized party subject and optional nonce bind actual request`() {
        for ((field, value) in listOf(
            "iss" to "https://wrong.example.test",
            "azp" to "wrong-client",
            "sub" to "",
            "sub" to " ",
            "sub" to "ç",
            "sub" to "a".repeat(256),
            "nonce" to "wrong"
        )) {
            reject(access = jwt(accessClaims().apply { addProperty(field, value) }.toString()))
        }
        for (field in listOf("iss", "azp", "sub", "iat", "exp")) {
            reject(access = jwt(accessClaims().apply { remove(field) }.toString()))
            reject(access = jwt(accessClaims().apply { add(field, JsonParser.parseString("null")) }.toString()))
        }
        reject(
            access = jwt(
                accessClaims().apply {
                    remove("azp")
                    addProperty("aud", "client")
                }.toString()
            )
        )
        reject(access = jwt(accessClaims().apply { addProperty("nonce", 1) }.toString()))
        reject(nonce = "")
        assertEquals(owner(), owner(access = jwt(accessClaims().apply { addProperty("nonce", "nonce") }.toString())))
    }

    @Test fun `access expiry issue time and not before are validated`() {
        for ((field, value) in listOf(
            "exp" to "1000",
            "iat" to "399",
            "iat" to "1601",
            "iat" to "1500",
            "iat" to "-1",
            "exp" to "1.5",
            "exp" to "9223372036854775808",
            "nbf" to "1001"
        )) {
            reject(access = jwt(accessClaims().apply { add(field, JsonParser.parseString(value)) }.toString()))
        }
        reject(now = -1)
        assertEquals(owner(), owner(access = jwt(accessClaims().apply { addProperty("nbf", 1000) }.toString())))
    }

    @Test fun `missing opaque malformed or improperly signed access response is rejected`() {
        for (access in listOf(
            null, "", " ", "opaque", "a.b.c", jwt(header = """{"alg":"none"}"""),
            jwt(header = """{"alg":"HS256"}"""), jwt(header = """{"alg":"RS256","crit":[]}"""),
            jwt(accessClaims().toString().dropLast(1) + ",\"sub\":\"other\"}")
        )) {
            reject(access = access)
        }
        reject(access = null, id = id())
        reject(access = "", id = id())
        for (aud in listOf("null", "[]", "[1]", "{}", "true", "\"\"")) {
            reject(access = jwt(accessClaims().apply { add("aud", JsonParser.parseString(aud)) }.toString()))
        }
    }

    @Test fun `OAuth owner remains isolated by subject issuer client and namespace`() {
        assertNotEquals(owner(), owner(access = jwt(accessClaims().apply { addProperty("sub", "other") }.toString())))
        assertNotEquals(owner(), owner(configuration = config.copy(sessionNamespace = "https://other-api.example.test/v1/")))
        assertNotEquals(
            owner(),
            owner(
                access = jwt(
                    accessClaims().apply {
                        addProperty("azp", "other")
                    }.toString()
                ),
                configuration = config.copy(clientId = "other")
            )
        )
        val otherIssuer = "https://other-login.example.test/realm"
        val otherConfig = config.copy(
            authorizationEndpoint = "$otherIssuer/protocol/openid-connect/auth",
            tokenEndpoint = "$otherIssuer/protocol/openid-connect/token",
            expectedIssuer = otherIssuer
        )
        assertNotEquals(
            owner(),
            owner(
                access = jwt(
                    accessClaims().apply {
                        addProperty("iss", otherIssuer)
                    }.toString()
                ),
                configuration = otherConfig
            )
        )
        reject(configuration = config.copy(tokenEndpoint = "https://other.example.test/token"))
        reject(configuration = config.copy(expectedIssuer = "http://login.example.test"))
    }

    @Test fun `refresh remains ID-only and rejects OAuth access identity`() {
        val failure = runCatching {
            reader.refreshedOwnerKey(jwt(), config, "nonce", "549f50c2ff90b343ecb196ff27c21c91124558f7804599277f604a71cf0ccac3", 1000)
        }.exceptionOrNull()
        assertNotNull(failure)
    }
}
