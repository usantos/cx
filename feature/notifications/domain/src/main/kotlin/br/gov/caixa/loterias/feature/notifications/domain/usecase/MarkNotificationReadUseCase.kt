package br.gov.caixa.loterias.feature.notifications.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult

fun interface MarkNotificationReadUseCase {
    suspend operator fun invoke(id: Long): AppResult<Unit>
}
