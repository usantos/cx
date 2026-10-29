package br.gov.caixa.loterias.feature.notifications.data.datasource.remote

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.notifications.data.dto.NotificationInboxEnvelopeDto

interface NotificationInboxRemoteDataSource {
    suspend fun load(identity: AuthenticatedSessionIdentity): Pair<NotificationInboxEnvelopeDto, NotificationInboxEnvelopeDto>
    suspend fun markRead(identity: AuthenticatedSessionIdentity, id: Long)
    suspend fun remove(identity: AuthenticatedSessionIdentity, id: Long)
    suspend fun clear(identity: AuthenticatedSessionIdentity)
}
