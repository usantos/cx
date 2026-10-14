package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.account.SessionSignOutRepository
import br.gov.caixa.loterias.core.domain.account.SessionSignOutResult
import br.gov.caixa.loterias.core.domain.service.ServiceFailure
import br.gov.caixa.loterias.core.network.BackendHttpFailure
import java.io.IOException

class RemoteSessionSignOutRepository(
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
