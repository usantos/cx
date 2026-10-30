package br.gov.caixa.loterias.apostas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.apostas.navigation.transition.AppScreenNavHost
import br.gov.caixa.loterias.core.designsystem.testing.captureToImage
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AppScreenTransitionTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private lateinit var nav: NavHostController

    private fun graph() {
        compose.setContent {
            nav = rememberNavController()
            Box(Modifier.fillMaxSize().background(Color.White).testTag("screen")) {
                AppScreenNavHost(nav, "first") {
                    composable("first") { Box(Modifier.fillMaxSize().background(Color.Red)) }
                    composable("second") { Box(Modifier.fillMaxSize().background(Color.Blue)) }
                }
            }
        }
        compose.waitForIdle()
    }

    @Test fun openingIsOpaqueFromTheFirstRenderedFrames() {
        graph()
        compose.mainClock.autoAdvance = false
        compose.runOnIdle { nav.navigate("second") }
        compose.mainClock.advanceTimeBy(EARLY_FRAMES_MS)
        assertCenterColor(Color.Blue)
    }

    @Test fun returningIsOpaqueFromTheFirstRenderedFrames() {
        graph()
        compose.runOnIdle { nav.navigate("second") }
        compose.waitForIdle()
        compose.mainClock.autoAdvance = false
        compose.runOnIdle { nav.popBackStack() }
        compose.mainClock.advanceTimeBy(EARLY_FRAMES_MS)
        assertCenterColor(Color.Red)
    }

    private fun assertCenterColor(expected: Color) {
        val pixels = compose.onNodeWithTag("screen").captureToImage().toPixelMap()
        assertEquals(expected.toArgb(), pixels[pixels.width / CENTER_DIVISOR, pixels.height / CENTER_DIVISOR].toArgb())
    }
}

private const val EARLY_FRAMES_MS = 64L
private const val CENTER_DIVISOR = 2
