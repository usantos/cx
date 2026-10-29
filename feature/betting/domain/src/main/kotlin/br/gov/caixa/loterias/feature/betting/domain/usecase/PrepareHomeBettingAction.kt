package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.feature.betting.domain.model.localizedMessage as messageText

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import br.gov.caixa.loterias.feature.onboarding.domain.repository.OnboardingRepository
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingAction
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingTarget

class PrepareHomeBettingAction(
    private val verifyAccess: VerifyAccountAccessUseCase,
    private val features: GetHomeFeatureConfigurationUseCase,
    private val onboarding: OnboardingRepository,
    private val isAuthenticated: () -> Boolean = { true },
) : PrepareHomeBettingActionUseCase {
    override suspend fun invoke(action: HomeBettingAction): AppResult<HomeBettingDecision> {
        if (action == HomeBettingAction.INSTANT &&
            !isAuthenticated()
        ) {
            return AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.INSTANT))
        }
        val destination = if (action == HomeBettingAction.RAPID &&
            !isAuthenticated()
        ) {
            PostLoginDestination.HOME
        } else {
            when (val access = verifyBettingAccount(verifyAccess)) {
                is AppResult.Success -> access.value
                is AppResult.Failure -> return access
            }
        }
        if (destination != PostLoginDestination.HOME) return AppResult.Success(HomeBettingDecision.CompleteAccount(destination))
        if (action == HomeBettingAction.INSTANT) return AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.INSTANT))
        val configuration = when (val result = features()) {
            is AppResult.Success -> result.value
            is AppResult.Failure -> return result
        }
        if (action == HomeBettingAction.COMBO) {
            if (!configuration.comboBettingEnabled) {
                return AppResult.Failure(
                    AppError.Validation(messageText("combo_indisponivel_no_momento_f03c03"))
                )
            }
            return AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.COMBOS))
        }
        if (!configuration.rapidBettingEnabled) {
            return AppResult.Failure(
                AppError.Validation(messageText("rapidao_indisponivel_no_momento_31419b"))
            )
        }
        val target = if (onboarding.isTutorialDismissed(
                BettingTutorial.RAPID
            )
        ) {
            HomeBettingTarget.RAPID_LIST
        } else {
            HomeBettingTarget.RAPID_CONFIGURATION
        }
        return AppResult.Success(HomeBettingDecision.Ready(target))
    }
}
