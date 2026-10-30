package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSPixPanel
import br.gov.caixa.loterias.core.designsystem.components.pixQrMatrix
import br.gov.caixa.loterias.core.designsystem.testing.captureToImage
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import com.google.zxing.BinaryBitmap
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CaixaDSPixQrCodeJvmTest {
    @get:Rule val compose = createComposeRule()

    @Test fun blankAndUnencodablePayloadsDoNotProduceMisleadingQrImages() {
        assertNull(pixQrMatrix("   "))
        assertNull(pixQrMatrix("X".repeat(UNENCODABLE_LENGTH)))
    }

    @Test fun renderedQrDecodesToTheCopyCodeAndStaysCenteredAboveTheField() {
        val code = mutableStateOf("  CONTROLLED-PIX-QR-DO-NOT-PAY-001  ")
        val remaining = mutableStateOf(ACTIVE_SECONDS)
        val dark = mutableStateOf(false)
        compose.setContent {
            CaixaDSTheme(darkTheme = dark.value) {
                CaixaDSPixPanel(
                    PURCHASE_ID, code.value, remaining.value, false, {}, {}, {},
                    modifier = Modifier.width(PANEL_WIDTH_DP.dp).verticalScroll(rememberScrollState()),
                    showIntro = false, showHeader = false, showRecoveryGuidance = false
                )
            }
        }
        for (value in listOf(false, true)) {
            compose.runOnIdle {
                dark.value = value
                if (value) code.value = "CONTROLLED-DO-NOT-PAY-" + "1234567890".repeat(DENSE_PAYLOAD_BLOCKS)
            }
            val qr = compose.onNodeWithContentDescription("QR Code para pagamento Pix").performScrollTo().assertIsDisplayed()
            val bitmap = qr.captureToImage().asAndroidBitmap()
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            val decoded = QRCodeReader().decode(BinaryBitmap(HybridBinarizer(RGBLuminanceSource(bitmap.width, bitmap.height, pixels))))
            assertEquals(code.value.trim(), decoded.text)
            val qrBounds = qr.fetchSemanticsNode().boundsInRoot
            val titleBounds = compose.onNodeWithText("Pix Copia e Cola").fetchSemanticsNode().boundsInRoot
            assertEquals(titleBounds.center.x, qrBounds.center.x, 1f)
            assertTrue(qrBounds.bottom <= titleBounds.top)
        }
        compose.runOnIdle { remaining.value = 0 }
        compose.onNodeWithContentDescription("QR Code para pagamento Pix").assertDoesNotExist()
        compose.runOnIdle {
            code.value = "   "
            remaining.value = ACTIVE_SECONDS
        }
        compose.onNodeWithContentDescription("QR Code para pagamento Pix").assertDoesNotExist()
    }
}

private const val ACTIVE_SECONDS = 900L
private const val PURCHASE_ID = 91L
private const val PANEL_WIDTH_DP = 360

private const val UNENCODABLE_LENGTH = 5000

private const val DENSE_PAYLOAD_BLOCKS = 40
