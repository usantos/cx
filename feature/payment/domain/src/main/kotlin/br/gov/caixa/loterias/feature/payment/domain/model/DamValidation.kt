package br.gov.caixa.loterias.feature.payment.domain.model

data class DamValidation(
    val blocked: Boolean,
    val message: String,
)
