package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSOptionDialog
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
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
class CaixaDSOptionDialogLargeFontTest {
    @get:Rule val compose = createComposeRule()

    @Test fun wrappedNumberPriceIsReadableAtDoubleFont() = atDoubleFont {
        showDialog()
        compose.onNodeWithText("6 números por R$ 6,00").performScrollTo()
        assertReadable("6 números por R$ 6,00")
    }

    @Test fun confirmationRemainsReadableAndDispatchesOneExplicitSelection() = atDoubleFont {
        val selected = mutableListOf<Int>()
        var dismissals = 0
        showDialog(onSelect = { selected += it }, onDismiss = { dismissals++ })
        compose.onNodeWithTag("betting-option-item-1").performScrollTo().performClick()
        assertReadable("Confirmar")
        compose.onNodeWithTag("betting-option-confirm").assertIsEnabled().performClick()
        compose.runOnIdle {
            assertEquals(listOf(SELECTED_QUANTITY), selected)
            assertEquals(1, dismissals)
        }
    }

    private fun showDialog(onSelect: (Int) -> Unit = {}, onDismiss: () -> Unit = {}) {
        compose.setContent {
            CaixaDSTheme {
                CaixaDSOptionDialog(
                    "Quantidade de números",
                    (MIN_QUANTITY..MAX_QUANTITY).toList(),
                    MIN_QUANTITY,
                    { "$it números por R$ 6,00" },
                    onSelect,
                    onDismiss
                )
            }
        }
    }

    private fun assertReadable(text: String) {
        val node = compose.onNodeWithText(text).assertIsDisplayed()
        val layouts = mutableListOf<TextLayoutResult>()
        node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val layout = layouts.single()
        assertEquals(2f, layout.layoutInput.density.fontScale, 0f)
        assertFalse("Option/action must not clip at 200%: $text", layout.hasVisualOverflow)
        assertTrue(layout.getLineBottom(layout.lineCount - 1) <= node.fetchSemanticsNode().boundsInRoot.height)
    }

    private fun atDoubleFont(block: () -> Unit) {
        val original = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(2f)
        try {
            block()
        } finally {
            RuntimeEnvironment.setFontScale(original)
        }
    }
}

private const val SELECTED_QUANTITY = 7
private const val MIN_QUANTITY = 6
private const val MAX_QUANTITY = 20
