package br.gov.caixa.loterias.core.auth.token

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.Strictness
import br.gov.caixa.loterias.core.auth.session.decodeBase64Url

/** Apenas política de exibição da expiração. Este analisador nunca estabelece identidade nem concede acesso ao servidor. */
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
