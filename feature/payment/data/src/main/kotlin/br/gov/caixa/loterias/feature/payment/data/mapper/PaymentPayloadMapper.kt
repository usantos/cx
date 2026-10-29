package br.gov.caixa.loterias.feature.payment.data.mapper

import br.gov.caixa.loterias.feature.payment.data.localizedMessage as messageText

import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.DamValidation
import br.gov.caixa.loterias.feature.payment.domain.model.PixCharge
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.math.BigDecimal

/** Mapeia catálogos e respostas de pagamento sem emitir requisições de pagamento. */
internal object PaymentPayloadMapper {
    fun methods(payload: JsonElement?): List<PaymentMethod> = payload.paymentObjects().map { value ->
        PaymentMethod(
            id = value.paymentLong("id"),
            purchaseName = value.paymentText("nomePagamentoCompra"),
            prizeName = value.paymentText("nomePagamentoPremio"),
            default = value.paymentBoolean("padrao"),
            publicKey = value.paymentText("publicKey")
        )
    }

    fun cards(payload: JsonElement?, paymentMethodId: Long) = payload.paymentObjects().map { value ->
        SavedPaymentCard(
            id = value.paymentText("idCartao").ifBlank { value.paymentLong("idCartao").toString() },
            paymentMethodId = paymentMethodId,
            firstDigits = value.paymentText("primeirosDigitos"),
            lastDigits = value.paymentText("ultimosDigitos"),
            expirationMonth = value.paymentInt("mesValidade"),
            expirationYear = value.paymentInt("anoValidade"),
            securityCodeLength = value.paymentInt("tamanhoCodigoSeguranca").takeIf { it > 0 } ?: 3,
            brand = value.paymentText("nomeMetodoPagamento"),
            paymentMethodCode = value.paymentText("idMetodoPagamento"),
            customerToken = value.paymentText("tokenCartao")
        )
    }

    fun pixRequest(): JsonObject = JsonObject().apply {
        addProperty("meioPagamento", PaymentMethod.PIX_ID)
        add("parametrosPagamento", null)
        addProperty("salvarCarrinhoFavorito", false)
        addProperty("nomeCarrinhoFavorito", "")
    }

    fun pix(payload: JsonElement?): PixCharge {
        val value = requireNotNull(payload?.takeIf { it.isJsonObject }?.asJsonObject) {
            messageText("cobranca_pix_sem_payload_8e1518")
        }
        return PixCharge(
            copyPaste = value.paymentText("pixCopiaECola"),
            transactionId = value.paymentText("txId"),
            qrCodeBase64 = value.paymentText("qrcodeArrByte"),
            createdAt = value.paymentText("dataHoraCriacao"),
            expiresAt = value.paymentText("dataHoraExpiracao"),
            purchaseId = value.paymentLong("idCompra"),
            amount = value.paymentDecimal("valorCobranca")
        )
    }

    fun dam(payload: JsonElement?): DamValidation {
        val value = payload?.takeIf { it.isJsonObject }?.asJsonObject
        return DamValidation(
            blocked = value?.paymentBoolean("payload") == true,
            message = value?.paymentText("mensagem").orEmpty()
        )
    }
}

internal fun JsonElement?.paymentObjects(): List<JsonObject> = when {
    this == null || isJsonNull -> emptyList()
    isJsonArray -> asJsonArray.mapNotNull { it.takeIf(JsonElement::isJsonObject)?.asJsonObject }
    isJsonObject -> listOf(asJsonObject)
    else -> emptyList()
}

internal fun JsonObject.paymentText(name: String): String = get(name)?.takeIf { !it.isJsonNull && it.isJsonPrimitive }?.asString.orEmpty()
internal fun JsonObject.paymentLong(name: String): Long =
    get(name)?.takeIf { !it.isJsonNull && it.isJsonPrimitive }?.runCatching { asLong }?.getOrDefault(0) ?: 0
internal fun JsonObject.paymentInt(name: String): Int = paymentLong(name).toInt()
internal fun JsonObject.paymentBoolean(name: String): Boolean =
    get(name)?.takeIf { !it.isJsonNull && it.isJsonPrimitive }?.runCatching { asBoolean }?.getOrDefault(false) ?: false
internal fun JsonObject.paymentDecimal(name: String): BigDecimal =
    get(name)?.takeIf { !it.isJsonNull && it.isJsonPrimitive }?.runCatching { asBigDecimal }?.getOrDefault(BigDecimal.ZERO)
        ?: BigDecimal.ZERO
