package br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.AccountSuspension

fun interface SuspendAccountUseCase {
    suspend operator fun invoke(periodHours: Int): AppResult<AccountSuspension>
}
