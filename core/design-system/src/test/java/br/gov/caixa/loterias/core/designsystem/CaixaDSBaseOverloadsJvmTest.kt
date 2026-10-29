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
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCard
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSDropdownMenu
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSDropdownMenuItem
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSSurface
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
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
class CaixaDSBaseOverloadsJvmTest {
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
            CaixaDSTheme {
                Column {
                    CaixaDSSurface(contentColor = Color.Magenta) {
                        inheritedColor = LocalContentColor.current
                        CaixaDSText("Estática")
                    }
                    CaixaDSSurface({ clicks++ }, Modifier.testTag("click"), enabled = enabled.value) { CaixaDSText("Abrir") }
                    CaixaDSSurface(selected = selected.value, onClick = {
                        selected.value = !selected.value
                    }, modifier = Modifier.testTag("selected")) { CaixaDSText("Escolher") }
                    CaixaDSSurface(checked = checked.value, onCheckedChange = {
                        checked.value = it
                    }, modifier = Modifier.testTag("checked")) { CaixaDSText("Aceitar") }
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
            CaixaDSTheme {
                Column {
                    CaixaDSText("Texto simples", onTextLayout = { plainLayout = it })
                    CaixaDSText(AnnotatedString("Texto anotado"), onTextLayout = { annotatedLayout = it })
                    CaixaDSIcon(CaixaDSIcons.Back, "Vetor")
                    CaixaDSIcon(ImageBitmap(12, 12), "Bitmap", Modifier.size(24.dp))
                    CaixaDSIcon(ColorPainter(Color.Blue), "Pintura", Modifier.size(24.dp))
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
            CaixaDSTheme {
                Column {
                    CaixaDSCard { CaixaDSText("Informação") }
                    CaixaDSCard({ calls.add("card") }, Modifier.testTag("card"), enabled = enabled.value) { CaixaDSText("Abrir cartão") }
                    CaixaDSIconButton({
                        calls.add("icon")
                    }, Modifier.testTag("icon"), enabled = enabled.value) { CaixaDSIcon(CaixaDSIcons.Back, "Voltar") }
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
            CaixaDSTheme {
                Box {
                    CaixaDSDropdownMenu(expanded.value, { expanded.value = false }) {
                        CaixaDSDropdownMenuItem(
                            { CaixaDSText("Abrir") },
                            {
                                choices++
                                expanded.value = false
                            },
                            leadingIcon = {
                                CaixaDSIcon(CaixaDSIcons.Back, "Origem")
                            },
                            trailingIcon = { CaixaDSIcon(CaixaDSIcons.Forward, "Destino") }
                        )
                        CaixaDSDropdownMenuItem({ CaixaDSText("Bloqueado") }, {}, enabled = false)
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
