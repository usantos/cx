package br.gov.caixa.loterias.feature.tickets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.tickets.presentation.screen.TicketScannerScreen
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TicketScannerOverlayTest {
    @get:Rule val compose = createComposeRule()

    @Test fun overlayIsTranslucentBlueWithClearCameraWindowAndLowerTitle() {
        compose.setContent {
            CaixaDSTheme {
                TicketScannerScreen(true, {}, {}, cameraPreview = { Box(it.background(Color.Red)) })
            }
        }
        val title = compose.onNodeWithText("Resultado").fetchSemanticsNode().boundsInRoot
        assertTrue(title.top >= TITLE_TOP_DP * compose.density.density)
        val pixels = compose.onRoot().captureToImage().toPixelMap()
        val tinted = pixels[EDGE_SAMPLE_PX, pixels.height / 2]
        val clear = pixels[pixels.width / 2, pixels.height / 2]
        // Uma contribuição vermelha visível comprova que o azul ao redor é translúcido.
        assertTrue(tinted.red > MINIMUM_RED_CONTRIBUTION && tinted.blue > MINIMUM_BLUE && tinted.green > MINIMUM_GREEN)
        assertTrue(clear.red > OPAQUE_CHANNEL && clear.green < EMPTY_CHANNEL && clear.blue < EMPTY_CHANNEL)
    }
}

private const val TITLE_TOP_DP = 58f
private const val EDGE_SAMPLE_PX = 8
private const val MINIMUM_RED_CONTRIBUTION = .1f
private const val MINIMUM_BLUE = .5f
private const val MINIMUM_GREEN = .25f
private const val OPAQUE_CHANNEL = .95f
private const val EMPTY_CHANNEL = .05f
