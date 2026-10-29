package br.gov.caixa.loterias.feature.account.domain.model

data class MunicipalityId(
    val stateId: Long,
    val number: Long,
    val checkDigit: Long,
)
