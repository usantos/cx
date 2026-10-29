package br.gov.caixa.loterias.feature.onboarding.presentation.state

sealed interface StartupAction {
    data object ValidationStarted : StartupAction
    data object Unavailable : StartupAction
    data object UpdateRequired : StartupAction
    data object Ready : StartupAction
}

fun reduceStartupState(ignored: StartupState, action: StartupAction): StartupState = when (action) {
    StartupAction.ValidationStarted -> StartupState.CHECKING
    StartupAction.Unavailable -> StartupState.UNAVAILABLE
    StartupAction.UpdateRequired -> StartupState.UPDATE_REQUIRED
    StartupAction.Ready -> StartupState.READY
}
