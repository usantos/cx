package br.gov.caixa.loterias.feature.onboarding.presentation.state

sealed interface QueueGateAction {
    data object Began : QueueGateAction
    data object Reset : QueueGateAction
    data object Passed : QueueGateAction
    data object NoInternet : QueueGateAction
    data object Failed : QueueGateAction
    data object Retried : QueueGateAction
}

fun reduceQueueGateState(state: QueueGateState, action: QueueGateAction): QueueGateState = when (action) {
    QueueGateAction.Began -> state.copy(status = QueueStatus.WAITING)
    QueueGateAction.Reset -> QueueGateState()
    QueueGateAction.Passed -> state.copy(status = QueueStatus.PASSED)
    QueueGateAction.NoInternet -> state.copy(status = QueueStatus.NO_INTERNET)
    QueueGateAction.Failed -> state.copy(status = QueueStatus.ERROR)
    QueueGateAction.Retried -> QueueGateState(attempt = state.attempt + 1)
}
