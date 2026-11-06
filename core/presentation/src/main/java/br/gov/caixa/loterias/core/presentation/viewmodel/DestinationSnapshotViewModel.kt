package br.gov.caixa.loterias.core.presentation.viewmodel

import androidx.lifecycle.ViewModel
import br.gov.caixa.loterias.core.presentation.state.DestinationSnapshotAction
import br.gov.caixa.loterias.core.presentation.state.DestinationSnapshotState
import br.gov.caixa.loterias.core.presentation.state.reduceDestinationSnapshot
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Um destino detém este retrato somente para leitura; requisições nunca publicam para um novo responsável. */
class DestinationSnapshotViewModel<T>(private val load: suspend () -> T?, dispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {
    private val requests = CoroutineScope(SupervisorJob() + dispatcher)
    private val store = reduxStateStore<DestinationSnapshotState<T>, DestinationSnapshotAction<T>>(
        DestinationSnapshotState(),
        ::reduceDestinationSnapshot
    )
    private var running: Job? = null
    private var closed = false
    val state = store.state.map { it.value }.stateIn(requests, SharingStarted.Eagerly, null)

    init {
        addCloseable {
            synchronized(store) {
                closed = true
                store.close()
                requests.cancel()
            }
        }
    }

    fun refresh() {
        val task = synchronized(store) {
            if (closed) return
            val request = store.value.request + 1
            store.dispatch(DestinationSnapshotAction.Requested(request))
            running?.cancel()
            requests.launch(start = CoroutineStart.LAZY) {
                val value = try {
                    load()
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    null
                }
                store.dispatch(DestinationSnapshotAction.Completed(request, value))
            }.also { running = it }
        }
        task.start()
    }
}
