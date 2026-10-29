package br.gov.caixa.loterias.core.designsystem

import android.graphics.Bitmap
import android.os.Looper
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.junit4.createComposeRule
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAnimationImage
import br.gov.caixa.loterias.core.designsystem.components.prepareAnimationImages
import com.airbnb.lottie.LottieCompositionFactory
import java.util.concurrent.atomic.AtomicBoolean
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class AnimationPreparationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun imageDecodingAndCroppingDoNotBlockTheUiThread() {
        val composition = requireNotNull(
            LottieCompositionFactory.fromJsonStringSync(
                """{"v":"5.7.0","fr":30,"ip":0,"op":60,"w":64,"h":64,
                "assets":[{"id":"image","w":64,"h":64,"u":"","p":"fixture.png"}],"layers":[]}""",
                null
            ).value
        )
        val finished = AtomicBoolean(false)
        val decodedOnMain = AtomicBoolean(true)
        var count = 0
        compose.setContent {
            LaunchedEffect(Unit) {
                val images = prepareAnimationImages(
                    composition,
                    listOf(CaixaDSAnimationImage("image", "Artwork", 1))
                ) {
                    decodedOnMain.set(Looper.myLooper() == Looper.getMainLooper())
                    Bitmap.createBitmap(TEST_BITMAP_SIZE, TEST_BITMAP_SIZE, Bitmap.Config.ARGB_8888)
                }
                count = images.size
                finished.set(true)
            }
        }
        compose.waitUntil(ANIMATION_TIMEOUT_MILLIS) { finished.get() }
        assertFalse("Image preparation must run outside the UI thread", decodedOnMain.get())
        assertEquals(1, count)
    }
}

private const val TEST_BITMAP_SIZE = 64
private const val ANIMATION_TIMEOUT_MILLIS = 5_000L
