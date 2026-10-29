package br.gov.caixa.loterias.feature.retail.domain.model

data class PixBeneficiary(
    val maskedCpf: String,
    val name: String,
    val key: String,
    val bankName: String,
)
