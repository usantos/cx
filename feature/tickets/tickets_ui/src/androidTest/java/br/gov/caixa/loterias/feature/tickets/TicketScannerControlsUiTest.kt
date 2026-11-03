package br.gov.caixa.loterias.feature.tickets

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.createFeatureComposeRuleV2
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.tickets.presentation.screen.TicketScannerScreenDelivered
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TicketScannerControlsUiTest {
    @get:Rule val compose = createFeatureComposeRuleV2()

    @Test fun automaticQrHasNoModeButtonAndCloseKeepsBack() {
        val revision = mutableIntStateOf(0)
        val requests = mutableListOf<Int>()
        var closes = 0
        compose.setFeatureContent {
            val current = revision.intValue
            LoteriasCaixaDSTheme {
                TicketScannerScreenDelivered(
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
