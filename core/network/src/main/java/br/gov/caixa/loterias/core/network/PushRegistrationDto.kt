package br.gov.caixa.loterias.core.network

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class PushRegistrationRequestDto(
    @SerializedName("identificadores") val identifiers: List<Long>,
    @SerializedName("token") val token: String,
    @SerializedName("plataforma") val platform: String = "android",
    @SerializedName("canal") val channel: String = "loterias",
) {
    override fun toString() = "PushRegistrationRequestDto(redacted)"
}

data class PushRegistrationPayloadDto(@SerializedName("codigo") val code: String? = null)

data class PushRegistrationEnvelopeDto(
    @SerializedName("payload") val payload: PushRegistrationPayloadDto? = null,
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("redirect") val redirect: JsonElement? = null,
    @SerializedName("errosValidacao") val errors: List<JsonElement>? = null,
) {
    override fun toString() = "PushRegistrationEnvelopeDto(redacted)"
}
