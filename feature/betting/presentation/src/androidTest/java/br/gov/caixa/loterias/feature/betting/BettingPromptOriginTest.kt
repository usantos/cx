package br.gov.caixa.loterias.feature.betting

import android.graphics.Color
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingGameRoute
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel
import java.io.File
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Referência pública instalada Dia1316/Lotofácil3798 em 1080x2408/450dpi/fontScale1.
 * textSelecionados nativo tem uma margem inicial de 16dp, não dois paddings de 8dp arredondados separadamente.
 */
class BettingPromptOriginTest {
    @get:Rule val compose = createComposeRule()

    @Test fun diaPromptStartsAtTheInstalledSingleMarginRasterOrigin() {
        render(
            LotteryGameParameters(
                LotteryModality(11, "Dia de Sorte", "DIA_DE_SORTE"), 1316, "06/10/2026", 31, 7, 15, 100, listOf(0),
                listOf(BetPrice(7, 0, BigDecimal("2.5")))
            )
        )
        assertInstalledPromptOrigin("dia")
    }

    @Test fun lotofacilPromptStartsAtTheInstalledSingleMarginRasterOrigin() {
        render(
            LotteryGameParameters(
                LotteryModality(8, "Lotofácil", "LOTOFACIL"), 3798, "06/10/2026", 25, 15, 20, 100, listOf(0),
                listOf(BetPrice(15, 0, BigDecimal("3.5")))
            )
        )
        assertInstalledPromptOrigin("lotofacil")
    }

    private fun render(game: LotteryGameParameters) {
        val vm = BettingGameViewModel(
            game.modality.id,
            GetBettingParametersUseCase { error("Prepared snapshot must avoid network") },
            AddBetToCartUseCase { error("Prompt geometry must never submit") },
            initialParameters = BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, listOf(game))
        )
        compose.setContent { CaixaDSTheme(darkTheme = false) { BettingGameRoute(vm, {}, {}) } }
        compose.waitForIdle()
    }

    private fun assertInstalledPromptOrigin(modality: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val resources = instrumentation.targetContext.resources
        assertEquals(450, resources.displayMetrics.densityDpi)
        assertEquals(1f, resources.configuration.fontScale, 0f)
        instrumentation.waitForIdleSync()
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        try {
            assertEquals(1080, bitmap.width)
            assertEquals(2408, bitmap.height)
            var left = bitmap.width
            var right = 0
            var top = bitmap.height
            var bottom = 0
            // Este recorte da referência somente para leitura contém apenas a orientação pública idêntica e a área branca.
            for (y in 610 until 710) {
                for (x in 80 until 900) {
                    val pixel = bitmap.getPixel(x, y)
                    if (minOf(Color.red(pixel), Color.green(pixel), Color.blue(pixel)) < 250) {
                        left = minOf(left, x)
                        right = maxOf(right, x)
                        top = minOf(top, y)
                        bottom = maxOf(bottom, y)
                    }
                }
            }
            // Conserva a captura e os quatro extremos para distinguir deslocamento de layout de rasterização da fonte.
            File(instrumentation.targetContext.filesDir, "prompt-origin-$modality.png").outputStream().use {
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
            }
            val raster = "left=$left right=$right top=$top bottom=$bottom"
            assertTrue("Prompt must render in the recorded public screen", left < right)
            assertEquals("Installed16dp single-margin glyph origin", 95, left)
            assertEquals("Installed prompt right edge", 543, right)
            assertEquals("Installed prompt top ($raster)", 633, top)
            assertEquals("Installed prompt bottom ($raster)", 671, bottom)
        } finally {
            bitmap.recycle()
        }
    }
}
