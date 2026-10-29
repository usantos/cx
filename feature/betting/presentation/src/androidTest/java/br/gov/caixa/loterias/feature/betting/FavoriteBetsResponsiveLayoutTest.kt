package br.gov.caixa.loterias.feature.betting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.presentation.components.AddFavoriteOptionsDialog
import br.gov.caixa.loterias.feature.betting.presentation.components.FavoriteModalityGrid
import br.gov.caixa.loterias.feature.betting.presentation.state.FavoriteBetsUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

/** Massas apenas de geometria: callbacks são contadores e nunca enviam apostas. */
class FavoriteBetsResponsiveLayoutTest {
    @get:Rule val compose = createComposeRule()

    @Test fun modalityNamesAreCenteredAndCompleteAtNarrowWidth() = checkModalities(1f)

    @Test fun modalityNamesRemainCompleteWithEnlargedFonts() = checkModalities(ENLARGED_FONT_SCALE)

    @Test fun narrowOptionsDialogShowsFullLabelsAndPreservesCallbacks() = checkOptions(1f)

    @Test fun narrowOptionsDialogGrowsForEnlargedFonts() = checkOptions(ENLARGED_FONT_SCALE)

    private fun checkModalities(fontScale: Float) {
        val names = listOf(
            null to "Todas",
            DIA_DE_SORTE_ID to "Dia de Sorte",
            MILIONARIA_ID to "+Milionária",
            MEGA_SENA_ID to "Mega-Sena"
        )
        var selected: Int? = null
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                CaixaDSTheme {
                    Column(Modifier.width(292.dp)) {
                        FavoriteModalityGrid(names, null) { selected = it }
                    }
                }
            }
        }
        names.forEach { (id, name) ->
            val label = if (id == null) name else name.lowercase()
            assertFullText(label)
            assertEquals(TextAlign.Center, layout(label).layoutInput.style.textAlign)
        }
        compose.onNodeWithText("dia de sorte").performClick()
        compose.runOnIdle { assertEquals(DIA_DE_SORTE_ID, selected) }
    }

    private fun checkOptions(fontScale: Float) {
        val modality = LotteryModality(MEGA_SENA_ID, "Mega-Sena", "MEGA_SENA")
        val game = LotteryGameParameters(
            modality, CONTEST_NUMBER, "", MAXIMUM_NUMBER, MINIMUM_SELECTION, MAXIMUM_SELECTION,
            MAXIMUM_SURPRISES, listOf(0, FIRST_RECURRING_OPTION, SECOND_RECURRING_OPTION),
            listOf(BetPrice(MINIMUM_SELECTION, 0, BigDecimal("6.00")))
        )
        val state = FavoriteBetsUiState(
            games = listOf(game),
            addCandidate = FavoriteBet(FAVORITE_ID, MEGA_SENA_ID, "Mega-Sena", "Minha Mega", listOf((1..MINIMUM_SELECTION).toList()))
        )
        var cancelled = 0
        var confirmed = 0
        compose.setContent {
            // Renderiza uma janela de 360dp, produzindo o modal relatado de 312dp, em qualquer densidade de emulador.
            val widthPx = LocalResources.current.displayMetrics.widthPixels
            CompositionLocalProvider(LocalDensity provides Density(widthPx / WINDOW_WIDTH_DP, fontScale)) {
                CaixaDSTheme {
                    AddFavoriteOptionsDialog(state, { cancelled++ }, { confirmed++ }, {}, {}, {})
                }
            }
        }
        assertFullText("Selecione as opções e clique em adicionar", scroll = true)
        assertFullText("Quantidade de teimosinhas", scroll = true)
        compose.onNodeWithText("Quantidade de teimosinhas").performClick()
        compose.onNodeWithText("2").assertIsDisplayed()
        assertFullText("Cancelar", scroll = true)
        compose.onNodeWithText("Cancelar").performClick()
        assertFullText("Adicionar ao carrinho", scroll = true)
        compose.onNodeWithText("Adicionar ao carrinho").assertIsEnabled().performClick()
        compose.runOnIdle {
            assertEquals(1, cancelled)
            assertEquals(1, confirmed)
        }
    }

    private fun assertFullText(text: String, scroll: Boolean = false) {
        val node = compose.onNodeWithText(text, useUnmergedTree = true)
        if (scroll) node.performScrollTo()
        node.assertIsDisplayed()
        val result = layout(text)
        assertFalse(
            "Full label must fit: $text; size=${result.size}; paragraph=${result.multiParagraph.width}x${result.multiParagraph.height}; " +
                "widthOverflow=${result.didOverflowWidth}; heightOverflow=${result.didOverflowHeight}",
            result.hasVisualOverflow
        )
    }

    private fun layout(text: String): TextLayoutResult {
        val results = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(text, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        return results.single()
    }

    private companion object {
        const val ENLARGED_FONT_SCALE = 1.5f
        const val DIA_DE_SORTE_ID = 11
        const val MILIONARIA_ID = 9
        const val MEGA_SENA_ID = 2
        const val FAVORITE_ID = 9L
        const val CONTEST_NUMBER = 2900
        const val MAXIMUM_NUMBER = 60
        const val MINIMUM_SELECTION = 6
        const val MAXIMUM_SELECTION = 20
        const val MAXIMUM_SURPRISES = 10
        const val FIRST_RECURRING_OPTION = 2
        const val SECOND_RECURRING_OPTION = 4
        const val WINDOW_WIDTH_DP = 360f
    }
}
