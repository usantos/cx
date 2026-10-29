package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.test.onRoot
import android.util.TypedValue
import androidx.test.platform.app.InstrumentationRegistry
import kotlin.math.roundToInt

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingLegacyGameHeader
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BettingNativeHeaderUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun normalHeaderMatchesInflatedNativeGeometryAndFontSizes() {
        show(game())
        val title = compose.onNodeWithText("mega-sena")
        assertEquals(16f, layout(title).layoutInput.style.fontSize.value, .01f)
        val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
        val top = root.bottom - px(48)
        assertEquals(px(48), title.fetchSemanticsNode().boundsInRoot.left - root.left, 1f)
        val back = compose.onNodeWithContentDescription("Voltar").fetchSemanticsNode().boundsInRoot
        assertEquals(px(0), back.left - root.left, 1f)
        assertEquals(px(48), back.width, 1f)
        assertEquals(px(48), back.height, 1f)
        val help = compose.onNodeWithContentDescription("Dúvidas").fetchSemanticsNode().boundsInRoot
        assertEquals(px(48), help.width, 1f)
        assertEquals(px(48), help.height, 1f)
        assertEquals(px(4), root.right - help.right, 1f)
        val label = compose.onNodeWithText("Concurso", useUnmergedTree = true)
        val value = compose.onNodeWithText("3073", useUnmergedTree = true)
        assertEquals(xmlFontSize(14f), layout(label).layoutInput.style.fontSize.value, .01f)
        assertEquals(xmlFontSize(12f), layout(value).layoutInput.style.fontSize.value, .01f)
        assertEquals(px(6), label.fetchSemanticsNode().boundsInRoot.top - top, 1f)
        assertEquals(px(0), value.fetchSemanticsNode().boundsInRoot.top - label.fetchSemanticsNode().boundsInRoot.bottom, 1f)
        assertEquals(label.fetchSemanticsNode().boundsInRoot.center.x, value.fetchSemanticsNode().boundsInRoot.center.x, 1f)
    }

    @Test fun modalityPaletteIsPreservedIncludingContrastingInk() {
        val current = mutableStateOf(game())
        compose.setContent { CaixaDSTheme { BettingLegacyGameHeader(BettingGameUiState(game = current.value), {}, {}, null) } }
        listOf(
            Triple(game(), Color(0xFF00A651), Color.White),
            Triple(game(20, "timemania", "TIMEMANIA"), Color(0xFFFFF200), Color(0xFF038141)),
            Triple(game(11, "dia de sorte", "DIA_DE_SORTE"), Color(0xFFE3C021), Color(0xFF7E6906))
        ).forEach { (next, background, ink) ->
            compose.runOnIdle { current.value = next }
            val image = compose.onRoot().captureToImage().toPixelMap()
            assertEquals(background, image[2, image.height - 2])
            assertEquals(ink, layout(compose.onNodeWithText(next.modality.name)).layoutInput.style.color)
        }
    }

    @Test fun specialLotofacilUsesNativeTwentyPercentTitleReduction() {
        show(game(8, "lotofácil", "LOTOFACIL").copy(contestType = "ESPECIAL"))
        val result = layout(compose.onNodeWithText("lotofácil da \nindependência"))
        assertEquals(12.8f, result.layoutInput.style.fontSize.value, .01f)
        assertFalse(result.hasVisualOverflow)
    }

    @Test fun enlargedTextCanWrapAndGrowHeaderWithoutClipping() {
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 1.6f)) {
                CaixaDSTheme { BettingLegacyGameHeader(BettingGameUiState(game = game()), {}, {}, null) }
            }
        }
        val result = layout(compose.onNodeWithText("mega-sena"))
        assertFalse(result.hasVisualOverflow)
        assertTrue(result.lineCount > 1)
        compose.onNodeWithContentDescription("Concurso 3073").assertExists()
    }

    @Test fun annualLotecaUsesNativeFixedTitleRatherThanBackendAlias() {
        show(
            game(19, "loteca", "LOTECA").copy(
                contestType = "ESPECIAL",
                modality = LotteryModality(19, "loteca", "LOTECA", specialName = "Nome do servidor")
            )
        )
        val result = layout(compose.onNodeWithText("loteca especial"))
        assertEquals(12.8f, result.layoutInput.style.fontSize.value, .01f)
        assertFalse(result.hasVisualOverflow)
        compose.onNodeWithText("Resultado", useUnmergedTree = true).assertExists()
    }

    @Test fun simulationQuinaIdUsesNativeSpecialTitle() {
        show(game(1, "quina", "QUINA").copy(contestType = "ESPECIAL"))
        val result = layout(compose.onNodeWithText("quina de \nsão joão"))
        assertEquals(16f, result.layoutInput.style.fontSize.value, .01f)
    }

    private fun show(game: LotteryGameParameters) {
        compose.setContent { CaixaDSTheme { BettingLegacyGameHeader(BettingGameUiState(game = game), {}, {}, null) } }
    }
    private fun layout(node: SemanticsNodeInteraction): TextLayoutResult {
        val results = mutableListOf<TextLayoutResult>()
        node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        return results.single()
    }
    private fun px(dp: Int) = with(compose.density) { dp.dp.toPx() }
    private fun xmlFontSize(size: Float): Float = with(compose.density) {
        // O XML arredonda cada dimensão para pixels antes de entregar o tamanho ao TextView.
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            size,
            InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics
        ).roundToInt().toSp().value
    }
    private fun game(id: Int = 2, name: String = "mega-sena", code: String = "MEGA_SENA") =
        LotteryGameParameters(LotteryModality(id, name, code), 3073, "22/10/2026", 60, 6, 20, 100, emptyList(), emptyList())
}
