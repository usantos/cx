package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.account.UnreadNotificationCountRepository
import br.gov.caixa.loterias.core.domain.account.UnreadNotificationCountUnavailableException
import com.google.gson.JsonParseException
import br.gov.caixa.loterias.core.network.NotificationReadApiProvider

class RemoteUnreadNotificationCountRepository(
    private val provider: NotificationReadApiProvider,
    private val sessions: AccountVerificationSessionBinding,
) : UnreadNotificationCountRepository {
    override suspend fun getCount(identity: AuthenticatedSessionIdentity): Int {
        val response = try {
            provider.current().unread(sessions.bind(identity))
        } catch (_: JsonParseException) {
            throw UnreadNotificationCountUnavailableException()
        }
        if (response.code() != 200) {
            response.errorBody()?.close()
            throw UnreadNotificationCountUnavailableException()
        }
        val envelope = response.body() ?: return 0
        if (envelope.code != null || envelope.redirect?.isJsonNull == false || !envelope.errors.isNullOrEmpty()) {
            throw UnreadNotificationCountUnavailableException()
        }
        return envelope.payload?.size ?: 0
    }
}
