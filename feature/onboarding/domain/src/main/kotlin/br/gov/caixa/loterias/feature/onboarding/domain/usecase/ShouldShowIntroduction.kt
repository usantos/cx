package br.gov.caixa.loterias.feature.onboarding.domain.usecase

import br.gov.caixa.loterias.feature.onboarding.domain.repository.OnboardingRepository

class ShouldShowIntroduction(
    private val repository: OnboardingRepository,
) : ShouldShowIntroductionUseCase {
    override suspend fun invoke(): Boolean = !repository.isIntroductionCompleted()
}
