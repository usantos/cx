package br.gov.caixa.loterias.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.home.presentation.components.LotteryCard
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class HomeResultRevealTest {
    @get:Rule val compose = createComposeRule()
    private val expanded = mutableStateOf(false)
    private val lottery = HomeLottery(
        2, "NORMAL", "megasena", "Mega-Sena", 3073, BigDecimal("200000000"), BigDecimal("6"),
        "22/10/2026 21:00:00", "", true, HomeLotteryKind.LOTTERY,
        Color(0xFF00A651), Color(0xFF00984A), Color.White,
        R.drawable.legacy_home_trevo_megasena_fundo_escuro, R.drawable.icon_data_megasena, R.drawable.icon_horario_megasena
    )

    private fun render() {
        compose.mainClock.autoAdvance = false
        val result = DrawResult(
            "megasena", RESULT_CONTEST_NUMBER, "21/10/2026", "", RESULT_DRAWN_NUMBERS,
            emptyList(), emptyList(), false, "", "0", "", emptyList()
        )
        compose.setContent {
            CaixaDSTheme {
                Box(Modifier.size(280.dp, 600.dp)) {
                    LotteryCard(lottery, ResultsUiState(result = result), expanded.value, { expanded.value = !expanded.value })
                }
            }
        }
    }

    private fun capture(name: String) {
        java.io.File("/tmp/simlo-result-$name.png").outputStream().use {
            compose.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG, PNG_QUALITY, it)
        }
    }

    @Test fun resultKeepsFullContentWidthThroughoutOpenCloseAndReversal() {
        render()
        compose.runOnIdle {
            expanded.value = true
            androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications()
        }
        compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeBy(OPENING_SAMPLE_MILLIS)
        compose.onNodeWithContentDescription("Fechar resultado").assertExists()
        assertEquals(
            RESULT_CONTENT_WIDTH,
            compose.onNodeWithTag("home-result-scroll").getUnclippedBoundsInRoot().let { (it.right - it.left).value },
            GEOMETRY_TOLERANCE
        )
        capture("opening")
        compose.runOnIdle {
            expanded.value = false
            androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications()
        }
        compose.mainClock.advanceTimeBy(REVERSAL_SAMPLE_MILLIS)
        assertEquals(
            RESULT_CONTENT_WIDTH,
            compose.onNodeWithTag("home-result-scroll").getUnclippedBoundsInRoot().let { (it.right - it.left).value },
            GEOMETRY_TOLERANCE
        )
        compose.runOnIdle {
            expanded.value = true
            androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications()
        }
        compose.mainClock.advanceTimeBy(RESULT_ANIMATION_SETTLE_MILLIS)
        val result = compose.onNodeWithTag("home-result-scroll").getUnclippedBoundsInRoot()
        assertEquals(EXPANDED_RESULT_HEIGHT, (result.bottom - result.top).value, GEOMETRY_TOLERANCE)
        assertTrue(compose.onNodeWithTag("home-normal-banner").getUnclippedBoundsInRoot().top.value > EXPANDED_BANNER_MIN_TOP)
        capture("expanded")
        val header = compose.onNodeWithText("Último resultado").getUnclippedBoundsInRoot()
        assertTrue(result.top > header.top)
        compose.onNodeWithText("Último resultado").performClick()
        compose.mainClock.advanceTimeBy(RESULT_ANIMATION_SETTLE_MILLIS)
        assertEquals(
            COLLAPSED_BANNER_TOP,
            compose.onNodeWithTag("home-normal-banner").getUnclippedBoundsInRoot().top.value,
            GEOMETRY_TOLERANCE
        )
    }
}

private const val RESULT_CONTEST_NUMBER = 3073
private val RESULT_DRAWN_NUMBERS = listOf(1, 5, 18, 22, 42, 57)
private const val RESULT_ANIMATION_SETTLE_MILLIS = 400L
private const val COLLAPSED_BANNER_TOP = 32f
private const val GEOMETRY_TOLERANCE = .5f
private const val PNG_QUALITY = 100
private const val OPENING_SAMPLE_MILLIS = 100L
private const val REVERSAL_SAMPLE_MILLIS = 64L
private const val RESULT_CONTENT_WIDTH = 280f
private const val EXPANDED_RESULT_HEIGHT = 497f
private const val EXPANDED_BANNER_MIN_TOP = 500f
