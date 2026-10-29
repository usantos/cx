package br.gov.caixa.loterias.feature.account.data.repository

import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountRemoteDataSource
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.repository.AccountMunicipalityRepository

class AccountMunicipalityRepositoryImpl(private val remote: AccountRemoteDataSource) : AccountMunicipalityRepository {
    override suspend fun municipality(): MunicipalityId {
        val municipality = requireNotNull(remote.currentUserProfile()?.municipalityId)
        return MunicipalityId(municipality.stateId ?: 0, municipality.number ?: 0, municipality.checkDigit ?: 0)
    }
}
