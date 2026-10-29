package br.gov.caixa.loterias.feature.retail.domain.model

data class LotteryRedemptionCode(
    val qrCode: String,
    val code: String,
    val nsb: String,
    val validityMinutes: Int,
)
