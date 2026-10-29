package br.gov.caixa.loterias.core.common

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import br.gov.caixa.loterias.core.common.event.SingleConsumerEventQueue

@OptIn(ExperimentalCoroutinesApi::class)
class SingleConsumerEventQueueTest {
    @Test
    fun eventsWaitForTheCollectorInOrderAndAreNotReplayed() = runTest {
        val queue = SingleConsumerEventQueue<Int>()
        queue.emit(1)
        queue.emit(2)
        assertEquals(listOf(1, 2), queue.events.take(2).toList())
        assertNull(withTimeoutOrNull(100) { queue.events.first() })
    }

    @Test
    fun returningCollectorReceivesOnlyEventsEmittedWhileItWasAbsent() = runTest {
        val queue = SingleConsumerEventQueue<String>()
        val received = mutableListOf<String>()
        val collector = launch { queue.events.collect { received += it } }
        runCurrent()
        queue.emit("first")
        runCurrent()
        collector.cancelAndJoin()
        queue.emit("second")
        assertEquals(listOf("first"), received)
        assertEquals("second", queue.events.first())
        assertNull(withTimeoutOrNull(100) { queue.events.first() })
    }

    @Test
    fun cancellingASuspendedSenderDoesNotEnqueueItsEvent() = runTest {
        val queue = SingleConsumerEventQueue<Int>()
        // Excede o buffer padrão limitado sem depender da capacidade configurada.
        var attempted = -1
        var completed = -1
        val producer = launch {
            repeat(10_000) { value ->
                attempted = value
                queue.emit(value)
                completed = value
            }
        }
        runCurrent()
        assertFalse(producer.isCompleted)
        producer.cancelAndJoin()
        val retained = mutableListOf<Int>()
        while (true) {
            val event = withTimeoutOrNull(100) { queue.events.first() } ?: break
            retained += event
        }
        assertEquals(completed + 1, attempted)
        assertEquals((0..completed).toList(), retained)
        assertFalse(retained.isEmpty())
        assertFalse(retained.contains(attempted))
    }
}
