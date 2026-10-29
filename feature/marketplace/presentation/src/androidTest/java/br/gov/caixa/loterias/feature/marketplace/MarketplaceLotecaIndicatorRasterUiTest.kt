package br.gov.caixa.loterias.feature.marketplace

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.res.ResourcesCompat
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.*
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailLotecaMatch
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.math.abs

class MarketplaceLotecaIndicatorRasterUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun uncheckedRingMatchesNativeBackgroundAndTintedImageComposite() {
        compose.setContent { CaixaDSTheme {
            Box(Modifier.background(Color.White)) {
                DetailLotecaMatch(0, LotecaMatchSelection(SportsMatch(1,
                    SportsTeam(1, "A"), SportsTeam(2, "B")), false, false, false))
            }
        } }
        val actual = compose.onNodeWithTag("pool-detail-loteca-0-draw").captureToImage().asAndroidBitmap()
        val expected = Bitmap.createBitmap(actual.width, actual.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(expected).apply { drawColor(AndroidColor.WHITE) }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val drawable = checkNotNull(ResourcesCompat.getDrawable(context.resources,
            R.drawable.legacy_marketplace_ic_radio_unchecked, context.theme)).mutate()
        drawable.setBounds(0, 0, expected.width, expected.height)
        // PartidaLotecaHolder da CX desenha o XML nativo como fundo e como imagem tonalizada.
        drawable.draw(canvas)
        drawable.setTint(AndroidColor.BLACK)
        drawable.draw(canvas)
        var error = 0L
        var samples = 0
        for (y in 0 until expected.height) for (x in 0 until expected.width) {
            val native = AndroidColor.red(expected.getPixel(x, y))
            // Pixels internos evitam diferenças de suavização de bordas entre a plataforma e o Compose.
            if (native in 36..50) {
                error += abs(native - AndroidColor.red(actual.getPixel(x, y)))
                samples++
            }
        }
        assertTrue("The native ring must contain fully covered interior pixels", samples > 20)
        val mean = error.toDouble() / samples
        assertTrue("Native double-layer ring interior mean error: $mean", mean <= 3.0)
    }
}
