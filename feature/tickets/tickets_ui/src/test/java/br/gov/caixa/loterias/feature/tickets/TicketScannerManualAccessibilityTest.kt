package br.gov.caixa.loterias.feature.tickets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.tickets.presentation.screen.TicketScannerScreenDelivered
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@ResourcesMode(ResourcesMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(application = android.app.Application::class, sdk = [36], qualifiers = "w360dp-h640dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TicketScannerManualAccessibilityTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun manualEntryIsReachableWithCameraAtDoubleFont() = checkManualEntry(true)

    @Test fun manualEntryIsReachableWithoutCameraAtDoubleFont() = checkManualEntry(false)

    private fun checkManualEntry(granted: Boolean) {
        val original = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(2f)
        var manualCalls = 0
        try {
            compose.setFeatureContent {
                LoteriasCaixaDSTheme {
                    Box(Modifier.size(width = 360.dp, height = 640.dp)) {
                        TicketScannerScreenDelivered(granted, {}, {}, cameraPreview = { Box(it) }, onManual = { manualCalls++ })
                    }
                }
            }
            compose.onNodeWithText("Digitar código").assertIsDisplayed().performClick()
            compose.runOnIdle { assertEquals(1, manualCalls) }
        } finally {
            RuntimeEnvironment.setFontScale(original)
        }
    }
}
