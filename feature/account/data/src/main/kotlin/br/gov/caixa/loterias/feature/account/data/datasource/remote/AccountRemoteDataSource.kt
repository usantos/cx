package br.gov.caixa.loterias.feature.account.data.datasource.remote

import br.gov.caixa.loterias.core.network.dto.account.AccountRegistrationDto
import br.gov.caixa.loterias.core.network.dto.account.MunicipalityDto
import br.gov.caixa.loterias.core.network.dto.account.NeighborhoodDto
import br.gov.caixa.loterias.core.network.dto.account.PersonalDataUpdateDto
import br.gov.caixa.loterias.core.network.dto.account.UserProfileDto

interface AccountRemoteDataSource {
    suspend fun currentUserProfile(): UserProfileDto?
    suspend fun municipalities(stateId: Long): List<MunicipalityDto>?
    suspend fun neighborhoodsByZipCode(zipCode: String): List<NeighborhoodDto>?
    suspend fun updatePersonalData(request: PersonalDataUpdateDto)
    suspend fun registerAccount(request: AccountRegistrationDto)
    suspend fun verifyAccount(): AccountVerificationRemoteData
    suspend fun recalculateAuthorizedLimit(): UserProfileDto?
}
