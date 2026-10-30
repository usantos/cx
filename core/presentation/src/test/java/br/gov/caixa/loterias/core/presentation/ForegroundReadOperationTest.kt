package br.gov.caixa.loterias.core.presentation

import br.gov.caixa.loterias.core.presentation.effect.ForegroundReadOperation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ForegroundReadOperationTest {
    @Test fun lateCancelledReadCannotStopOrPublishOverTheResumedRequest() = runTest {
        var cancelled = 0
        var started = 0
        val received = mutableListOf<String>()
        val operation = ForegroundReadOperation<String>(this, StandardTestDispatcher(testScheduler)) { cancelled++ }
        val old = CompletableDeferred<String>()
        val current = CompletableDeferred<String>()
        operation.launch({ true }, { started++ }, { withContext(NonCancellable) { old.await() } }, received::add)
        runCurrent()
        operation.launch({ true }, { started++ }, { "duplicado" }, received::add)
        assertEquals(1, started)
        operation.setActive(false)
        operation.setActive(false)
        operation.launch({ true }, { started++ }, { "background" }, received::add)
        assertEquals(1, started)
        operation.setActive(true)
        operation.launch({ true }, { started++ }, { current.await() }, received::add)
        runCurrent()
        old.complete("antigo")
        runCurrent()
        assertEquals(emptyList<String>(), received)
        assertEquals(1, cancelled)
        current.complete("atual")
        advanceUntilIdle()
        assertEquals(listOf("atual"), received)
        assertEquals(2, started)
        operation.setActive(false)
        assertEquals(1, cancelled)
    }

    @Test fun blockedDisposedAndCancelledReadsReleaseTheUiWithoutInventingConnectionErrors() = runTest {
        var cancelled = 0
        var started = 0
        val dispatcher = StandardTestDispatcher(testScheduler)
        val operation = ForegroundReadOperation<String>(this, dispatcher) { cancelled++ }
        operation.launch({ false }, { started++ }, { "bloqueado" }, { error("Não deve publicar") })
        assertEquals(0, started)
        operation.launch({ true }, { started++ }, { throw CancellationException("Leitura cancelada") }, { error("Não deve publicar") })
        advanceUntilIdle()
        assertEquals(1, cancelled)
        val deadScope = CoroutineScope(Job().apply { cancel() } + dispatcher)
        val dead = ForegroundReadOperation<String>(deadScope, dispatcher) { cancelled++ }
        dead.launch({ true }, { started++ }, { "encerrado" }, { error("Não deve publicar") })
        assertEquals(1, started)
    }
}
