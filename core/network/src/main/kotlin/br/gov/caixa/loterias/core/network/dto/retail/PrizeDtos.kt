package br.gov.caixa.loterias.core.network.dto.retail

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class PrizeRedemptionEnvelopeDto(
    @SerializedName("payload") val payload: JsonElement? = null,
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("redirect") val redirect: String? = null,
    @SerializedName("mensagem") val message: String? = null,
) {
    override fun toString(): String = "PrizeRedemptionEnvelopeDto(redacted)"
}

data class MercadoPagoRegistrationEnvelopeDto(
    @SerializedName("payload") val payload: String? = null,
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("redirect") val redirect: String? = null,
) {
    override fun toString(): String = "MercadoPagoRegistrationEnvelopeDto(redacted)"
}
