package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSHtmlDialog
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LoteriasCaixaDSHtmlAccessibilityJvmTest {
    @get:Rule val compose = createComposeRule()

    @Test fun htmlDialogAnchorBecomesAnAccessibleLinkPreservingTextAndBoldSpans() {
        compose.setContent {
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSHtmlDialog("Ajuda", "<b>Leia</b> <a href=\"https://loterias.caixa.gov.br/termos\">os termos</a>", {})
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText("Leia os termos")
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val text = layouts.single().layoutInput.text
        assertEquals("Leia os termos", text.text)
        assertEquals(1, text.getLinkAnnotations(0, text.length).size)
        assertEquals("os termos", text.getLinkAnnotations(0, text.length).single().let { text.text.substring(it.start, it.end) })
        assertTrue(text.spanStyles.any { it.start == 0 && it.end == "Leia".length })
    }
}
