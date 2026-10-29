package br.gov.caixa.loterias.feature.account.data.datasource.remote

import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.feature.account.data.dto.PersonalDataUpdateDto
import br.gov.caixa.loterias.feature.account.data.dto.AccountRegistrationDto
import br.gov.caixa.loterias.feature.account.data.dto.AccountVerificationRequestDto

class AccountRemoteDataSourceImpl(
    private val apiProvider: LoteriasApiProvider,
) : AccountRemoteDataSource {
    override suspend fun currentUserProfile() = apiProvider.current().featureAccountApi().currentUserProfile().payload

    override suspend fun municipalities(stateId: Long) = apiProvider.current().featureAccountApi().municipalities(stateId).payload

    override suspend fun neighborhoodsByZipCode(zipCode: String) =
        apiProvider.current().featureAccountApi().neighborhoodsByZipCode(zipCode).payload

    override suspend fun updatePersonalData(request: PersonalDataUpdateDto) {
        apiProvider.current().featureAccountApi().updatePersonalData(request)
    }

    override suspend fun registerAccount(request: AccountRegistrationDto) {
        apiProvider.current().featureAccountApi().registerAccount(request)
    }

    override suspend fun verifyAccount(): AccountVerificationRemoteData {
        val response = apiProvider.current().featureAccountApi().verifyAccount(AccountVerificationRequestDto())
        return AccountVerificationRemoteData(response.payload, response.redirect)
    }

    override suspend fun recalculateAuthorizedLimit() =
        apiProvider.current().featureAccountApi().recalculateAuthorizedLimit().payload
}
