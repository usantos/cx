package br.gov.caixa.loterias.feature.betting.presentation.state

internal fun reduceRapidConfigurationNotices(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction,
): RapidConfigurationUiState = when (action) {
    is RapidConfigurationAction.ServiceFailureReceived -> reduceRapidConfigurationServiceFailureReceived(state, action)
    is RapidConfigurationAction.ServiceNoticeDismissed -> reduceRapidConfigurationServiceNoticeDismissed(state, action)
    is RapidConfigurationAction.ServiceNoticeAccepted -> reduceRapidConfigurationServiceNoticeAccepted(state, action)
    RapidConfigurationAction.ServiceRedirectConsumed -> reduceRapidConfigurationServiceRedirectConsumed(state)
    else -> state
}

private fun reduceRapidConfigurationServiceFailureReceived(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.ServiceFailureReceived,
): RapidConfigurationUiState = run {
    val it = state
    it.copy(loading = false, serviceFailure = action.presentation)
}

private fun reduceRapidConfigurationServiceNoticeDismissed(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.ServiceNoticeDismissed,
): RapidConfigurationUiState = run {
    val it = state
    it.copy(serviceFailure = it.serviceFailure.dismiss(action.expected))
}

private fun reduceRapidConfigurationServiceNoticeAccepted(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.ServiceNoticeAccepted,
): RapidConfigurationUiState = run {
    val it = state
    if (it.serviceFailure.notice !== action.expected) it else it.copy(serviceFailure = action.presentation)
}

private fun reduceRapidConfigurationServiceRedirectConsumed(state: RapidConfigurationUiState): RapidConfigurationUiState = run {
    val it = state
    it.copy(serviceFailure = it.serviceFailure.copy(redirect = null))
}
