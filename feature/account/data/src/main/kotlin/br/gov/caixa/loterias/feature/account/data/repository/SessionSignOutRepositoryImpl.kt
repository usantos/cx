package br.gov.caixa.loterias.feature.account.data.repository

import br.gov.caixa.loterias.core.data.mapper.toServiceConnectionFailure
import br.gov.caixa.loterias.core.data.mapper.toServiceFailure
import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.network.error.BackendHttpFailure
import br.gov.caixa.loterias.feature.account.data.datasource.remote.SessionSignOutRemoteDataSource
import br.gov.caixa.loterias.feature.account.domain.model.SessionSignOutResult
import br.gov.caixa.loterias.feature.account.domain.repository.SessionSignOutRepository
import java.io.IOException

class SessionSignOutRepositoryImpl(
    private val remote: SessionSignOutRemoteDataSource,
) : SessionSignOutRepository {
    override suspend fun signOut(identity: AuthenticatedSessionIdentity): SessionSignOutResult = try {
        remote.signOut(identity)
        SessionSignOutResult.Confirmed(identity)
    } catch (failure: BackendHttpFailure) {
        SessionSignOutResult.Rejected(failure.toServiceFailure(allowRegistration = false))
    } catch (failure: IOException) {
        SessionSignOutResult.Rejected(ServiceFailure.ConnectionFailed(failure.toServiceConnectionFailure()))
    }
}
