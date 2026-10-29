package br.gov.caixa.loterias.feature.account.presentation.state

import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionLaunch

sealed interface LogoutAction {
    data object SignOutStarted : LogoutAction
    data class ServerRejected(val failure: ServiceFailure) : LogoutAction
    data object LocalLogoutFailed : LogoutAction
    data object LocalCleanupStarted : LogoutAction
    data object LocalCleanupFailed : LogoutAction
    data object SimulatedLogoutCompleted : LogoutAction
    data class BrowserPrepared(val launch: EndSessionLaunch) : LogoutAction
    data object BrowserUnconfirmed : LogoutAction
    data object BrowserLaunchTaken : LogoutAction
    data class BrowserCompleted(val notice: LogoutNotice?) : LogoutAction
    data object NoticeDismissed : LogoutAction
}

fun reduceLogoutState(state: LogoutUiState, action: LogoutAction): LogoutUiState = when (action) {
    LogoutAction.SignOutStarted -> LogoutUiState(signingOut = true)

    is LogoutAction.ServerRejected -> LogoutUiState(
        notice = LogoutNotice.SERVER_FAILURE,
        serverFailure = action.failure,
        localSessionEnded = state.localSessionEnded
    )

    LogoutAction.LocalLogoutFailed -> LogoutUiState(notice = LogoutNotice.LOCAL_FAILURE, localSessionEnded = state.localSessionEnded)

    LogoutAction.LocalCleanupStarted -> LogoutUiState(clearingLocalData = true, localSessionEnded = true)

    LogoutAction.LocalCleanupFailed -> LogoutUiState(notice = LogoutNotice.LOCAL_DATA_FAILURE, localSessionEnded = true)

    LogoutAction.SimulatedLogoutCompleted -> LogoutUiState(notice = LogoutNotice.SIMULATED, localSessionEnded = true)

    is LogoutAction.BrowserPrepared -> LogoutUiState(launch = action.launch, localSessionEnded = state.localSessionEnded)

    LogoutAction.BrowserUnconfirmed -> LogoutUiState(notice = LogoutNotice.UNCONFIRMED, localSessionEnded = state.localSessionEnded)

    LogoutAction.BrowserLaunchTaken -> LogoutUiState(waitingForBrowser = true, localSessionEnded = state.localSessionEnded)

    is LogoutAction.BrowserCompleted -> LogoutUiState(notice = action.notice, localSessionEnded = state.localSessionEnded)

    LogoutAction.NoticeDismissed -> LogoutUiState()
}
