package br.gov.caixa.loterias.feature.account.data.datasource.remote

import br.gov.caixa.loterias.core.data.datasource.remote.PushRegistrationRemoteDataSource
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.network.api.NotificationReadApiProvider
import br.gov.caixa.loterias.feature.account.data.dto.PushRegistrationRequestDto

class PushRegistrationRemoteDataSourceImpl(
    private val provider: NotificationReadApiProvider,
    private val sessions: AccountVerificationSessionBinding,
) : PushRegistrationRemoteDataSource {
    override suspend fun register(identity: AuthenticatedSessionIdentity, cpf: Long, token: String): Boolean {
        val response = provider.current().registerPushDevice(
            PushRegistrationRequestDto(listOf(cpf), token), sessions.bind(identity),
        )
        return response.payload?.code == "200" && response.code == null &&
            response.redirect?.isJsonNull != false && response.errors.isNullOrEmpty()
    }
}
