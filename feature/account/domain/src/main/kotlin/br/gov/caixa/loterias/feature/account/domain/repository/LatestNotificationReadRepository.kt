package br.gov.caixa.loterias.feature.account.domain.repository

import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.NotificationReadResult

fun interface LatestNotificationReadRepository {
    suspend fun markRead(identity: AuthenticatedSessionIdentity, notificationId: Long): NotificationReadResult
}
