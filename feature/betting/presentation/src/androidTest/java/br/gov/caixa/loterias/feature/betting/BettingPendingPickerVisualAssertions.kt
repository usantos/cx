package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

/** DialogUtils nativo: cinza_button #a5aab6 com alfa .6, sem fundo na linha antes da escolha. */
internal fun SemanticsNodeInteraction.assertNativeDisabledConfirmPixels() {
    val pixels = captureToImage().toPixelMap()
    // O alfa nativo mistura o texto desabilitado à superfície branca do diálogo.
    val expected = intArrayOf(201, 204, 211)
    var greyPixels = 0
    var bluePixels = 0
    for (y in 0 until pixels.height) for (x in 0 until pixels.width) {
        val pixel = pixels[x, y]
        val rgb = intArrayOf((pixel.red * 255).toInt(), (pixel.green * 255).toInt(), (pixel.blue * 255).toInt())
        if (rgb.indices.all { kotlin.math.abs(rgb[it] - expected[it]) <= 2 }) greyPixels++
        if (pixel.blue - pixel.red > .2f) bluePixels++
    }
    assertTrue("Native disabled Confirmar must render grey glyph pixels", greyPixels > 10)
    assertEquals("Disabled Confirmar must have no enabled blue glyphs", 0, bluePixels)
}

internal fun SemanticsNodeInteraction.assertNativeUnchosenRowPixels() {
    val pixels = captureToImage().toPixelMap()
    val background = pixels[pixels.width / 2, 3]
    assertEquals("Native unchosen row has no selection pill", 1f, background.red, .005f)
    assertEquals(1f, background.green, .005f)
    assertEquals(1f, background.blue, .005f)
}
