package br.gov.caixa.loterias.feature.onboarding.domain.repository

import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset

interface LauncherIconConfigurationRepository {
    val activePreset: LauncherIconPreset
}
