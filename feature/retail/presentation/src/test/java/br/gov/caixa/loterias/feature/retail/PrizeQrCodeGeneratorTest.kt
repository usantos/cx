package br.gov.caixa.loterias.feature.retail

import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.BitMatrix
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.feature.retail.presentation.mapper.PrizeQrCodeGenerator

class PrizeQrCodeGeneratorTest {
    @Test fun `encoding and pixel generation run on the worker and preserve the legacy QR`() = runBlocking {
        val caller = Thread.currentThread()
        val encodingThread = AtomicReference<Thread>()
        val value = "controlled-redemption-9002"
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { worker ->
            val generator = PrizeQrCodeGenerator(worker) { text, size ->
                encodingThread.set(Thread.currentThread())
                QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
            }
            val actual = generator.generate(value)
            assertNotSame(caller, encodingThread.get())
            assertSame(caller, Thread.currentThread())
            assertEquals(640 * 640, actual.size)
            val original = QRCodeWriter().encode(value, BarcodeFormat.QR_CODE, 640, 640)
            for (y in 0 until 640) for (x in 0 until 640) {
                assertEquals(if (original[x, y]) 0xff000000.toInt() else 0xffffffff.toInt(), actual[y * 640 + x])
            }
            val decoded = MultiFormatReader().decode(BinaryBitmap(HybridBinarizer(RGBLuminanceSource(640, 640, actual))))
            assertEquals(value, decoded.text)
        }
    }

    @Test fun `production default encoder retains every legacy pixel`() = runBlocking {
        val value = "default-controlled-prize"
        val actual = PrizeQrCodeGenerator().generate(value)
        val legacy = QRCodeWriter().encode(value, BarcodeFormat.QR_CODE, 640, 640)
        val expected = IntArray(640 * 640) { index ->
            if (legacy[index % 640, index / 640]) 0xff000000.toInt() else 0xffffffff.toInt()
        }
        assertArrayEquals(expected, actual)
    }

    @Test fun `default generator leaves the calling thread`() = runBlocking {
        val caller = Thread.currentThread()
        val thread = AtomicReference<Thread>()
        PrizeQrCodeGenerator(encode = { _, size -> thread.set(Thread.currentThread()); BitMatrix(size) }).generate("qr")
        assertNotSame(caller, thread.get())
    }

    @Test fun `generation waits for its worker dispatcher without blocking the caller`() = runTest {
        val worker = StandardTestDispatcher(testScheduler)
        var encoded = false
        val generated = async { PrizeQrCodeGenerator(worker) { _, size -> encoded = true; BitMatrix(size) }.generate("qr") }
        assertFalse(encoded)
        assertFalse(generated.isCompleted)
        runCurrent()
        assertEquals(640 * 640, generated.await().size)
        assertTrue(encoded)
    }

    @Test fun `cancellation before the worker starts never encodes or publishes`() = runTest {
        val calls = AtomicInteger()
        var published = false
        val job = launch {
            PrizeQrCodeGenerator(StandardTestDispatcher(testScheduler)) { _, size -> calls.incrementAndGet(); BitMatrix(size) }.generate("qr")
            published = true
        }
        job.cancelAndJoin()
        runCurrent()
        assertEquals(0, calls.get())
        assertFalse(published)
    }

    @Test fun `cancellation during encoding propagates without publishing pixels`() = runTest {
        var published = false
        val job = launch {
            val ownJob = kotlinx.coroutines.currentCoroutineContext()[kotlinx.coroutines.Job]!!
            PrizeQrCodeGenerator(StandardTestDispatcher(testScheduler)) { _, size ->
                ownJob.cancel(CancellationException("obsolete QR"))
                BitMatrix(size)
            }.generate("old")
            published = true
        }
        job.join()
        assertTrue(job.isCancelled)
        assertFalse(published)
    }

    @Test fun `encoding errors propagate instead of displaying a fabricated QR`() = runTest {
        val expected = IllegalArgumentException("invalid QR")
        try {
            PrizeQrCodeGenerator(StandardTestDispatcher(testScheduler)) { _, _ -> throw expected }.generate("invalid")
            fail("Expected encoding failure")
        } catch (actual: IllegalArgumentException) {
            assertEquals(expected.message, actual.message)
        }
    }
}
