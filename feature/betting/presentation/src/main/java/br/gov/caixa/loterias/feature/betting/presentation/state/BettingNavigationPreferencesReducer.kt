package br.gov.caixa.loterias.feature.betting.presentation.state

fun reduceBettingNavigationPreferencesState(
    state: BettingNavigationPreferencesState,
    action: BettingNavigationPreferencesAction,
): BettingNavigationPreferencesState = when (action) {
    is BettingNavigationPreferencesAction.Started -> state.copy(
        loading = true,
        shakeEnabled = false,
        failure = null,
        requestId = action.requestId
    )

    is BettingNavigationPreferencesAction.TutorialLoaded -> if (action.requestId == state.requestId) {
        state.copy(loading = false, tutorialDismissed = action.dismissed)
    } else {
        state
    }

    is BettingNavigationPreferencesAction.ShakeLoaded -> if (action.requestId == state.requestId) {
        state.copy(loading = false, shakeEnabled = action.enabled)
    } else {
        state
    }

    is BettingNavigationPreferencesAction.Failed -> if (action.requestId == state.requestId) {
        state.copy(loading = false, failure = action.failure)
    } else {
        state
    }

    is BettingNavigationPreferencesAction.Completed -> if (action.requestId == state.requestId) {
        state.copy(loading = false, rapidCompletion = action.completion)
    } else {
        state
    }

    is BettingNavigationPreferencesAction.Deactivated -> state.copy(
        loading = false,
        rapidCompletion = null,
        requestId = action.requestId
    )

    BettingNavigationPreferencesAction.CompletionAcknowledged -> state.copy(rapidCompletion = null)
}
