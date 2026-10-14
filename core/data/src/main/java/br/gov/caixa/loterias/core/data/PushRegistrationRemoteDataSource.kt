package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.network.NotificationReadApiProvider
import br.gov.caixa.loterias.core.network.PushRegistrationRequestDto

/** The transport reports only a checked business acknowledgement to its repository. */
fun interface PushRegistrationRemoteDataSource {
    suspend fun register(identity: AuthenticatedSessionIdentity, cpf: Long, token: String): Boolean
}

class RetrofitPushRegistrationRemoteDataSource(
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
