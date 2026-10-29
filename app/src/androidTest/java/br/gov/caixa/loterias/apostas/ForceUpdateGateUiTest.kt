package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.apostas.composition.startup.ForceUpdateGateContent
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.SplashScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.state.ForceUpdateState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ForceUpdateGateUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun finishingUpdateCheckDoesNotRestartSplashPlayback() {
        val state = mutableStateOf(ForceUpdateState.CHECKING)
        compose.mainClock.autoAdvance = false
        compose.setContent {
            CaixaDSTheme {
                ForceUpdateGateContent(state.value, {}) { SplashScreen() }
            }
        }
        compose.waitUntil(UPDATE_CHECK_TIMEOUT_MILLIS) {
            compose.mainClock.advanceTimeBy(FRAME_STEP_MILLIS)
            hasOpaqueClover(compose.onRoot().captureToImage())
        }
        compose.runOnIdle { state.value = ForceUpdateState.READY }
        compose.mainClock.advanceTimeBy(TRANSITION_TIME_MILLIS)
        assertTrue(
            "Update check must preserve the visible clover and animation position",
            hasOpaqueClover(compose.onRoot().captureToImage())
        )
    }

    private fun hasOpaqueClover(image: ImageBitmap): Boolean {
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
        return whitePixels > MIN_VISIBLE_PIXELS
    }

    @Test fun forceUpdatePreventsLoginAndBackAndKeepsBlockingAfterOpeningStore() {
        val state = mutableStateOf(ForceUpdateState.CHECKING)
        var store = 0
        compose.setContent {
            CaixaDSTheme {
                ForceUpdateGateContent(state.value, { store++ }) { CaixaDSText("LOGIN FIXTURE") }
            }
        }
        compose.onNodeWithText("LOGIN FIXTURE").assertDoesNotExist()
        compose.runOnIdle { state.value = ForceUpdateState.REQUIRED }
        compose.onNodeWithText("Atualização necessária").assertIsDisplayed()
        compose.onNodeWithText("LOGIN FIXTURE").assertDoesNotExist()
        compose.runOnIdle {
            compose.activity.onBackPressedDispatcher.onBackPressed()
            assertFalse(compose.activity.isFinishing)
        }
        compose.onNodeWithText("Atualizar aplicativo").performClick()
        compose.onNodeWithText("Atualização necessária").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, store)
            state.value = ForceUpdateState.READY
        }
        compose.onNodeWithText("LOGIN FIXTURE").assertIsDisplayed()
    }
}

// As coordenadas usam o canvas Lottie original do legado, antes de centerCrop.
private const val SPLASH_CANVAS_WIDTH = 1000f
private const val SPLASH_CANVAS_HEIGHT = 1778f
private const val CLOVER_TOP = 690f
private const val CLOVER_BOTTOM = 830f
private const val CLOVER_LEFT = 430f
private const val CLOVER_RIGHT = 580f
private const val OPAQUE_CHANNEL = 0.95f
private const val MIN_VISIBLE_PIXELS = 10
private const val UPDATE_CHECK_TIMEOUT_MILLIS = 10_000L
private const val FRAME_STEP_MILLIS = 100L
private const val TRANSITION_TIME_MILLIS = 200L
