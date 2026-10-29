package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartSummary
import br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartSummaryTokens
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

private const val BET_COUNT = 5

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w320dp-h640dp-xhdpi")
class CaixaDSCartSummaryAccessibilityJvmTest {
    @get:Rule val compose = createComposeRule()

    @Test fun actualScaleTwoPreservesCompleteAmountAndLabelCountAndOneNavigationAction() {
        val originalScale = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(2f)
        try {
            var clicks = 0
            compose.setContent {
                CaixaDSTheme {
                    Box(Modifier.width(320.dp)) {
                        CaixaDSCartSummary(BigDecimal("22.00"), BET_COUNT, Modifier.testTag("summary"), { clicks++ })
                    }
                }
            }
            val owner = compose.onNodeWithTag("summary")
            val ownerBounds = owner.fetchSemanticsNode().boundsInRoot
            listOf("R$ 22,00", "Valor atual do carrinho").forEach { text ->
                val node = compose.onNodeWithText(text, useUnmergedTree = true).assertIsDisplayed()
                val layouts = mutableListOf<TextLayoutResult>()
                node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
                val layout = layouts.single()
                assertEquals("Must exercise actual system scale", 2f, layout.layoutInput.density.fontScale, 0f)
                assertFalse(
                    "Complete cart text required: $text size=${layout.size} constraints=${layout.layoutInput.constraints}",
                    layout.hasVisualOverflow
                )
                val bounds = node.fetchSemanticsNode().boundsInRoot
                assertTrue(
                    "Text must stay inside summary: $text $bounds owner=$ownerBounds",
                    bounds.left >= ownerBounds.left && bounds.top >= ownerBounds.top &&
                        bounds.right <= ownerBounds.right && bounds.bottom <= ownerBounds.bottom
                )
                assertTrue("Last line must fit text bounds", layout.getLineBottom(layout.lineCount - 1) <= bounds.height)
            }
            compose.onNodeWithText(BET_COUNT.toString(), useUnmergedTree = true).assertIsDisplayed()
            owner.assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "5 apostas")).performClick()
            compose.runOnIdle { assertEquals(1, clicks) }
        } finally {
            RuntimeEnvironment.setFontScale(originalScale)
        }
    }

    @Test fun normalScaleRetainsOriginalFooterHeightAndFullCount() {
        val originalScale = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(1f)
        try {
            compose.setContent {
                CaixaDSTheme { CaixaDSCartSummary(BigDecimal("22.00"), BET_COUNT, Modifier.testTag("summary")) }
            }
            compose.onNodeWithTag("summary").assertHeightIsEqualTo(CaixaDSCartSummaryTokens.Height)
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "5 apostas"))
        } finally {
            RuntimeEnvironment.setFontScale(originalScale)
        }
    }
}
