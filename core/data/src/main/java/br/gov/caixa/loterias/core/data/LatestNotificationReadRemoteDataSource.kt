package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.network.NotificationReadApiProvider
import br.gov.caixa.loterias.core.network.toBackendHttpFailure
import retrofit2.HttpException

fun interface LatestNotificationReadRemoteDataSource {
    suspend fun markRead(identity: AuthenticatedSessionIdentity, notificationId: Long)
}

class RetrofitLatestNotificationReadRemoteDataSource(
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
