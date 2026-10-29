package br.gov.caixa.loterias.feature.account.domain.model

data class AccountRegistration(
    val zipCode: String,
    val gender: Gender,
    val municipalityId: MunicipalityId,
    val acceptsTerms: Boolean,
    val acceptsNotifications: Boolean,
)
