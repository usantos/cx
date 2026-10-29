package br.gov.caixa.loterias.feature.account.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.account.domain.model.AccountRegistration
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedUser
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.RegisterAccountUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase
import br.gov.caixa.loterias.feature.account.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.account.presentation.state.RegistrationAction
import br.gov.caixa.loterias.feature.account.presentation.state.RegistrationUiState
import br.gov.caixa.loterias.feature.account.presentation.state.reduceRegistrationState
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RegistrationViewModel(
    identityProvider: CurrentUserIdentityProvider,
    private val resolveZipCode: ResolveZipCodeUseCase,
    private val registerAccount: RegisterAccountUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = reduxStateStore<RegistrationUiState, RegistrationAction>(
        identityProvider.currentUser().toState(),
        ::reduceRegistrationState
    ).also {
        addCloseable(it)
    }
    private var submissionJob: Job? = null
    val state = mutableState.state
    private val zipCodeRequestVersion = AtomicLong()
    private var zipCodeJob: Job? = null

    fun changeZipCode(value: String) {
        val zipCode = value.filter(Char::isDigit).take(8)
        val requestVersion = synchronized(mutableState) {
            val version = zipCodeRequestVersion.incrementAndGet()
            zipCodeJob?.cancel()
            mutableState.dispatch(RegistrationAction.ZipCodeChanged(zipCode, version))
            version
        }
        if (zipCode.length == 8) zipCodeJob = resolve(zipCode, requestVersion)
    }

    fun changeGender(gender: Gender) = mutableState.dispatch(RegistrationAction.GenderChanged(gender))
    fun toggleTerms() = mutableState.dispatch(RegistrationAction.TermsToggled)
    fun toggleNotifications() = mutableState.dispatch(RegistrationAction.NotificationsToggled)

    fun submit() {
        val task = synchronized(mutableState) {
            if (submissionJob?.isCompleted == false || state.value.loading || state.value.registered) return
            val current = state.value
            mutableState.dispatch(RegistrationAction.SubmitStarted)
            viewModelScope.launch(dispatchers.io, start = CoroutineStart.LAZY) {
                val registration = AccountRegistration(
                    zipCode = current.zipCode,
                    gender = current.gender,
                    municipalityId = current.municipalityId,
                    acceptsTerms = current.acceptsTerms,
                    acceptsNotifications = current.acceptsNotifications
                )
                when (val result = registerAccount(registration)) {
                    is AppResult.Success -> mutableState.dispatch(RegistrationAction.Submitted)
                    is AppResult.Failure -> mutableState.dispatch(RegistrationAction.SubmitFailed(result.error.userMessage()))
                }
            }.also { submissionJob = it }
        }
        task.start()
    }

    private fun resolve(zipCode: String, requestVersion: Long) = viewModelScope.launch(dispatchers.io) {
        if (zipCodeRequestVersion.get() != requestVersion || state.value.zipCode != zipCode) return@launch
        when (val result = resolveZipCode(zipCode)) {
            is AppResult.Success -> mutableState.dispatch(RegistrationAction.ZipCodeResolved(zipCode, requestVersion, result.value))

            is AppResult.Failure -> mutableState.dispatch(
                RegistrationAction.ZipCodeFailed(
                    zipCode,
                    requestVersion,
                    if (result.error is AppError.Validation) null else result.error.userMessage(),
                    if (result.error is AppError.Validation) result.error.userMessage() else null
                )
            )
        }
    }

    private fun AppError.userMessage(): String = when (this) {
        AppError.Network -> messageText("nao_foi_possivel_conectar_verifi_d9330e")
        is AppError.Validation -> reason.ifBlank { "Confira os dados informados." }
        AppError.Unexpected -> "Ocorreu um erro inesperado. Tente novamente."
    }

    private fun AuthenticatedUser?.toState() = this?.let {
        RegistrationUiState(cpf = it.cpf, name = it.name, email = it.email)
    } ?: RegistrationUiState(errorMessage = messageText("nao_foi_possivel_recuperar_os_da_23a90e"))
}

internal fun canRegister(state: RegistrationUiState): Boolean =
    !state.loading && state.cpf.isNotBlank() && state.name.isNotBlank() && state.email.isNotBlank() &&
        state.zipCode.length == 8 && state.stateCode.isNotBlank() && state.city.isNotBlank() &&
        state.municipalityId.stateId > 0 && state.acceptsTerms
