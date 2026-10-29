package br.gov.caixa.loterias.feature.retail.presentation.state

/** Metadados imutáveis; bytes e objetos de plataforma nunca pertencem ao estado da entrega. */
internal data class PrizeReceiptDeliveryState(
    val pendingFileName: String? = null,
    val saved: Map<String, String> = emptyMap(),
    val generation: Long = 0,
    val activeWrite: Long? = null,
    val recovering: Boolean = false,
    val recoveryFailure: Boolean = false,
    val completion: PrizeReceiptCompletion? = null,
    val viewerFailure: String? = null,
    val saveFailure: String? = null,
) {
    val saving: Boolean get() = activeWrite != null
    val idle: Boolean get() = !saving && !recovering
    val awaitingBytes: Boolean get() = pendingFileName == null && idle && completion == null
    val canReceiveDestination: Boolean get() = idle && !recoveryFailure && saveFailure == null
    val canRecover: Boolean get() = awaitingBytes && !recoveryFailure
}

internal data class PrizeReceiptCompletion(val claim: Long, val destination: String)

internal sealed interface PrizeReceiptDeliveryAction {
    data class ReceiptReceived(val fileName: String) : PrizeReceiptDeliveryAction
    data object RecoveryRequested : PrizeReceiptDeliveryAction
    data object RecoveryFailed : PrizeReceiptDeliveryAction
    data class WriteStarted(val claim: Long) : PrizeReceiptDeliveryAction
    data class WriteSucceeded(val claim: Long, val fileName: String, val destination: String) : PrizeReceiptDeliveryAction
    data class WriteFailed(val claim: Long, val destination: String) : PrizeReceiptDeliveryAction
    data class CompletionClaimed(val completion: PrizeReceiptCompletion) : PrizeReceiptDeliveryAction
    data class ViewerFailed(val destination: String) : PrizeReceiptDeliveryAction
    data object ViewerDismissed : PrizeReceiptDeliveryAction
    data object Cancelled : PrizeReceiptDeliveryAction
}
