package br.gov.caixa.loterias.feature.onboarding.domain.usecase

import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import br.gov.caixa.loterias.feature.onboarding.domain.repository.OnboardingRepository

class SetBettingTutorialDismissed(
    private val repository: OnboardingRepository,
) : SetBettingTutorialDismissedUseCase {
    override suspend fun invoke(tutorial: BettingTutorial, dismissed: Boolean) = repository.setTutorialDismissed(tutorial, dismissed)
}
