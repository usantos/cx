package br.gov.caixa.loterias.feature.onboarding.domain.usecase

import br.gov.caixa.loterias.feature.onboarding.domain.repository.ForceUpdateRepository
import java.io.Closeable

class ObserveForceUpdateUseCase(private val repository: ForceUpdateRepository) {
    operator fun invoke(onChanged: (Boolean) -> Unit): Closeable = repository.observe(onChanged)
}
