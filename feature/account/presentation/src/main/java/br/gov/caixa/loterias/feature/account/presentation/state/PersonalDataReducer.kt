package br.gov.caixa.loterias.feature.account.presentation.state

import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.model.ZipCodeLocation
import java.math.BigDecimal

private const val DAILY_LIMIT_INPUT_LENGTH = 18

sealed interface PersonalDataAction {
    data class ZipCodeChanged(val zipCode: String, val version: Long) : PersonalDataAction
    data class ZipCodeResolved(val zipCode: String, val version: Long, val location: ZipCodeLocation) : PersonalDataAction
    data class ZipCodeFailed(val zipCode: String, val version: Long, val error: String?, val validationError: String?) : PersonalDataAction
    data class GenderChanged(val gender: Gender) : PersonalDataAction
    data object NotificationsToggled : PersonalDataAction
    data object SubmitStarted : PersonalDataAction
    data object Submitted : PersonalDataAction
    data class SubmitFailed(val message: String) : PersonalDataAction
    data object ProfileLoadStarted : PersonalDataAction
    data class DraftPreserved(val loading: Boolean) : PersonalDataAction
    data class LoadFailed(val message: String) : PersonalDataAction
    data class ProfileLoaded(val profile: AccountProfile, val dailyLimit: String) : PersonalDataAction
    data class DailyLimitChanged(val value: String) : PersonalDataAction
    data object ErrorDismissed : PersonalDataAction
    data object LimitRecalculationStarted : PersonalDataAction
    data class LimitRecalculated(val limit: BigDecimal) : PersonalDataAction
    data class LimitRejected(val message: String) : PersonalDataAction
}

fun reducePersonalDataState(state: PersonalDataUiState, action: PersonalDataAction): PersonalDataUiState = when (action) {
    PersonalDataAction.SubmitStarted -> state.copy(saving = true, errorMessage = null)

    PersonalDataAction.Submitted -> state.copy(saving = false, saved = true)

    is PersonalDataAction.SubmitFailed -> state.copy(saving = false, errorMessage = action.message)

    PersonalDataAction.ProfileLoadStarted -> state.copy(loading = true, errorMessage = null, saved = false)

    is PersonalDataAction.DraftPreserved -> state.copy(loading = action.loading)

    is PersonalDataAction.LoadFailed -> state.copy(loading = false, errorMessage = action.message)

    is PersonalDataAction.ProfileLoaded -> PersonalDataUiState(
        loading = false, cpf = action.profile.cpf, name = action.profile.name,
        email = action.profile.email, zipCode = action.profile.zipCode.filter(Char::isDigit), gender = action.profile.gender,
        stateCode = action.profile.stateCode, city = action.profile.city, municipalityId = action.profile.municipalityId,
        dailyLimit = action.dailyLimit,
        minimumDailyLimit = action.profile.minimumDailyLimit,
        authorizedDailyLimit = action.profile.authorizedDailyLimit,
        acceptsNotifications = action.profile.acceptsNotifications, zipCodeRequestVersion = state.zipCodeRequestVersion
    )

    is PersonalDataAction.DailyLimitChanged -> state.copy(dailyLimit = action.value.take(DAILY_LIMIT_INPUT_LENGTH), errorMessage = null)

    PersonalDataAction.ErrorDismissed -> state.copy(errorMessage = null)

    PersonalDataAction.LimitRecalculationStarted -> state.copy(loading = true, errorMessage = null)

    is PersonalDataAction.LimitRecalculated -> state.copy(loading = false, authorizedDailyLimit = action.limit)

    is PersonalDataAction.LimitRejected -> state.copy(errorMessage = action.message)

    else -> reducePersonalDataDraft(state, action)
}

private fun reducePersonalDataDraft(state: PersonalDataUiState, action: PersonalDataAction): PersonalDataUiState = when (action) {
    is PersonalDataAction.ZipCodeChanged -> state.copy(
        zipCode = action.zipCode,
        zipCodeRequestVersion = action.version,
        loading = action.zipCode.length == 8,
        stateCode = "",
        city = "",
        municipalityId = MunicipalityId(0, 0, 0),
        errorMessage = null,
        zipCodeErrorMessage = null
    )

    is PersonalDataAction.ZipCodeResolved -> if (state.zipCodeRequestVersion == action.version && state.zipCode == action.zipCode) {
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

    is PersonalDataAction.ZipCodeFailed -> if (state.zipCodeRequestVersion == action.version && state.zipCode == action.zipCode) {
        state.copy(
            loading = false,
            errorMessage = action.error,
            zipCodeErrorMessage = action.validationError,
            municipalityId = MunicipalityId(0, 0, 0),
            stateCode = "",
            city = ""
        )
    } else {
        state
    }

    is PersonalDataAction.GenderChanged -> state.copy(gender = action.gender, errorMessage = null)

    PersonalDataAction.NotificationsToggled -> state.copy(acceptsNotifications = !state.acceptsNotifications, errorMessage = null)

    else -> state
}
