package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceScreen

class MarketplaceCarouselParityUiTest {
    @get:Rule val compose = createComposeRule()
    private val pool = MarketplacePool("B1", "MEGA_SENA", 999, "Fonte da Sorte", "31/12/2026",
        "12.50", 83, 3, 10, false, 10, "São Paulo - SP", 100,
        lastSharePriceWithFeeValue = BigDecimal("12.50"), regularSharePriceWithFee = BigDecimal.TEN)
    private val header = LotteryGameParameters(LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
        4100, "2026-12-31", 60, 6, 20, 1, emptyList(), emptyList(), estimatedPrize = BigDecimal("7654321"))

    @Test fun headerUsesLiveContestAndFirstFourPoolsAreNotRepeatedInList() {
        compose.setContent { CaixaDSTheme { Screen(MarketplaceUiState(
            pools = (1..5).map { pool.copy(code = "B$it") }, contestHeader = header)) } }
        compose.onNodeWithText("4100").assertIsDisplayed()
        compose.onNodeWithText("31/12").assertIsDisplayed()
        compose.onNodeWithTag("marketplace-carousel").assertIsDisplayed()
        compose.onNodeWithTag("pool-list-B1").assertDoesNotExist()
        compose.onNodeWithTag("marketplace-pools-list").performScrollToNode(hasTestTag("pool-list-B5"))
        compose.onNodeWithTag("pool-list-B5").assertIsDisplayed()
    }

    @Test fun collapsedListPreservesBothNativeSummaryLinesAndCompleteRetailerName() {
        compose.setContent { CaixaDSTheme { Screen(MarketplaceUiState(
            pools = (1..5).map { pool.copy(code = "B$it", lotteryName = "Lotérica Centro 5") })) } }
        compose.onNodeWithTag("marketplace-pools-list").performScrollToNode(hasTestTag("pool-list-B5"))
        compose.onNode(hasText("Lotérica Centro 5") and hasAnyAncestor(hasTestTag("pool-list-B5"))).assertIsDisplayed()
        compose.onNodeWithText("Restam 83 de 100 cotas\n3 apostas de 10 dezenas").assertIsDisplayed()
    }

    @Test fun longListRetailerNameWrapsAtWordsWithoutClippingItsSecondLine() {
        compose.setContent { CaixaDSTheme { Screen(MarketplaceUiState(
            pools = (1..5).map { pool.copy(code = "B$it", lotteryName = "LOTÉRICA DA PRAÇA CENTRAL DE BRASÍLIA COMÉRCIO E SERVIÇOS") })) } }
        compose.onNodeWithTag("marketplace-pools-list").performScrollToNode(hasTestTag("pool-list-B5"))
        val title = compose.onNode(hasText("Lotérica da Praça Central de\nBrasília Comércio e Serviços") and
            hasAnyAncestor(hasTestTag("pool-list-B5")))
        title.assertIsDisplayed()
        val layouts = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
        title.performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(2, layouts.single().lineCount)
        val layout = layouts.single()
        org.junit.Assert.assertFalse("Title overflow: width=${layout.didOverflowWidth}, height=${layout.didOverflowHeight}, size=${layout.size}, maxLine=${(0 until layout.lineCount).maxOf { layout.getLineRight(it) }}, constraints=${layout.layoutInput.constraints}", layout.hasVisualOverflow)
    }

    @Test fun exhaustedListPreservesNativeEndLabel() {
        compose.setContent { CaixaDSTheme { Screen(MarketplaceUiState(
            pools = (1..6).map { pool.copy(code = "B$it") }, canLoadMore = false)) } }
        compose.onNodeWithTag("marketplace-pools-list").performScrollToNode(hasText("Fim da lista"))
        compose.onNodeWithText("Fim da lista").assertIsDisplayed()
    }

    @Test fun carouselPreservesQuantityDetailFavoriteAndReservationActions() {
        val calls = mutableListOf<String>()
        compose.setContent { CaixaDSTheme { Screen(MarketplaceUiState(pools = listOf(pool)), calls) } }
        compose.onNodeWithTag("carousel-increase-B1").performClick()
        compose.onNodeWithTag("carousel-detail-B1").performClick()
        compose.onNodeWithTag("carousel-favorite-B1").performClick()
        compose.onNodeWithTag("carousel-add-B1").performClick()
        compose.runOnIdle { assertEquals(listOf("increase-B1", "detail-B1", "favorite-B1", "add-B1"), calls) }
    }

    @Composable private fun Screen(state: MarketplaceUiState, calls: MutableList<String> = mutableListOf()) {
        MarketplaceScreen(state, onBack = {}, onFilter = {}, onHelp = {}, onRetailerSelected = {},
            onRetry = {}, onLoadMore = {}, onPoolSelected = { calls += "detail-$it" },
            onToggleExpanded = {}, onDecrease = {}, onIncrease = { calls += "increase-$it" },
            onFavorite = { calls += "favorite-$it" }, onAddToCart = { calls += "add-$it" },
            onSelectTab = {}, onRemoveRetailer = {}, retailerView = false)
    }
}
