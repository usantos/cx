package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.network.SessionLogoutApiProvider
import br.gov.caixa.loterias.core.network.toBackendHttpFailure
import retrofit2.HttpException

fun interface SessionSignOutRemoteDataSource {
    suspend fun signOut(identity: AuthenticatedSessionIdentity)
}

class RetrofitSessionSignOutRemoteDataSource(
    private val provider: SessionLogoutApiProvider,
    private val sessions: AccountVerificationSessionBinding,
) : SessionSignOutRemoteDataSource {
    override suspend fun signOut(identity: AuthenticatedSessionIdentity) {
        val response = provider.current().signOut(sessions.bind(identity))
        if (!response.isSuccessful) throw HttpException(response).toBackendHttpFailure()
        // The legacy contract returns the raw NetworkResponse; no payload is required.
        response.body()?.close()
    }
}
