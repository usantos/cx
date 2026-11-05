package br.gov.caixa.loterias.core.domain.contract.betting.model

data class LuckyMonth(
    val number: Int,
    val name: String,
    val abbreviation: String = "",
)
