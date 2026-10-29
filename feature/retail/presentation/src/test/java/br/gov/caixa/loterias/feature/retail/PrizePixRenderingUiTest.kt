// Literais numéricos descrevem dados sintéticos, níveis de API da plataforma e limites esperados dos dados de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.retail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeAward
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeDetails
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizePixOtherKeyField
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizePixTab
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeResultHeader
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import java.math.BigDecimal
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
class PrizePixRenderingUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun `selected Pix tab and invalid key have visibly distinct rendered states`() {
        val cpfSelected = mutableStateOf(true)
        val error = mutableStateOf(false)
        val key = mutableStateOf("")
        var density = 1f
        compose.setContent {
            density = LocalDensity.current.density
            CaixaDSTheme {
                Column(Modifier.width(280.dp).padding(8.dp)) {
                    Row(Modifier.fillMaxWidth().height(56.dp).testTag("tabs")) {
                        PrizePixTab("CPF", cpfSelected.value, { cpfSelected.value = true }, Modifier.weight(1f))
                        PrizePixTab("Outra Chave", !cpfSelected.value, { cpfSelected.value = false }, Modifier.weight(1f))
                    }
                    Box(Modifier.fillMaxWidth().testTag("key-field")) { PrizePixOtherKeyField(key.value, { key.value = it }, error.value) }
                }
            }
        }
        val initialTabs = compose.onNodeWithTag("tabs").captureToImage().asAndroidBitmap()
        compose.onNodeWithText("Outra Chave").performClick()
        compose.onNode(hasSetTextAction()).performTextReplacement("invalid")
        val fieldBeforeError = compose.onNodeWithTag("key-field").captureToImage().toPixelMap()
        compose.runOnIdle {
            error.value = true
            Snapshot.sendApplyNotifications()
        }
        compose.waitForIdle()
        val changedTabs = compose.onNodeWithTag("tabs").captureToImage().asAndroidBitmap()
        assertFalse(initialTabs.sameAs(changedTabs))
        compose.onNodeWithText("Chave inválida!").assertExists()
        val pixels = compose.onNodeWithTag("key-field").captureToImage().toPixelMap()
        var changedUnderlinePixels = 0
        // Um traço de um dp pode ter suavização; compara a faixa real do sublinhado com
        // o mesmo texto antes da validação, sem exigir o RGB puro do token.
        for (y in (51f * density).toInt() until minOf(pixels.height, (54f * density).toInt())) {
            for (x in 0 until pixels.width) {
                val before = fieldBeforeError[x, y]
                val after = pixels[x, y]
                if (abs(before.red - after.red) + abs(before.green - after.green) + abs(before.blue - after.blue) >
                    .1f
                ) {
                    changedUnderlinePixels++
                }
            }
        }
        assertTrue("validation must change the rendered underline above its separate error message", changedUnderlinePixels > 100)
        compose.onNode(hasSetTextAction()).performTextReplacement("fixture@example.test")
        compose.runOnIdle {
            error.value = false
            Snapshot.sendApplyNotifications()
        }
        compose.waitForIdle()
        compose.onNodeWithText("Chave inválida!").assertDoesNotExist()
        compose.runOnIdle { assertEquals("fixture@example.test", key.value) }
    }

    @Test fun `prize header draws clipped modality trapezoid and derives recurring range from award contests`() {
        var density = 1f
        lateinit var style: CaixaDSLotteryStyle
        val details = PrizeDetails(
            9,
            BigDecimal.TEN,
            "synthetic",
            emptyList(),
            initialContest = 3000,
            targetContest = 3000,
            recurringDraws = 3,
            awards = listOf(PrizeAward(3000, "Primeira faixa", BigDecimal.ONE), PrizeAward(3001, "Segunda faixa", null))
        )
        compose.setContent {
            density = LocalDensity.current.density
            CaixaDSTheme {
                style = caixaDSLotteryStyle(2, "Mega-Sena")
                Column(Modifier.width(320.dp).testTag("header")) { PrizeResultHeader(PrizeRedemptionUiState(details = details), style) }
            }
        }
        compose.onNodeWithText("3000 - 3001").assertExists()
        val image = compose.onNodeWithTag("header").captureToImage().toPixelMap()
        val dark = image[(5 * density).toInt(), (5 * density).toInt()]
        val outsideClip = image[(85 * density).toInt(), (50 * density).toInt()]
        assertTrue(
            abs(dark.red - style.dark.red) < .03f && abs(dark.green - style.dark.green) < .03f && abs(dark.blue - style.dark.blue) < .03f
        )
        assertTrue(
            abs(outsideClip.red - style.light.red) < .03f && abs(outsideClip.green - style.light.green) < .03f &&
                abs(outsideClip.blue - style.light.blue) < .03f
        )
    }
}
