package br.gov.caixa.loterias.feature.responsiblegaming.domain.model

data class AccountSuspension(
    val active: Boolean,
    val duration: String,
)
