package br.gov.caixa.loterias.feature.payment.data.dto

import com.google.gson.annotations.SerializedName

data class PurchaseEnvelopeDto(
    @SerializedName("payload") val payload: PurchaseProcessingDto? = null,
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("redirect") val redirect: String? = null,
    @SerializedName("mensagem") val message: String? = null,
    @SerializedName("dataHoraServidor") val serverDateTime: String? = null,
)
