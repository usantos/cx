package br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetLauncherIconPresetUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ObserveLauncherIconCalendarUseCase
import br.gov.caixa.loterias.feature.onboarding.presentation.state.LauncherIconAction
import br.gov.caixa.loterias.feature.onboarding.presentation.state.LauncherIconApplyOutcome
import br.gov.caixa.loterias.feature.onboarding.presentation.state.LauncherIconState
import br.gov.caixa.loterias.feature.onboarding.presentation.state.reduceLauncherIconState

class LauncherIconViewModel(
    private val getPreset: GetLauncherIconPresetUseCase,
    private val observeCalendar: ObserveLauncherIconCalendarUseCase,
) : ViewModel() {
    private val mutableState = reduxStateStore<LauncherIconState, LauncherIconAction>(
        LauncherIconState(),
        ::reduceLauncherIconState
    ).also { addCloseable(it) }
    val state = mutableState.state

    /** Seleção inicial após ativar o Remote Config; o runtime reavalia o calendário ao retomar e em segundo plano. */
    fun load(previewValue: String? = null) {
        if (mutableState.value.preset != null) return
        if (previewValue == null) {
            runCatching {
                observeCalendar { mutableState.dispatch(LauncherIconAction.CalendarChanged(getPreset())) }
            }.getOrNull()?.let { addCloseable(it) }
        }
        val preset = previewValue?.let(LauncherIconPreset::fromConfig) ?: getPreset()
        mutableState.dispatch(LauncherIconAction.Selected(preset))
    }

    fun complete(outcome: LauncherIconApplyOutcome, revision: Long = state.value.revision) {
        mutableState.dispatch(LauncherIconAction.Completed(outcome, revision))
    }
}
