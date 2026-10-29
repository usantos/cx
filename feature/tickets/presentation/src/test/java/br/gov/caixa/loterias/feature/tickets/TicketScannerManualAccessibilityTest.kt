package br.gov.caixa.loterias.feature.tickets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.tickets.presentation.screen.TicketScannerScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w360dp-h640dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TicketScannerManualAccessibilityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun manualEntryIsReachableWithCameraAtDoubleFont() = checkManualEntry(true)

    @Test fun manualEntryIsReachableWithoutCameraAtDoubleFont() = checkManualEntry(false)

    private fun checkManualEntry(granted: Boolean) {
        val original = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(2f)
        var manualCalls = 0
        try {
            compose.setContent {
                CaixaDSTheme {
                    Box(Modifier.size(width = 360.dp, height = 640.dp)) {
                        TicketScannerScreen(granted, {}, {}, cameraPreview = { Box(it) }, onManual = { manualCalls++ })
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
