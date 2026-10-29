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
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceBet
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailBetGrid
import kotlin.math.abs
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MarketplaceNumberRingRasterUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun numberRingMatchesNativeViewport41CircleAndStroke() {
        compose.setContent { CaixaDSTheme {
            Box(Modifier.background(Color.White)) {
                DetailBetGrid(0, MarketplaceBet(listOf(1), emptyList(), false), Color.Green, "MEGA_SENA")
            }
        } }
        val actual = compose.onNodeWithTag("pool-detail-ball-0-0").captureToImage().asAndroidBitmap()
        val expected = Bitmap.createBitmap(actual.width, actual.height, Bitmap.Config.ARGB_8888)
        val context = InstrumentationRegistry.getInstrumentation().context
        val resource = context.resources.getIdentifier("native_pool_number_ring", "drawable", context.packageName)
        val drawable = checkNotNull(ResourcesCompat.getDrawable(context.resources, resource, context.theme))
        drawable.setBounds(0, 0, expected.width, expected.height)
        val canvas = Canvas(expected).apply { drawColor(AndroidColor.WHITE) }
        drawable.draw(canvas)
        var error = 0L
        var samples = 0
        val centerX = expected.width / 2.0
        val centerY = expected.height / 2.0
        val radius = expected.width * .34
        for (y in 0 until expected.height) for (x in 0 until expected.width) {
            val dx = x - centerX
            val dy = y - centerY
            if (dx * dx + dy * dy > radius * radius) {
                val native = expected.getPixel(x, y)
                val compose = actual.getPixel(x, y)
                error += abs(AndroidColor.red(native) - AndroidColor.red(compose))
                error += abs(AndroidColor.green(native) - AndroidColor.green(compose))
                error += abs(AndroidColor.blue(native) - AndroidColor.blue(compose))
                samples += 3
            }
        }
        val mean = error.toDouble() / samples
        assertTrue("35dp native ring annulus RGB mean error: $mean", mean <= 3.0)
    }
}
