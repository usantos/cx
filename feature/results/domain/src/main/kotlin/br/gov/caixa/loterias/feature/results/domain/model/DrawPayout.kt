package br.gov.caixa.loterias.feature.results.domain.model

data class DrawPayout(
    val description: String,
    val winners: Long,
    val value: String,
)
