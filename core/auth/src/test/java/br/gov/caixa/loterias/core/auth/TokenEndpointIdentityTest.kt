package br.gov.caixa.loterias.core.auth

import com.google.gson.JsonParser
import java.util.Base64
import org.junit.Assert.*
import org.junit.Test

class TokenEndpointIdentityTest {
    private val reader = TokenEndpointIdentity { Base64.getUrlDecoder().decode(it) }
    private val issuer = "https://login.example.test/auth/realms/internet"
    private val configuration = OidcConfiguration("$issuer/protocol/openid-connect/auth", "$issuer/protocol/openid-connect/token",
        "client", "app:/callback", "$issuer/protocol/openid-connect/logout", "app:/callback", "https://api.example.test/v1/")
    private fun claims() = JsonParser.parseString("""{"iss":"$issuer","sub":"subject","aud":"client","nonce":"nonce","iat":990,"exp":1500}""").asJsonObject
    private fun encode(value: String) = Base64.getUrlEncoder().withoutPadding().encodeToString(value.toByteArray(Charsets.UTF_8))
    private fun token(claims: String = claims().toString(), header: String = """{"alg":"RS256"}""") = "${encode(header)}.${encode(claims)}.AQID"
    private fun key(token: String = token(), config: OidcConfiguration = configuration, nonce: String = "nonce", now: Long = 1000) = reader.ownerKey(token, config, nonce, now)

    @Test fun `same verified subject has stable hash but another subject client issuer or environment does not`() {
        val original = key()
        assertEquals("549f50c2ff90b343ecb196ff27c21c91124558f7804599277f604a71cf0ccac3", original)
        assertTrue(original.matches(Regex("[0-9a-f]{64}")))
        assertFalse(original.contains("subject"))
        assertEquals(original, key(token(claims().apply { addProperty("exp", 2000); addProperty("name", "ignored") }.toString())))
        assertEquals(original, key(token(claims().apply { add("aud", JsonParser.parseString("[\"client\"]")); addProperty("azp", "client") }.toString())))
        assertNotEquals(original, key(token(claims().apply { addProperty("sub", "another") }.toString())))
        assertNotEquals(original, key(config = configuration.copy(sessionNamespace = "https://another-api.example.test/v1/")))
        assertNotEquals(original, key(token(claims().apply { addProperty("aud", "other-client") }.toString()), configuration.copy(clientId = "other-client")))
        val other = "https://another-login.example.test/realm"
        val config = configuration.copy(authorizationEndpoint = "$other/protocol/openid-connect/auth", tokenEndpoint = "$other/protocol/openid-connect/token", expectedIssuer = other)
        assertNotEquals(original, key(token(claims().apply { addProperty("iss", other) }.toString()), config))
    }

    @Test fun `issuer audience authorized party subject and nonce must match the actual request`() {
        val invalid = listOf("iss" to "https://other.test", "iss" to "$issuer/", "sub" to "", "sub" to " ",
            "sub" to "a".repeat(256), "sub" to "ç", "aud" to "other", "nonce" to "other", "azp" to "other")
        invalid.forEach { (field, value) -> reject(token(claims().apply { addProperty(field, value) }.toString())) }
        // Keep the invalid surrogate as a JSON escape, not a replacement '?' from UTF-8 encoding.
        reject(token(claims().toString().replace("\"subject\"", "\"\\ud800\"")))
        for (field in listOf("iss", "sub", "aud", "nonce", "iat", "exp")) {
            reject(token(claims().apply { remove(field) }.toString()))
            reject(token(claims().apply { add(field, JsonParser.parseString("null")) }.toString()))
            reject(token(claims().apply { add(field, JsonParser.parseString("{}")) }.toString()))
        }
        for (aud in listOf("[]", "[\"client\",\"unknown\"]", "[1]", "true")) reject(token(claims().apply { add("aud", JsonParser.parseString(aud)) }.toString()))
        reject(token(claims().apply { addProperty("aud", "wrong"); addProperty("azp", "client") }.toString()))
        assertTrue(runCatching { key(nonce = "") }.isFailure)
    }

    @Test fun `expiry and issue time enforce integer bounds without clock overflow`() {
        for ((field, value) in listOf("exp" to "1000", "exp" to "0", "iat" to "1601", "iat" to "399", "iat" to "1500",
            "exp" to "1.5", "iat" to "-1", "exp" to "1e999999", "exp" to "9223372036854775808", "iat" to "9223372036854775807")) {
            reject(token(claims().apply { add(field, JsonParser.parseString(value)) }.toString()))
        }
        reject(token(claims().apply { addProperty("exp", "1500") }.toString()))
        assertTrue(runCatching { key(now = -1) }.isFailure)
        assertEquals(key(), key(token(claims().apply { addProperty("iat", 400) }.toString())))
        assertEquals(key(), key(token(claims().apply { addProperty("iat", 1600); addProperty("exp", 2000) }.toString())))
    }

    @Test fun `malformed compact tokens algorithms critical headers duplicate claims and invalid UTF8 are rejected`() {
        for (invalid in listOf("", "x".repeat(65_537), "a", "a.b", "a.b.c.d", "a..b", "a=.b.c", "%%%.$ {x}.a", "a.b.c")) reject(invalid)
        for (header in listOf("[]", "{", "{\"alg\":\"none\"}", "{\"alg\":\"HS256\"}", "{\"alg\":\"ES256\"}",
            "{\"alg\":\"RS256\",\"crit\":[]}", "{\"alg\":\"RS256\",\"b64\":false}", "{\"alg\":\"RS256\",\"alg\":\"RS256\"}")) {
            reject(token(header = header))
        }
        for (body in listOf("[]", "{", claims().toString() + "{}", claims().toString().dropLast(1) + ",\"sub\":\"other\"}", "{unquoted:true}")) reject(token(body))
        reject("${encode("{\"alg\":\"RS256\"}")}.${Base64.getUrlEncoder().withoutPadding().encodeToString(byteArrayOf(0xc3.toByte(), 0x28))}.AQID")
    }

    @Test fun `configuration cannot silently change issuer endpoint or trust an insecure namespace`() {
        for (config in listOf(configuration.copy(expectedIssuer = "http://login.example.test"),
            configuration.copy(expectedIssuer = "https:///invalid"), configuration.copy(expectedIssuer = "$issuer?x=y"),
            configuration.copy(expectedIssuer = "$issuer#fragment"), configuration.copy(expectedIssuer = "https://user@login.example.test/realm"),
            configuration.copy(tokenEndpoint = "https://other.test/token"), configuration.copy(authorizationEndpoint = "https://other.test/auth"),
            configuration.copy(clientId = ""), configuration.copy(sessionNamespace = ""),
            configuration.copy(sessionNamespace = "http://api.test"), configuration.copy(sessionNamespace = "https://user@api.test"),
            configuration.copy(sessionNamespace = "https:///invalid"), configuration.copy(sessionNamespace = "https://api.test?x=1"),
            configuration.copy(sessionNamespace = "https://api.test#fragment"))) {
            assertTrue(runCatching { key(config = config) }.isFailure)
        }
    }

    @Test fun `refresh identity permits omitted nonce but binds same subject and issued nonce`() {
        val original = key()
        val withoutNonce = token(claims().apply { remove("nonce") }.toString())
        assertEquals(original, reader.refreshedOwnerKey(withoutNonce, configuration, "nonce", original, 1000))
        assertEquals(original, reader.refreshedOwnerKey(token(), configuration, "nonce", original, 1000))
        assertTrue(runCatching { reader.refreshedOwnerKey(token(), configuration, "other", original, 1000) }.isFailure)
        assertTrue(runCatching { reader.refreshedOwnerKey(token(claims().apply { addProperty("sub", "other") }.toString()), configuration, "nonce", original, 1000) }.isFailure)
    }

    private fun reject(token: String) {
        val failure = runCatching { key(token) }.exceptionOrNull()
        assertNotNull("Must reject malformed identity", failure)
        assertEquals("Identidade da autenticação inválida", failure!!.message)
        assertNull(failure.cause)
    }
}
