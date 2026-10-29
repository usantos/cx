package br.gov.caixa.loterias.feature.onboarding.presentation.state

import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset

enum class LauncherIconApplyOutcome { APPLIED, UNCHANGED, DEFERRED, FAILED }

data class LauncherIconState(
    val preset: LauncherIconPreset? = null,
    val outcome: LauncherIconApplyOutcome? = null,
    val revision: Long = 0,
    val completedRevision: Long? = null,
)
