package br.gov.caixa.loterias.feature.tickets

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.tickets.presentation.screen.TicketScannerScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TicketScannerControlsUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun automaticQrHasNoModeButtonAndCloseKeepsBack() {
        val revision = mutableIntStateOf(0)
        val requests = mutableListOf<Int>()
        var closes = 0
        compose.setContent {
            val current = revision.intValue
            CaixaDSTheme {
                TicketScannerScreen(
                    permissionGranted = true,
                    onBack = { closes++ },
                    onRequestCameraPermission = { error("Camera is already granted") },
                    cameraPreview = { Box(it) }
                )
            }
        }
        compose.onNodeWithText("QR Code").assertDoesNotExist()
        compose.runOnIdle { revision.intValue = 1 }
        compose.onNodeWithText("QR Code").assertDoesNotExist()
        compose.onNodeWithContentDescription("Fechar").performClick()
        compose.runOnIdle {
            assertEquals(emptyList<Int>(), requests)
            assertEquals(1, closes)
        }
    }
}
