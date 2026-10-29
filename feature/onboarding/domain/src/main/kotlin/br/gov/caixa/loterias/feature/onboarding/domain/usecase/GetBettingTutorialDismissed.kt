package br.gov.caixa.loterias.feature.onboarding.domain.usecase

import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import br.gov.caixa.loterias.feature.onboarding.domain.repository.OnboardingRepository

class GetBettingTutorialDismissed(private val repository: OnboardingRepository) : GetBettingTutorialDismissedUseCase {
    override suspend fun invoke(tutorial: BettingTutorial): Boolean = repository.isTutorialDismissed(tutorial)
}
