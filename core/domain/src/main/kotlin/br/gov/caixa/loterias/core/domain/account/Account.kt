package br.gov.caixa.loterias.core.domain.account

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf
import java.math.BigDecimal

enum class Gender { MALE, FEMALE }

data class MunicipalityId(
    val stateId: Long,
    val number: Long,
    val checkDigit: Long,
)

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

data class PersonalDataUpdate(
    val zipCode: String,
    val gender: Gender,
    val municipalityId: MunicipalityId,
    val dailyLimit: BigDecimal,
    val acceptsNotifications: Boolean,
)

data class ZipCodeLocation(
    val municipalityId: MunicipalityId,
    val stateCode: String,
    val city: String,
)

data class AccountRegistration(
    val zipCode: String,
    val gender: Gender,
    val municipalityId: MunicipalityId,
    val acceptsTerms: Boolean,
    val acceptsNotifications: Boolean,
)

enum class PostLoginDestination { HOME, REGISTRATION, TERMS, SELF_ASSESSMENT }

enum class AccountStatus { ACTIVE, BLOCKED, TOTALLY_BLOCKED, PARTIALLY_BLOCKED, UNKNOWN }

data class AccountAccess(
    val destination: PostLoginDestination,
    val temporarySuspension: Boolean,
    val dailyLimit: BigDecimal,
    val status: AccountStatus? = null,
)

interface AccountRepository {
    suspend fun profile(): AccountProfile
    suspend fun locationForZipCode(zipCode: String): ZipCodeLocation
    suspend fun updatePersonalData(update: PersonalDataUpdate)
    suspend fun register(registration: AccountRegistration)
    suspend fun verifyAccess(): AccountAccess
    suspend fun recalculateAuthorizedLimit(): BigDecimal
}

fun interface RecalculateAuthorizedLimitUseCase {
    suspend operator fun invoke(): AppResult<BigDecimal>
}

class RecalculateAuthorizedLimit(private val repository: AccountRepository) : RecalculateAuthorizedLimitUseCase {
    override suspend fun invoke() = appResultOf {
        repository.recalculateAuthorizedLimit().also { require(it >= BigDecimal.ZERO) { "Limite inválido" } }
    }
}

fun interface VerifyAccountAccessUseCase {
    suspend operator fun invoke(): AppResult<AccountAccess>
}

class VerifyAccountAccess(private val repository: AccountRepository) : VerifyAccountAccessUseCase {
    override suspend fun invoke() = appResultOf { repository.verifyAccess() }
}

fun interface RegisterAccountUseCase {
    suspend operator fun invoke(registration: AccountRegistration): AppResult<Unit>
}

class RegisterAccount(private val repository: AccountRepository) : RegisterAccountUseCase {
    override suspend fun invoke(registration: AccountRegistration) = appResultOf {
        require(registration.acceptsTerms) { "É necessário aceitar os Termos de Uso e a Política de Privacidade" }
        require(registration.zipCode.filter(Char::isDigit).length == 8) { "CEP inválido" }
        require(registration.municipalityId.stateId > 0 && registration.municipalityId.number > 0) {
            "Município inválido"
        }
        repository.register(registration)
    }
}

fun interface GetAccountProfileUseCase {
    suspend operator fun invoke(): AppResult<AccountProfile>
}

class GetAccountProfile(private val repository: AccountRepository) : GetAccountProfileUseCase {
    override suspend fun invoke() = appResultOf { repository.profile() }
}

fun interface ResolveZipCodeUseCase {
    suspend operator fun invoke(zipCode: String): AppResult<ZipCodeLocation>
}

class ResolveZipCode(private val repository: AccountRepository) : ResolveZipCodeUseCase {
    override suspend fun invoke(zipCode: String) = appResultOf {
        val normalized = zipCode.filter(Char::isDigit)
        require(normalized.length == 8) { "CEP inválido" }
        repository.locationForZipCode(normalized)
    }
}

fun interface UpdatePersonalDataUseCase {
    suspend operator fun invoke(update: PersonalDataUpdate, profile: AccountProfile): AppResult<Unit>
}

class UpdatePersonalData(private val repository: AccountRepository) : UpdatePersonalDataUseCase {
    override suspend fun invoke(update: PersonalDataUpdate, profile: AccountProfile) = appResultOf {
        require(update.zipCode.filter(Char::isDigit).length == 8) { "CEP inválido" }
        require(update.municipalityId.stateId > 0 && update.municipalityId.number > 0) {
            "Município inválido"
        }
        require(update.dailyLimit >= profile.minimumDailyLimit) { "Limite abaixo do mínimo permitido" }
        require(update.dailyLimit <= profile.authorizedDailyLimit) { "Limite acima do máximo permitido" }
        repository.updatePersonalData(update)
    }
}
