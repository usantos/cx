package br.gov.caixa.loterias.feature.account.data.repository

import br.gov.caixa.loterias.feature.account.data.localizedMessage as messageText

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.repository.LatestNotificationReadRepository
import br.gov.caixa.loterias.feature.account.domain.model.NotificationReadResult
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.network.error.BackendHttpFailure
import java.io.IOException
import br.gov.caixa.loterias.feature.account.data.datasource.remote.LatestNotificationReadRemoteDataSource
import br.gov.caixa.loterias.core.data.mapper.toServiceConnectionFailure
import br.gov.caixa.loterias.core.data.mapper.toServiceFailure

class LatestNotificationReadRepositoryImpl(private val remote: LatestNotificationReadRemoteDataSource) : LatestNotificationReadRepository {
    override suspend fun markRead(identity: AuthenticatedSessionIdentity, notificationId: Long): NotificationReadResult {
        require(notificationId > 0) { messageText("identificacao_de_notificacao_inv_7398ea") }
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
