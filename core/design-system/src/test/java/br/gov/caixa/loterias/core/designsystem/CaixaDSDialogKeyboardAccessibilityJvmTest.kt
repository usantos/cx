package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

private const val LONG_DIALOG_REPETITIONS = 100

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w320dp-h240dp")
class CaixaDSDialogKeyboardAccessibilityJvmTest {
    @get:Rule val compose = createComposeRule()

    @Test fun shortViewportAtScaleTwoKeepsConfirmationReachableByScrollAndKeyboard() {
        val originalFontScale = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(2f)
        try {
            var confirms = 0
            compose.setContent {
                CaixaDSTheme {
                    CaixaDSDialog("Aviso", "Leia a instrução. ".repeat(LONG_DIALOG_REPETITIONS), "Confirmar", {
                        confirms++
                    }, negativeLabel = "Cancelar")
                }
            }
            val confirmation = compose.onNodeWithText("Confirmar").performScrollTo().assertIsDisplayed()
            val layouts = mutableListOf<TextLayoutResult>()
            confirmation.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            assertEquals("Dialog confirmation must receive actual system scale", 2f, layouts.single().layoutInput.density.fontScale, 0f)
            compose.onNodeWithText("Confirmar").performScrollTo().assertIsDisplayed()
                .performSemanticsAction(SemanticsActions.RequestFocus) { it() }
            compose.onNodeWithText("Confirmar").performKeyInput {
                keyDown(Key.Enter)
                keyUp(Key.Enter)
            }
            compose.runOnIdle { assertEquals(1, confirms) }
        } finally {
            RuntimeEnvironment.setFontScale(originalFontScale)
        }
    }
}
