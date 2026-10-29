package br.gov.caixa.loterias.feature.onboarding.domain.usecase

import br.gov.caixa.loterias.feature.onboarding.domain.repository.OnboardingRepository

class CompleteIntroduction(
    private val repository: OnboardingRepository,
) : CompleteIntroductionUseCase {
    override suspend fun invoke() = repository.completeIntroduction()
}
