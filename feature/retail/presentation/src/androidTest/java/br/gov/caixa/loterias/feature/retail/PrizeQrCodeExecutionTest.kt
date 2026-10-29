package br.gov.caixa.loterias.feature.retail

import android.os.Looper
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.retail.presentation.mapper.PrizeQrCodeGenerator
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizeQrCodeImage
import br.gov.caixa.loterias.feature.retail.presentation.components.renderPrizeQrCode

class PrizeQrCodeExecutionTest {
    @get:Rule val compose = createComposeRule()

    @Test fun bitmapGenerationCalledFromMainUsesAWorkerAndPreservesDecodedContent() {
        val workerThread = AtomicReference<Thread>()
        val generator = PrizeQrCodeGenerator(encode = { value, size ->
            workerThread.set(Thread.currentThread())
            QRCodeWriter().encode(value, BarcodeFormat.QR_CODE, size, size)
        })
        val result = AtomicReference<ImageBitmap>()
        val callerThread = AtomicReference<Thread>()
        compose.setContent {
            LaunchedEffect(Unit) {
                callerThread.set(Thread.currentThread())
                result.set(renderPrizeQrCode("controlled-prize-9002", generator))
            }
        }
        compose.waitUntil(5_000) { result.get() != null }
        val image = result.get()
        assertSame(Looper.getMainLooper().thread, callerThread.get())
        assertNotSame(Looper.getMainLooper().thread, workerThread.get())
        assertEquals(640, image.width)
        assertEquals(640, image.height)
        val pixels = IntArray(640 * 640)
        image.asAndroidBitmap().getPixels(pixels, 0, 640, 0, 0, 640, 640)
        val decoded = MultiFormatReader().decode(BinaryBitmap(HybridBinarizer(RGBLuminanceSource(640, 640, pixels))))
        assertEquals("controlled-prize-9002", decoded.text)
    }

    @Test fun obsoleteGenerationCannotReplaceTheCurrentQrAfterAValueChange() {
        val value = mutableStateOf("old")
        val entered = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        val obsoleteFinished = CompletableDeferred<Unit>()
        val oldImage = Bitmap.createBitmap(640, 640, Bitmap.Config.ARGB_8888).apply { eraseColor(android.graphics.Color.RED) }.asImageBitmap()
        val newImage = Bitmap.createBitmap(640, 640, Bitmap.Config.ARGB_8888).apply { eraseColor(android.graphics.Color.BLUE) }.asImageBitmap()
        compose.setContent {
            PrizeQrCodeImage(value.value) { code ->
                if (code == "old") {
                    entered.complete(Unit)
                    // Simula um codificador da plataforma que termina mesmo após o cancelamento de seu titular.
                    withContext(NonCancellable) { release.await() }
                    obsoleteFinished.complete(Unit)
                    oldImage
                } else newImage
            }
        }
        try {
            compose.waitUntil(5_000) { entered.isCompleted }
            compose.runOnIdle { value.value = "new" }
            compose.onNodeWithContentDescription("QR Code de resgate").assertExists()
            assertEquals(android.graphics.Color.BLUE, centerColor())
            release.complete(Unit)
            compose.waitUntil(5_000) { obsoleteFinished.isCompleted }
            compose.waitForIdle()
            assertEquals(android.graphics.Color.BLUE, centerColor())
        } finally {
            release.complete(Unit)
        }
    }

    private fun centerColor(): Int {
        val bitmap = compose.onNodeWithContentDescription("QR Code de resgate").captureToImage().asAndroidBitmap()
        return bitmap.getPixel(bitmap.width / 2, bitmap.height / 2)
    }

    @Test fun composedQrRetainsItsSizeAndAccessibilityAfterValueChanges() {
        val value = mutableStateOf("first-controlled-code")
        compose.setContent { Box(Modifier.size(300.dp)) { PrizeQrCodeImage(value.value) } }
        compose.waitUntil(5_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasContentDescription("QR Code de resgate")).fetchSemanticsNodes().size == 1
        }
        compose.onNodeWithContentDescription("QR Code de resgate").assertWidthIsEqualTo(200.dp).assertHeightIsEqualTo(200.dp)
        compose.runOnIdle { value.value = "second-controlled-code" }
        compose.waitUntil(5_000) {
            compose.onAllNodes(androidx.compose.ui.test.hasContentDescription("QR Code de resgate")).fetchSemanticsNodes().size == 1
        }
        compose.onNodeWithContentDescription("QR Code de resgate").assertWidthIsEqualTo(200.dp).assertHeightIsEqualTo(200.dp)
    }
}
