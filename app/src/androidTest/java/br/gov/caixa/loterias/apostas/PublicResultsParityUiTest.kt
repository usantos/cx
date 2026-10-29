package br.gov.caixa.loterias.apostas

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.results.domain.model.DrawPayout
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSelectionReward
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsMatch
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsTeam
import br.gov.caixa.loterias.feature.results.domain.model.UpcomingDrawContest
import br.gov.caixa.loterias.feature.results.presentation.route.ResultsScreen
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** As verificações de pixels e posição usam textos não mesclados; as asserções dos blocos falados usam a árvore do TalkBack. */
@RunWith(AndroidJUnit4::class)
class PublicResultsParityUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun superSeteUsesNativeNumberedBoxesWithoutGenericDrawnNumberTitle() {
        val digits = listOf(9, 8, 7, 6, 5, 4, 3)
        show(
            result("SUPER_7").copy(
                firstDrawNumbers = digits,
                firstDrawNumberColumns = digits.map { listOf(it) }
            ),
            "supersete"
        )
        compose.onNodeWithText("Números sorteados:", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("1ª coluna", useUnmergedTree = true).assertDoesNotExist()
        (1..7).forEach { column ->
            compose.onNodeWithTag("result_super_seven_header_$column", useUnmergedTree = true)
                .assertIsDisplayed().assert(hasAnyDescendant(hasText(column.toString())))
            compose.onNodeWithTag("result_super_seven_digit_$column", useUnmergedTree = true)
                .assertIsDisplayed().assert(hasAnyDescendant(hasText(digits[column - 1].toString())))
        }
    }

    @Test fun superSeteResultBoxesMatchNativeThirtyByTwentyFourDpWithThreeDpCellPadding() {
        val digits = listOf(9, 8, 7, 6, 5, 4, 3)
        show(
            result("SUPER_7").copy(
                firstDrawNumbers = digits,
                firstDrawNumberColumns = digits.map { listOf(it) }
            ),
            "supersete"
        )
        val headers = (1..7).map { column ->
            val node = compose.onNodeWithTag("result_super_seven_header_$column", useUnmergedTree = true)
            node.assertWidthIsEqualTo(30.dp).assertHeightIsEqualTo(24.dp)
            node.fetchSemanticsNode().boundsInRoot
        }
        (1..7).forEach { column ->
            val digit = compose.onNodeWithTag("result_super_seven_digit_$column", useUnmergedTree = true)
            digit.assertWidthIsEqualTo(30.dp).assertHeightIsEqualTo(24.dp)
            val bounds = digit.fetchSemanticsNode().boundsInRoot
            // O XML arredonda a célula e cada lado do padding separadamente.
            assertEquals(
                "Native rows retain 3dp cell padding above and below",
                with(compose.density) {
                    (24.dp.roundToPx() + 2 * 3.dp.roundToPx()).toFloat()
                },
                bounds.top - headers[column - 1].top,
                1f
            )
            assertEquals(headers[column - 1].left, bounds.left, 1f)
            if (column > 1) {
                assertEquals(
                    "Native columns retain 3dp padding on both sides",
                    with(compose.density) {
                        (30.dp.roundToPx() + 2 * 3.dp.roundToPx()).toFloat()
                    },
                    headers[column - 1].left - headers[column - 2].left,
                    1f
                )
            }
        }
        // Cor nativa super_sete_claro_mkp de colors.xml, usada por SuperSeteResultadoLayout.rv_head.
        val pixels = compose.onNodeWithTag("result_super_seven_header_1", useUnmergedTree = true)
            .captureToImage().toPixelMap()
        assertEquals(Color(0xFFBED730).toArgb(), pixels[pixels.width / 2, 2].toArgb())
    }

    @Test fun lotecaUsesResultDateLabel() {
        show(result("LOTECA"), "loteca")
        compose.onNodeWithText("Data do resultado: 11 out 2026", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("Data do sorteio: 11 out 2026", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test fun lotecaKeepsThreeOutcomeCardsWinningHighlightAndHorizontalPagination() {
        show(
            result("LOTECA").copy(
                lotecaMatches = listOf(
                    DrawSportsMatch(
                        1,
                        DrawSportsTeam("FLAMENGO", "RJ", won = true),
                        DrawSportsTeam("PALMEIRAS", "SP", won = false),
                        draw = false
                    ),
                    DrawSportsMatch(2, DrawSportsTeam("SANTOS", "SP", won = false), DrawSportsTeam("BAHIA", "BA", won = false), draw = true)
                )
            ),
            "loteca"
        )
        compose.onNodeWithContentDescription(
            "Loteca. Partida um de dois. Domingo. Jogo um. FLAMENGO, RJ. PALMEIRAS, SP. Vencedor: FLAMENGO."
        ).assertIsDisplayed()
        compose.onNodeWithText("1 de 2", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("FLAMENGO", useUnmergedTree = true).assertIsDisplayed()
        compose.onNode(
            hasText("Empate") and hasAnyAncestor(hasContentDescription("Loteca. Partida um de dois.", substring = true)),
            useUnmergedTree = true
        ).assertIsDisplayed()
        compose.onNodeWithText("PALMEIRAS", useUnmergedTree = true).assertIsDisplayed()
        compose.onNode(hasText("FLAMENGO") and hasAnyAncestor(isSelected()), useUnmergedTree = true).assertExists()
        compose.onNodeWithTag("result_loteca_pager").performTouchInput { swipeLeft() }
        compose.onNodeWithContentDescription(
            "Loteca. Partida dois de dois. Domingo. Jogo dois. SANTOS, SP. BAHIA, BA. Resultado: empate."
        ).assertIsDisplayed()
        compose.onNodeWithText("2 de 2", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("SANTOS", useUnmergedTree = true).assertIsDisplayed()
        compose.onNode(hasText("Empate") and hasAnyAncestor(isSelected()), useUnmergedTree = true).assertExists()
    }

    @Test fun timemaniaShowsDrawnTeamBeforeDrawDate() {
        show(result("TIMEMANIA").copy(favoriteTeamReward = DrawSelectionReward("FLAMENGO/RJ", 10, "8")), "timemania")
        val team = compose.onNodeWithText("FLAMENGO/RJ", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val date = compose.onNodeWithText("Data do sorteio: 11 out 2026", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        org.junit.Assert.assertTrue("O time sorteado deve aparecer junto às dezenas, antes da data", team.bottom <= date.top)
    }

    @Test fun diaDeSorteShowsDrawnMonthBeforeDrawDate() {
        show(result("DIA_DE_SORTE").copy(luckyMonthReward = DrawSelectionReward("OUTUBRO", 10, "2")), "diadesorte")
        val month = compose.onNodeWithText("OUTUBRO", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val date = compose.onNodeWithText("Data do sorteio: 11 out 2026", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        org.junit.Assert.assertTrue("O mês sorteado deve aparecer junto às dezenas, antes da data", month.bottom <= date.top)
    }

    @Test fun specialResultKeepsNativeCampaignHeaderArtwork() {
        show(result("MEGA_SENA").copy(specialContest = true, specialName = "Mega da Virada"), "megasena")
        compose.onNodeWithContentDescription("Mega da Virada").assertExists()
    }

    @Test fun maisMilionariaRendersEachDrawnCloverInsteadOfPlainJoinedText() {
        show(result("MAIS_MILIONARIA").copy(clovers = listOf(1, 6)), "maismilionaria")
        compose.onNodeWithContentDescription("Trevo 1", useUnmergedTree = true).assertExists()
        compose.onNodeWithContentDescription("Trevo 6", useUnmergedTree = true).assertExists()
        compose.onNodeWithContentDescription("Trevos sorteados: um, seis.", substring = true).assertExists()
        compose.onNodeWithText("Trevos sorteados: 1 • 6", useUnmergedTree = true).assertDoesNotExist()
    }

    private fun result(modality: String) = DrawResult(
        modality, 3069, "11 out 2026", "",
        listOf(5, 19, 22, 44, 47, 57), emptyList(), emptyList(), false, "", "98228184", "", emptyList()
    )

    private fun show(result: DrawResult, modality: String) {
        compose.setContent {
            CaixaDSTheme {
                ResultsScreen(
                    ResultsUiState(modality = modality, result = result, latestContest = 3069),
                    onBack = {}, onSelectModality = {}, onContestChange = {}, onSearch = {}, onRetry = {},
                    onPreviousContest = {}, onNextContest = {}, showDetail = true
                )
            }
        }
    }

    @Test fun publicResultDoesNotInsertHomeContestSummaryIntoPrizeDetails() {
        val result = DrawResult(
            "MEGA_SENA", 3069, "11 out 2026", "SÃO PAULO - SP",
            listOf(5, 19, 22, 44, 47, 57), emptyList(), emptyList(), true,
            "150000000", "98228184", "Aviso do concurso",
            listOf(DrawPayout("0001", 0, "0"), DrawPayout("0002", 150, "26095.23")),
            nextFinalZeroContest = UpcomingDrawContest(3070, "14 out 2026", "137920959.97")
        )
        compose.setContent {
            CaixaDSTheme {
                ResultsScreen(
                    ResultsUiState(modality = "megasena", result = result, latestContest = 3069),
                    onBack = {}, onSelectModality = {}, onContestChange = {}, onSearch = {}, onRetry = {},
                    onPreviousContest = {}, onNextContest = {}, showDetail = true
                )
            }
        }
        compose.onNodeWithText("Concurso nº 3069", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("Arrecadação total", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("150 apostas ganhadoras", useUnmergedTree = true).assertExists()
        compose.onNodeWithContentDescription("Cento e cinquenta apostas ganhadoras", substring = true).assertExists()
        compose.onNodeWithContentDescription(
            "Arrecadação total: noventa e oito milhões duzentos e vinte e oito mil cento e oitenta e quatro reais."
        ).assertExists()
        compose.onNodeWithText("Acumulou", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("Local do sorteio: SÃO PAULO - SP", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("Aviso do concurso", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("Próximo concurso final zero: 3070", useUnmergedTree = true).assertDoesNotExist()
    }
}
