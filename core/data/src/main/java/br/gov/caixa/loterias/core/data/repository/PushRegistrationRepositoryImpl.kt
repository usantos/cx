package br.gov.caixa.loterias.core.data.repository

import br.gov.caixa.loterias.core.data.datasource.remote.PushRegistrationRemoteDataSource
import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.push.repository.PushRegistrationRepository

class PushRegistrationRepositoryImpl(
    private val remote: PushRegistrationRemoteDataSource,
) : PushRegistrationRepository {
    override suspend fun register(identity: AuthenticatedSessionIdentity, cpf: Long, token: String): Boolean =
        remote.register(identity, cpf, token)
}
