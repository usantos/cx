package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.AccountRegistration

fun interface RegisterAccountUseCase {
    suspend operator fun invoke(registration: AccountRegistration): AppResult<Unit>
}
