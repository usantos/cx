package br.gov.caixa.loterias.feature.account.data.repository

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.repository.UnreadNotificationCountRepository
import br.gov.caixa.loterias.feature.account.domain.model.UnreadNotificationCountUnavailableException
import com.google.gson.JsonParseException
import br.gov.caixa.loterias.core.network.api.NotificationReadApiProvider
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding

class UnreadNotificationCountRepositoryImpl(
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
