package br.gov.caixa.loterias.core.network.logging

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import okhttp3.HttpUrl
import java.util.Locale

/** Redação compartilhada entre o Logcat e o inspetor local exclusivo de debug. */
object NetworkTrafficSanitizer {
    const val REDACTED = "██REDACTED██"
    const val OMITTED_BODY = "[non-JSON, truncated or unreadable body omitted]"

    fun sensitive(name: String): Boolean {
        val normalized = name.lowercase(Locale.ROOT).replace("_", "").replace("-", "")
        return markers.any { it in normalized } || normalized in setOf(
            "nome", "nomecompleto", "nomeapostador", "name", "login", "sub", "code", "nonce", "chave",
        )
    }

    fun url(url: HttpUrl): HttpUrl = url.newBuilder().apply {
        username(""); password(""); fragment(null)
        url.queryParameterNames.filter { sensitive(it) || it.equals("codigo", ignoreCase = true) }
            .forEach { setQueryParameter(it, REDACTED) }
    }.build()

    fun body(text: String): String = runCatching {
        val json = JsonParser.parseString(text)
        if (!json.isJsonObject && !json.isJsonArray) return OMITTED_BODY
        redact(json)
        json.toString()
    }.getOrDefault(OMITTED_BODY)

    private fun redact(value: JsonElement) {
        when {
            value.isJsonObject -> {
                val objectValue = value.asJsonObject
                val redemption = objectValue.keySet().any { it.equals("qrCode", true) || it.equals("nsb", true) }
                objectValue.entrySet().forEach { (name, child) ->
                    if (sensitive(name) || (redemption && name.equals("codigo", true))) {
                        objectValue.add(name, JsonPrimitive(REDACTED))
                    } else redact(child)
                }
            }
            value.isJsonArray -> value.asJsonArray.forEach(::redact)
        }
    }

    private val markers = setOf(
        "authorization", "cookie", "token", "secret", "password", "senha", "cpf", "cnpj",
        "account", "conta", "card", "cartao", "cartão", "cvv", "apikey", "signature",
        "email", "document", "bin", "publickey", "subscriptionkey", "telefone", "phone",
        "endereco", "address", "nascimento", "birth", "credential", "identification",
        "securitycode", "chavepix", "qrcode", "nsb",
    )
}
