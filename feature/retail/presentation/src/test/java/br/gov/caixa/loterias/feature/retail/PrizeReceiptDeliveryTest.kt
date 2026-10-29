package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.feature.retail.presentation.coordinator.PrizeReceiptDelivery
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeReceipt
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PrizeReceiptDeliveryTest {
    private val receipt = PrizeReceipt("original PDF".toByteArray(), "receipt.pdf")

    @Test fun `failed write retries original bytes and completion is claimed once`() = runBlocking {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { io ->
            val delivery = PrizeReceiptDelivery(this, io)
            assertTrue(delivery.accept(receipt))
            delivery.save("destination") { throw IOException("full") }
            while (delivery.state.value.saving) yield()
            assertEquals("destination", delivery.state.value.saveFailure)
            val written = CompletableDeferred<ByteArray>()
            delivery.save("destination") { written.complete(it.copyOf()) }
            while (delivery.state.value.saving) yield()
            assertArrayEquals(receipt.bytes, written.await())
            val completion = delivery.state.value.completion!!
            assertTrue(delivery.claimCompletion(completion))
            assertFalse(delivery.claimCompletion(completion))
            assertEquals("destination", delivery.state.value.saved[receipt.fileName])
            delivery.close()
        }
    }

    @Test fun `duplicate destination results cannot write the pending receipt twice`() = runBlocking {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { io ->
            val delivery = PrizeReceiptDelivery(this, io)
            val entered = CompletableDeferred<Unit>()
            val release = CountDownLatch(1)
            var writes = 0
            delivery.accept(receipt)
            try {
                delivery.save("destination") {
                    writes++
                    entered.complete(Unit)
                    check(release.await(PROVIDER_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                }
                entered.await()
                delivery.save("duplicate") { error("Duplicate write") }
            } finally {
                release.countDown()
            }
            while (delivery.state.value.saving) yield()
            assertEquals(1, writes)
            assertEquals("destination", delivery.state.value.saved[receipt.fileName])
            delivery.close()
        }
    }

    @Test fun `cancelled owner cannot publish completion after a blocking provider returns`() = runBlocking {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { io ->
            val owner = CoroutineScope(coroutineContext + SupervisorJob())
            val delivery = PrizeReceiptDelivery(owner, io)
            val entered = CompletableDeferred<Unit>()
            val release = CountDownLatch(1)
            val returned = CompletableDeferred<Unit>()
            delivery.accept(receipt)
            try {
                delivery.save("destination") {
                    entered.complete(Unit)
                    check(release.await(PROVIDER_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                    returned.complete(Unit)
                }
                entered.await()
                owner.cancel()
                delivery.close()
            } finally {
                release.countDown()
            }
            returned.await()
            yield()
            assertNull(delivery.state.value.completion)
            assertTrue(delivery.state.value.saved.isEmpty())
        }
    }

    @Test fun `provider cancellation releases claim without completion or failure`() = runBlocking {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { io ->
            val delivery = PrizeReceiptDelivery(this, io)
            delivery.accept(receipt)
            delivery.save("destination") { throw CancellationException("cancelled provider") }
            while (delivery.state.value.saving) yield()
            assertNull(delivery.state.value.completion)
            assertNull(delivery.state.value.saveFailure)
            assertNull(delivery.state.value.pendingFileName)
            assertTrue(delivery.state.value.saved.isEmpty())
            delivery.close()
        }
    }

    @Test fun `cancel removes retry bytes and recovery failure requires an explicit retry`() = runBlocking {
        val delivery = PrizeReceiptDelivery(this)
        assertTrue(delivery.beginRecovery())
        delivery.recoveryFailed()
        assertTrue(delivery.state.value.recoveryFailure)
        assertTrue(delivery.beginRecovery())
        assertFalse(delivery.beginRecovery())
        assertTrue(delivery.accept(receipt))
        delivery.cancel()
        assertNull(delivery.state.value.pendingFileName)
        delivery.save("destination") { error("Cancelled receipt must not be written") }
        assertFalse(delivery.state.value.saving)
        delivery.close()
    }
}

private const val PROVIDER_TIMEOUT_SECONDS = 5L
