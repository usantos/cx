package br.gov.caixa.loterias.feature.retail.data.dto

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement

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
