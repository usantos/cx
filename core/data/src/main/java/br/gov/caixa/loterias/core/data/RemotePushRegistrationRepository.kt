package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.push.PushRegistrationRepository

class RemotePushRegistrationRepository(
    private val remote: PushRegistrationRemoteDataSource,
) : PushRegistrationRepository {
    override suspend fun register(identity: AuthenticatedSessionIdentity, cpf: Long, token: String): Boolean =
        remote.register(identity, cpf, token)
}
