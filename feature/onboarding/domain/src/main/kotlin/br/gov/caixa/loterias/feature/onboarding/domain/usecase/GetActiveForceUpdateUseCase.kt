package br.gov.caixa.loterias.feature.onboarding.domain.usecase

import br.gov.caixa.loterias.feature.onboarding.domain.repository.ForceUpdateRepository

class GetActiveForceUpdateUseCase(private val repository: ForceUpdateRepository) {
    operator fun invoke(): Boolean = repository.active
}
