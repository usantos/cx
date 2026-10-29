package br.gov.caixa.loterias.feature.results

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.results.presentation.components.ResultModalityScreen
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
class ResultModalitiesLargeFontTest {
    @get:Rule val compose = createComposeRule()

    @Test fun everyModalityNameRemainsReadableAndReachableAtDoubleFont() = atDoubleFont {
        showModalities()
        listOf(
            "dia de sorte", "dupla sena", "loteca", "lotofácil", "lotomania",
            "+milionária", "mega-sena", "quina", "super sete", "timemania"
        )
            .forEach { text ->
                compose.onNode(hasScrollAction()).performScrollToNode(hasText(text))
                val node = compose.onNodeWithText(text).assertIsDisplayed()
                val layouts = mutableListOf<TextLayoutResult>()
                node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
                val layout = layouts.single()
                assertEquals(2f, layout.layoutInput.density.fontScale, 0f)
                assertFalse("Modality name must not be clipped: $text", layout.hasVisualOverflow)
                assertTrue(layout.getLineBottom(layout.lineCount - 1) <= node.fetchSemanticsNode().boundsInRoot.height)
            }
    }

    @Test fun enlargedModalityStillSelectsItsOwnCodeOnce() = atDoubleFont {
        val selected = mutableListOf<String>()
        showModalities { selected += it }
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("mega-sena"))
        compose.onNodeWithText("mega-sena").performClick()
        compose.runOnIdle { assertEquals(listOf("megasena"), selected) }
    }

    private fun showModalities(onSelect: (String) -> Unit = {}) {
        compose.setContent {
            CaixaDSTheme {
                Box(Modifier.size(width = 360.dp, height = 640.dp)) {
                    ResultModalityScreen({}, onSelect)
                }
            }
        }
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
