package br.gov.caixa.loterias.feature.account.presentation.state

sealed interface SessionRecoveryAction {
    data object RecoveryStarted : SessionRecoveryAction
    data class Recovered(val acknowledgementRequired: Boolean) : SessionRecoveryAction
    data object Failed : SessionRecoveryAction
    data object Acknowledged : SessionRecoveryAction
}

fun reduceSessionRecoveryState(state: SessionRecoveryState, action: SessionRecoveryAction): SessionRecoveryState = when (action) {
    SessionRecoveryAction.RecoveryStarted -> SessionRecoveryState.CHECKING

    is SessionRecoveryAction.Recovered -> if (action.acknowledgementRequired) {
        SessionRecoveryState.AWAITING_ACKNOWLEDGMENT
    } else {
        SessionRecoveryState.READY
    }

    SessionRecoveryAction.Failed -> SessionRecoveryState.FAILED

    SessionRecoveryAction.Acknowledged -> if (state == SessionRecoveryState.AWAITING_ACKNOWLEDGMENT) SessionRecoveryState.READY else state
}
