package br.gov.caixa.loterias.feature.notifications.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationRepository

class MarkAllNotificationsRead(private val repository: NotificationRepository) : MarkAllNotificationsReadUseCase {
    override suspend fun invoke() = appResultOf { repository.markAllAsRead() }
}
