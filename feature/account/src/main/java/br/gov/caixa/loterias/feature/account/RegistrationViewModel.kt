package br.gov.caixa.loterias.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.auth.AuthenticatedUser
import br.gov.caixa.loterias.core.auth.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.account.AccountRegistration
import br.gov.caixa.loterias.core.domain.account.Gender
import br.gov.caixa.loterias.core.domain.account.MunicipalityId
import br.gov.caixa.loterias.core.domain.account.RegisterAccountUseCase
import br.gov.caixa.loterias.core.domain.account.ResolveZipCodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistrationUiState(
    val cpf: String = "",
    val name: String = "",
    val email: String = "",
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

class RegistrationViewModel(
    identityProvider: CurrentUserIdentityProvider,
    private val resolveZipCode: ResolveZipCodeUseCase,
    private val registerAccount: RegisterAccountUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = MutableStateFlow(identityProvider.currentUser().toState())
    val state = mutableState.asStateFlow()

    fun changeZipCode(value: String) {
        val zipCode = value.filter(Char::isDigit).take(8)
        mutableState.update {
            it.copy(
                zipCode = zipCode,
                stateCode = "",
                city = "",
                municipalityId = MunicipalityId(0, 0, 0),
                errorMessage = null,
                zipCodeErrorMessage = null,
            )
        }
        if (zipCode.length == 8) resolve(zipCode)
    }

    fun changeGender(gender: Gender) = mutableState.update { it.copy(gender = gender, errorMessage = null) }
    fun toggleTerms() = mutableState.update { it.copy(acceptsTerms = !it.acceptsTerms, errorMessage = null) }
    fun toggleNotifications() = mutableState.update {
        it.copy(acceptsNotifications = !it.acceptsNotifications, errorMessage = null)
    }

    fun submit() {
        val current = state.value
        viewModelScope.launch(dispatchers.io) {
            mutableState.update { it.copy(loading = true, errorMessage = null) }
            val registration = AccountRegistration(
                zipCode = current.zipCode,
                gender = current.gender,
                municipalityId = current.municipalityId,
                acceptsTerms = current.acceptsTerms,
                acceptsNotifications = current.acceptsNotifications,
            )
            when (val result = registerAccount(registration)) {
                is AppResult.Success -> mutableState.update { it.copy(loading = false, registered = true) }
                is AppResult.Failure -> mutableState.update {
                    it.copy(loading = false, errorMessage = result.error.userMessage())
                }
            }
        }
    }

    private fun resolve(zipCode: String) = viewModelScope.launch(dispatchers.io) {
        mutableState.update { it.copy(loading = true, errorMessage = null, zipCodeErrorMessage = null) }
        when (val result = resolveZipCode(zipCode)) {
            is AppResult.Success -> mutableState.update {
                it.copy(
                    loading = false,
                    stateCode = result.value.stateCode,
                    city = result.value.city,
                    municipalityId = result.value.municipalityId,
                    zipCodeErrorMessage = null,
                )
            }
            is AppResult.Failure -> mutableState.update {
                it.copy(
                    loading = false,
                    errorMessage = if (result.error is AppError.Validation) null else result.error.userMessage(),
                    zipCodeErrorMessage = if (result.error is AppError.Validation) result.error.userMessage() else null,
                )
            }
        }
    }

    private fun AppError.userMessage(): String = when (this) {
        AppError.Network -> "Não foi possível conectar. Verifique sua internet e tente novamente."
        is AppError.Validation -> reason.ifBlank { "Confira os dados informados." }
        AppError.Unexpected -> "Ocorreu um erro inesperado. Tente novamente."
    }

    private fun AuthenticatedUser?.toState() = this?.let {
        RegistrationUiState(cpf = it.cpf, name = it.name, email = it.email)
    } ?: RegistrationUiState(errorMessage = "Não foi possível recuperar os dados da Conta CAIXA.")
}

internal fun canRegister(state: RegistrationUiState): Boolean =
    !state.loading && state.cpf.isNotBlank() && state.name.isNotBlank() && state.email.isNotBlank() &&
        state.zipCode.length == 8 && state.stateCode.isNotBlank() && state.city.isNotBlank() &&
        state.municipalityId.stateId > 0 && state.acceptsTerms
