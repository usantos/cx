package br.gov.caixa.loterias.core.domain.betting

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.domain.account.PostLoginDestination
import br.gov.caixa.loterias.core.domain.account.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.core.domain.configuration.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.core.domain.onboarding.BettingTutorial
import br.gov.caixa.loterias.core.domain.onboarding.OnboardingRepository

enum class HomeBettingAction { COMBO, INSTANT, RAPID }
enum class HomeBettingTarget { COMBOS, INSTANT, RAPID_CONFIGURATION, RAPID_LIST }

sealed interface HomeBettingDecision {
    data class Ready(val target: HomeBettingTarget) : HomeBettingDecision
    data class CompleteAccount(val destination: PostLoginDestination) : HomeBettingDecision
}

fun interface PrepareHomeBettingActionUseCase {
    suspend operator fun invoke(action: HomeBettingAction): AppResult<HomeBettingDecision>
}

class PrepareHomeBettingAction(
    private val verifyAccess: VerifyAccountAccessUseCase,
    private val features: GetHomeFeatureConfigurationUseCase,
    private val onboarding: OnboardingRepository,
    private val isAuthenticated: () -> Boolean = { true },
) : PrepareHomeBettingActionUseCase {
    override suspend fun invoke(action: HomeBettingAction): AppResult<HomeBettingDecision> {
        if (action == HomeBettingAction.INSTANT && !isAuthenticated()) return AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.INSTANT))
        val destination = when (val access = verifyBettingAccount(verifyAccess)) {
            is AppResult.Success -> access.value
            is AppResult.Failure -> return access
        }
        if (destination != PostLoginDestination.HOME) return AppResult.Success(HomeBettingDecision.CompleteAccount(destination))
        if (action == HomeBettingAction.INSTANT) return AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.INSTANT))
        val configuration = when (val result = features()) {
            is AppResult.Success -> result.value
            is AppResult.Failure -> return result
        }
        if (action == HomeBettingAction.COMBO) {
            if (!configuration.comboBettingEnabled) return AppResult.Failure(AppError.Validation("Combo indisponível no momento."))
            return AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.COMBOS))
        }
        if (!configuration.rapidBettingEnabled) return AppResult.Failure(AppError.Validation("Rapidão indisponível no momento."))
        val target = if (onboarding.isTutorialDismissed(BettingTutorial.RAPID)) HomeBettingTarget.RAPID_LIST else HomeBettingTarget.RAPID_CONFIGURATION
        return AppResult.Success(HomeBettingDecision.Ready(target))
    }
}
