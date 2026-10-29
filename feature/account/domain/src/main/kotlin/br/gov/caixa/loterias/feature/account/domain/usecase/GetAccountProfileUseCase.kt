package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile

fun interface GetAccountProfileUseCase {
    suspend operator fun invoke(): AppResult<AccountProfile>
}
