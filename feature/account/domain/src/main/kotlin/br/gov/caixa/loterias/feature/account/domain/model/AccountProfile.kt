package br.gov.caixa.loterias.feature.account.domain.model

import java.math.BigDecimal

data class AccountProfile(
    val cpf: String,
    val name: String,
    val email: String,
    val zipCode: String,
    val gender: Gender,
    val municipalityId: MunicipalityId,
    val stateCode: String,
    val city: String,
    val dailyLimit: BigDecimal,
    val minimumDailyLimit: BigDecimal,
    val authorizedDailyLimit: BigDecimal,
    val acceptsNotifications: Boolean,
    val birthDate: String = "",
    val phone: String = "",
)
