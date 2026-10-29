package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.feature.betting.domain.repository.ShakeSettingsRepository

class GetShakeEnabled(private val repository: ShakeSettingsRepository) : GetShakeEnabledUseCase {
    override suspend fun invoke(): Boolean = repository.enabled()
}
