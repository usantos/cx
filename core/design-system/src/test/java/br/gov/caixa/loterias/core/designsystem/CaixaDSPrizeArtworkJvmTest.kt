// Limites e dimensões em pixels descrevem o contrato nativo das artes dos canais de premiação.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSMercadoPagoBrand
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPrizeLotteryMark
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPrizeMercadoPagoMark
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAnchoredTooltip
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSCurrentCartChevron
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSPrizeChevron
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
class CaixaDSPrizeArtworkJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun prizeChannelBrandsAndNavigationChevronsPaintTheirSuppliedFrames() {
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSPrizeLotteryMark(Modifier.size(80.dp).background(Color.Black).testTag("lottery"))
                    CaixaDSPrizeMercadoPagoMark(Modifier.size(80.dp, 40.dp).background(Color.White).testTag("prize-mercado"))
                    CaixaDSMercadoPagoBrand(Modifier.size(80.dp, 40.dp).background(Color.White).testTag("mercado-brand"))
                    CaixaDSPrizeChevron(Modifier.size(24.dp).background(Color.White).testTag("prize-chevron"))
                    Image(
                        CaixaDSCurrentCartChevron,
                        contentDescription = "Detalhes da aposta",
                        modifier = Modifier.size(24.dp).background(Color.White).testTag("cart-chevron")
                    )
                }
            }
        }
        compose.onNodeWithTag("prize-mercado").assertContentDescriptionEquals("Mercado Pago").assertIsDisplayed()
        compose.onNodeWithContentDescription("Detalhes da aposta").assertIsDisplayed()
        listOf("prize-mercado", "mercado-brand", "prize-chevron", "cart-chevron").forEach { tag ->
            val pixels = compose.onNodeWithTag(tag).captureToImage().toPixelMap()
            var ink = 0
            for (y in 0 until pixels.height) {
                for (x in 0 until pixels.width) {
                    if (pixels[x, y] != Color.White) ink++
                }
            }
            assertTrue("$tag should paint visible ink", ink > 0)
            assertTrue("$tag should preserve its transparent surround", ink < pixels.width * pixels.height)
        }
        val lottery = compose.onNodeWithTag("lottery").captureToImage().toPixelMap()
        assertEquals(Color.Black, lottery[0, 0])
        assertEquals(17f, CaixaDSCurrentCartChevron.viewportWidth, 0f)
        assertEquals(17f, CaixaDSCurrentCartChevron.viewportHeight, 0f)
    }

    @Test
    fun anchoredTooltipDrawsTriangularPointerAndReadableMessage() {
        compose.setContent {
            CaixaDSTheme {
                CaixaDSAnchoredTooltip("O prêmio estará disponível após a conferência", onDismiss = {}, color = Color.Red)
            }
        }
        compose.onNodeWithText("O prêmio estará disponível após a conferência").assertIsDisplayed()
        val pixels = compose.onNode(isPopup()).captureToImage().toPixelMap()
        var red = 0
        var white = 0
        for (y in 0 until pixels.height) {
            for (x in 0 until pixels.width) {
                if (pixels[x, y] == Color.Red) red++
                if (pixels[x, y] == Color.White) white++
            }
        }
        assertTrue("Tooltip surface and pointer must use supplied red", red > pixels.width)
        assertTrue("Message must retain white foreground", white > 0)
        assertTrue("The pointed top must leave the outer corner transparent", pixels[0, 0] != Color.Red)
    }
}
