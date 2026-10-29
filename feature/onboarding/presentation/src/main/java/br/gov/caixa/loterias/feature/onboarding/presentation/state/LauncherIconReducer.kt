package br.gov.caixa.loterias.feature.onboarding.presentation.state

import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset

sealed interface LauncherIconAction {
    data class Selected(val preset: LauncherIconPreset) : LauncherIconAction
    data class CalendarChanged(val preset: LauncherIconPreset) : LauncherIconAction
    data class Completed(val outcome: LauncherIconApplyOutcome, val revision: Long) : LauncherIconAction
}

fun reduceLauncherIconState(state: LauncherIconState, action: LauncherIconAction): LauncherIconState = when (action) {
    is LauncherIconAction.Selected -> if (state.preset == null) state.copy(preset = action.preset) else state

    is LauncherIconAction.CalendarChanged -> state.copy(preset = action.preset, revision = state.revision + 1)

    is LauncherIconAction.Completed -> if (
        state.preset != null && state.revision == action.revision && state.completedRevision != action.revision
    ) {
        state.copy(outcome = action.outcome, completedRevision = action.revision)
    } else {
        state
    }
}
