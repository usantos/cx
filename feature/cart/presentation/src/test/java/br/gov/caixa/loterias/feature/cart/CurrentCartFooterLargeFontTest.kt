package br.gov.caixa.loterias.feature.cart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartFooter
import java.math.BigDecimal
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
class CurrentCartFooterLargeFontTest {
    @get:Rule val compose = createComposeRule()

    @Test fun totalAmountAndCounterAreFullyReadableAtDoubleFont() = atDoubleFont {
        val item = CartItem(1, CartItemKind.INDIVIDUAL_BET, "Lotofácil", "", BigDecimal("3.50"))
        val cart = ShoppingCart(1, BigDecimal("7.00"), listOf(item), totalBetCount = 2)
        compose.setContent {
            CaixaDSTheme {
                Box(Modifier.size(width = 360.dp, height = 640.dp)) {
                    CurrentCartFooter(cart, true, {}, {})
                }
            }
        }
        listOf("Total do carrinho", "R$ 7,00", "2").forEach(::assertReadable)
    }

    private fun assertReadable(text: String) {
        val node = compose.onNodeWithText(text, useUnmergedTree = true).assertIsDisplayed()
        val layouts = mutableListOf<TextLayoutResult>()
        node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val layout = layouts.single()
        assertEquals(2f, layout.layoutInput.density.fontScale, 0f)
        assertFalse(
            "Cart footer text must not clip: $text size=${layout.size} " +
                "paragraph=${layout.multiParagraph.width}x${layout.multiParagraph.height} " +
                "constraints=${layout.layoutInput.constraints} bounds=${node.fetchSemanticsNode().boundsInRoot} " +
                "width=${layout.didOverflowWidth} height=${layout.didOverflowHeight}",
            layout.hasVisualOverflow
        )
        assertTrue(layout.getLineBottom(layout.lineCount - 1) <= node.fetchSemanticsNode().boundsInRoot.height)
    }

    private fun atDoubleFont(block: () -> Unit) {
        val originalFont = RuntimeEnvironment.getFontScale()
        val originalLocale = Locale.getDefault()
        RuntimeEnvironment.setFontScale(2f)
        Locale.setDefault(Locale.forLanguageTag("pt-BR"))
        try {
            block()
        } finally {
            RuntimeEnvironment.setFontScale(originalFont)
            Locale.setDefault(originalLocale)
        }
    }
}
