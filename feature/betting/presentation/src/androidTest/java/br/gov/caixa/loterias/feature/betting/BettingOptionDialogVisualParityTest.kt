package br.gov.caixa.loterias.feature.betting

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSOptionDialog
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import java.io.ByteArrayOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BettingOptionDialogVisualParityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun lotomaniaActionsRemainCompleteOnOneLine() {
        showLotomania()
        compose.mainClock.advanceTimeBy(ANIMATION_SETTLE_MS)
        compose.waitForIdle()
        capture("lotomania-selected")
        for (label in listOf("Cancelar", "Confirmar")) {
            val layouts = mutableListOf<TextLayoutResult>()
            compose.onNodeWithText(label, useUnmergedTree = true)
                .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            val text = layouts.single()
            assertEquals("$label must remain on one line", 1, text.lineCount)
            for (index in label.indices) {
                val character = text.getBoundingBox(index)
                assertTrue(
                    "$label character $index must remain inside the visible text area",
                    character.left >= 0f && character.right <= text.size.width &&
                        character.top >= 0f && character.bottom <= text.size.height
                )
            }
            assertEquals(label.length, text.getLineEnd(0, visibleEnd = true))
        }
    }

    @Test fun numberListAndActionsMatchInstalledLegacyXml() {
        showLotomania()
        val target = InstrumentationRegistry.getInstrumentation().targetContext
        val legacy = target.createPackageContext(LEGACY_PACKAGE, Context.CONTEXT_IGNORE_SECURITY)
        val resources = legacy.resources
        val themed = ContextThemeWrapper(legacy, resources.getIdentifier("DialogUtilsTheme", "style", LEGACY_PACKAGE))
        val minimumWidth = android.util.TypedValue()
        check(themed.theme.resolveAttribute(android.R.attr.windowMinWidthMinor, minimumWidth, true))
        val screenWidth = target.resources.displayMetrics.widthPixels
        val width = minimumWidth.getFraction(screenWidth.toFloat(), screenWidth.toFloat()).toInt()
        lateinit var native: View
        compose.runOnUiThread {
            native = LayoutInflater.from(themed).inflate(resources.getIdentifier("layout_dialog_itens", "layout", LEGACY_PACKAGE), null)
            native.findViewById<android.widget.TextView>(resources.getIdentifier("titulo_dialog_itens", "id", LEGACY_PACKAGE))
                .text = "Quantidade de números"
            native.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.UNSPECIFIED)
            native.layout(0, 0, native.measuredWidth, native.measuredHeight)
        }
        val list = native.findViewById<View>(resources.getIdentifier("lista_itens_dialog_itens", "id", LEGACY_PACKAGE))
        val actual = compose.onNodeWithTag("betting-option-list").fetchSemanticsNode().boundsInRoot
        assertEquals("Installed legacy list height", list.height.toFloat(), actual.height, PIXEL_TOLERANCE)
        assertEquals("Installed legacy dialog content width", list.width.toFloat(), actual.width, PIXEL_TOLERANCE)
        for ((tag, id) in listOf("cancel" to "button_negativo_itens", "confirm" to "button_positivo_itens")) {
            val button = native.findViewById<Button>(resources.getIdentifier(id, "id", LEGACY_PACKAGE))
            val bounds = compose.onNodeWithTag("betting-option-$tag").fetchSemanticsNode().boundsInRoot
            assertEquals("Native $tag width", button.width.toFloat(), bounds.width, PIXEL_TOLERANCE)
            assertEquals("Native $tag height", button.height.toFloat(), bounds.height, PIXEL_TOLERANCE)
        }
    }

    private fun showLotomania() {
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                CaixaDSOptionDialog("Quantidade de números", listOf(LOTOMANIA_NUMBER_COUNT), LOTOMANIA_NUMBER_COUNT, {
                    "50 números por R$ 3,00"
                }, {}, {})
            }
        }
        compose.onNodeWithTag("betting-option-item-0").performClick()
        compose.onNodeWithTag("betting-option-confirm").assertIsEnabled()
    }

    private fun capture(name: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        // O efeito de onda da plataforma Android usa o tempo real dos quadros, independente do relógio de teste do Compose.
        instrumentation.waitForIdleSync()
        instrumentation.uiAutomation.waitForIdle(NATIVE_IDLE_MS, NATIVE_IDLE_TIMEOUT_MS)
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        try {
            val output = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, output)
            val chunks = Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP).chunked(LOG_CHUNK_SIZE)
            chunks.forEachIndexed { index, chunk -> Log.i("CodexOptionDialog", "$name:$index/${chunks.size}:$chunk") }
        } finally {
            bitmap.recycle()
        }
    }

    private companion object {
        const val LEGACY_PACKAGE = "br.gov.caixa.loterias.apostas.cxv2validation"
        const val PIXEL_TOLERANCE = 1f
        const val LOTOMANIA_NUMBER_COUNT = 50
        const val PNG_QUALITY = 100
        const val LOG_CHUNK_SIZE = 3000
        const val ANIMATION_SETTLE_MS = 1000L
        const val NATIVE_IDLE_MS = 500L
        const val NATIVE_IDLE_TIMEOUT_MS = 5000L
    }
}
