package br.gov.caixa.loterias.feature.account.domain.model

data class ZipCodeLocation(
    val municipalityId: MunicipalityId,
    val stateCode: String,
    val city: String,
)
