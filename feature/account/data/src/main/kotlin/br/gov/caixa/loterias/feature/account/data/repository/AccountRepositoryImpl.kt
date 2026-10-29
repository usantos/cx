package br.gov.caixa.loterias.feature.account.data.repository

import br.gov.caixa.loterias.feature.account.data.localizedMessage as messageText

import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile
import br.gov.caixa.loterias.feature.account.domain.model.AccountRegistration
import br.gov.caixa.loterias.feature.account.domain.repository.AccountRepository
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.model.PersonalDataUpdate
import br.gov.caixa.loterias.feature.account.domain.model.ZipCodeLocation
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.data.dto.MunicipalityIdDto
import br.gov.caixa.loterias.feature.account.data.dto.AccountRegistrationDto
import br.gov.caixa.loterias.feature.account.data.dto.PersonalDataUpdateDto
import java.math.BigDecimal
import br.gov.caixa.loterias.feature.account.data.mapper.accountAccess
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountRemoteDataSource

class AccountRepositoryImpl(private val remote: AccountRemoteDataSource) : AccountRepository {
    override suspend fun profile(): AccountProfile {
        val dto = requireNotNull(remote.currentUserProfile()) { messageText("dados_do_apostador_indisponiveis_a42aa2") }
        val municipalityId = dto.municipalityId.toDomain()
        val municipality = remote.municipalities(municipalityId.stateId).orEmpty()
            .firstOrNull { candidate ->
                candidate.id?.number == municipalityId.number &&
                    candidate.id?.checkDigit == municipalityId.checkDigit
            }
        return AccountProfile(
            cpf = dto.cpf.orEmpty(),
            name = dto.name.orEmpty(),
            email = dto.email.orEmpty(),
            zipCode = dto.zipCode.orEmpty(),
            gender = if (dto.gender.equals("F", ignoreCase = true)) Gender.FEMALE else Gender.MALE,
            municipalityId = municipalityId,
            stateCode = municipality?.state?.code.orEmpty(),
            city = municipality?.name.orEmpty(),
            dailyLimit = dto.dailyLimit.orZero(),
            minimumDailyLimit = dto.minimumDailyLimit.orZero(),
            authorizedDailyLimit = dto.authorizedDailyLimit.orZero(),
            acceptsNotifications = dto.acceptsNotifications == true,
            birthDate = dto.birthDate.orEmpty(),
            phone = listOfNotNull(dto.phone?.areaCode, dto.phone?.number).joinToString("")
        )
    }

    override suspend fun updatePersonalData(update: PersonalDataUpdate) {
        remote.updatePersonalData(
            PersonalDataUpdateDto(
                zipCode = update.zipCode.filter(Char::isDigit),
                municipalityId = update.municipalityId.toDto(),
                gender = if (update.gender == Gender.FEMALE) "F" else "M",
                dailyLimit = update.dailyLimit,
                acceptsNotifications = update.acceptsNotifications
            )
        )
    }

    override suspend fun register(registration: AccountRegistration) {
        remote.registerAccount(
            AccountRegistrationDto(
                zipCode = registration.zipCode.filter(Char::isDigit),
                acceptsTerms = registration.acceptsTerms,
                acceptsNotifications = registration.acceptsNotifications,
                municipalityId = registration.municipalityId.toDto(),
                gender = if (registration.gender == Gender.FEMALE) "F" else "M"
            )
        )
    }

    override suspend fun verifyAccess(): AccountAccess {
        val response = remote.verifyAccount()
        return accountAccess(response.session, response.redirect)
    }

    override suspend fun recalculateAuthorizedLimit(): BigDecimal = requireNotNull(
        remote.recalculateAuthorizedLimit()?.authorizedDailyLimit
    ) { messageText("limite_autorizado_indisponivel_fafc9d") }

    override suspend fun locationForZipCode(zipCode: String): ZipCodeLocation {
        val municipality = requireNotNull(
            remote.neighborhoodsByZipCode(zipCode.filter(Char::isDigit)).orEmpty()
                .firstOrNull()?.municipality
        ) { messageText("o_cep_informado_nao_foi_localiza_857ff7") }
        return ZipCodeLocation(
            municipalityId = municipality.id.toDomain(),
            stateCode = municipality.state?.code.orEmpty(),
            city = municipality.name.orEmpty()
        )
    }

    private fun MunicipalityIdDto?.toDomain() = MunicipalityId(
        stateId = this?.stateId ?: 0,
        number = this?.number ?: 0,
        checkDigit = this?.checkDigit ?: 0
    )

    private fun MunicipalityId.toDto() = MunicipalityIdDto(stateId, number, checkDigit)
    private fun BigDecimal?.orZero() = this ?: BigDecimal.ZERO
}
