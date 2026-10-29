package br.gov.caixa.loterias.feature.cart.domain.model

data class CartTeam(
    val number: Int,
    val name: String?,
    val countryNumber: Int,
    val country: String?,
    val countryCode: String?,
    val state: String?,
    val className: String?,
    val isNationalTeam: Boolean,
    val shortDescription: String?,
    val longDescription: String?,
)
