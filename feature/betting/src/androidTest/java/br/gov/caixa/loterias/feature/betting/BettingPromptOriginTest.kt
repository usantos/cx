package br.gov.caixa.loterias.feature.betting

import android.graphics.Color
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.betting.*
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Public installed Dia1316/Lotofácil3798 oracle at1080x2408/450dpi/fontScale1.
 * Native textSelecionados has one16dp start margin, not two independently rounded8dp paddings.
 */
class BettingPromptOriginTest {
    @get:Rule val compose = createComposeRule()

    @Test fun diaPromptStartsAtTheInstalledSingleMarginRasterOrigin() {
        render(LotteryGameParameters(LotteryModality(11,"Dia de Sorte","DIA_DE_SORTE"),1316,"06/10/2026",31,7,15,100,listOf(0),
            listOf(BetPrice(7,0,BigDecimal("2.5")))))
        assertInstalledPromptOrigin()
    }

    @Test fun lotofacilPromptStartsAtTheInstalledSingleMarginRasterOrigin() {
        render(LotteryGameParameters(LotteryModality(8,"Lotofácil","LOTOFACIL"),3798,"06/10/2026",25,15,20,100,listOf(0),
            listOf(BetPrice(15,0,BigDecimal("3.5")))))
        assertInstalledPromptOrigin()
    }

    private fun render(game:LotteryGameParameters) {
        val vm = BettingGameViewModel(game.modality.id,GetBettingParametersUseCase { error("Prepared snapshot must avoid network") },
            AddBetToCartUseCase { error("Prompt geometry must never submit") },
            initialParameters=BettingParameters(BigDecimal("500"),BigDecimal("30"),50,listOf(game)))
        compose.setContent { CaixaDSTheme(darkTheme=false) { BettingGameRoute(vm,{}, {}) } }
        compose.waitForIdle()
    }

    private fun assertInstalledPromptOrigin() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val resources = instrumentation.targetContext.resources
        assertEquals(450,resources.displayMetrics.densityDpi)
        assertEquals(1f,resources.configuration.fontScale,0f)
        instrumentation.waitForIdleSync()
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        try {
            assertEquals(1080,bitmap.width)
            assertEquals(2408,bitmap.height)
            var left = bitmap.width
            var right = 0
            var top = bitmap.height
            var bottom = 0
            // This read-only oracle crop contains only the identical public prompt and white canvas.
            for (y in 610 until 710) for (x in 80 until 900) {
                val pixel = bitmap.getPixel(x,y)
                if (minOf(Color.red(pixel),Color.green(pixel),Color.blue(pixel)) < 250) {
                    left = minOf(left,x);right = maxOf(right,x)
                    top = minOf(top,y);bottom = maxOf(bottom,y)
                }
            }
            assertTrue("Prompt must render in the recorded public screen",left < right)
            assertEquals("Installed16dp single-margin glyph origin",95,left)
            assertEquals("Installed prompt right edge",543,right)
            assertEquals("Installed prompt top",633,top)
            assertEquals("Installed prompt bottom",671,bottom)
        } finally { bitmap.recycle() }
    }
}
