package br.gov.caixa.loterias.feature.home

import android.graphics.Bitmap
import android.util.Base64
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.CaixaDSNotificationPlaceholder
import br.gov.caixa.loterias.core.domain.account.AccountNotification
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class HomeNotificationPopupTest {
    @get:Rule val compose = createComposeRule()

    @Test fun absentLinkCentersCloseAndPreservesPlainNotificationText() {
        var closes = 0
        compose.setContent { CaixaDSTheme(darkTheme = false) {
            HomeNotificationPopup(AccountNotification(null, "Título", "Mensagem <b>literal</b>", null, null), { closes++ }, { error("No link") })
        } }
        compose.onNodeWithText("Título").assertIsDisplayed()
        compose.onNodeWithText("Mensagem <b>literal</b>").assertIsDisplayed()
        compose.onNodeWithText("Abrir Link").assertDoesNotExist()
        compose.onNodeWithText("Fechar").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1, closes) }
        assertEquals(308f, CaixaDSNotificationPlaceholder.viewportWidth)
        assertEquals(308f, CaixaDSNotificationPlaceholder.viewportHeight)
    }

    @Test fun openingLinkDoesNotClosePopup() {
        var opens = 0
        var closes = 0
        compose.setContent { CaixaDSTheme(darkTheme = false) {
            HomeNotificationPopup(AccountNotification(7, "Título", "Mensagem", null, "https://example.invalid"), { closes++ }, { opens++ })
        } }
        compose.onNodeWithText("Abrir Link").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1, opens); assertEquals(0, closes) }
        compose.onNodeWithText("Fechar").assertIsDisplayed()
    }

    @Test fun bitmapDecoderRejectsMissingMalformedAndExcessiveInputAndDownsamplesValidImage() {
        assertNull(decodeNotificationImage(null)); assertNull(decodeNotificationImage(""))
        assertNull(decodeNotificationImage("%%%")); assertNull(decodeNotificationImage("A".repeat(1_400_001)))
        fun encoded(width: Int): String {
            val bitmap = Bitmap.createBitmap(width, 2, Bitmap.Config.ARGB_8888)
            val bytes = java.io.ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
            bitmap.recycle()
            return Base64.encodeToString(bytes.toByteArray(), Base64.NO_WRAP)
        }
        assertNull(decodeNotificationImage(encoded(20_000)))
        val decoded = decodeNotificationImage(encoded(2000))!!
        assertTrue(decoded.width in 1..1600)
        decoded.recycle()
    }
}
