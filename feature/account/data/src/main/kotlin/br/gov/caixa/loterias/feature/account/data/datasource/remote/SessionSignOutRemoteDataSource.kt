package br.gov.caixa.loterias.feature.account.data.datasource.remote

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.network.api.SessionLogoutApiProvider
import br.gov.caixa.loterias.core.network.error.toBackendHttpFailure
import retrofit2.HttpException

fun interface SessionSignOutRemoteDataSource {
    suspend fun signOut(identity: AuthenticatedSessionIdentity)
}

class SessionSignOutRemoteDataSourceImpl(
    private val provider: SessionLogoutApiProvider,
    private val sessions: AccountVerificationSessionBinding,
) : SessionSignOutRemoteDataSource {
    override suspend fun signOut(identity: AuthenticatedSessionIdentity) {
        val response = provider.current().signOut(sessions.bind(identity))
        if (!response.isSuccessful) throw HttpException(response).toBackendHttpFailure()
        // O contrato legado retorna NetworkResponse bruto; nenhum payload é exigido.
        response.body()?.close()
    }
}
