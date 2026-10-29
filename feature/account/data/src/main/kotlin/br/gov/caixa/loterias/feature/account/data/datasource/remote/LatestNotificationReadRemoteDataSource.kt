package br.gov.caixa.loterias.feature.account.data.datasource.remote

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.network.api.NotificationReadApiProvider
import br.gov.caixa.loterias.core.network.error.toBackendHttpFailure
import retrofit2.HttpException

fun interface LatestNotificationReadRemoteDataSource {
    suspend fun markRead(identity: AuthenticatedSessionIdentity, notificationId: Long)
}

class LatestNotificationReadRemoteDataSourceImpl(
    private val provider: NotificationReadApiProvider,
    private val sessions: AccountVerificationSessionBinding,
) : LatestNotificationReadRemoteDataSource {
    override suspend fun markRead(identity: AuthenticatedSessionIdentity, notificationId: Long) {
        val response = provider.current().markRead(notificationId, sessions.bind(identity))
        if (response.code() == 204) return
        if (response.code() != 200) throw HttpException(response).toBackendHttpFailure()
        val envelope = checkNotNull(response.body()) { "Confirmação de leitura ausente" }
        check(envelope.code == null && envelope.redirect?.isJsonNull != false && envelope.errors.isNullOrEmpty()) {
            "Leitura de notificação não confirmada"
        }
    }
}
