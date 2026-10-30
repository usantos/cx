// Literais numéricos descrevem geometria fixa de teste, IDs das fixtures, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSAttentionIcon
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryMark
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPixMark
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSSelfAssessmentIcon
import br.gov.caixa.loterias.core.designsystem.testing.captureToImage
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CaixaDSBrandArtworkJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun responsibleGamingArtworkPaintsVisibleInkAndPreservesFitCenterMargins() {
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                Column {
                    CaixaDSAttentionIcon(Modifier.size(100.dp, 80.dp).background(Color.White).testTag("attention"))
                    CaixaDSSelfAssessmentIcon(Modifier.size(80.dp).background(Color.White).testTag("assessment"))
                }
            }
        }
        val attention = compose.onNodeWithTag("attention").captureToImage().toPixelMap()
        assertTrue(countInk(attention) > 50)
        // Uma área de desenho vertical ajustada a um quadro horizontal deve deixar ambas as faixas laterais vazias.
        for (y in 0 until attention.height) {
            assertEquals(Color.White, attention[0, y])
            assertEquals(Color.White, attention[attention.width - 1, y])
        }
        val assessment = compose.onNodeWithTag("assessment").captureToImage().toPixelMap()
        assertTrue(countInk(assessment) > 100)
        assertEquals(Color.White, assessment[assessment.width - 1, assessment.height - 1])
    }

    @Test
    fun lotteryAndPixMarksHonorSuppliedBrandColorsWithoutFillingTheirBackground() {
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSLotteryMark(Modifier.size(100.dp, 60.dp).background(Color.Black).testTag("lottery"), Color.Red, Color.Green)
                    CaixaDSPixMark(Modifier.size(80.dp).background(Color.Black).testTag("pix"), Color.Cyan)
                }
            }
        }
        val lottery = compose.onNodeWithTag("lottery").captureToImage().toPixelMap()
        assertTrue(countColor(lottery, Color.Red) > 100)
        assertTrue(countColor(lottery, Color.Green) > 100)
        assertEquals(Color.Black, lottery[0, lottery.height / 2])
        assertEquals(Color.Black, lottery[lottery.width - 1, lottery.height / 2])
        val pix = compose.onNodeWithTag("pix").captureToImage().toPixelMap()
        assertTrue(countColor(pix, Color.Cyan) > 100)
        assertEquals(Color.Cyan, pix[pix.width / 2, pix.height / 2])
        assertEquals(Color.Black, pix[0, 0])
    }

    private fun countInk(pixels: PixelMap): Int {
        var count = 0
        for (y in 0 until pixels.height) for (x in 0 until pixels.width) if (pixels[x, y] != Color.White) count++
        return count
    }

    private fun countColor(pixels: PixelMap, color: Color): Int {
        var count = 0
        for (y in 0 until pixels.height) for (x in 0 until pixels.width) if (pixels[x, y] == color) count++
        return count
    }
}
