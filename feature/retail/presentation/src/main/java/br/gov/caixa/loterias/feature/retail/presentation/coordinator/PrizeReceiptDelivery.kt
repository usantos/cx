package br.gov.caixa.loterias.feature.retail.presentation.coordinator

import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeReceipt
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeReceiptCompletion
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeReceiptDeliveryAction
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeReceiptDeliveryState
import br.gov.caixa.loterias.feature.retail.presentation.state.reducePrizeReceiptDelivery
import java.io.Closeable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** Retém somente bytes e trabalho da entrega; o callback de escrita pertence à borda de plataforma. */
internal class PrizeReceiptDelivery(private val scope: CoroutineScope, private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) :
    Closeable {
    private val store = reduxStateStore(PrizeReceiptDeliveryState(), ::reducePrizeReceiptDelivery)
    val state = store.state
    private var pending: PrizeReceipt? = null
    private var writeJob: Job? = null
    private var closed = false

    @Synchronized
    fun accept(receipt: PrizeReceipt): Boolean {
        if (closed || state.value.completion != null) return false
        if (state.value.saving || pending != null) return false
        pending = receipt.copy(bytes = receipt.bytes.copyOf())
        store.dispatch(PrizeReceiptDeliveryAction.ReceiptReceived(receipt.fileName))
        return true
    }

    @Synchronized
    fun beginRecovery(): Boolean {
        if (closed || !state.value.awaitingBytes) return false
        store.dispatch(PrizeReceiptDeliveryAction.RecoveryRequested)
        return true
    }

    @Synchronized
    fun recoveryFailed() {
        if (!closed) store.dispatch(PrizeReceiptDeliveryAction.RecoveryFailed)
    }

    @Synchronized
    fun save(destination: String, write: (ByteArray) -> Unit) {
        val receipt = pending ?: return
        if (closed || state.value.saving) return
        val claim = state.value.generation + 1
        store.dispatch(PrizeReceiptDeliveryAction.WriteStarted(claim))
        writeJob = scope.launch {
            try {
                savePrizeReceipt(
                    receipt,
                    ioDispatcher,
                    write,
                    onSaved = { saved(claim, receipt.fileName, destination) },
                    onFailure = { failed(claim, destination) }
                )
            } catch (cancelled: CancellationException) {
                cancelWrite(claim)
                throw cancelled
            }
        }
    }

    @Synchronized
    private fun saved(claim: Long, fileName: String, destination: String) {
        if (closed || state.value.activeWrite != claim) return
        pending = null
        store.dispatch(PrizeReceiptDeliveryAction.WriteSucceeded(claim, fileName, destination))
    }

    @Synchronized
    private fun cancelWrite(claim: Long) {
        if (!closed && state.value.activeWrite == claim) {
            pending = null
            store.dispatch(PrizeReceiptDeliveryAction.Cancelled)
        }
    }

    @Synchronized
    private fun failed(claim: Long, destination: String) {
        if (!closed) store.dispatch(PrizeReceiptDeliveryAction.WriteFailed(claim, destination))
    }

    @Synchronized
    fun claimCompletion(completion: PrizeReceiptCompletion): Boolean {
        if (closed || state.value.completion != completion) return false
        store.dispatch(PrizeReceiptDeliveryAction.CompletionClaimed(completion))
        return true
    }

    fun viewerFailed(destination: String) = store.dispatch(PrizeReceiptDeliveryAction.ViewerFailed(destination))

    fun dismissViewerFailure() = store.dispatch(PrizeReceiptDeliveryAction.ViewerDismissed)

    @Synchronized
    fun cancel() {
        store.dispatch(PrizeReceiptDeliveryAction.Cancelled)
        pending = null
        writeJob?.cancel()
        writeJob = null
    }

    @Synchronized
    override fun close() {
        closed = true
        cancel()
        store.close()
    }
}
