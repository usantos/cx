package br.gov.caixa.loterias.feature.betting.domain.model

data class LotteryModality(
    val id: Int,
    val name: String,
    val code: String,
    val specialName: String = "",
    val managerName: String = "",
)
