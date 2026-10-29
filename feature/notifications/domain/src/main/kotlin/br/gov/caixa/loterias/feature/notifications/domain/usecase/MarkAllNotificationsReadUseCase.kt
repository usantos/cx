package br.gov.caixa.loterias.feature.notifications.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult

fun interface MarkAllNotificationsReadUseCase {
    suspend operator fun invoke(): AppResult<Unit>
}
