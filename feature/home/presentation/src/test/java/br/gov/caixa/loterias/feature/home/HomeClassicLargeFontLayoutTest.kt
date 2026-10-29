package br.gov.caixa.loterias.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.home.navigation.HomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.home.presentation.components.HomeCarousel
import br.gov.caixa.loterias.feature.home.presentation.mapper.remainingTimeLabel
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeActions
import br.gov.caixa.loterias.feature.home.presentation.screen.HomePresentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import java.math.BigDecimal
import java.time.Instant
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
class HomeClassicLargeFontLayoutTest {
    @get:Rule val compose = createComposeRule()
    private var poolCalls = 0
    private var betCalls = 0
    private val lottery = HomeLottery(
        2, "NORMAL", "megasena", "Mega-Sena", 3076,
        BigDecimal("305000000"), BigDecimal("6"), "27/10/2026 21:00:00", "27/10/2026 21:00:00",
        true, HomeLotteryKind.LOTTERY, Color.Green, Color.Black, Color.White,
        R.drawable.trevo_megasena_fundo_branco, R.drawable.ic_calendario, R.drawable.ic_calendario
    )
    private val clock = Instant.parse("2026-10-27T15:00:00Z")
    private val betLabel = "Aposte\npor ${formatBrazilianCurrency(lottery.minimumBetAmount)}"

    @Test fun actualDoubleFontCurrencyAndActionTextRemainFullyReadable() = atDoubleFont {
        showCarousel()
        assertReadable(formatBrazilianCurrency(lottery.estimatedPrize))
        assertReadable("Compre seu bolão")
        assertReadable(betLabel)
    }

    @Test fun countdownCanScrollAboveSeparateReachableActions() = atDoubleFont {
        showCarousel()
        val countdown = compose.onNodeWithText(remainingTimeLabel(lottery.closingDateTime, clock.toEpochMilli()))
        countdown.performScrollTo().assertIsDisplayed()
        val pool = compose.onNodeWithText("Compre seu bolão").assertIsDisplayed()
        assertTrue(
            "Countdown content must not underlap the pool action",
            countdown.getUnclippedBoundsInRoot().bottom <= pool.getUnclippedBoundsInRoot().top
        )
        pool.performClick()
        val bet = compose.onNodeWithText(betLabel)
        try {
            bet.assertIsDisplayed()
        } catch (failure: AssertionError) {
            throw AssertionError(
                "Bet action visibility: clipped=${bet.fetchSemanticsNode().boundsInRoot} " +
                    "unclipped=${bet.getUnclippedBoundsInRoot()} pool=${pool.getUnclippedBoundsInRoot()} " +
                    "countdown=${countdown.getUnclippedBoundsInRoot()} root=${compose.onRoot().getUnclippedBoundsInRoot()}",
                failure
            )
        }
        bet.performClick()
        compose.runOnIdle {
            assertEquals(1, poolCalls)
            assertEquals(1, betCalls)
        }
    }

    private fun assertReadable(text: String) {
        val node = compose.onNodeWithText(text).assertIsDisplayed()
        val layouts = mutableListOf<TextLayoutResult>()
        node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val layout = layouts.single()
        assertEquals("The real screen must render at the OS setting", 2f, layout.layoutInput.density.fontScale, 0f)
        assertFalse(
            "Body/action text must not overflow: $text size=${layout.size} " +
                "paragraph=${layout.multiParagraph.width}x${layout.multiParagraph.height} " +
                "constraints=${layout.layoutInput.constraints} lines=${layout.lineCount} " +
                "overflowWidth=${layout.didOverflowWidth} overflowHeight=${layout.didOverflowHeight} " +
                "node=${node.fetchSemanticsNode().boundsInRoot}",
            layout.hasVisualOverflow
        )
        assertTrue(
            "All text lines must fit their visible node: $text",
            layout.getLineBottom(layout.lineCount - 1) <= node.fetchSemanticsNode().boundsInRoot.height
        )
    }

    private fun showCarousel() {
        compose.setContent {
            CompositionLocalProvider(
                LocalHomeRuntimeConfiguration provides HomeRuntimeConfiguration(
                    false,
                    "fixture",
                    ServerTimeSource {
                        clock
                    }
                )
            ) {
                CaixaDSTheme {
                    val pager = rememberPagerState { 1 }
                    Box(Modifier.size(width = VIEWPORT_WIDTH, height = VIEWPORT_HEIGHT)) {
                        HomeCarousel(
                            HomeUiState(loading = false), listOf(lottery), ResultsUiState(), lottery, pager,
                            HomePresentation(false, null, false, 0.dp, 0f), CARD_WIDTH, PAGE_PADDING, true, false,
                            HomeActions({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, { poolCalls++ }, { betCalls++ }, {})
                        )
                    }
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

private val VIEWPORT_WIDTH = 360.dp
private val VIEWPORT_HEIGHT = 640.dp
private val CARD_WIDTH = 280.dp
private val PAGE_PADDING = 40.dp
