// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.presentation.components.PoolCartArrow
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartHelpAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
class CartArtworkHelpUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun poolArrowRendersHorizontalConnectorAndDownwardTipInsideItsBounds() {
        compose.setContent {
            CaixaDSTheme {
                PoolCartArrow(Color.Red, Modifier.size(40.dp, 50.dp).background(Color.White).testTag("arrow"))
            }
        }
        val pixels = compose.onNodeWithTag("arrow").captureToImage().toPixelMap()
        fun isRed(x: Int, y: Int) = pixels[x, y].red - pixels[x, y].green > .15f && pixels[x, y].red - pixels[x, y].blue > .15f
        val connectorY = (pixels.height * 18f / 50).toInt()
        assertTrue("Connector must cross the arrow width", (connectorY - 2..connectorY + 2).any { y -> isRed(pixels.width / 2, y) })
        assertTrue(
            "Arrowhead must extend below connector",
            (pixels.height * .75f).toInt().until(pixels.height).any { y ->
                (pixels.width - 5 until pixels.width).any { x -> isRed(x, y) }
            }
        )
        assertEquals(Color.White, pixels[pixels.width / 2, pixels.height / 2])
    }

    @Test fun helpCanonicalizesLegacyAndSpecialModalityNamesWhilePreservingOriginalLabel() {
        val examples = mapOf(
            "Mega-Sena" to 2, "Mega da Virada" to 2, "Quina" to 3, "Quina de São João" to 3,
            "Super Sete" to 7, "SUPER_7" to 7, "Lotofácil" to 8, "Lotofácil da Independência" to 8,
            "+Milionária" to 9, "MAIS_MILIONARIA" to 9, "Dia de Sorte" to 11, "Lotogol" to 14,
            "Lotomania" to 16, "Dupla Sena" to 18, "Dupla de Páscoa" to 18, "Loteca" to 19, "Timemania" to 20
        )
        val calls = mutableListOf<Pair<Int, String>>()
        for ((label, id) in examples) {
            checkNotNull(cartHelpAction(label) { actualId, actualLabel -> calls.add(actualId to actualLabel) }).invoke()
            assertEquals(id to label, calls.last())
        }
        assertNull(cartHelpAction("Unknown") { _, _ -> error("Unsupported help must not navigate") })
        assertNull(cartHelpAction("Mega-Sena", null))
        assertEquals(examples.size, calls.size)
    }
}
