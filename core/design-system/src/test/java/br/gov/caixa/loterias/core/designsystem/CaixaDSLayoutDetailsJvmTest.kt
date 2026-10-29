// Literais numéricos descrevem geometria fixa de teste, IDs das fixtures, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAnchoredTooltip
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSBrandTopBar
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSSupportTopBar
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSTopBar
import br.gov.caixa.loterias.core.designsystem.layout.CaixaDSPrizeMessage
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
class CaixaDSLayoutDetailsJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun prizeMessageKeepsCelebrationAboveUpdatedAwardCopy() {
        val amount = mutableStateOf("Você ganhou R$ 20,00")
        compose.setContent { CaixaDSTheme { CaixaDSPrizeMessage(amount.value, Color.Blue) } }
        val title = compose.onNodeWithText("Pode comemorar!").assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        val message = compose.onNodeWithText(amount.value).assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        assertTrue(title.bottom <= message.top)
        compose.runOnIdle { amount.value = "Você ganhou R$ 100,00" }
        compose.onNodeWithText("Você ganhou R$ 20,00").assertDoesNotExist()
        compose.onNodeWithText("Você ganhou R$ 100,00").assertIsDisplayed()
    }

    @Test
    fun tooltipUsesRowWidthWhenProvidedAndRetainsReadableCopyWhenAnchorChanges() {
        val bounds = mutableStateOf<IntRect?>(null)
        val text = mutableStateOf("Veja o prêmio estimado")
        compose.setContent {
            CaixaDSTheme {
                Box(Modifier.size(32.dp)) { CaixaDSAnchoredTooltip(text.value, {}, rowBounds = bounds.value) }
            }
        }
        val original = compose.onNodeWithText(text.value).assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        assertTrue(original.width > 0)
        compose.runOnIdle {
            bounds.value = IntRect(0, 0, 200, 40)
            text.value = "Prêmio atualizado"
        }
        val changed = compose.onNodeWithText("Prêmio atualizado").assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        assertEquals(with(compose.density) { 252.dp.toPx() } - 200f, original.width - changed.width, 1f)
        compose.onNodeWithText("Veja o prêmio estimado").assertDoesNotExist()
    }

    @Test
    fun extendedToolbarSupportsGradientSubtitleAnnotatedAndCustomContent() {
        val custom = mutableStateOf(false)
        var backs = 0
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSTopBar(
                        "Fallback",
                        { backs++ },
                        gradient = true,
                        subtitle = "Informações da aposta",
                        annotatedTitle = AnnotatedString("Título anotado"),
                        navigationContent = if (custom.value) {
                            (
                                {
                                    Text("Navegação personalizada")
                                }
                                )
                        } else {
                            null
                        },
                        titleContent = if (custom.value) ({ Text("Título personalizado") }) else null,
                        action = { Text("Ação adicional") }
                    )
                    CaixaDSBrandTopBar("Institucional")
                }
            }
        }
        compose.onNodeWithText("Título anotado").assertIsDisplayed()
        compose.onNodeWithText("Informações da aposta").assertIsDisplayed()
        compose.onNodeWithText("Ação adicional").assertIsDisplayed()
        compose.onNodeWithText("Institucional").assertIsDisplayed()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(1, backs)
            custom.value = true
        }
        compose.onNodeWithText("Navegação personalizada").assertIsDisplayed()
        compose.onNodeWithText("Título personalizado").assertIsDisplayed()
        compose.onNodeWithText("Título anotado").assertDoesNotExist()
    }

    @Test
    fun flatExtendedHeaderAndSupportHeaderKeepTheirBackAction() {
        var backs = 0
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSTopBar("Resumo", null, subtitle = "Detalhes adicionais", startColor = Color.Blue, endColor = Color.Blue)
                    CaixaDSSupportTopBar("Ajuda", { backs++ })
                }
            }
        }
        compose.onNodeWithText("Resumo").assertIsDisplayed()
        compose.onNodeWithText("Detalhes adicionais").assertIsDisplayed()
        compose.onNodeWithText("Ajuda").assertIsDisplayed()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle { assertEquals(1, backs) }
    }
}
