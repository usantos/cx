package br.gov.caixa.loterias.feature.account.domain.model

import java.math.BigDecimal

data class PersonalDataUpdate(
    val zipCode: String,
    val gender: Gender,
    val municipalityId: MunicipalityId,
    val dailyLimit: BigDecimal,
    val acceptsNotifications: Boolean,
)
