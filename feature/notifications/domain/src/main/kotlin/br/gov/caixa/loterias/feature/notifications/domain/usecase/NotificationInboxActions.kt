package br.gov.caixa.loterias.feature.notifications.domain.usecase

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationInboxRepository

class NotificationInboxActions(private val repository: NotificationInboxRepository) {
    suspend fun load(identity: AuthenticatedSessionIdentity) = repository.load(identity)
    suspend fun markRead(identity: AuthenticatedSessionIdentity, id: Long) {
        require(id > 0)
        repository.markRead(identity, id)
    }
    suspend fun remove(identity: AuthenticatedSessionIdentity, id: Long) {
        require(id > 0)
        repository.remove(identity, id)
    }
    suspend fun clear(identity: AuthenticatedSessionIdentity) = repository.clear(identity)
}
