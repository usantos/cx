// Literais numéricos descrevem geometria fixa de teste, IDs das fixtures, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.geometry.Rect
import br.gov.caixa.loterias.core.designsystem.components.animationImage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CaixaDSAnimationImageJvmTest {
    @Test
    fun artworkFitsItsOpaqueBoundsWithoutStretchingAndCentersUnusedSpace() {
        val source = Bitmap.createBitmap(8, 8, Bitmap.Config.ARGB_8888)
        for (y in 2..3) for (x in 2..5) source.setPixel(x, y, Color.RED)
        val result = animationImage(source, Rect(0f, 0f, 1f, 1f), 8, 8)
        assertEquals(8, result.width)
        assertEquals(8, result.height)
        assertEquals(Color.TRANSPARENT, result.getPixel(4, 0))
        assertEquals(Color.RED, result.getPixel(4, 3))
        assertEquals(Color.RED, result.getPixel(4, 4))
        assertEquals(Color.TRANSPARENT, result.getPixel(4, 7))
        assertEquals(Color.RED, source.getPixel(2, 2))
        assertEquals(Color.TRANSPARENT, source.getPixel(0, 0))
    }

    @Test
    fun cropExcludesOtherArtworkAndTransparentSourceStaysTransparent() {
        val source = Bitmap.createBitmap(4, 2, Bitmap.Config.ARGB_8888)
        for (y in 0..1) {
            source.setPixel(0, y, Color.BLUE)
            source.setPixel(1, y, Color.BLUE)
            source.setPixel(2, y, Color.RED)
            source.setPixel(3, y, Color.RED)
        }
        // Mantém o recorte na resolução nativa: a ampliação filtrada mistura texels vizinhos
        // da origem na borda; o ajuste e a escala são verificados pelo teste acima.
        val crop = animationImage(source, Rect(.5f, 0f, 1f, 1f), 2, 2)
        assertEquals(2, crop.width)
        assertEquals(2, crop.height)
        assertEquals(Color.RED, crop.getPixel(1, 1))
        assertEquals(Color.RED, crop.getPixel(0, 0))
        val transparent = animationImage(Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888), Rect(0f, 0f, 1f, 1f), 3, 4)
        assertEquals(3, transparent.width)
        assertEquals(4, transparent.height)
        for (y in 0 until 4) for (x in 0 until 3) assertEquals(Color.TRANSPARENT, transparent.getPixel(x, y))
    }

    @Test
    fun invalidCropOrOutputGeometryIsRejectedBeforeDrawing() {
        val source = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888)
        listOf(
            Rect(-.1f, 0f, 1f, 1f),
            Rect(0f, -.1f, 1f, 1f),
            Rect(0f, 0f, 1.1f, 1f),
            Rect(0f, 0f, 1f, 1.1f),
            Rect(.5f, 0f, .5f, 1f),
            Rect(0f, .5f, 1f, .5f)
        ).forEach { region ->
            assertThrows(IllegalArgumentException::class.java) { animationImage(source, region, 4, 4) }
        }
        assertThrows(IllegalArgumentException::class.java) { animationImage(source, Rect(0f, 0f, 1f, 1f), 0, 4) }
        assertThrows(IllegalArgumentException::class.java) { animationImage(source, Rect(0f, 0f, 1f, 1f), 4, -1) }
    }
}
