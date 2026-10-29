package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.feature.tickets.presentation.route.deliverBetReceipt
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetReceipt
import java.io.File
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class BetReceiptDeliveryTest {
    private val receipt = BetReceipt("%PDF-1.7\ncontrolled bet".toByteArray(), "comprovante-aposta-41.pdf")

    @Test fun `write uses injected worker and viewer receives successful result on caller`() = runBlocking {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { io ->
            val caller = Thread.currentThread()
            var writeThread: Thread? = null
            val file = File.createTempFile("bet-receipt-", ".pdf")
            var opened: String? = null
            try {
                deliverBetReceipt(receipt, io, write = { actual ->
                    writeThread = Thread.currentThread()
                    assertEquals("comprovante-aposta-41.pdf", actual.fileName)
                    file.outputStream().use { it.write(actual.bytes) }
                    "content://app.fileprovider/receipts/comprovante-aposta-41.pdf"
                }, open = { uri ->
                    assertEquals(caller, Thread.currentThread())
                    assertArrayEquals(receipt.bytes, file.readBytes())
                    opened = uri
                    true
                }, onFailure = { fail("Successful receipt must not fail") })
                assertNotEquals(caller, writeThread)
                assertEquals("content://app.fileprovider/receipts/comprovante-aposta-41.pdf", opened)
            } finally {
                file.delete()
            }
        }
    }

    @Test fun `failed write never opens and reports failure once`() = runBlocking {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { io ->
            var failures = 0
            var opens = 0
            deliverBetReceipt<String>(
                receipt,
                io,
                write = { throw IOException("Full storage") },
                open = {
                    opens++
                    true
                },
                onFailure = { failures++ }
            )
            assertEquals(0, opens)
            assertEquals(1, failures)
        }
    }

    @Test fun `missing viewer retains existing failure reporting`() = runBlocking {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { io ->
            var failures = 0
            deliverBetReceipt(
                receipt,
                io,
                write = { "content://receipt/41.pdf" },
                open = { false },
                onFailure = { failures++ }
            )
            assertEquals(1, failures)
        }
    }

    @Test fun `write cancellation propagates without false viewer failure`() = runBlocking {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { io ->
            val cancelled = CancellationException("Owner disposed")
            var failures = 0
            var opens = 0
            try {
                deliverBetReceipt<String>(
                    receipt,
                    io,
                    write = { throw cancelled },
                    open = {
                        opens++
                        true
                    },
                    onFailure = { failures++ }
                )
                fail("Cancellation must propagate")
            } catch (expected: CancellationException) {
                assertEquals("Owner disposed", expected.message)
                assertTrue(generateSequence<Throwable>(expected) { it.cause }.any { it === cancelled })
            }
            assertEquals(0, opens)
            assertEquals(0, failures)
        }
    }

    @Test fun `owner cancellation while blocking write finishes cannot open or report failure`() = runBlocking {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { io ->
            val entered = CompletableDeferred<Unit>()
            val release = CountDownLatch(1)
            var failures = 0
            var opens = 0
            val job = launch {
                deliverBetReceipt(receipt, io, write = {
                    entered.complete(Unit)
                    check(release.await(5, java.util.concurrent.TimeUnit.SECONDS))
                    "content://receipt/41.pdf"
                }, open = {
                    opens++
                    true
                }, onFailure = { failures++ })
            }
            try {
                entered.await()
                job.cancel()
            } finally {
                release.countDown()
            }
            job.join()
            assertTrue(job.isCancelled)
            assertEquals(0, opens)
            assertEquals(0, failures)
        }
    }
}
