package br.gov.caixa.loterias.feature.retail.domain.model

data class Retailer(
    val id: Long,
    val code: String,
    val fantasyName: String,
    val legalName: String,
    val address: String,
    val district: String,
    val city: String,
    val state: String,
    val favorite: Boolean,
)
