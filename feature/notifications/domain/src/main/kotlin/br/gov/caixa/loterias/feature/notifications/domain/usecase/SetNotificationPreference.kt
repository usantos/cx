package br.gov.caixa.loterias.feature.notifications.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationRepository

class SetNotificationPreference(private val repository: NotificationRepository) : SetNotificationPreferenceUseCase {
    override suspend fun invoke(category: NotificationCategory?, enabled: Boolean) =
        appResultOf { repository.setPreference(category, enabled) }
}
