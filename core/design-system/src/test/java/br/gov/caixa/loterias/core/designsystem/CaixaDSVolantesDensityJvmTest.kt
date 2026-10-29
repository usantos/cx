package br.gov.caixa.loterias.core.designsystem

import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSDrawablePainter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "hdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CaixaDSVolantesDensityJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun densityAliasPreservesOriginalMdpiArtworkAtHdpi() {
        compose.setContent {
            val resources = LocalResources.current
            val options = BitmapFactory.Options().apply {
                inDensity = DisplayMetrics.DENSITY_MEDIUM
                inTargetDensity = resources.displayMetrics.densityDpi
            }
            val original = resources.openRawResource(R.drawable.ic_volantes_pixels).use {
                requireNotNull(BitmapFactory.decodeStream(it, null, options)).asImageBitmap()
            }
            Column {
                Image(original, null, Modifier.size(ARTWORK_WIDTH.dp, ARTWORK_HEIGHT.dp).background(Color.White).testTag("original"))
                Image(
                    caixaDSDrawablePainter(R.drawable.ic_volantes),
                    null,
                    Modifier.size(ARTWORK_WIDTH.dp, ARTWORK_HEIGHT.dp).background(Color.White).testTag("alias")
                )
            }
        }
        val original = compose.onNodeWithTag("original").captureToImage().toPixelMap()
        val actual = compose.onNodeWithTag("alias").captureToImage().toPixelMap()
        assertEquals(original.width, actual.width)
        assertEquals(original.height, actual.height)
        var difference = 0.0
        for (y in 0 until original.height) {
            for (x in 0 until original.width) {
                val before = original[x, y]
                val after = actual[x, y]
                difference += kotlin.math.abs(before.red - after.red) + kotlin.math.abs(before.green - after.green) +
                    kotlin.math.abs(before.blue - after.blue)
            }
        }
        assertTrue("A interpolação deve preservar a imagem original", difference / (original.width * original.height) < MAX_DIFFERENCE)
    }

    private companion object {
        const val ARTWORK_WIDTH = 180
        const val ARTWORK_HEIGHT = 109
        const val MAX_DIFFERENCE = 0.02
    }
}
