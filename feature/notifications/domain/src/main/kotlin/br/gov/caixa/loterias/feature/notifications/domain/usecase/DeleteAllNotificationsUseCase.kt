package br.gov.caixa.loterias.feature.notifications.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult

fun interface DeleteAllNotificationsUseCase {
    suspend operator fun invoke(): AppResult<Unit>
}
