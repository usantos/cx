package br.gov.caixa.loterias.feature.notifications.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationPreference

fun interface GetNotificationPreferencesUseCase {
    suspend operator fun invoke(): AppResult<List<NotificationPreference>>
}
