package br.gov.caixa.loterias.core.presentation.effect

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** Cancela somente leituras pendentes; resultados de uma tela anterior nunca são publicados. */
class ForegroundReadOperation<T>(
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
    private val onCancelled: () -> Unit,
) {
    private val lock = Any()
    private var active = true
    private var pending: Job? = null

    fun setActive(value: Boolean) = synchronized(lock) {
        if (active == value) return@synchronized
        active = value
        if (!active) {
            val previous = pending
            pending = null
            previous?.cancel()
            if (previous != null) onCancelled()
        }
    }

    fun launch(allowed: () -> Boolean, onStarted: () -> Unit, read: suspend () -> T, onResult: (T) -> Unit) = synchronized(lock) {
        if (!active || pending != null || !scope.isActive || !allowed()) return@synchronized
        onStarted()
        val request = scope.launch(dispatcher, start = CoroutineStart.LAZY) {
            val identity = currentCoroutineContext()[Job]
            try {
                val result = read()
                currentCoroutineContext().ensureActive()
                synchronized(lock) {
                    if (active && pending === identity) {
                        // A leitura terminou. Efeitos posteriores conhecidos não são cancelados pelo background.
                        pending = null
                        onResult(result)
                    }
                }
            } finally {
                synchronized(lock) {
                    if (pending === identity) {
                        pending = null
                        onCancelled()
                    }
                }
            }
        }
        pending = request
        request.start()
        Unit
    }
}
