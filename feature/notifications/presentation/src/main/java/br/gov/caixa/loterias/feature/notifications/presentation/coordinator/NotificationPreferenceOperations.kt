package br.gov.caixa.loterias.feature.notifications.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class NotificationPreferenceOperations {
    private val lock = Any()
    private var tail: Job? = null

    fun enqueue(scope: CoroutineScope, dispatchers: CoroutineDispatchers, operation: suspend () -> Unit): Job = synchronized(lock) {
        val previous = tail
        scope.launch(start = CoroutineStart.LAZY) {
            previous?.join()
            withContext(dispatchers.io) { operation() }
        }.also {
            tail = it
            it.start()
        }
    }
}
