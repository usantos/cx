package br.gov.caixa.loterias.feature.marketplace

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplacePoolExpansion
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplacePoolSummary
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import java.io.ByteArrayOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private const val PHONE_WIDTH = 384
private const val WIDE_WIDTH = 426
private const val COMPACT_WIDTH = 360
private const val NARROW_WIDTH = 320
private const val NATIVE_ACTION_WIDTH = 177
private val minimumActionGap = 12.dp
private val expansionColor = Color(0xFF851F82)

class MarketplaceExpandedListLayoutTest {
    @get:Rule val compose = createComposeRule()

    @Test fun phoneWidthKeepsAddLabelOnOneLineAndSeparateFromQuantity() = checkExpansion(PHONE_WIDTH)

    @Test fun widerDisplayKeepsNativeActionWidth() = checkExpansion(WIDE_WIDTH)

    @Test fun compactPhoneKeepsCounterAlignedAndAddActionSeparate() = checkExpansion(COMPACT_WIDTH)

    @Test fun narrowPhoneKeepsAllControlsAndCompleteAddLabelVisible() = checkExpansion(NARROW_WIDTH)

    private fun checkExpansion(width: Int) {
        val actions = mutableListOf<String>()
        lateinit var sceneDensity: Density
        compose.setContent {
            BoxWithConstraints {
                val base = LocalDensity.current
                sceneDensity = Density(base.density * minOf(1f, maxWidth.value / width), base.fontScale)
                CompositionLocalProvider(LocalDensity provides sceneDensity) {
                    CaixaDSTheme {
                        Column(Modifier.width(width.dp)) {
                            CaixaDSMarketplacePoolExpansion(
                                null, AnnotatedString("1 aposta de 11 dezenas"),
                                AnnotatedString("6 cotas disponíveis de 22"), 1, false, true, false, true,
                                expansionColor, Color.White, expansionColor, {}, {}, { actions += "increase" }, { actions += "add" }
                            )
                        }
                    }
                }
            }
        }
        val label = compose.onNodeWithText("Adicionar ao Carrinho")
        val layouts = mutableListOf<TextLayoutResult>()
        label.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals("Native 177dp button label fits a single line", 1, layouts.single().lineCount)
        assertTrue("Every glyph must fit inside the button", layouts.single().getLineRight(0) <= layouts.single().size.width + 1f)
        capturePreview("expanded-$width")
        val add = label.fetchSemanticsNode().boundsInRoot
        if (width >= PHONE_WIDTH) assertEquals(with(sceneDensity) { NATIVE_ACTION_WIDTH.dp.toPx() }, add.width, 1f)
        val plus = compose.onNodeWithText("+").fetchSemanticsNode().boundsInRoot
        val heading = compose.onNodeWithText("Quantidades de Cotas").fetchSemanticsNode().boundsInRoot
        val minus = compose.onNodeWithText("-").fetchSemanticsNode().boundsInRoot
        assertEquals("Counter begins at the same left edge as its heading", heading.left, minus.left, 1f)
        val minimumGap = with(sceneDensity) { minimumActionGap.toPx() }
        assertTrue(
            "Add action stays separate from the quantity controls",
            add.left - plus.right >= minimumGap - 1f || add.top - plus.bottom >= minimumGap - 1f
        )
        compose.onNodeWithText("+").performClick()
        label.performClick()
        compose.runOnIdle { assertEquals(listOf("increase", "add"), actions) }
    }

    @Test fun expandedPriceAlignsWithRetailerInsteadOfStartingBelowItsName() {
        compose.setContent {
            CaixaDSTheme {
                Column(Modifier.width(384.dp)) {
                    CaixaDSMarketplacePoolSummary(
                        "J. J. Loterias Ltda", "Sao Paulo - Sp", "", "R$ 170,30",
                        false, "", "", Color.Green, true, false, true, {}, {}
                    )
                }
            }
        }
        val title = compose.onNodeWithText("J. J. Loterias Ltda", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val priceLabel = compose.onNodeWithText("Valor da Cota", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val city = compose.onNodeWithText("Sao Paulo - Sp", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val amount = compose.onNodeWithText("R$ 170,30", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val tolerance = with(compose.density) { 2.dp.toPx() }
        assertEquals("Native label aligns with the retailer name", title.top, priceLabel.top, tolerance)
        assertEquals("Native amount aligns with the city", city.bottom, amount.bottom, tolerance)
    }
    private fun capturePreview(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        try {
            val output = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, output)
            val chunks = Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP).chunked(LOG_CHUNK_SIZE)
            chunks.forEachIndexed { index, chunk -> Log.i("MarketplacePreview", "$name:$index/${chunks.size}:$chunk") }
        } finally {
            bitmap.recycle()
        }
    }

    private companion object {
        const val PNG_QUALITY = 100
        const val LOG_CHUNK_SIZE = 3000
    }
}
