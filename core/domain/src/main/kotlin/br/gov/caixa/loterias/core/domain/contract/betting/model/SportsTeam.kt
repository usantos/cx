package br.gov.caixa.loterias.core.domain.contract.betting.model

data class SportsTeam(
    val number: Int,
    val name: String,
    val state: String = "",
    val shortDescription: String = "",
    val longDescription: String = "",
    val countryNumber: Int = 0,
    val country: String = "",
    val countryCode: String = "",
    val className: String = "",
    val selectionIndicator: Boolean = false,
    val shieldBase64: String? = null,
)
