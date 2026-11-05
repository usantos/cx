package br.gov.caixa.loterias.feature.notifications.domain.repository

import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationInbox

interface NotificationInboxRepository {
    suspend fun load(identity: AuthenticatedSessionIdentity): NotificationInbox
    suspend fun markRead(identity: AuthenticatedSessionIdentity, id: Long)
    suspend fun remove(identity: AuthenticatedSessionIdentity, id: Long)
    suspend fun clear(identity: AuthenticatedSessionIdentity)
}
