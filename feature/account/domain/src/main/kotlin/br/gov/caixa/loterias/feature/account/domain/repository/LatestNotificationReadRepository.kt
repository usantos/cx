package br.gov.caixa.loterias.feature.account.domain.repository

import br.gov.caixa.loterias.feature.account.domain.model.NotificationReadResult
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity

fun interface LatestNotificationReadRepository {
    suspend fun markRead(identity: AuthenticatedSessionIdentity, notificationId: Long): NotificationReadResult
}
