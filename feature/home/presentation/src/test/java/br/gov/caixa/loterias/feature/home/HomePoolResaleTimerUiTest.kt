// Literais representam escalas, intervalos e cores explícitos dos cenários.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.navigation.HomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.home.presentation.components.HomePoolResaleTimer
import br.gov.caixa.loterias.feature.home.presentation.components.rememberHomePoolResale
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomePoolResaleCountdown
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
class HomePoolResaleTimerUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun normalFontKeepsFixedHeightAndUpdatesCountdownColors() = atFontScale(1f) {
        renderAndCheckColors(1f)
        val bounds = compose.onNodeWithTag(TIMER_TAG).getUnclippedBoundsInRoot()
        assertEquals(80f, (bounds.bottom - bounds.top).value, .5f)
    }

    @Test fun enlargedFontFitsAllLabelsInItsContentHeightAndUpdatesColors() = atFontScale(2f) {
        renderAndCheckColors(2f)
        val title = compose.onNodeWithText("Você tem").getUnclippedBoundsInRoot()
        val counter = compose.onNodeWithText("00:01").getUnclippedBoundsInRoot()
        val caption = compose.onNodeWithText("para comprar o seu bolão").getUnclippedBoundsInRoot()
        assertTrue(title.bottom <= counter.top)
        assertTrue(counter.bottom <= caption.top)
        assertTrue(compose.onNodeWithTag(TIMER_TAG).getUnclippedBoundsInRoot().bottom >= caption.bottom)
    }

    @Test fun serverClockOpensResaleWindowTicksToExpiryAndStopsWhenDisposed() = atFontScale(1f) {
        val originalAutoAdvance = compose.mainClock.autoAdvance
        compose.mainClock.autoAdvance = false
        var now = Instant.parse("2026-10-21T20:59:59Z")
        var reads = 0
        val visible = mutableStateOf(true)
        val serverTime = ServerTimeSource {
            reads++
            now
        }
        try {
            compose.setContent {
                CompositionLocalProvider(
                    LocalHomeRuntimeConfiguration provides HomeRuntimeConfiguration(false, "teste", serverTime)
                ) {
                    CaixaDSTheme {
                        if (visible.value) {
                            val countdown by rememberHomePoolResale("21/10/2026 18:00:00", "21/10/2026 18:00:02")
                            countdown?.let { HomePoolResaleTimer(it) }
                        }
                    }
                }
            }
            compose.mainClock.advanceTimeByFrame()
            compose.onNodeWithTag(TIMER_TAG).assertDoesNotExist()

            fun tickTo(instant: String) {
                compose.runOnIdle { now = Instant.parse(instant) }
                compose.mainClock.advanceTimeBy(1_000)
                compose.mainClock.advanceTimeByFrame()
            }

            tickTo("2026-10-21T21:00:00Z")
            compose.onNodeWithText("00:02").assertIsDisplayed()
            assertEquals(Color(0xFFF60E0E), layoutOf("00:02").layoutInput.style.color)
            tickTo("2026-10-21T21:00:01Z")
            compose.onNodeWithText("00:01").assertIsDisplayed()
            compose.onNodeWithText("00:02").assertDoesNotExist()
            tickTo("2026-10-21T21:00:02Z")
            compose.onNodeWithTag(TIMER_TAG).assertDoesNotExist()

            compose.runOnIdle { visible.value = false }
            compose.mainClock.advanceTimeByFrame()
            val readsAfterDisposal = compose.runOnIdle { reads }
            compose.mainClock.advanceTimeBy(2_000)
            compose.runOnIdle { assertEquals(readsAfterDisposal, reads) }
        } finally {
            compose.mainClock.autoAdvance = originalAutoAdvance
        }
    }

    private fun renderAndCheckColors(fontScale: Float) {
        val countdown = mutableStateOf(HomePoolResaleCountdown("01:10:00", 0xFF004680.toInt()))
        compose.setContent {
            CaixaDSTheme {
                Box(Modifier.width(320.dp)) { HomePoolResaleTimer(countdown.value) }
            }
        }
        compose.onNodeWithTag(TIMER_TAG).assertIsDisplayed()
        for (label in listOf("Você tem", "para comprar o seu bolão")) {
            compose.onNodeWithText(label).assertIsDisplayed()
            assertEquals(CaixaDSColorTokens.NotificationAction, layoutOf(label).layoutInput.style.color)
        }
        val states = listOf(
            HomePoolResaleCountdown("01:10:00", 0xFF004680.toInt()),
            HomePoolResaleCountdown("09:59", 0xFFEAAA28.toInt()),
            HomePoolResaleCountdown("00:01", 0xFFF60E0E.toInt())
        )
        states.forEach { state ->
            compose.runOnIdle { countdown.value = state }
            compose.onNodeWithText(state.label).assertIsDisplayed()
            val layout = layoutOf(state.label)
            assertEquals(Color(state.colorArgb), layout.layoutInput.style.color)
            assertEquals(fontScale, layout.layoutInput.density.fontScale, 0f)
            assertEveryCharacterFits(state.label, layout)
        }
        compose.onNodeWithText("01:10:00").assertDoesNotExist()
        compose.onNodeWithText("09:59").assertDoesNotExist()
    }

    private fun layoutOf(label: String): TextLayoutResult {
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(label).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        return layouts.single()
    }

    private fun assertEveryCharacterFits(label: String, layout: TextLayoutResult) {
        val bounds = compose.onNodeWithText(label).fetchSemanticsNode().boundsInRoot
        val diagnostic = "texto=$label escala=${layout.layoutInput.density.fontScale} " +
            "tamanho=${layout.size} parágrafo=${layout.multiParagraph.width}x${layout.multiParagraph.height} " +
            "limites=$bounds restrições=${layout.layoutInput.constraints}"
        assertFalse("O contador não deve ter corte vertical: $diagnostic", layout.didOverflowHeight)
        assertTrue(
            "A última linha deve caber no contador: $diagnostic",
            layout.getLineBottom(layout.lineCount - 1) <= bounds.height + GLYPH_BOUNDS_TOLERANCE
        )
        // A largura máxima retida pelo parágrafo pode superar a largura intrínseca do Text.
        // Conferir cada caractere detecta cortes reais também na faixa normal de 18 dp.
        for (index in layout.layoutInput.text.indices) {
            val glyph = layout.getBoundingBox(index)
            assertTrue(
                "O caractere $index ultrapassou a borda esquerda: $glyph; $diagnostic",
                glyph.left >= -GLYPH_BOUNDS_TOLERANCE
            )
            assertTrue(
                "O caractere $index ultrapassou a borda direita: $glyph; $diagnostic",
                glyph.right <= bounds.width + GLYPH_BOUNDS_TOLERANCE
            )
            assertTrue(
                "O caractere $index ultrapassou a borda superior: $glyph; $diagnostic",
                glyph.top >= -GLYPH_BOUNDS_TOLERANCE
            )
            assertTrue(
                "O caractere $index ultrapassou a borda inferior: $glyph; $diagnostic",
                glyph.bottom <= bounds.height + GLYPH_BOUNDS_TOLERANCE
            )
        }
    }

    private fun atFontScale(scale: Float, block: () -> Unit) {
        val original = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(scale)
        try {
            block()
        } finally {
            RuntimeEnvironment.setFontScale(original)
        }
    }
}

private const val TIMER_TAG = "home-pool-resale-timer"
private const val GLYPH_BOUNDS_TOLERANCE = .5f
