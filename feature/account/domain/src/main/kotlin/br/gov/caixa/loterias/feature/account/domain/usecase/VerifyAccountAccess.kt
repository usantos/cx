package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.account.domain.repository.AccountRepository

class VerifyAccountAccess(private val repository: AccountRepository) : VerifyAccountAccessUseCase {
    override suspend fun invoke() = appResultOf { repository.verifyAccess() }
}
