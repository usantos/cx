package br.gov.caixa.loterias.feature.onboarding.presentation.state

fun reduceStartupRecoveryNavigationState(
    state: StartupRecoveryNavigationState,
    action: StartupRecoveryNavigationAction,
): StartupRecoveryNavigationState = when (action) {
    is StartupRecoveryNavigationAction.Started -> state.copy(loading = true, storageFailed = false, requestId = action.requestId)

    is StartupRecoveryNavigationAction.Recovered -> if (action.requestId == state.requestId) {
        state.copy(loading = false, target = action.target)
    } else {
        state
    }

    is StartupRecoveryNavigationAction.Failed -> if (action.requestId == state.requestId) {
        state.copy(loading = false, storageFailed = true)
    } else {
        state
    }

    is StartupRecoveryNavigationAction.Deactivated -> state.copy(loading = false, target = null, requestId = action.requestId)

    StartupRecoveryNavigationAction.TargetAcknowledged -> state.copy(target = null)
}
