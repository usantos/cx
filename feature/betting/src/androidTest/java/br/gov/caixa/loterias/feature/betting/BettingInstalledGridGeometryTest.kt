package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.betting.*
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Compose semantics geometry compared with the saved XCover7 1.24 raster measurements. */
class BettingInstalledGridGeometryTest {
    @get:Rule val compose = createComposeRule()

    @Test fun commonBallsRetainInstalledSixColumnRemainderDistribution() {
        val metrics = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics
        assertEquals(1080, metrics.widthPixels)
        assertEquals(450, metrics.densityDpi)
        val vm = bettingVm(game(9, "+Milionária", 50, 6, 6, listOf(BetPrice(6, 2, BigDecimal("6"))),
            minimumClovers = 2, maximumClovers = 6, maximumCloverNumber = 6))
        compose.setContent { CaixaDSTheme(darkTheme = false) { BettingGameRoute(vm, {}, {}) } }
        val cells = (1..6).map { compose.onNodeWithTag("betting-number-${it.toString().padStart(2, '0')}").fetchSemanticsNode().boundsInRoot }
        assertEquals(listOf(127f, 127f, 127f, 127f, 127f, 127f), cells.map { it.width })
        assertEquals(listOf(0f, 156f, 312f, 469f, 625f, 781f), cells.map { it.left - cells.first().left })
    }

    @Test fun footerSummaryAndNumberGridRemainVisibleAfterComposeMigration() {
        val vm = bettingVm(game(9, "+Milionária", 50, 6, 6, listOf(BetPrice(6, 2, BigDecimal("6"))),
            minimumClovers = 2, maximumClovers = 6, maximumCloverNumber = 6))
        compose.setContent { CaixaDSTheme(darkTheme = false) { BettingGameRoute(vm, {}, {}) } }
        compose.onNodeWithTag("betting-footer-amount-Valor dessa aposta").assertIsDisplayed()
        compose.onNodeWithText("R$ 6,00").assertIsDisplayed()
        compose.onNodeWithTag("betting-number-01").assertIsDisplayed().performClick()
        assertEquals(setOf(1), vm.state.value.selectedNumbers)
        (2..6).forEach { compose.onNodeWithTag("betting-number-${it.toString().padStart(2, '0')}").performClick() }
        vm.primaryAction()
        compose.onNodeWithTag("betting-clover-1").assertIsDisplayed()
    }

    @Test fun cartBadgeKeepsInstalledFooterFrameDimensions() {
        val vm = bettingVm(game(9, "+Milionária", 50, 6, 6, listOf(BetPrice(6, 2, BigDecimal("6"))),
            minimumClovers = 2, maximumClovers = 6, maximumCloverNumber = 6))
        compose.setContent { CaixaDSTheme(darkTheme = false) {
            BettingGameRoute(vm, {}, {}, cartTotal = BigDecimal("12"), cartBetCount = 2)
        } }
        val metrics = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics
        val cart = compose.onNodeWithTag("betting-cart-footer").fetchSemanticsNode().boundsInRoot
        assertEquals(px(56, metrics.density), cart.width.toInt())
        assertEquals(px(64, metrics.density), cart.height.toInt())
        compose.onNodeWithContentDescription("Carrinho de apostas, 2 itens").assertIsDisplayed()
    }

    @Test fun diaDeSorteGridKeepsItsInstalledShortTicketVerticalSpacing() {
        val metrics = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics
        assertEquals(1080, metrics.widthPixels)
        assertEquals(450, metrics.densityDpi)
        val prices = listOf("2.5", "20", "90", "300", "825", "1980", "4290", "8580", "16087.5")
            .mapIndexed { index, amount -> BetPrice(index + 7, 0, BigDecimal(amount)) }
        assertShortTicketSpacing(game(11, "Dia de Sorte", 31, 7, 15, prices), "31", 745f)
    }

    @Test fun lotofacilGridKeepsItsInstalledShortTicketVerticalSpacing() {
        val metrics = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics
        assertEquals(1080, metrics.widthPixels)
        assertEquals(450, metrics.densityDpi)
        assertShortTicketSpacing(game(8, "Lotofácil", 25, 15, 20, listOf(BetPrice(15, 0, BigDecimal("3.5")))), "25", 596f)
    }

    private fun assertShortTicketSpacing(game: LotteryGameParameters, lastNumber: String, expectedPixels: Float) {
        val vm = bettingVm(game)
        compose.setContent { CaixaDSTheme(darkTheme = false) { BettingGameRoute(vm, {}, {}) } }
        val first = compose.onNodeWithTag("betting-number-01").fetchSemanticsNode().boundsInRoot
        val last = compose.onNodeWithTag("betting-number-$lastNumber").fetchSemanticsNode().boundsInRoot
        assertEquals(expectedPixels, last.top - first.top, 1f)
        (1..game.minimumSelection).forEach(vm::toggleNumber)
        assertEquals(game.minimumSelection, vm.state.value.selectedNumbers.size)
    }

    private fun bettingVm(game: LotteryGameParameters) = BettingGameViewModel(
        game.modality.id,
        GetBettingParametersUseCase { error("Prepared snapshot must avoid network") },
        AddBetToCartUseCase { error("Geometry test must never submit") },
        initialParameters = BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, listOf(game)),
    )

    private fun game(
        id: Int, name: String, maximum: Int, minimumSelection: Int, maximumSelection: Int,
        prices: List<BetPrice>, minimumClovers: Int = 0, maximumClovers: Int = 0, maximumCloverNumber: Int = 0,
    ) = LotteryGameParameters(
        LotteryModality(id, name, name), 396, "07/10/2026", maximum, minimumSelection, maximumSelection, 100, listOf(0), prices,
        minimumClovers = minimumClovers, maximumClovers = maximumClovers, maximumCloverNumber = maximumCloverNumber,
    )

    private fun px(dp: Int, density: Float) = (dp * density + .5f).toInt()
}
