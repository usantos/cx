package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.marketplace.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MarketplaceDetailSpecialGamesUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun superSevenPreservesColumnMembershipAndZeroWithoutFlatteningIntoOrdinaryBalls() {
        val columns = listOf(listOf(0, 3), listOf(2), listOf(4), listOf(6), listOf(8), listOf(1), listOf(9))
        show(detail("SUPER_7", listOf(MarketplaceBet(columns.flatten(), emptyList(), false, numberColumns = columns))))
        compose.onNodeWithText("Colunas").performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Coluna 1, número 0").assertIsDisplayed()
        val first = compose.onNodeWithTag("pool-detail-super-0-0-0").fetchSemanticsNode().boundsInRoot
        val secondInFirst = compose.onNodeWithTag("pool-detail-super-0-0-1").fetchSemanticsNode().boundsInRoot
        val secondColumn = compose.onNodeWithTag("pool-detail-super-0-1-0").fetchSemanticsNode().boundsInRoot
        assertEquals(first.center.x, secondInFirst.center.x, 1f)
        assertTrue(secondInFirst.top > first.bottom)
        assertTrue(secondColumn.left > first.right)
    }

    @Test fun teamAndMonthUseNativeComplementLabelsAndKeepTheirBackendValues() {
        val team = SportsTeam(7, "Paraná", state = "PR", countryCode = "BRA", selectionIndicator = true)
        val current = mutableStateOf(detail("TIMEMANIA", listOf(MarketplaceBet(listOf(1, 2), emptyList(), false, favoriteTeam = team))))
        compose.setContent { CaixaDSTheme { MarketplaceDetailScreen(MarketplaceDetailUiState(detail = current.value, isLoading = false), {}, {}, {}, {}, {}) } }
        compose.onNodeWithText("Time do coração: Paraná-PR").performScrollTo().assertIsDisplayed()
        compose.runOnIdle { current.value = detail("DIA_DE_SORTE", listOf(MarketplaceBet(listOf(1, 2), emptyList(), false, luckyMonth = LuckyMonth(7, "Julho")))) }
        compose.onNodeWithText("Mês de sorte: Julho").performScrollTo().assertIsDisplayed()
    }

    @Test fun lotecaShowsOnlyFirstBetMatchesAndEveryStoredDoubleOrTripleSelection() {
        val match = SportsMatch(1, SportsTeam(1, "Time Azul", "SP", countryCode = "BRA"),
            SportsTeam(2, "Time Branco", "RJ", countryCode = "BRA"), legendDescription = "FUT MAS")
        val first = MarketplaceBet(emptyList(), emptyList(), false,
            lotecaSelections = listOf(LotecaMatchSelection(match, true, false, true)))
        val hiddenSecond = first.copy(lotecaSelections = listOf(LotecaMatchSelection(match.copy(home = SportsTeam(3, "Não mostrar")), false, true, false)))
        show(detail("LOTECA", listOf(first, hiddenSecond)))
        compose.onNodeWithText("Time Azul/SP (MAS)").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Time Branco/RJ (MAS)").assertIsDisplayed()
        val home = compose.onNodeWithTag("pool-detail-loteca-0-home").fetchSemanticsNode().config[SemanticsProperties.Selected]
        val draw = compose.onNodeWithTag("pool-detail-loteca-0-draw").fetchSemanticsNode().config[SemanticsProperties.Selected]
        val away = compose.onNodeWithTag("pool-detail-loteca-0-away").fetchSemanticsNode().config[SemanticsProperties.Selected]
        assertTrue(home); assertFalse(draw); assertTrue(away)
        compose.onNodeWithText("Não mostrar", substring = true).assertDoesNotExist()
    }

    @Test fun milionariaCountsActualNumbersAndRendersCloversInTheirOwnSixColumnGrid() {
        show(detail("MAIS_MILIONARIA", listOf(MarketplaceBet((1..6).toList(), listOf(2, 5), false))))
        compose.onNodeWithText("2 apostas de 6 dezenas").assertIsDisplayed()
        compose.onNodeWithText("Trevos").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("pool-detail-clover-0-0").assertIsDisplayed()
        compose.onNodeWithTag("pool-detail-clover-0-1").assertIsDisplayed()
    }

    private fun show(detail: MarketplacePoolDetail) = compose.setContent { CaixaDSTheme {
        MarketplaceDetailScreen(MarketplaceDetailUiState(detail = detail, isLoading = false), {}, {}, {}, {}, {})
    } }
    private fun detail(modality: String, bets: List<MarketplaceBet>) = MarketplacePoolDetail("B1", modality, 3070, 10,
        "Lotérica", "1", "Cidade - SP", "14/10/2026", "1000.00", 20, 50, 2, 8, "10.00", "0.50", "10.50", bets)
}
