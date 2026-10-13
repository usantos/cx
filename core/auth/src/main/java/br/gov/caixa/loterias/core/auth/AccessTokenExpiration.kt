package br.gov.caixa.loterias.core.auth

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.Strictness

/** Expiry display policy only. This parser never establishes identity or grants backend access. */
internal fun accessTokenExpiration(token: String): Long? = runCatching {
    require(token.length in 1..65_536)
    val payload = token.split('.').getOrNull(1) ?: return null
    val claims = GsonBuilder().setStrictness(Strictness.STRICT).create()
        .fromJson(payload.decodeBase64Url().toString(Charsets.UTF_8), JsonObject::class.java)
    val expiration = claims.get("exp")
    require(expiration.isJsonPrimitive)
    val literal = expiration.asString
    require(literal.matches(Regex("-?[0-9]{1,19}")))
    literal.toLong()
}.getOrNull()
