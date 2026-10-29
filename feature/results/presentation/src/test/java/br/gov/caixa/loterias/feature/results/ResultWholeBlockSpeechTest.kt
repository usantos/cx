package br.gov.caixa.loterias.feature.results

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.results.domain.model.DrawPayout
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsMatch
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsTeam
import br.gov.caixa.loterias.feature.results.presentation.components.DrawLotecaResults
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultDrawSpokenDescription
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultPayoutSpokenDescription
import br.gov.caixa.loterias.feature.results.presentation.screen.ResultContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xhdpi")
class ResultWholeBlockSpeechTest {
    @get:Rule val compose = createComposeRule()

    @Test fun numericDrawIsACompleteBlockWithContestNumbersAndDateInWords() {
        compose.setContent { CaixaDSTheme { ResultContent(result(), "megasena") } }
        compose.onNodeWithContentDescription(
            "Mega-Sena. Concurso três mil e vinte e seis. Números sorteados: dois, três. " +
                "Data do sorteio: vinte e sete de outubro de dois mil e vinte e seis."
        ).assertIsDisplayed()
    }

    @Test fun payoutKeepsRangeWinnerCountAndMoneyInTheSameReadout() {
        compose.setContent { CaixaDSTheme { ResultContent(result(), "megasena") } }
        compose.onNodeWithContentDescription(
            "Premiação. Quadra - quatro números acertados. Duas apostas ganhadoras. Prêmio: três reais e cinquenta centavos."
        ).assertExists()
    }

    @Test fun secondDrawHasItsOwnLabelAndDoesNotMergeItsNumbersWithTheFirst() {
        val draw = result().copy(modality = "duplasena", secondDrawNumbers = listOf(0, 1))
        val spoken = resultDrawSpokenDescription(draw, "duplasena")
        assertTrue(spoken.contains("Primeiro sorteio: dois, três. Segundo sorteio: zero, um."))
    }

    @Test fun superSevenAnnouncesEveryColumnAndZeroInOrder() {
        val draw = result().copy(modality = "supersete", firstDrawNumberColumns = List(SUPER_SEVEN_COLUMNS) { listOf(0, 2) })
        val spoken = resultDrawSpokenDescription(draw, "supersete")
        assertTrue(spoken.contains("Coluna um: zero, dois. Coluna dois: zero, dois."))
        assertTrue(spoken.contains("Coluna sete: zero, dois."))
        assertFalse(spoken.any(Char::isDigit))
    }

    @Test fun cloversStayLabelledSeparatelyFromTheDrawnNumbers() {
        val draw = result().copy(modality = "maismilionaria", clovers = listOf(1, 2))
        assertTrue(
            resultDrawSpokenDescription(draw, "maismilionaria").contains("Números sorteados: dois, três. Trevos sorteados: um, dois.")
        )
    }

    @Test fun emptyWinningRangeDoesNotAnnounceTheHiddenPrizeAsAWinningAmount() {
        assertEquals(
            "Premiação do segundo sorteio. Quadra - quatro números acertados. Sem ganhadores.",
            resultPayoutSpokenDescription("Premiação do 2º sorteio", "megasena", DrawPayout("Quadra - 4 números acertados", 0, "9999.99"))
        )
    }

    @Test fun lotecaReadsTheVisibleMatchAsABlockAndKeepsNativePagingActions() {
        val matches = listOf(
            DrawSportsMatch(1, DrawSportsTeam("Casa", "SP", won = true), DrawSportsTeam("Fora", "RJ", won = false)),
            DrawSportsMatch(2, DrawSportsTeam("Outro", "MG"), DrawSportsTeam("Visitante", "PR"), draw = true)
        )
        compose.setContent { CaixaDSTheme { DrawLotecaResults(matches, Color.White) } }
        compose.onNodeWithContentDescription(
            "Loteca. Partida um de dois. Domingo. Jogo um. Casa, SP. Fora, RJ. Vencedor: Casa."
        ).assertIsDisplayed()
        compose.onNodeWithTag("result_loteca_pager").assert(SemanticsMatcher.keyIsDefined(SemanticsActions.ScrollBy))
    }

    private fun result() = DrawResult(
        "megasena", CONTEST, "27/10/2026", "", listOf(2, SELECTED_NUMBER), emptyList(), emptyList(), false, "", "125.50", "",
        listOf(DrawPayout("Quadra - 4 números acertados", 2, "3.50"))
    )
}

private const val CONTEST = 3026

private const val SUPER_SEVEN_COLUMNS = 7

private const val SELECTED_NUMBER = 3
