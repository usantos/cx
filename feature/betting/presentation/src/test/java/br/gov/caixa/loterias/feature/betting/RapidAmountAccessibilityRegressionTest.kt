package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSRapidAmountDialog
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w320dp-h360dp-xhdpi")
class RapidAmountAccessibilityRegressionTest {
    @get:Rule val compose = createComposeRule()

    @Test fun enlargedCancelTextFitsItsReachableActionAndInvokesOnce() {
        val originalFontScale = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(2f)
        try {
            var cancellations = 0
            compose.setContent {
                CaixaDSTheme {
                    CaixaDSRapidAmountDialog(
                        "Valor mínimo",
                        "10",
                        "Digite o valor",
                        "Mínimo 10, máximo 100",
                        null,
                        {},
                        {},
                        { cancellations++ }
                    )
                }
            }
            val cancel = compose.onNodeWithText("Cancelar").performScrollTo().assertIsDisplayed()
            val layouts = mutableListOf<TextLayoutResult>()
            cancel.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            val layout = layouts.single()
            assertEquals("Dialog text must use the actual system fontScale fixture", 2f, layout.layoutInput.density.fontScale, 0f)
            assertFalse(
                "Enlarged action text must not overflow: text=${layout.layoutInput.text.text} " +
                    "density=${layout.layoutInput.density} fontSize=${layout.layoutInput.style.fontSize} " +
                    "size=${layout.size} paragraph=${layout.multiParagraph.width}x${layout.multiParagraph.height} " +
                    "overflow=${layout.hasVisualOverflow} height=${layout.didOverflowHeight} width=${layout.didOverflowWidth} " +
                    "constraints=${layout.layoutInput.constraints} lines=${layout.lineCount} " +
                    "node=${cancel.fetchSemanticsNode().boundsInRoot}",
                layout.hasVisualOverflow
            )
            assertTrue(layout.getLineBottom(layout.lineCount - 1) <= cancel.fetchSemanticsNode().boundsInRoot.height)
            cancel.performClick()
            compose.runOnIdle { assertEquals(1, cancellations) }
            compose.onNodeWithText("Salvar").performScrollTo().assertIsDisplayed()
        } finally {
            RuntimeEnvironment.setFontScale(originalFontScale)
        }
    }
}
