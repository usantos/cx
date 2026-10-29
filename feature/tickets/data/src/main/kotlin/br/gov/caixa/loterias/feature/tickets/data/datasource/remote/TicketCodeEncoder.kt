package br.gov.caixa.loterias.feature.tickets.data.datasource.remote

import br.gov.caixa.loterias.feature.tickets.domain.model.TicketQuery
import com.google.gson.JsonParser
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher

data class EncodedTicketCode(val value: String, val qrCode: Boolean)

fun interface TicketCodeEncoder {
    fun encode(query: TicketQuery): EncodedTicketCode
}

/** Reproduz o payload RSA enviado pelos fluxos de código de barras e QR Code do aplicativo legado. */
object RsaTicketCodeEncoder : TicketCodeEncoder {
    override fun encode(query: TicketQuery): EncodedTicketCode {
        val prepared = query.prepareLegacyTicketPayload()
        val keyBytes = Base64.getDecoder().decode(BARCODE_PUBLIC_KEY)
        val publicKey = KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(keyBytes))
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encrypted = cipher.doFinal(prepared.value.toByteArray(StandardCharsets.UTF_8))
        return prepared.copy(value = Base64.getEncoder().encodeToString(encrypted))
    }

    private const val BARCODE_PUBLIC_KEY =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmFerQzZx7Z+JPp12mtl7B5cy+pB6L53OAFLd8h1k1gy/my0kqJGugKIZRcklszhPZMyfrNc4fkSh3QPcn4jVVizjzSyGZBc8D97yuDsID0FiNYl5v7so9/6HI95DPdTzrEiGazXm6SuIwW1ylCLKanADzcPb67+pYV3nNuaOQuw7n5e71h97DP+FgJ6xvIuIVQpGYnawmqG3O2MqF00aWXvdJTEEVPogx1azJOjjGYu97DvNj1jpmkOBb0B5I++Z+GYPM+Uu/1z+bcfsUHSkOvLtjkhDmrsbYTnJibyZgb0ohLSfOpMGsvqpqjwzhT11+Sr0Xf55gETvfiiNUnhEqQIDAQAB"
}

internal fun TicketQuery.prepareLegacyTicketPayload(): EncodedTicketCode {
    preparedScan?.let { return EncodedTicketCode(it.value, it.qrCode) }
    val cleanCode = code.filterNot { it == '-' || it.isWhitespace() }
    val cleanRetailer = retailerCode.filterNot { it == '-' || it == '.' || it.isWhitespace() }
    if (cleanRetailer.isNotBlank()) return EncodedTicketCode("$cleanCode==$cleanRetailer", true)
    if (!qrCode) return EncodedTicketCode(cleanCode, false)

    val qr = runCatching {
        JsonParser.parseString(code.replace("APOSTA", "")).asJsonObject
    }.getOrNull() ?: return EncodedTicketCode(cleanCode, false)
    val nsb = qr.text("nsb")
    val ci = qr.text("ci")
    val pro = qr.text("pro")
    if (nsb.isBlank() || ci.isBlank() || pro.isBlank()) return EncodedTicketCode(cleanCode, false)
    val cf = qr.text("cf").ifBlank { ci }
    val modality = modalityCode(pro.toIntOrNull())
    return EncodedTicketCode("$nsb==$ci==$pro==$modality==$cf", true)
}

private fun com.google.gson.JsonObject.text(name: String): String =
    get(name)?.takeUnless { it.isJsonNull }?.let { runCatching { it.asString }.getOrNull() }.orEmpty()

private fun modalityCode(value: Int?): String = when (value) {
    2 -> "MEGA_SENA"
    3 -> "QUINA"
    7 -> "SUPER_7"
    8 -> "LOTOFACIL"
    9 -> "MAIS_MILIONARIA"
    11 -> "DIA_DE_SORTE"
    14 -> "LOTOGOL"
    16 -> "LOTOMANIA"
    18 -> "DUPLA_SENA"
    19 -> "LOTECA"
    20 -> "TIMEMANIA"
    1502 -> "INSTANTANEA"
    else -> "null"
}
