package br.gov.caixa.loterias.feature.account.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.model.PersonalDataUpdate
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.RecalculateAuthorizedLimitUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.UpdatePersonalDataUseCase
import br.gov.caixa.loterias.feature.account.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.account.presentation.state.PersonalDataAction
import br.gov.caixa.loterias.feature.account.presentation.state.PersonalDataUiState
import br.gov.caixa.loterias.feature.account.presentation.state.reducePersonalDataState
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PersonalDataViewModel(
    private val getProfile: GetAccountProfileUseCase,
    private val resolveZipCode: ResolveZipCodeUseCase,
    private val updatePersonalData: UpdatePersonalDataUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val recalculateAuthorizedLimit: RecalculateAuthorizedLimitUseCase = RecalculateAuthorizedLimitUseCase {
        AppResult.Success(BigDecimal.ZERO)
    },
    private val deferInitialLoad: Boolean = false,
    private val isSessionCurrent: () -> Boolean = { true },
) : ViewModel() {
    private val mutableState = reduxStateStore<PersonalDataUiState, PersonalDataAction>(
        PersonalDataUiState(loading = !deferInitialLoad, awaitingInitialProfile = true),
        ::reducePersonalDataState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state
    private val zipCodeRequestVersion = AtomicLong()
    private var zipCodeJob: Job? = null
    private var profile: AccountProfile? = null
    private var submissionJob: Job? = null
    private var firstResume = true

    init {
        if (!deferInitialLoad) load()
    }

    // O carregamento inicial pertence a init ou à verificação autorizada de entrada. Uma atualização no retorno
    // aguarda que as edições sejam salvas ou revertidas em vez de substituir o rascunho do usuário.
    fun onResume() {
        if (firstResume) {
            firstResume = false
            return
        }
        if (profile != null && !state.value.loading && !state.value.saving && !state.value.saved && !hasUnsavedChanges()) {
            loadProfile(preserveDraft = true)
        }
    }

    fun load() = loadProfile(preserveDraft = false)

    private fun loadProfile(preserveDraft: Boolean) = viewModelScope.launch(dispatchers.io) {
        if (!isSessionCurrent()) return@launch
        mutableState.dispatch(PersonalDataAction.ProfileLoadStarted)
        val result = getProfile()
        if (!isSessionCurrent()) return@launch
        synchronized(mutableState) {
            if (submissionJob?.isCompleted == false || state.value.saved || (preserveDraft && hasUnsavedChanges())) {
                mutableState.dispatch(PersonalDataAction.DraftPreserved(zipCodeJob?.isActive == true))
                return@launch
            }
            when (result) {
                is AppResult.Success -> showProfile(result.value)
                is AppResult.Failure -> mutableState.dispatch(PersonalDataAction.LoadFailed(result.error.userMessage()))
            }
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        val original = profile ?: return false
        val draft = state.value
        return draft.zipCode != original.zipCode.filter(Char::isDigit) ||
            draft.gender != original.gender || draft.acceptsNotifications != original.acceptsNotifications ||
            draft.stateCode != original.stateCode || draft.city != original.city ||
            draft.municipalityId != original.municipalityId ||
            parseBrazilianCurrency(draft.dailyLimit)?.compareTo(original.dailyLimit) != 0
    }

    fun changeZipCode(value: String) {
        val zipCode = value.filter(Char::isDigit).take(8)
        val requestVersion = synchronized(mutableState) {
            val version = zipCodeRequestVersion.incrementAndGet()
            zipCodeJob?.cancel()
            mutableState.dispatch(PersonalDataAction.ZipCodeChanged(zipCode, version))
            version
        }
        if (zipCode.length == 8) zipCodeJob = lookupZipCode(zipCode, requestVersion)
    }

    fun changeGender(value: Gender) = mutableState.dispatch(PersonalDataAction.GenderChanged(value))
    fun changeDailyLimit(value: String) = mutableState.dispatch(PersonalDataAction.DailyLimitChanged(value))
    fun toggleNotifications() = mutableState.dispatch(PersonalDataAction.NotificationsToggled)
    fun dismissError() = mutableState.dispatch(PersonalDataAction.ErrorDismissed)

    fun recalculateLimit() = viewModelScope.launch(dispatchers.io) {
        mutableState.dispatch(PersonalDataAction.LimitRecalculationStarted)
        when (val result = recalculateAuthorizedLimit()) {
            is AppResult.Success -> {
                profile = profile?.copy(authorizedDailyLimit = result.value)
                mutableState.dispatch(PersonalDataAction.LimitRecalculated(result.value))
            }

            is AppResult.Failure -> mutableState.dispatch(PersonalDataAction.LoadFailed(result.error.userMessage()))
        }
    }

    fun submit() {
        val task = synchronized(mutableState) {
            if (submissionJob?.isCompleted == false || state.value.loading || state.value.saving || state.value.saved) return
            val original = profile ?: return
            val current = state.value
            val limit = parseBrazilianCurrency(current.dailyLimit)
            if (limit == null) {
                mutableState.dispatch(PersonalDataAction.LimitRejected(messageText("informe_um_limite_diario_valido_584186")))
                return
            }
            mutableState.dispatch(PersonalDataAction.SubmitStarted)
            viewModelScope.launch(dispatchers.io, start = CoroutineStart.LAZY) {
                val update = PersonalDataUpdate(
                    zipCode = current.zipCode,
                    gender = current.gender,
                    municipalityId = current.municipalityId,
                    dailyLimit = limit,
                    acceptsNotifications = current.acceptsNotifications
                )
                when (val result = updatePersonalData(update, original)) {
                    is AppResult.Success -> mutableState.dispatch(PersonalDataAction.Submitted)
                    is AppResult.Failure -> mutableState.dispatch(PersonalDataAction.SubmitFailed(result.error.userMessage()))
                }
            }.also { submissionJob = it }
        }
        task.start()
    }

    private fun lookupZipCode(zipCode: String, requestVersion: Long) = viewModelScope.launch(dispatchers.io) {
        if (zipCodeRequestVersion.get() != requestVersion || state.value.zipCode != zipCode || !isSessionCurrent()) {
            return@launch
        }
        val result = resolveZipCode(zipCode)
        if (!isSessionCurrent()) return@launch
        when (result) {
            is AppResult.Success -> mutableState.dispatch(PersonalDataAction.ZipCodeResolved(zipCode, requestVersion, result.value))

            is AppResult.Failure -> mutableState.dispatch(
                PersonalDataAction.ZipCodeFailed(
                    zipCode,
                    requestVersion,
                    if (result.error is AppError.Validation) null else result.error.userMessage(),
                    if (result.error is AppError.Validation) result.error.userMessage() else null
                )
            )
        }
    }

    private fun showProfile(value: AccountProfile) {
        profile = value
        mutableState.dispatch(PersonalDataAction.ProfileLoaded(value, formatAccountCurrency(value.dailyLimit)))
    }

    private fun AppError.userMessage(): String = when (this) {
        AppError.Network -> messageText("nao_foi_possivel_conectar_verifi_d9330e")
        is AppError.Validation -> reason.ifBlank { "Confira os dados informados." }
        AppError.Unexpected -> "Ocorreu um erro inesperado. Tente novamente."
    }
}

internal fun parseBrazilianCurrency(value: String): BigDecimal? = runCatching {
    val normalized = value.trim()
        .removePrefix("R$")
        .replace(".", "")
        .replace(',', '.')
        .trim()
    normalized.takeIf(String::isNotBlank)?.toBigDecimal()
}.getOrNull()

internal fun formatAccountCurrency(value: BigDecimal): String = NumberFormat
    .getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    .format(value)
    .replace('\u00A0', ' ')
    .replace('\u202F', ' ')
