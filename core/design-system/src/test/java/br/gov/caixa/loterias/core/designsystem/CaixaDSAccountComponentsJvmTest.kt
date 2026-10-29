package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountChoice
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountField
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountInlineWarning
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountSaveFooter
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSAccountComponentsJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun accountFieldShowsMaskThenPropagatesEditsAndExternalUpdates() {
        val value = mutableStateOf("")
        val enabled = mutableStateOf(true)
        val updates = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSAccountField(
                    "Nome",
                    value.value,
                    {
                        updates.add(it)
                        value.value = it
                    },
                    enabled = enabled.value,
                    showValidation = true,
                    emptyMask = "Informe seu nome",
                    error = true
                )
            }
        }
        compose.onNodeWithText("Nome").assertIsDisplayed()
        compose.onNodeWithText("Informe seu nome").assertIsDisplayed()
        val field = compose.onNode(hasSetTextAction())
        field.performTextReplacement("Maria")
        field.assertTextEquals("Maria")
        compose.onNodeWithText("Informe seu nome").assertDoesNotExist()
        compose.runOnIdle {
            value.value = "Nome cadastrado"
            enabled.value = false
        }
        compose.onNodeWithText("Nome cadastrado").assertTextEquals("Nome cadastrado").assertIsNotEnabled()
        compose.onNode(hasSetTextAction()).assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf("Maria"), updates) }
    }

    @Test
    fun currencyFieldFormatsCentsClearsAndHonorsServerReplacement() {
        val value = mutableStateOf("")
        val updates = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSAccountField(
                    "",
                    value.value,
                    {
                        updates.add(it)
                        value.value = it
                    },
                    currencyInput = true,
                    emptyMask = "R$ 0,00"
                )
            }
        }
        compose.onNodeWithText("R$ 0,00").assertIsDisplayed()
        val field = compose.onNode(hasSetTextAction())
        field.performTextReplacement("12345")
        field.assertTextEquals("R$ 123,45")
        field.performTextReplacement("")
        compose.runOnIdle {
            assertEquals(listOf("R$ 123,45", ""), updates)
            value.value = "R$ 50,00"
        }
        field.assertTextEquals("R$ 50,00")
    }

    @Test
    fun accountChoiceReturnsDomainValuesAndSaveRespectsEnabledState() {
        val selected = mutableStateOf(1)
        val canSave = mutableStateOf(false)
        var saves = 0
        val choices = mutableListOf<Int>()
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSAccountChoice(selected.value, listOf("Primeira" to 1, "Segunda" to 2)) {
                        choices.add(it)
                        selected.value = it
                    }
                    CaixaDSAccountInlineWarning("Confira os dados", Modifier.testTag("warning"))
                    CaixaDSAccountSaveFooter("Salvar", { saves++ }, canSave.value)
                }
            }
        }
        compose.onNodeWithText("Primeira").assertIsDisplayed().performClick()
        compose.onNodeWithText("Segunda").performClick()
        compose.onNodeWithTag("warning").assertIsDisplayed()
        compose.onNodeWithText("Confira os dados").assertIsDisplayed()
        compose.onNodeWithText("Salvar").assertIsNotEnabled()
        compose.runOnIdle {
            assertEquals(listOf(1, 2), choices)
            assertEquals(2, selected.value)
            assertEquals(0, saves)
            canSave.value =
                true
        }
        compose.onNodeWithText("Salvar").assertIsEnabled().performClick()
        compose.runOnIdle { assertEquals(1, saves) }
    }
}
