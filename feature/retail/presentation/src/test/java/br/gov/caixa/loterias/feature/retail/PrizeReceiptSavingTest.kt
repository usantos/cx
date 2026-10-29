package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.feature.retail.presentation.coordinator.savePrizeReceipt
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeReceipt
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class PrizeReceiptSavingTest {
    private val receipt = PrizeReceipt("%PDF-1.7\ncontrolled prize".toByteArray(), "comprovante-premio-9002.pdf")

    @Test fun `writes original bytes on injected worker then caches and opens on caller`() = runBlocking {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { io ->
            val file = File.createTempFile("prize-receipt-", ".pdf")
            val caller = Thread.currentThread()
            var writeThread: Thread? = null
            var completionThread: Thread? = null
            val cached = mutableMapOf<String, String>()
            val opened = mutableListOf<String>()
            try {
                savePrizeReceipt(receipt, io, write = { bytes ->
                    writeThread = Thread.currentThread()
                    file.outputStream().use { it.write(bytes) }
                }, onSaved = {
                    completionThread = Thread.currentThread()
                    assertArrayEquals(receipt.bytes, file.readBytes())
                    cached[receipt.fileName] = "content://receipt/saved.pdf"
                    opened += "content://receipt/saved.pdf"
                }, onFailure = { fail("Successful write must not fail") })
                assertNotEquals(caller, writeThread)
                assertEquals(caller, completionThread)
                assertEquals(mapOf("comprovante-premio-9002.pdf" to "content://receipt/saved.pdf"), cached)
                assertEquals(listOf("content://receipt/saved.pdf"), opened)
            } finally {
                file.delete()
            }
        }
    }

    @Test fun `failed write reports once without caching or opening incomplete receipt`() = runBlocking {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { io ->
            var failures = 0
            var saved = false
            savePrizeReceipt(
                receipt,
                io,
                write = { throw IOException("Full storage") },
                onSaved = { saved = true },
                onFailure = { failures++ }
            )
            assertFalse(saved)
            assertEquals(1, failures)
        }
    }

    @Test fun `write cancellation propagates without failure or success`() = runBlocking {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { io ->
            val cancelled = CancellationException("Provider cancelled")
            var failures = 0
            var saved = false
            try {
                savePrizeReceipt(
                    receipt,
                    io,
                    write = { throw cancelled },
                    onSaved = { saved = true },
                    onFailure = { failures++ }
                )
                fail("Cancellation must propagate")
            } catch (expected: CancellationException) {
                assertEquals("Provider cancelled", expected.message)
                assertTrue(generateSequence<Throwable>(expected) { it.cause }.any { it === cancelled })
            }
            assertFalse(saved)
            assertEquals(0, failures)
        }
    }

    @Test fun `disposing owner during blocking write prevents late cache open and failure`() = runBlocking {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { io ->
            val entered = CompletableDeferred<Unit>()
            val release = CountDownLatch(1)
            var saved = false
            var failures = 0
            val job = launch {
                savePrizeReceipt(receipt, io, write = {
                    entered.complete(Unit)
                    check(release.await(5, java.util.concurrent.TimeUnit.SECONDS))
                }, onSaved = { saved = true }, onFailure = { failures++ })
            }
            try {
                entered.await()
                job.cancel()
            } finally {
                release.countDown()
            }
            job.join()
            assertTrue(job.isCancelled)
            assertFalse(saved)
            assertEquals(0, failures)
        }
    }
}
