package br.gov.caixa.loterias.feature.account.data.dto

import com.google.gson.annotations.SerializedName

data class AccountVerificationEnvelopeDto(
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("mensagem") val message: String? = null,
    @SerializedName("tipo") val type: Int? = null,
    @SerializedName("redirect") val redirect: String? = null,
    @SerializedName("errosValidacao") val validationErrors: List<String>? = null,
    @SerializedName("payload") val payload: AccountSessionDto? = null,
) {
    override fun toString() = "AccountVerificationEnvelopeDto(redacted)"
}
