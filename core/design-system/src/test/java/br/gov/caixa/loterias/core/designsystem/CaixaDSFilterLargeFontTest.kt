package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterAccordionHeader
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterButton
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterNumberCell
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
class CaixaDSFilterLargeFontTest {
    @get:Rule val compose = createComposeRule()

    @Test fun numberCellKeepsBothDigitsReadableAndTogglesOnce() = checkControl("16") { click ->
        CaixaDSMarketplaceFilterNumberCell(TWO_DIGIT_SELECTION, false, onClick = click)
    }

    @Test fun accordionTitleIsFullyReadableAndOpensOnce() = checkControl("Números que não quero") { click ->
        CaixaDSMarketplaceFilterAccordionHeader("Números que não quero", "unwanted", click)
    }

    @Test fun dialogActionGrowsForItsCompleteLabel() = checkControl("Confirmar seleção de números") { click ->
        CaixaDSMarketplaceFilterButton(
            "Confirmar seleção de números",
            false,
            click,
            height = 31.dp,
            textSize = 12,
            lineHeight = 16.2.sp
        )
    }

    private fun checkControl(label: String, content: @Composable (() -> Unit) -> Unit) {
        val original = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(2f)
        try {
            var calls = 0
            compose.setContent { CaixaDSTheme { Box(Modifier.width(300.dp)) { content { calls++ } } } }
            val node = compose.onNodeWithText(label).assertIsDisplayed()
            val layouts = mutableListOf<TextLayoutResult>()
            node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            val layout = layouts.single()
            assertEquals(2f, layout.layoutInput.density.fontScale, 0f)
            assertFalse("Filter text must not clip at 200%: $label", layout.hasVisualOverflow)
            assertTrue(layout.getLineBottom(layout.lineCount - 1) <= node.fetchSemanticsNode().boundsInRoot.height)
            node.performClick()
            compose.runOnIdle { assertEquals(1, calls) }
        } finally {
            RuntimeEnvironment.setFontScale(original)
        }
    }
}

private const val TWO_DIGIT_SELECTION = 16
