package br.gov.caixa.loterias.core.auth

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.Strictness
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.StringReader
import java.net.URI
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.security.MessageDigest

/**
 * Additional checks ONLY for the ID token returned directly by AppAuth's TLS token exchange.
 * This is not a signature verifier and must never authenticate an ID token from an Intent/cache.
 */
internal class TokenEndpointIdentity(private val decodeBase64Url: (String) -> ByteArray) {
    fun ownerKey(token: String, configuration: OidcConfiguration, expectedNonce: String, nowSeconds: Long): String =
        validatedOwnerKey(token, configuration, expectedNonce, nowSeconds, false)

    /** Only for a refresh response received directly from the configured TLS token endpoint. */
    fun refreshedOwnerKey(token: String, configuration: OidcConfiguration, originalNonce: String,
        expectedOwner: String, nowSeconds: Long): String {
        val owner = validatedOwnerKey(token, configuration, originalNonce, nowSeconds, true)
        check(owner == expectedOwner) { "Identidade da autenticação inválida" }
        return owner
    }

    private fun validatedOwnerKey(token: String, configuration: OidcConfiguration, expectedNonce: String,
        nowSeconds: Long, refresh: Boolean): String {
        try {
            check(token.length in 1..65_536 && expectedNonce.isNotBlank())
            validateConfiguration(configuration)
            val sections = token.split('.')
            check(sections.size == 3 && sections.all { it.matches(Regex("[A-Za-z0-9_-]+")) })
            val header = objectFrom(sections[0])
            check(header.string("alg") == "RS256" && !header.has("crit") && !header.has("b64"))
            val claims = objectFrom(sections[1])
            check(claims.string("iss") == configuration.expectedIssuer)
            val subject = claims.string("sub")
            check(subject.isNotBlank() && subject.length <= 255 && subject.all { it.code <= 127 })
            val audience = claims.get("aud")
            val audiences = if (audience.isJsonArray) audience.asJsonArray.map { it.strictString() } else listOf(audience.strictString())
            // No other audiences have been approved for this native client.
            check(audiences.isNotEmpty() && audiences.all { it == configuration.clientId })
            if (claims.has("azp")) check(claims.string("azp") == configuration.clientId)
            if (!refresh || claims.has("nonce")) check(claims.string("nonce") == expectedNonce)
            val expiration = claims.number("exp")
            val issuedAt = claims.number("iat")
            check(nowSeconds >= 0 && expiration > nowSeconds && issuedAt >= 0 && issuedAt < expiration)
            check(issuedAt.toBigDecimal().subtract(nowSeconds.toBigDecimal()).abs() <= 600.toBigDecimal())
            return ownerHash(configuration, subject)
        } catch (_: Exception) {
            // Never propagate parser input, subject or token claims through an error/log.
            throw IllegalStateException("Identidade da autenticação inválida")
        }
    }

    private fun objectFrom(section: String): JsonObject {
        val json = Charsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(decodeBase64Url(section))).toString()
        val gson = GsonBuilder().setStrictness(Strictness.STRICT).create()
        return JsonReader(StringReader(json)).use { reader ->
            reader.strictness = Strictness.STRICT
            val result = JsonObject()
            reader.beginObject()
            while (reader.hasNext()) {
                val name = reader.nextName()
                check(!result.has(name))
                result.add(name, gson.fromJson<JsonElement>(reader, JsonElement::class.java))
            }
            reader.endObject()
            check(reader.peek() == JsonToken.END_DOCUMENT)
            result
        }
    }

    private fun validateConfiguration(configuration: OidcConfiguration) {
        val issuer = URI(configuration.expectedIssuer)
        check(issuer.scheme == "https" && !issuer.host.isNullOrBlank() && issuer.rawUserInfo == null && issuer.rawQuery == null && issuer.rawFragment == null)
        check(configuration.tokenEndpoint == "${configuration.expectedIssuer}/protocol/openid-connect/token")
        check(configuration.authorizationEndpoint == "${configuration.expectedIssuer}/protocol/openid-connect/auth")
        val namespace = URI(configuration.sessionNamespace)
        check(namespace.scheme == "https" && !namespace.host.isNullOrBlank() && namespace.rawUserInfo == null && namespace.rawQuery == null && namespace.rawFragment == null)
        check(configuration.clientId.isNotBlank())
    }

    private fun ownerHash(configuration: OidcConfiguration, subject: String): String {
        val bytes = ByteArrayOutputStream()
        DataOutputStream(bytes).use { output ->
            for (field in listOf("loterias-session-owner-v1", configuration.sessionNamespace,
                configuration.expectedIssuer, configuration.clientId, subject)) {
                val encoded = field.toByteArray(Charsets.UTF_8)
                output.writeInt(encoded.size); output.write(encoded)
            }
        }
        return MessageDigest.getInstance("SHA-256").digest(bytes.toByteArray()).joinToString("") {
            (it.toInt() and 255).toString(16).padStart(2, '0')
        }
    }

    private fun JsonElement.strictString(): String {
        check(isJsonPrimitive && asJsonPrimitive.isString)
        return asString
    }
    private fun JsonObject.string(key: String) = get(key).strictString()
    private fun JsonObject.number(key: String): Long = get(key).let {
        check(it.isJsonPrimitive && it.asJsonPrimitive.isNumber)
        check(it.asString.matches(Regex("[0-9]{1,19}")))
        it.asString.toLong()
    }
}
