package br.gov.caixa.loterias.feature.support.presentation.state

internal sealed interface SupportMutation {
    data class LoadStarted(val requestId: Long) : SupportMutation
    data class LoadCompleted(val requestId: Long, val action: SupportAction) : SupportMutation
    data class LoadFinished(val requestId: Long) : SupportMutation
    data class ParametersReceived(val requestId: Long, val value: GameParametersLoaded) : SupportMutation
    data class Action(val value: SupportAction) : SupportMutation
    data class GameHelpLoaded(val html: String) : SupportMutation
    data class GameParametersLoaded(val contestNumber: Int?, val drawDate: String, val closingDateTime: String) : SupportMutation
    data class AcceptanceFailed(val message: String) : SupportMutation
}
