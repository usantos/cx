package br.gov.caixa.loterias.feature.retail.presentation.coordinator

import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeReceipt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/** Executa o trabalho bloqueante do provedor em IO; somente o titular ativo pode publicar o recibo salvo. */
internal suspend fun savePrizeReceipt(
    receipt: PrizeReceipt,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    write: (ByteArray) -> Unit,
    onSaved: () -> Unit,
    onFailure: () -> Unit,
) {
    try {
        withContext(ioDispatcher) { write(receipt.bytes) }
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: Exception) {
        currentCoroutineContext().ensureActive()
        onFailure()
        return
    }
    currentCoroutineContext().ensureActive()
    onSaved()
}
