package br.gov.caixa.loterias.feature.payment.data.dto

import com.google.gson.annotations.SerializedName

data class PurchaseProcessingDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("periodicidade") val intervalMillis: Long? = null,
    @SerializedName("repeticoes") val repetitions: Int? = null,
    @SerializedName("idSituacaoCompra") val status: Int? = null,
)
