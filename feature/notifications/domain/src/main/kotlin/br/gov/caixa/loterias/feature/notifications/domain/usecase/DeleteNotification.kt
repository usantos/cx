package br.gov.caixa.loterias.feature.notifications.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationRepository

class DeleteNotification(private val repository: NotificationRepository) : DeleteNotificationUseCase {
    override suspend fun invoke(id: Long) = appResultOf { repository.delete(id) }
}
