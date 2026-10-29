package br.gov.caixa.loterias.feature.notifications.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification

fun interface GetNotificationsUseCase {
    suspend operator fun invoke(): AppResult<List<AppNotification>>
}
