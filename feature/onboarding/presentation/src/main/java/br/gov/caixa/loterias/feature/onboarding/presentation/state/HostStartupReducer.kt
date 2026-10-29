package br.gov.caixa.loterias.feature.onboarding.presentation.state

sealed interface HostStartupAction {
    data object Reset : HostStartupAction
    data object SplashElapsed : HostStartupAction
    data object Retried : HostStartupAction
    data class SecurityPrepared(val ready: Boolean) : HostStartupAction
}

fun reduceHostStartupState(state: HostStartupState, action: HostStartupAction): HostStartupState = when (action) {
    HostStartupAction.Reset -> HostStartupState()
    HostStartupAction.SplashElapsed -> state.copy(minimumSplashElapsed = true)
    HostStartupAction.Retried -> state.copy(attempt = state.attempt + 1, securityFailed = false)
    is HostStartupAction.SecurityPrepared -> state.copy(securityReady = action.ready, securityFailed = !action.ready)
}
