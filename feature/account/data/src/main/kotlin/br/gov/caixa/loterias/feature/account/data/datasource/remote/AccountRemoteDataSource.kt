package br.gov.caixa.loterias.feature.account.data.datasource.remote

import br.gov.caixa.loterias.feature.account.data.dto.UserProfileDto
import br.gov.caixa.loterias.feature.account.data.dto.MunicipalityDto
import br.gov.caixa.loterias.feature.account.data.dto.NeighborhoodDto
import br.gov.caixa.loterias.feature.account.data.dto.PersonalDataUpdateDto
import br.gov.caixa.loterias.feature.account.data.dto.AccountRegistrationDto

interface AccountRemoteDataSource {
    suspend fun currentUserProfile(): UserProfileDto?
    suspend fun municipalities(stateId: Long): List<MunicipalityDto>?
    suspend fun neighborhoodsByZipCode(zipCode: String): List<NeighborhoodDto>?
    suspend fun updatePersonalData(request: PersonalDataUpdateDto)
    suspend fun registerAccount(request: AccountRegistrationDto)
    suspend fun verifyAccount(): AccountVerificationRemoteData
    suspend fun recalculateAuthorizedLimit(): UserProfileDto?
}
