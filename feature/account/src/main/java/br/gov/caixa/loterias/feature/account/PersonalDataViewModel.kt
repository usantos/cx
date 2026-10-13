package br.gov.caixa.loterias.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.account.AccountProfile
import br.gov.caixa.loterias.core.domain.account.Gender
import br.gov.caixa.loterias.core.domain.account.GetAccountProfileUseCase
import br.gov.caixa.loterias.core.domain.account.MunicipalityId
import br.gov.caixa.loterias.core.domain.account.PersonalDataUpdate
import br.gov.caixa.loterias.core.domain.account.ResolveZipCodeUseCase
import br.gov.caixa.loterias.core.domain.account.RecalculateAuthorizedLimitUseCase
import br.gov.caixa.loterias.core.domain.account.UpdatePersonalDataUseCase
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PersonalDataUiState(
    val loading: Boolean = true,
    val saving: Boolean = false,
    val saved: Boolean = false,
    val cpf: String = "",
    val name: String = "",
    val email: String = "",
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

class PersonalDataViewModel(
    private val getProfile: GetAccountProfileUseCase,
    private val resolveZipCode: ResolveZipCodeUseCase,
    private val updatePersonalData: UpdatePersonalDataUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val recalculateAuthorizedLimit: RecalculateAuthorizedLimitUseCase = RecalculateAuthorizedLimitUseCase {
        AppResult.Success(BigDecimal.ZERO)
    },
    private val deferInitialLoad: Boolean = false,
) : ViewModel() {
    private val mutableState = MutableStateFlow(PersonalDataUiState(loading = !deferInitialLoad))
    val state = mutableState.asStateFlow()
    private var profile: AccountProfile? = null

    init {
        if (!deferInitialLoad) load()
    }

    fun load() = viewModelScope.launch(dispatchers.io) {
        mutableState.update { it.copy(loading = true, errorMessage = null, saved = false) }
        when (val result = getProfile()) {
            is AppResult.Success -> showProfile(result.value)
            is AppResult.Failure -> mutableState.update {
                it.copy(loading = false, errorMessage = result.error.userMessage())
            }
        }
    }

    fun changeZipCode(value: String) {
        val zipCode = value.filter(Char::isDigit).take(8)
        mutableState.update {
            it.copy(
                zipCode = zipCode,
                stateCode = if (zipCode.length == 8) it.stateCode else "",
                city = if (zipCode.length == 8) it.city else "",
                municipalityId = if (zipCode.length == 8) it.municipalityId else MunicipalityId(0, 0, 0),
                errorMessage = null,
                zipCodeErrorMessage = null,
            )
        }
        if (zipCode.length == 8) lookupZipCode(zipCode)
    }

    fun changeGender(value: Gender) = mutableState.update { it.copy(gender = value, errorMessage = null) }
    fun changeDailyLimit(value: String) = mutableState.update {
        it.copy(dailyLimit = value.take(18), errorMessage = null)
    }
    fun toggleNotifications() = mutableState.update {
        it.copy(acceptsNotifications = !it.acceptsNotifications, errorMessage = null)
    }
    fun dismissError() = mutableState.update { it.copy(errorMessage = null) }

    fun recalculateLimit() = viewModelScope.launch(dispatchers.io) {
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        when (val result = recalculateAuthorizedLimit()) {
            is AppResult.Success -> {
                profile = profile?.copy(authorizedDailyLimit = result.value)
                mutableState.update { it.copy(loading = false, authorizedDailyLimit = result.value) }
            }
            is AppResult.Failure -> mutableState.update {
                it.copy(loading = false, errorMessage = result.error.userMessage())
            }
        }
    }

    fun submit() {
        val original = profile ?: return
        val current = state.value
        val limit = parseBrazilianCurrency(current.dailyLimit)
        if (limit == null) {
            mutableState.update { it.copy(errorMessage = "Informe um limite diário válido.") }
            return
        }
        viewModelScope.launch(dispatchers.io) {
            mutableState.update { it.copy(saving = true, errorMessage = null) }
            val update = PersonalDataUpdate(
                zipCode = current.zipCode,
                gender = current.gender,
                municipalityId = current.municipalityId,
                dailyLimit = limit,
                acceptsNotifications = current.acceptsNotifications,
            )
            when (val result = updatePersonalData(update, original)) {
                is AppResult.Success -> mutableState.update { it.copy(saving = false, saved = true) }
                is AppResult.Failure -> mutableState.update {
                    it.copy(saving = false, errorMessage = result.error.userMessage())
                }
            }
        }
    }

    private fun lookupZipCode(zipCode: String) = viewModelScope.launch(dispatchers.io) {
        mutableState.update { it.copy(loading = true, errorMessage = null, zipCodeErrorMessage = null) }
        when (val result = resolveZipCode(zipCode)) {
            is AppResult.Success -> mutableState.update {
                it.copy(
                    loading = false,
                    municipalityId = result.value.municipalityId,
                    stateCode = result.value.stateCode,
                    city = result.value.city,
                    zipCodeErrorMessage = null,
                )
            }
            is AppResult.Failure -> mutableState.update {
                it.copy(
                    loading = false,
                    municipalityId = MunicipalityId(0, 0, 0),
                    stateCode = "",
                    city = "",
                    errorMessage = if (result.error is AppError.Validation) null else result.error.userMessage(),
                    zipCodeErrorMessage = if (result.error is AppError.Validation) result.error.userMessage() else null,
                )
            }
        }
    }

    private fun showProfile(value: AccountProfile) {
        profile = value
        mutableState.value = PersonalDataUiState(
            loading = false,
            cpf = value.cpf,
            name = value.name,
            email = value.email,
            zipCode = value.zipCode.filter(Char::isDigit),
            gender = value.gender,
            stateCode = value.stateCode,
            city = value.city,
            municipalityId = value.municipalityId,
            dailyLimit = formatAccountCurrency(value.dailyLimit),
            minimumDailyLimit = value.minimumDailyLimit,
            authorizedDailyLimit = value.authorizedDailyLimit,
            acceptsNotifications = value.acceptsNotifications,
        )
    }

    private fun AppError.userMessage(): String = when (this) {
        AppError.Network -> "Não foi possível conectar. Verifique sua internet e tente novamente."
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
