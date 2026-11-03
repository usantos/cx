package br.gov.caixa.loterias.feature.tickets.presentation.route

import br.gov.caixa.loterias.feature.tickets.presentation.state.BetReceipt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

internal suspend fun <T : Any> deliverBetReceipt(
    receipt: BetReceipt,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    write: (BetReceipt) -> T,
    open: (T) -> Boolean,
    onFailure: () -> Unit,
) {
    val target = try {
        withContext(ioDispatcher) { write(receipt) }
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: Exception) {
        currentCoroutineContext().ensureActive()
        onFailure()
        return
    }
    currentCoroutineContext().ensureActive()
    if (!open(target)) onFailure()
}
