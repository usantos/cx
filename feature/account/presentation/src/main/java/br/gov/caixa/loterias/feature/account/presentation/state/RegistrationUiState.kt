package br.gov.caixa.loterias.feature.account.presentation.state

import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId

data class RegistrationUiState(
    val cpf: String = "",
    val name: String = "",
    val email: String = "",
    val zipCodeRequestVersion: Long = 0,
    val zipCode: String = "",
    val stateCode: String = "",
    val city: String = "",
    val municipalityId: MunicipalityId = MunicipalityId(0, 0, 0),
    val gender: Gender = Gender.MALE,
    val acceptsTerms: Boolean = false,
    val acceptsNotifications: Boolean = true,
    val loading: Boolean = false,
    val registered: Boolean = false,
    val errorMessage: String? = null,
    val zipCodeErrorMessage: String? = null,
)
