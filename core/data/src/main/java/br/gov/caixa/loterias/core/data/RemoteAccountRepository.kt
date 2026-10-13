package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.account.AccountProfile
import br.gov.caixa.loterias.core.domain.account.AccountRegistration
import br.gov.caixa.loterias.core.domain.account.AccountRepository
import br.gov.caixa.loterias.core.domain.account.Gender
import br.gov.caixa.loterias.core.domain.account.MunicipalityId
import br.gov.caixa.loterias.core.domain.account.PersonalDataUpdate
import br.gov.caixa.loterias.core.domain.account.ZipCodeLocation
import br.gov.caixa.loterias.core.domain.account.AccountAccess
import br.gov.caixa.loterias.core.network.MunicipalityIdDto
import br.gov.caixa.loterias.core.network.AccountRegistrationDto
import br.gov.caixa.loterias.core.network.PersonalDataUpdateDto
import java.math.BigDecimal

class RemoteAccountRepository(
    private val remote: AccountRemoteDataSource,
) : AccountRepository {
    override suspend fun profile(): AccountProfile {
        val dto = requireNotNull(remote.currentUserProfile()) { "Dados do apostador indisponíveis" }
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
            phone = listOfNotNull(dto.phone?.areaCode, dto.phone?.number).joinToString(""),
        )
    }

    override suspend fun updatePersonalData(update: PersonalDataUpdate) {
        remote.updatePersonalData(
            PersonalDataUpdateDto(
                zipCode = update.zipCode.filter(Char::isDigit),
                municipalityId = update.municipalityId.toDto(),
                gender = if (update.gender == Gender.FEMALE) "F" else "M",
                dailyLimit = update.dailyLimit,
                acceptsNotifications = update.acceptsNotifications,
            ),
        )
    }

    override suspend fun register(registration: AccountRegistration) {
        remote.registerAccount(
            AccountRegistrationDto(
                zipCode = registration.zipCode.filter(Char::isDigit),
                acceptsTerms = registration.acceptsTerms,
                acceptsNotifications = registration.acceptsNotifications,
                municipalityId = registration.municipalityId.toDto(),
                gender = if (registration.gender == Gender.FEMALE) "F" else "M",
            ),
        )
    }

    override suspend fun verifyAccess(): AccountAccess {
        val response = remote.verifyAccount()
        return accountAccess(response.session, response.redirect)
    }

    override suspend fun recalculateAuthorizedLimit(): BigDecimal = requireNotNull(
        remote.recalculateAuthorizedLimit()?.authorizedDailyLimit,
    ) { "Limite autorizado indisponível" }

    override suspend fun locationForZipCode(zipCode: String): ZipCodeLocation {
        val municipality = requireNotNull(
            remote.neighborhoodsByZipCode(zipCode.filter(Char::isDigit)).orEmpty()
                .firstOrNull()?.municipality,
        ) { "O CEP informado não foi localizado. Por favor, insira um CEP válido." }
        return ZipCodeLocation(
            municipalityId = municipality.id.toDomain(),
            stateCode = municipality.state?.code.orEmpty(),
            city = municipality.name.orEmpty(),
        )
    }

    private fun MunicipalityIdDto?.toDomain() = MunicipalityId(
        stateId = this?.stateId ?: 0,
        number = this?.number ?: 0,
        checkDigit = this?.checkDigit ?: 0,
    )

    private fun MunicipalityId.toDto() = MunicipalityIdDto(stateId, number, checkDigit)
    private fun BigDecimal?.orZero() = this ?: BigDecimal.ZERO
}
