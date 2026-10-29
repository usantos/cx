package br.gov.caixa.loterias.apostas

import android.content.ActivityNotFoundException
import android.content.ContextWrapper
import android.content.Intent
import androidx.core.net.toUri
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.feature.tickets.presentation.route.betReceiptViewIntent
import br.gov.caixa.loterias.feature.tickets.presentation.route.openBetReceipt
import br.gov.caixa.loterias.feature.tickets.presentation.route.writeBetReceipt
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetReceipt
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class BetReceiptOpeningTest {
    @Test fun generatedReceiptCanBeReadThroughTheGrantedContentUri() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val bytes = "%PDF-1.7\ncontrolled receipt".toByteArray()
        val uri = writeBetReceipt(context, BetReceipt(bytes, "comprovante-aposta-test.pdf"))
        assertEquals("content", uri.scheme)
        assertEquals(context.packageName + ".fileprovider", uri.authority)
        assertArrayEquals(bytes, context.contentResolver.openInputStream(uri)!!.use { it.readBytes() })
        val intent = betReceiptViewIntent(uri)
        assertEquals("application/pdf", intent.type)
        assertTrue(intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION != 0)
    }

    @Test fun invalidResponseCannotBeExposedAsAPdf() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        try {
            writeBetReceipt(context, BetReceipt("{error: unauthorized}".toByteArray(), "invalid.pdf"))
            fail("Non-PDF responses must be rejected")
        } catch (expected: IllegalArgumentException) {
            assertEquals("Comprovante PDF inválido", expected.message)
        }
    }

    @Test fun missingPdfViewerReportsFailureInsteadOfSilentlySucceeding() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val noViewer = object : ContextWrapper(context) {
            override fun startActivity(intent: Intent): Unit = throw ActivityNotFoundException("No viewer")
        }
        assertFalse(openBetReceipt(noViewer, "content://test/receipt.pdf".toUri()))
    }
}
