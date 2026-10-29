package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.NotificationReadResult
import br.gov.caixa.loterias.feature.account.domain.model.AccountNotification

fun interface ReadLatestNotificationUseCase {
    suspend operator fun invoke(notification: AccountNotification): AppResult<NotificationReadResult>
}
