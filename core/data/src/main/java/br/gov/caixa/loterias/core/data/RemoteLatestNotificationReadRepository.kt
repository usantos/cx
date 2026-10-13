package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.account.LatestNotificationReadRepository
import br.gov.caixa.loterias.core.domain.account.NotificationReadResult
import br.gov.caixa.loterias.core.domain.service.ServiceFailure
import br.gov.caixa.loterias.core.network.BackendHttpFailure
import java.io.IOException

class RemoteLatestNotificationReadRepository(
    private val remote: LatestNotificationReadRemoteDataSource,
) : LatestNotificationReadRepository {
    override suspend fun markRead(identity: AuthenticatedSessionIdentity, notificationId: Long): NotificationReadResult {
        require(notificationId > 0) { "Identificação de notificação inválida" }
        return try {
            remote.markRead(identity, notificationId)
            NotificationReadResult.Confirmed
        } catch (failure: BackendHttpFailure) {
            NotificationReadResult.Rejected(failure.toServiceFailure(allowRegistration = false))
        } catch (failure: IOException) {
            NotificationReadResult.Rejected(ServiceFailure.ConnectionFailed(failure.toServiceConnectionFailure()))
        }
    }
}
