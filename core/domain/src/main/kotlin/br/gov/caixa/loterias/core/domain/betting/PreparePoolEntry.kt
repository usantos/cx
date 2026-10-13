package br.gov.caixa.loterias.core.domain.betting

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.domain.account.PostLoginDestination
import br.gov.caixa.loterias.core.domain.account.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.core.domain.onboarding.BettingTutorial
import br.gov.caixa.loterias.core.domain.onboarding.OnboardingRepository

data class PoolEntryRequest(val modalityId: Int, val contestType: String)

sealed interface PoolEntryDecision {
    data class Ready(val request: PoolEntryRequest, val showTutorial: Boolean) : PoolEntryDecision
    data class CompleteAccount(val destination: PostLoginDestination) : PoolEntryDecision
}

fun interface PreparePoolEntryUseCase {
    suspend operator fun invoke(request: PoolEntryRequest): AppResult<PoolEntryDecision>
}

class PreparePoolEntry(
    private val verifyAccess: VerifyAccountAccessUseCase,
    private val onboarding: OnboardingRepository,
) : PreparePoolEntryUseCase {
    override suspend fun invoke(request: PoolEntryRequest): AppResult<PoolEntryDecision> {
        val type = request.contestType.uppercase(java.util.Locale.ROOT)
        if (request.modalityId <= 0 || type !in setOf("NORMAL", "ESPECIAL")) {
            return AppResult.Failure(AppError.Validation("Modalidade ou tipo de concurso inválido."))
        }
        val destination = when (val result = verifyBettingAccount(verifyAccess)) {
            is AppResult.Success -> result.value
            is AppResult.Failure -> return result
        }
        if (destination != PostLoginDestination.HOME) {
            return AppResult.Success(PoolEntryDecision.CompleteAccount(destination))
        }
        return AppResult.Success(PoolEntryDecision.Ready(request.copy(contestType = type),
            showTutorial = !onboarding.isTutorialDismissed(BettingTutorial.POOL)))
    }
}
