package br.gov.caixa.loterias.feature.payment.data.dto

import com.google.gson.annotations.SerializedName

/** Payloads de erro não precisam ter a estrutura do DTO de processamento. Somente os metadados de negócio são lidos. */
data class PurchaseErrorEnvelopeDto(
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("redirect") val redirect: String? = null,
    @SerializedName("mensagem") val message: String? = null,
    @SerializedName("tipo") val type: String? = null,
)
