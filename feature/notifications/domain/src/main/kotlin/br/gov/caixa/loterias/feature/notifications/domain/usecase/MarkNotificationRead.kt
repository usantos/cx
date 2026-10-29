package br.gov.caixa.loterias.feature.notifications.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationRepository

class MarkNotificationRead(private val repository: NotificationRepository) : MarkNotificationReadUseCase {
    override suspend fun invoke(id: Long) = appResultOf { repository.markAsRead(id) }
}
