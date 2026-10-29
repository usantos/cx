package br.gov.caixa.loterias.feature.onboarding

import android.graphics.Bitmap
import android.view.View
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.LocalSplashAnimation
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.SplashAnimationHost
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.SplashScreen
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSStatusBar
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import com.airbnb.lottie.compose.LottieAnimatable
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashParityUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun loadingRepeatsTheMovingFramesWithoutTheLongFrozenTail() {
        lateinit var animation: LottieAnimatable
        compose.mainClock.autoAdvance = false
        compose.setContent {
            SplashAnimationHost {
                animation = LocalSplashAnimation.current!!
                SplashScreen()
            }
        }
        compose.waitUntil(5_000) {
            compose.mainClock.advanceTimeByFrame()
            animation.composition != null
        }
        compose.mainClock.advanceTimeBy(5_000)
        compose.runOnIdle {
            assertTrue("Loading must repeat before the static tail", animation.iteration > 1)
            assertTrue(
                "Playback must stay within the moving section",
                animation.progress * animation.composition!!.endFrame <= MOVING_SECTION_END_FRAME
            )
        }
    }

    @Test fun splashArtworkRemainsVisibleBehindTheStatusBar() {
        lateinit var animation: LottieAnimatable
        compose.mainClock.autoAdvance = false
        compose.setContent {
            CaixaDSStatusBar {
                SplashAnimationHost {
                    animation = LocalSplashAnimation.current!!
                    SplashScreen()
                }
            }
        }
        compose.waitUntil(5_000) {
            compose.mainClock.advanceTimeByFrame()
            animation.composition != null
        }
        compose.mainClock.advanceTimeBy(ARTWORK_SETTLE_MILLIS)
        val pixels = compose.onRoot().captureToImage().toPixelMap()
        val top = pixels[pixels.width / 2, 1]
        assertTrue(
            "Status inset must show the artwork instead of an opaque blue strip",
            top != CaixaDSColorTokens.Blue700 && top != Color.Transparent
        )
    }

    @Test fun cloverUsesItsOriginalEntranceInsteadOfFadingTheWholeLogo() {
        lateinit var animation: LottieAnimatable
        lateinit var scope: CoroutineScope
        compose.mainClock.autoAdvance = false
        compose.setContent {
            SplashAnimationHost {
                scope = rememberCoroutineScope()
                animation = LocalSplashAnimation.current!!
                SplashScreen()
            }
        }
        compose.waitUntil(5_000) {
            compose.mainClock.advanceTimeByFrame()
            animation.composition != null
        }
        for (frame in BRAND_KEYFRAMES) {
            compose.runOnIdle {
                scope.launch { animation.snapTo(progress = frame / animation.composition!!.durationFrames) }
            }
            compose.mainClock.advanceTimeByFrame()
            compose.waitForIdle()
            compose.waitUntil(5_000) {
                compose.mainClock.advanceTimeByFrame()
                cloverWhitePixels(compose.onRoot().captureToImage()) > MIN_VISIBLE_PIXELS
            }
            val image = compose.onRoot().captureToImage()
            assertTrue(
                "Clover must already be opaque at frame $frame, independently of the words",
                cloverWhitePixels(image) > MIN_VISIBLE_PIXELS
            )
            val directory = InstrumentationRegistry.getInstrumentation().context.getExternalFilesDir(null)!!
            val screenshot = File(directory, "splash-frame-${frame.toInt()}.png")
            screenshot.outputStream().use {
                image.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, it)
            }
            val command = "cp ${screenshot.absolutePath} /data/local/tmp/simlo-splash-${frame.toInt()}.png"
            val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(command)
            android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { it.readBytes() }
        }
    }

    private fun cloverWhitePixels(image: ImageBitmap): Int {
        val pixels = image.toPixelMap()
        val scale = maxOf(image.width / SPLASH_CANVAS_WIDTH, image.height / SPLASH_CANVAS_HEIGHT)
        val offsetX = (image.width - SPLASH_CANVAS_WIDTH * scale) / 2f
        val offsetY = (image.height - SPLASH_CANVAS_HEIGHT * scale) / 2f
        var whitePixels = 0
        for (y in (offsetY + CLOVER_TOP * scale).toInt() until (offsetY + CLOVER_BOTTOM * scale).toInt()) {
            for (x in (offsetX + CLOVER_LEFT * scale).toInt() until (offsetX + CLOVER_RIGHT * scale).toInt()) {
                val pixel = pixels[x, y]
                if (pixel.red > OPAQUE_CHANNEL && pixel.green > OPAQUE_CHANNEL && pixel.blue > OPAQUE_CHANNEL) whitePixels++
            }
        }
        return whitePixels
    }

    @Test fun movingBetweenStartupStagesDoesNotRestartAnimation() {
        val stage = mutableStateOf(0)
        lateinit var animation: LottieAnimatable
        lateinit var view: View
        compose.mainClock.autoAdvance = false
        compose.setContent {
            SplashAnimationHost {
                view = LocalView.current
                animation = LocalSplashAnimation.current!!
                key(stage.value) { SplashScreen() }
            }
        }
        compose.waitUntil(5_000) {
            compose.mainClock.advanceTimeByFrame()
            animation.composition != null
        }
        compose.mainClock.advanceTimeBy(1_500)
        var progress = 0f
        compose.runOnIdle {
            progress = animation.progress
            assertTrue("Animation must advance before changing startup stage", progress > 0.05f)
            stage.value++
        }
        compose.mainClock.advanceTimeBy(300)
        compose.runOnIdle {
            assertTrue("Startup transition must continue playback", animation.progress > progress)
            assertTrue("Startup transition must not skip ahead", animation.progress - progress < 0.03f)
        }
        compose.waitUntil(5_000) {
            ViewCompat.getRootWindowInsets(view)?.isVisible(WindowInsetsCompat.Type.statusBars()) == true
        }
    }

    @Test fun splashKeepsStatusBarVisibleDuringAnimationAndOnExit() {
        val visible = mutableStateOf(true)
        lateinit var view: View
        compose.setContent {
            view = LocalView.current
            if (visible.value) SplashScreen()
        }
        compose.waitUntil(5_000) {
            ViewCompat.getRootWindowInsets(view)?.isVisible(WindowInsetsCompat.Type.statusBars()) == true
        }
        compose.runOnIdle { visible.value = false }
        compose.waitUntil(5_000) {
            ViewCompat.getRootWindowInsets(view)?.isVisible(WindowInsetsCompat.Type.statusBars()) == true
        }
        assertTrue(ViewCompat.getRootWindowInsets(view)!!.isVisible(WindowInsetsCompat.Type.statusBars()))
    }
}

// As coordenadas estão na área de desenho Lottie legada inalterada, antes de centerCrop.
private const val SPLASH_CANVAS_WIDTH = 1000f
private const val SPLASH_CANVAS_HEIGHT = 1778f
private const val CLOVER_TOP = 690f
private const val CLOVER_BOTTOM = 830f
private const val CLOVER_LEFT = 430f
private const val CLOVER_RIGHT = 580f
private const val OPAQUE_CHANNEL = 0.95f
private const val MIN_VISIBLE_PIXELS = 10
private val BRAND_KEYFRAMES = listOf(85f, 110f)
private const val PNG_QUALITY = 100

private const val MOVING_SECTION_END_FRAME = 120f
private const val ARTWORK_SETTLE_MILLIS = 1_000L
