package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.base.*
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CaixaDSBaseContractTest {
    @get:Rule val compose = createComposeRule()

    @Test fun baseControlsPreserveRenderedAppearanceWhenConsumersMoveToDs() {
        val ds = mutableStateOf(false)
        compose.setContent { CaixaDSTheme {
            Column(Modifier.width(280.dp)) {
                if (ds.value) {
                    CaixaDSText("Texto e espaçamento")
                    CaixaDSIcon(CaixaDSIcons.Back, "Voltar")
                    CaixaDSButton({}) { CaixaDSText("Ação") }
                    CaixaDSTextButton({}) { CaixaDSText("Secundária") }
                    CaixaDSOutlinedButton({}) { CaixaDSText("Contorno") }
                    Row { CaixaDSCheckbox(true, {}); CaixaDSRadioButton(false, {}) }
                    CaixaDSCard { CaixaDSText("Cartão") }
                    CaixaDSHorizontalDivider()
                } else {
                    Text("Texto e espaçamento")
                    Icon(CaixaDSIcons.Back, "Voltar")
                    Button({}) { Text("Ação") }
                    TextButton({}) { Text("Secundária") }
                    OutlinedButton({}) { Text("Contorno") }
                    Row { Checkbox(true, {}); RadioButton(false, {}) }
                    Card { Text("Cartão") }
                    HorizontalDivider()
                }
            }
        } }
        val previous = compose.onRoot().captureToImage().asAndroidBitmap()
        compose.runOnIdle { ds.value = true }
        val centralized = compose.onRoot().captureToImage().asAndroidBitmap()
        assertTrue("Ownership migration must preserve rendered controls", previous.sameAs(centralized))
    }

    @Test fun enabledDisabledAndSelectionCallbacksRemainWithTheConsumer() {
        var clicks = 0
        val checked = mutableStateOf(false)
        compose.setContent { CaixaDSTheme { Column {
            CaixaDSButton({ clicks++ }) { CaixaDSText("Ativo") }
            CaixaDSButton({ error("Disabled control dispatched") }, enabled = false) { CaixaDSText("Inativo") }
            CaixaDSCheckbox(checked.value, { checked.value = it }, Modifier.testTag("check"))
        } } }
        compose.onNodeWithText("Ativo").performClick()
        compose.onNodeWithText("Inativo").assertIsNotEnabled()
        compose.onNodeWithTag("check").performClick().assertIsOn()
        compose.runOnIdle { assertEquals(1, clicks); assertTrue(checked.value) }
    }
}
