package br.gov.caixa.loterias.feature.responsiblegaming.data.dto

import com.google.gson.annotations.SerializedName

data class SuspendAccountRequestDto(
    @SerializedName("cpfApostador") val cpf: String,
    @SerializedName("periodoSuspensao") val periodHours: Int,
)

data class AccountSuspensionDto(
    @SerializedName("apostadorSuspenso") val active: Boolean? = null,
    @SerializedName("prazoHorasDias") val duration: String? = null,
)
