package br.gov.caixa.loterias.feature.betting.domain.model

data class BetComboEntry(
    val modality: LotteryModality,
    val contestType: String,
    val betQuantity: Int,
    val recurringDraws: Int,
    val numberQuantity: Int,
    val cloverQuantity: Int,
    val contestNumber: Int,
)
