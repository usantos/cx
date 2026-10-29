package br.gov.caixa.loterias.feature.notifications.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationRepository

class GetNotifications(private val repository: NotificationRepository) : GetNotificationsUseCase {
    override suspend fun invoke() = appResultOf { repository.getNotifications() }
}
