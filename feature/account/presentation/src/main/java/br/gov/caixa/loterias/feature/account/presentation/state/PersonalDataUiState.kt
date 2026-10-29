package br.gov.caixa.loterias.feature.account.presentation.state

import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import java.math.BigDecimal

data class PersonalDataUiState(
    val loading: Boolean = true,
    val awaitingInitialProfile: Boolean = false,
    val saving: Boolean = false,
    val saved: Boolean = false,
    val cpf: String = "",
    val name: String = "",
    val email: String = "",
    val zipCodeRequestVersion: Long = 0,
    val zipCode: String = "",
    val gender: Gender = Gender.MALE,
    val stateCode: String = "",
    val city: String = "",
    val municipalityId: MunicipalityId = MunicipalityId(0, 0, 0),
    val dailyLimit: String = "",
    val minimumDailyLimit: BigDecimal = BigDecimal.ZERO,
    val authorizedDailyLimit: BigDecimal = BigDecimal.ZERO,
    val acceptsNotifications: Boolean = false,
    val errorMessage: String? = null,
    val zipCodeErrorMessage: String? = null,
)
