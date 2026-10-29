package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.localizedMessage as messageText

/** Compartilhado pela entrada individual e de bolões; a entrada de bolões não deve aplicar regras de encerramento individual. */
internal suspend fun verifyBettingAccount(verifyAccess: VerifyAccountAccessUseCase): AppResult<PostLoginDestination> {
    val access = when (val result = verifyAccess()) {
        is AppResult.Success -> result.value
        is AppResult.Failure -> return result
    }
    if (access.temporarySuspension) {
        return AppResult.Failure(
            AppError.Validation(
                messageText("ha_uma_suspensao_temporaria_ativ_a64d6f")
            )
        )
    }
    return AppResult.Success(access.destination)
}
