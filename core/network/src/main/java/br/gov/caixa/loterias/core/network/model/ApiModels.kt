package br.gov.caixa.loterias.core.network.model

import com.google.gson.annotations.SerializedName

data class ApiEnvelope<T>(
    @SerializedName("payload") val payload: T? = null,
    @SerializedName("redirect") val redirect: String? = null,
    @SerializedName("mensagem") val message: String? = null,
    @SerializedName("dataHoraServidor") val serverDateTime: String? = null,
)
