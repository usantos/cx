package br.gov.caixa.loterias.feature.results

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsMatch
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsTeam
import br.gov.caixa.loterias.feature.results.presentation.components.DrawLotecaResults
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
class ResultLotecaLargeFontTest {
    @get:Rule val compose = createComposeRule()

    @Test fun longTeamNamesRemainWholeAndAlignedAtDoubleFont() = withFont(2f) {
        show()
        listOf("BARCELONA", "REAL MADRID").forEach { name ->
            val layout = layout(name)
            assertEquals("The complete team name fits its row", 1, layout.lineCount)
            assertFalse("No team glyph is clipped", layout.hasVisualOverflow)
        }
        val home = compose.onNodeWithText("BARCELONA", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val away = compose.onNodeWithText("REAL MADRID", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals("Team text uses the same aligned column", home.left, away.left, ALIGNMENT_TOLERANCE)
    }

    @Test fun gameCounterKeepsEveryGlyphAtDoubleFont() = withFont(2f) {
        show()
        val node = compose.onNodeWithText("1 de 2", useUnmergedTree = true).assertIsDisplayed()
        val counter = layout("1 de 2")
        val bounds = node.fetchSemanticsNode().boundsInRoot
        assertFalse("O contador não deve ter corte vertical em 200%", counter.didOverflowHeight)
        assertTrue(counter.getLineBottom(counter.lineCount - 1) <= bounds.height)
        // O parágrafo pode reter a largura máxima de medição enquanto o Text usa sua largura intrínseca.
        // Verifica os limites de cada caractere para distinguir essa folga de um corte real.
        for (index in counter.layoutInput.text.indices) {
            val glyph = counter.getBoundingBox(index)
            assertTrue("O caractere $index ultrapassou a borda esquerda", glyph.left >= -GLYPH_BOUNDS_TOLERANCE)
            assertTrue("O caractere $index ultrapassou a borda direita", glyph.right <= bounds.width + GLYPH_BOUNDS_TOLERANCE)
            assertTrue("O caractere $index ultrapassou a borda superior", glyph.top >= -GLYPH_BOUNDS_TOLERANCE)
            assertTrue("O caractere $index ultrapassou a borda inferior", glyph.bottom <= bounds.height + GLYPH_BOUNDS_TOLERANCE)
        }
    }

    @Test fun winningTeamTextHasAaContrastAtDefaultFont() = withFont(1f) {
        show()
        val foreground = layout("REAL MADRID").layoutInput.style.color
        val contrast = ColorUtils.calculateContrast(foreground.toArgb(), CaixaDSColorTokens.RapidExcluded.toArgb())
        assertTrue("Winner text contrast is $contrast, minimum $MIN_TEXT_CONTRAST", contrast >= MIN_TEXT_CONTRAST)
    }

    private fun show() {
        val match = DrawSportsMatch(
            1,
            DrawSportsTeam("BARCELONA", countryCode = "ESP", won = false),
            DrawSportsTeam("REAL MADRID", countryCode = "ESP", won = true)
        )
        compose.setContent {
            CaixaDSTheme {
                Box(Modifier.size(360.dp, 640.dp)) {
                    DrawLotecaResults(
                        listOf(match, match.copy(number = 2, home = DrawSportsTeam("OUTRO"), away = DrawSportsTeam("VISITANTE"))),
                        Color.White
                    )
                }
            }
        }
    }

    private fun layout(text: String): TextLayoutResult {
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(text, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        return layouts.single()
    }

    private fun withFont(scale: Float, block: () -> Unit) {
        val previous = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(scale)
        try {
            block()
        } finally {
            RuntimeEnvironment.setFontScale(previous)
        }
    }
}

private const val ALIGNMENT_TOLERANCE = .5f
private const val MIN_TEXT_CONTRAST = 4.5
private const val GLYPH_BOUNDS_TOLERANCE = .5f
