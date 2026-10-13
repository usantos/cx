package br.gov.caixa.loterias.core.domain.betting

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.domain.account.PostLoginDestination
import br.gov.caixa.loterias.core.domain.account.VerifyAccountAccessUseCase

/** Shared by individual and pool entry; pool entry must not apply individual closing rules. */
internal suspend fun verifyBettingAccount(verifyAccess: VerifyAccountAccessUseCase): AppResult<PostLoginDestination> {
    val access = when (val result = verifyAccess()) {
        is AppResult.Success -> result.value
        is AppResult.Failure -> return result
    }
    if (access.temporarySuspension) return AppResult.Failure(AppError.Validation(
        "Há uma suspensão temporária ativa para compras. Novas apostas poderão ser realizadas no Portal Loterias Online e no Aplicativo Loterias CAIXA após o término do período selecionado.",
    ))
    return AppResult.Success(access.destination)
}
