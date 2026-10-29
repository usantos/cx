package br.gov.caixa.loterias.feature.notifications.data.dto

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class NotificationInboxEnvelopeDto(
    @SerializedName("payload") val payload: JsonElement? = null,
    @SerializedName("codigo") val code: JsonElement? = null,
    @SerializedName("redirect") val redirect: JsonElement? = null,
    @SerializedName("errosValidacao") val errors: JsonElement? = null,
) {
    override fun toString() = "NotificationInboxEnvelopeDto(redacted)"
}
