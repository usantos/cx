package br.gov.caixa.loterias.core.designsystem

import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSTopBar
import br.gov.caixa.loterias.core.designsystem.testing.captureToImage
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class LoteriasCaixaDSFlatHeaderTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun legacyGradientRequestStillRendersSolidBlue() {
        compose.setContent {
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSTopBar(
                    "Combo",
                    {},
                    gradient = true,
                    startColor = LoteriasCaixaDSColorTokens.TealAction,
                    endColor = LoteriasCaixaDSColorTokens.Blue700
                )
            }
        }
        val pixels = compose.onRoot().captureToImage().toPixelMap()
        val y = pixels.height - 2
        assertEquals(LoteriasCaixaDSColorTokens.Blue700, pixels[2, y])
        assertEquals(LoteriasCaixaDSColorTokens.Blue700, pixels[pixels.width - HEADER_EDGE_INSET, y])
    }
}

private const val HEADER_EDGE_INSET = 3
