package br.gov.caixa.loterias.feature.account.presentation.state

import br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment

sealed interface EnvironmentAction {
    data object SelectionApplied : EnvironmentAction
    data class Selected(val environment: TestEnvironment) : EnvironmentAction
    data object Unbound : EnvironmentAction
    data object SaveStarted : EnvironmentAction
    data class Saved(val persisted: Boolean) : EnvironmentAction
    data object SaveFailed : EnvironmentAction
    data object SaveFinished : EnvironmentAction
}

fun reduceEnvironmentState(state: EnvironmentUiState, action: EnvironmentAction): EnvironmentUiState = when (action) {
    EnvironmentAction.SelectionApplied -> EnvironmentUiState()
    is EnvironmentAction.Selected -> EnvironmentUiState(pending = action.environment)
    EnvironmentAction.Unbound -> state.copy(saving = false)
    EnvironmentAction.SaveStarted -> state.copy(saving = true, failed = false)
    is EnvironmentAction.Saved -> state.copy(persisted = action.persisted)
    EnvironmentAction.SaveFailed -> state.copy(failed = true)
    EnvironmentAction.SaveFinished -> state.copy(saving = false)
}
