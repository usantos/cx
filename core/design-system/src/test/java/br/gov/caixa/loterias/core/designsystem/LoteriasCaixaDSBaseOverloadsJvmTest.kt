// Literais numéricos descrevem geometria fixa de teste, IDs das fixtures, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCard
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSDropdownMenu
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSDropdownMenuItem
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSSurface
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
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
class LoteriasCaixaDSBaseOverloadsJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun surfacesPreserveContentColorSelectionCheckedAndEnabledContracts() {
        val selected = mutableStateOf(false)
        val checked = mutableStateOf(false)
        val enabled = mutableStateOf(true)
        var clicks = 0
        var inheritedColor = Color.Unspecified
        compose.setContent {
            LoteriasCaixaDSTheme {
                Column {
                    LoteriasCaixaDSSurface(contentColor = Color.Magenta) {
                        inheritedColor = LocalContentColor.current
                        LoteriasCaixaDSText("Estática")
                    }
                    LoteriasCaixaDSSurface({
                        clicks++
                    }, Modifier.testTag("click"), enabled = enabled.value) { LoteriasCaixaDSText("Abrir") }
                    LoteriasCaixaDSSurface(selected = selected.value, onClick = {
                        selected.value = !selected.value
                    }, modifier = Modifier.testTag("selected")) { LoteriasCaixaDSText("Escolher") }
                    LoteriasCaixaDSSurface(checked = checked.value, onCheckedChange = {
                        checked.value = it
                    }, modifier = Modifier.testTag("checked")) { LoteriasCaixaDSText("Aceitar") }
                }
            }
        }
        compose.onNodeWithText("Estática").assertIsDisplayed()
        compose.onNodeWithTag("click").performClick()
        compose.onNodeWithTag("selected").assertIsNotSelected().performClick().assertIsSelected()
        compose.onNodeWithTag("checked").assertIsOff().performClick().assertIsOn()
        compose.runOnIdle {
            assertEquals(Color.Magenta, inheritedColor)
            assertEquals(1, clicks)
            enabled.value = false
        }
        compose.onNodeWithTag("click").assertIsNotEnabled()
    }

    @Test
    fun textOverloadsDeliverLayoutAndIconsExposeAlternativeText() {
        var plainLayout: TextLayoutResult? = null
        var annotatedLayout: TextLayoutResult? = null
        compose.setContent {
            LoteriasCaixaDSTheme {
                Column {
                    LoteriasCaixaDSText("Texto simples", onTextLayout = { plainLayout = it })
                    LoteriasCaixaDSText(AnnotatedString("Texto anotado"), onTextLayout = { annotatedLayout = it })
                    LoteriasCaixaDSIcon(LoteriasCaixaDSIcons.Back, "Vetor")
                    LoteriasCaixaDSIcon(ImageBitmap(12, 12), "Bitmap", Modifier.size(24.dp))
                    LoteriasCaixaDSIcon(ColorPainter(Color.Blue), "Pintura", Modifier.size(24.dp))
                }
            }
        }
        compose.onNodeWithText("Texto simples").assertIsDisplayed()
        compose.onNodeWithText("Texto anotado").assertIsDisplayed()
        listOf("Vetor", "Bitmap", "Pintura").forEach { compose.onNodeWithContentDescription(it).assertIsDisplayed() }
        compose.runOnIdle {
            assertEquals("Texto simples", plainLayout?.layoutInput?.text?.text)
            assertEquals("Texto anotado", annotatedLayout?.layoutInput?.text?.text)
            assertTrue((plainLayout?.lineCount ?: 0) > 0)
            assertTrue((annotatedLayout?.lineCount ?: 0) > 0)
        }
    }

    @Test
    fun clickableCardAndIconButtonHonorParentEnabledState() {
        val enabled = mutableStateOf(true)
        val calls = mutableListOf<String>()
        compose.setContent {
            LoteriasCaixaDSTheme {
                Column {
                    LoteriasCaixaDSCard { LoteriasCaixaDSText("Informação") }
                    LoteriasCaixaDSCard({
                        calls.add("card")
                    }, Modifier.testTag("card"), enabled = enabled.value) { LoteriasCaixaDSText("Abrir cartão") }
                    LoteriasCaixaDSIconButton({
                        calls.add("icon")
                    }, Modifier.testTag("icon"), enabled = enabled.value) { LoteriasCaixaDSIcon(LoteriasCaixaDSIcons.Back, "Voltar") }
                }
            }
        }
        compose.onNodeWithText("Informação").assertIsDisplayed()
        compose.onNodeWithTag("card").performClick()
        compose.onNodeWithTag("icon").performClick()
        compose.runOnIdle {
            assertEquals(listOf("card", "icon"), calls)
            enabled.value = false
        }
        compose.onNodeWithTag("card").assertIsNotEnabled()
        compose.onNodeWithTag("icon").assertIsNotEnabled()
    }

    @Test
    fun menuShowsIconSlotsDisabledItemsAndClosesAfterSelection() {
        val expanded = mutableStateOf(true)
        var choices = 0
        compose.setContent {
            LoteriasCaixaDSTheme {
                Box {
                    LoteriasCaixaDSDropdownMenu(expanded.value, { expanded.value = false }) {
                        LoteriasCaixaDSDropdownMenuItem(
                            { LoteriasCaixaDSText("Abrir") },
                            {
                                choices++
                                expanded.value = false
                            },
                            leadingIcon = {
                                LoteriasCaixaDSIcon(LoteriasCaixaDSIcons.Back, "Origem")
                            },
                            trailingIcon = { LoteriasCaixaDSIcon(LoteriasCaixaDSIcons.Forward, "Destino") }
                        )
                        LoteriasCaixaDSDropdownMenuItem({ LoteriasCaixaDSText("Bloqueado") }, {}, enabled = false)
                    }
                }
            }
        }
        compose.onNodeWithText("Bloqueado").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Origem", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithContentDescription("Destino", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("Abrir").performClick()
        compose.onNodeWithText("Abrir").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, choices)
            assertEquals(false, expanded.value)
        }
    }
}
