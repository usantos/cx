package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryDecision
import br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryRequest
import br.gov.caixa.loterias.feature.betting.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import br.gov.caixa.loterias.feature.onboarding.domain.repository.OnboardingRepository

class PreparePoolEntry(private val verifyAccess: VerifyAccountAccessUseCase, private val onboarding: OnboardingRepository) :
    PreparePoolEntryUseCase {
    override suspend fun invoke(request: PoolEntryRequest): AppResult<PoolEntryDecision> {
        val type = request.contestType.uppercase(java.util.Locale.ROOT)
        if (request.modalityId <= 0 || type !in setOf("NORMAL", "ESPECIAL")) {
            return AppResult.Failure(AppError.Validation(messageText("modalidade_ou_tipo_de_concurso_i_ccc42d")))
        }
        val destination = when (val result = verifyBettingAccount(verifyAccess)) {
            is AppResult.Success -> result.value
            is AppResult.Failure -> return result
        }
        if (destination != PostLoginDestination.HOME) {
            return AppResult.Success(PoolEntryDecision.CompleteAccount(destination))
        }
        return AppResult.Success(
            PoolEntryDecision.Ready(
                request.copy(contestType = type),
                showTutorial = !onboarding.isTutorialDismissed(BettingTutorial.POOL)
            )
        )
    }
}
