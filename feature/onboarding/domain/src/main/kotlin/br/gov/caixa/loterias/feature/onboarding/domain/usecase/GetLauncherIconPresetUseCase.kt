package br.gov.caixa.loterias.feature.onboarding.domain.usecase

import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import br.gov.caixa.loterias.feature.onboarding.domain.repository.LauncherIconConfigurationRepository

class GetLauncherIconPresetUseCase(private val repository: LauncherIconConfigurationRepository) {
    operator fun invoke(): LauncherIconPreset = repository.activePreset
}
