package br.gov.caixa.loterias.feature.notifications.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory

fun interface SetNotificationPreferenceUseCase {
    suspend operator fun invoke(category: NotificationCategory?, enabled: Boolean): AppResult<Unit>
}
