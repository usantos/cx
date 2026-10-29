package br.gov.caixa.loterias.feature.tickets

import android.content.res.Configuration
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.tickets.presentation.screen.TicketScannerScreen

/** Exercita ações reais de sobreposição/permissão; só a prévia do hardware é fornecida pelo teste. */
class TicketScannerScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun deniedPermissionOffersTheCurrentPermissionCallbackAndNoScannerControls() {
        val replacement = mutableStateOf(false)
        var first = 0
        var second = 0
        compose.setContent { CaixaDSTheme {
            TicketScannerScreen(false, { error("Scanner close must be hidden") },
                if (replacement.value) ({ second++ }) else ({ first++ }),
                cameraPreview = { error("Denied permission must not compose camera hardware") })
        } }
        compose.onNodeWithText("Permita o acesso à câmera para ler o código do bilhete.").assertIsDisplayed()
        compose.onNodeWithContentDescription("Fechar").assertDoesNotExist()
        compose.onNodeWithText("QR Code").assertDoesNotExist()
        compose.onNodeWithText("Permitir câmera").performClick()
        compose.runOnIdle { replacement.value = true }
        compose.onNodeWithText("Permitir câmera").performClick()
        compose.runOnIdle { assertEquals(1, first); assertEquals(1, second) }
    }

    @Test fun landscapeShowsAutomaticQrGuidanceWithoutModeButton() {
        var portraitRequests = 0
        compose.setContent { CaixaDSTheme {
            val landscape = Configuration(LocalConfiguration.current).apply {
                orientation = Configuration.ORIENTATION_LANDSCAPE
            }
            CompositionLocalProvider(LocalConfiguration provides landscape) {
                TicketScannerScreen(true, {}, {}, cameraPreview = { Box(it) })
            }
        }
        }
        compose.onNodeWithText("Posicione a câmera sobre o QR Code e aguarde. A leitura é automática.").assertIsDisplayed()
        compose.onNodeWithText("QR Code").assertDoesNotExist()
        compose.onNodeWithText("Digite sua aposta").assertDoesNotExist()
        compose.runOnIdle { assertEquals(0, portraitRequests) }
    }

    @Test fun grantedPermissionShowsLegacyOverlayAndCloseWithoutInventingAQrButtonAction() {
        var close = 0
        compose.setContent {
            CaixaDSTheme {
                TicketScannerScreen(true, { close++ }, { error("Already granted") }, cameraPreview = { Box(it) })
            }
        }
        compose.onNodeWithText("Resultado").assertIsDisplayed()
        compose.onNodeWithText("Posicione a câmera sobre o QR Code e aguarde. A leitura é automática.").assertIsDisplayed()
        compose.onNodeWithText("Permitir câmera").assertDoesNotExist()
        compose.onNodeWithText("QR Code").assertDoesNotExist()
        compose.runOnIdle { assertEquals(0, close) }
        compose.onNodeWithContentDescription("Fechar").performClick()
        compose.runOnIdle { assertEquals(1, close) }
    }
}
