package br.gov.caixa.loterias.feature.onboarding.domain.usecase

import br.gov.caixa.loterias.feature.onboarding.domain.repository.ForceUpdateRepository

class RefreshForceUpdateUseCase(private val repository: ForceUpdateRepository) {
    suspend operator fun invoke(): Boolean = repository.refresh()
}
