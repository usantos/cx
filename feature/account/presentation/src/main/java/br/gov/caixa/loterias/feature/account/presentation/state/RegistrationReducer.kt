package br.gov.caixa.loterias.feature.account.presentation.state

import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.model.ZipCodeLocation

sealed interface RegistrationAction {
    data class ZipCodeChanged(val zipCode: String, val version: Long) : RegistrationAction
    data class ZipCodeResolved(val zipCode: String, val version: Long, val location: ZipCodeLocation) : RegistrationAction
    data class ZipCodeFailed(val zipCode: String, val version: Long, val error: String?, val validationError: String?) : RegistrationAction
    data class GenderChanged(val gender: Gender) : RegistrationAction
    data object NotificationsToggled : RegistrationAction
    data object SubmitStarted : RegistrationAction
    data object Submitted : RegistrationAction
    data class SubmitFailed(val message: String) : RegistrationAction
    data object TermsToggled : RegistrationAction
}

fun reduceRegistrationState(state: RegistrationUiState, action: RegistrationAction): RegistrationUiState = when (action) {
    is RegistrationAction.ZipCodeChanged -> state.copy(
        zipCode = action.zipCode,
        zipCodeRequestVersion = action.version,
        loading = action.zipCode.length == 8,
        stateCode = "",
        city = "",
        municipalityId = MunicipalityId(0, 0, 0),
        errorMessage = null,
        zipCodeErrorMessage = null
    )

    is RegistrationAction.ZipCodeResolved -> if (state.zipCodeRequestVersion == action.version && state.zipCode == action.zipCode) {
        state.copy(
            loading = false,
            stateCode = action.location.stateCode,
            city = action.location.city,
            municipalityId = action.location.municipalityId,
            zipCodeErrorMessage = null
        )
    } else {
        state
    }

    is RegistrationAction.ZipCodeFailed -> if (state.zipCodeRequestVersion == action.version && state.zipCode == action.zipCode) {
        state.copy(loading = false, errorMessage = action.error, zipCodeErrorMessage = action.validationError)
    } else {
        state
    }

    is RegistrationAction.GenderChanged -> state.copy(gender = action.gender, errorMessage = null)

    RegistrationAction.NotificationsToggled -> state.copy(acceptsNotifications = !state.acceptsNotifications, errorMessage = null)

    RegistrationAction.TermsToggled -> state.copy(acceptsTerms = !state.acceptsTerms, errorMessage = null)

    RegistrationAction.SubmitStarted -> state.copy(loading = true, errorMessage = null)

    RegistrationAction.Submitted -> state.copy(loading = false, registered = true)

    is RegistrationAction.SubmitFailed -> state.copy(loading = false, errorMessage = action.message)
}
