package br.gov.caixa.loterias.feature.home

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeDrawResult
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class HomeAccessibilityRegressionTest {
    @get:Rule val compose = createComposeRule()

    @Test fun drawnNumbersKeepSixteenSpAtDoubleFontScale() {
        val lottery = HomeLottery(
            2, "NORMAL", "megasena", "Mega-Sena", 1,
            BigDecimal.TEN, BigDecimal.ONE, "27/10/2026", "", true, HomeLotteryKind.LOTTERY,
            Color.Black, Color.Black, Color.White, 0, 0, 0
        )
        val result = DrawResult(
            "megasena", 1, "27/10/2026", "", listOf(DRAWN_NUMBER), emptyList(), emptyList(),
            false, "", "", "", emptyList()
        )
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                CaixaDSTheme { HomeDrawResult(result, lottery, Modifier) }
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText("07").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(BASE_NUMBER_SIZE_SP, layouts.single().layoutInput.style.fontSize.value, 0f)
    }
}

private const val DRAWN_NUMBER = 7
private const val BASE_NUMBER_SIZE_SP = 16f
