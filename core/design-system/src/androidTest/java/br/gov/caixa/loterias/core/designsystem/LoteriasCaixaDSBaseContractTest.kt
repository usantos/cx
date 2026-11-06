package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCard
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCheckbox
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSHorizontalDivider
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSOutlinedButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSRadioButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSTextButton
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoteriasCaixaDSBaseContractTest {
    @get:Rule val compose = createComposeRule()

    @Test fun baseControlsPreserveRenderedAppearanceWhenConsumersMoveToDs() {
        val ds = mutableStateOf(false)
        compose.setContent {
            LoteriasCaixaDSTheme {
                Column(Modifier.width(280.dp)) {
                    if (ds.value) {
                        LoteriasCaixaDSText("Texto e espaçamento")
                        LoteriasCaixaDSIcon(LoteriasCaixaDSIcons.Back, "Voltar")
                        LoteriasCaixaDSButton({}) { LoteriasCaixaDSText("Ação") }
                        LoteriasCaixaDSTextButton({}) { LoteriasCaixaDSText("Secundária") }
                        LoteriasCaixaDSOutlinedButton({}) { LoteriasCaixaDSText("Contorno") }
                        Row {
                            LoteriasCaixaDSCheckbox(true, {})
                            LoteriasCaixaDSRadioButton(false, {})
                        }
                        LoteriasCaixaDSCard { LoteriasCaixaDSText("Cartão") }
                        LoteriasCaixaDSHorizontalDivider()
                    } else {
                        Text("Texto e espaçamento")
                        Icon(LoteriasCaixaDSIcons.Back, "Voltar")
                        Button({}) { Text("Ação") }
                        TextButton({}) { Text("Secundária") }
                        OutlinedButton({}) { Text("Contorno") }
                        Row {
                            Checkbox(true, {})
                            RadioButton(false, {})
                        }
                        Card { Text("Cartão") }
                        HorizontalDivider()
                    }
                }
            }
        }
        val previous = compose.onRoot().captureToImage().asAndroidBitmap()
        compose.runOnIdle { ds.value = true }
        val centralized = compose.onRoot().captureToImage().asAndroidBitmap()
        assertTrue("Ownership migration must preserve rendered controls", previous.sameAs(centralized))
    }

    @Test fun enabledDisabledAndSelectionCallbacksRemainWithTheConsumer() {
        var clicks = 0
        val checked = mutableStateOf(false)
        compose.setContent {
            LoteriasCaixaDSTheme {
                Column {
                    LoteriasCaixaDSButton({ clicks++ }) { LoteriasCaixaDSText("Ativo") }
                    LoteriasCaixaDSButton({ error("Disabled control dispatched") }, enabled = false) { LoteriasCaixaDSText("Inativo") }
                    LoteriasCaixaDSCheckbox(checked.value, { checked.value = it }, Modifier.testTag("check"))
                }
            }
        }
        compose.onNodeWithText("Ativo").performClick()
        compose.onNodeWithText("Inativo").assertIsNotEnabled()
        compose.onNodeWithTag("check").performClick().assertIsOn()
        compose.runOnIdle {
            assertEquals(1, clicks)
            assertTrue(checked.value)
        }
    }
}
