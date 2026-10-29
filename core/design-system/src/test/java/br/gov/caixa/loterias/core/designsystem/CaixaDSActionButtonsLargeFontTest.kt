package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSButtonVariant
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSFooterButton
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSOutlinedButton
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSPrimaryButton
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
class CaixaDSActionButtonsLargeFontTest {
    @get:Rule val compose = createComposeRule()
    private val label = "Excluir todas as notificações"

    @Test fun inboxPrimaryLabelAndActionArePreserved() = checkButton { click -> CaixaDSButton(label, click) }

    @Test fun inboxSecondaryLabelAndActionArePreserved() = checkButton { click ->
        CaixaDSButton(label, click, variant = CaixaDSButtonVariant.Secondary)
    }

    @Test fun primaryActionLabelAndCallbackArePreserved() = checkButton { click -> CaixaDSPrimaryButton(label, click) }

    @Test fun outlinedActionLabelAndCallbackArePreserved() = checkButton { click -> CaixaDSOutlinedButton(label, click) }

    @Test fun footerLabelAndCallbackArePreserved() = checkButton { click -> CaixaDSFooterButton(label, click) }

    @Test fun disabledInboxActionRemainsDisabledWithReadableLabel() = atDoubleFont {
        compose.setContent { CaixaDSTheme { Box(Modifier.width(300.dp)) { CaixaDSButton(label, {}, enabled = false) } } }
        assertReadable()
        compose.onNodeWithText(label).assertIsNotEnabled()
    }

    private fun checkButton(content: @Composable (() -> Unit) -> Unit) = atDoubleFont {
        var calls = 0
        compose.setContent { CaixaDSTheme { Box(Modifier.width(300.dp)) { content { calls++ } } } }
        assertReadable()
        compose.onNodeWithText(label).performClick()
        compose.runOnIdle { assertEquals(1, calls) }
    }

    private fun assertReadable() {
        val node = compose.onNodeWithText(label).assertIsDisplayed()
        val layouts = mutableListOf<TextLayoutResult>()
        node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val layout = layouts.single()
        assertEquals(2f, layout.layoutInput.density.fontScale, 0f)
        assertFalse("Full action label must remain readable at 200%", layout.hasVisualOverflow)
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
