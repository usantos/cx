package br.gov.caixa.loterias.core.domain.contract.betting.model

data class LotteryModality(
    val id: Int,
    val name: String,
    val code: String,
    val specialName: String = "",
    val managerName: String = "",
)
