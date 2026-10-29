package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFavoriteRetailer
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailPurchasePanel
import br.gov.caixa.loterias.feature.marketplace.presentation.components.MarketplaceCarousel
import br.gov.caixa.loterias.feature.marketplace.presentation.components.MarketplaceTabs
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceTab
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import java.math.BigDecimal
import java.util.Locale
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
class MarketplaceLargeFontTest {
    @get:Rule val compose = createComposeRule()
    private val pool = MarketplacePool(
        "B1", "MEGA_SENA", 3076, "Lotérica Central", "27/10/2026", "12.50", 21, 10, 9, false,
        lotteryId = 10, cityAndState = "São Paulo - SP", totalShares = 100, estimatedPrize = "305000000",
        lastSharePriceWithFeeValue = BigDecimal("12.50")
    )

    @Test fun favoriteRetailerTabLabelIsReadableAndSelectsOnce() = atDoubleFont {
        val selected = mutableListOf<MarketplaceTab>()
        show { MarketplaceTabs(MarketplaceTab.POOLS) { selected += it } }
        val node = compose.onNodeWithText("Lotéricas Favoritas")
        assertReadable(node)
        node.performClick()
        compose.runOnIdle { assertEquals(listOf(MarketplaceTab.FAVORITE_RETAILERS), selected) }
    }

    @Test fun carouselBodyAndAllActionsRemainReachableWithoutTextClipping() = atDoubleFont {
        val calls = mutableListOf<String>()
        show {
            MarketplaceCarousel(
                MarketplaceUiState(pools = listOf(pool), lotteryFiltered = true),
                { calls += "decrease" },
                { calls += "increase" },
                { calls += "favorite" },
                { calls += "detail" },
                { calls += "add" }
            )
        }
        listOf("305.000.000", "apostas de", "cotas disponíveis", "Quantidade de Cotas").forEach { text ->
            assertReadable(reachable(compose.onNode(hasText(text, substring = true))))
        }
        reachable(compose.onNodeWithTag("carousel-increase-B1")).performClick()
        reachable(compose.onNodeWithTag("carousel-detail-B1")).performClick()
        val add = reachable(compose.onNodeWithTag("carousel-add-B1"))
        assertReadable(add)
        add.performClick()
        compose.runOnIdle { assertEquals(listOf("increase", "detail", "add"), calls) }
    }

    @Test fun detailPurchasePanelShowsQuantityAmountsAndCounter() = atDoubleFont {
        val detail = MarketplacePoolDetail(
            "B1", "MEGA_SENA", CONTEST, LOTTERY_ID, "Lotérica Central", "000001", "São Paulo - SP", "27/10/2026",
            "305000000", PROGNOSTICS, AVAILABLE_SHARES, TOTAL_SHARES, NUMBER_COUNT, "12.50", "0", "12.50", emptyList(),
            lastSharePriceWithFeeValue = BigDecimal("12.50")
        )
        show {
            DetailPurchasePanel(MarketplaceDetailUiState(detail = detail, isLoading = false), {}, {}, {}, BigDecimal("7"), 2, {})
        }
        listOf("Quantidade de Cotas", "Valor dessa aposta", "Total do carrinho", "2").forEach {
            assertReadable(compose.onNodeWithText(it, useUnmergedTree = true))
        }
    }

    @Test fun favoriteRetailerCityDoesNotOverlapItsOpenAction() = atDoubleFont {
        var calls = 0
        show { CaixaDSMarketplaceFavoriteRetailer("Caminho da Fortuna", "SAO PAULO - SP", { calls++ }, {}) }
        val city = compose.onNodeWithText("SAO PAULO - SP").assertIsDisplayed()
        val open = compose.onNodeWithText("Ver Bolões").assertIsDisplayed()
        assertReadable(city)
        val cityBounds = city.getUnclippedBoundsInRoot()
        val actionBounds = open.getUnclippedBoundsInRoot()
        assertFalse(
            "City must not overlap the pool action",
            cityBounds.left < actionBounds.right && actionBounds.left < cityBounds.right &&
                cityBounds.top < actionBounds.bottom && actionBounds.top < cityBounds.bottom
        )
        open.performClick()
        compose.runOnIdle { assertEquals(1, calls) }
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setContent { CaixaDSTheme { Box(Modifier.size(width = 360.dp, height = 640.dp)) { content() } } }
    }

    private fun reachable(node: SemanticsNodeInteraction): SemanticsNodeInteraction {
        val ancestors = generateSequence(node.fetchSemanticsNode().parent) { it.parent }
        if (ancestors.any { it.config.contains(SemanticsActions.ScrollBy) }) node.performScrollTo()
        return node
    }

    private fun assertReadable(node: SemanticsNodeInteraction) {
        node.assertIsDisplayed()
        val layouts = mutableListOf<TextLayoutResult>()
        node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val layout = layouts.single()
        assertEquals(2f, layout.layoutInput.density.fontScale, 0f)
        assertFalse("Marketplace body/action must not clip at 200%", layout.hasVisualOverflow)
        assertTrue(layout.getLineBottom(layout.lineCount - 1) <= node.fetchSemanticsNode().boundsInRoot.height)
    }

    private fun atDoubleFont(block: () -> Unit) {
        val original = RuntimeEnvironment.getFontScale()
        val locale = Locale.getDefault()
        RuntimeEnvironment.setFontScale(2f)
        Locale.setDefault(Locale.forLanguageTag("pt-BR"))
        try {
            block()
        } finally {
            RuntimeEnvironment.setFontScale(original)
            Locale.setDefault(locale)
        }
    }
}

private const val CONTEST = 3076
private const val TOTAL_SHARES = 10
private const val PROGNOSTICS = 21
private const val AVAILABLE_SHARES = 100
private const val NUMBER_COUNT = 9

private const val LOTTERY_ID = 10L
