package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.AnnotatedString
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSCard
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSChoiceRow
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSDivider
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSEmptyState
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSLabelledField
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSPoolTutorialPrimary
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSSectionHeader
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTutorialPreference
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSFormContractsJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun labelledFieldPropagatesEditsAndParentCanDisableIt() {
        val value = mutableStateOf("")
        val enabled = mutableStateOf(true)
        val edits = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSLabelledField("CPF", value.value, {
                    edits.add(it)
                    value.value = it
                }, placeholder = "Digite o CPF", enabled = enabled.value)
            }
        }
        compose.onNodeWithText("CPF").assertIsDisplayed()
        compose.onNodeWithText("Digite o CPF").assertIsDisplayed()
        compose.onNode(hasSetTextAction()).performTextReplacement("12345678900")
        compose.onNode(hasSetTextAction()).assertTextEquals("12345678900")
        compose.runOnIdle {
            assertEquals(listOf("12345678900"), edits)
            enabled.value = false
        }
        compose.onNodeWithText("12345678900").assertIsNotEnabled()
        compose.onNode(hasSetTextAction()).assertDoesNotExist()
    }

    @Test
    fun choiceRowsShowOptionalDetailsAndSendActionsFromWholeRow() {
        val single = mutableStateOf(false)
        val multiple = mutableStateOf(false)
        val calls = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSChoiceRow("Simples", "Uma aposta", single.value, {
                        calls.add("single")
                        single.value = !single.value
                    }, Modifier.testTag("single"))
                    CaixaDSChoiceRow("Várias", selected = multiple.value, onClick = {
                        calls.add("multiple")
                        multiple.value =
                            !multiple.value
                    }, modifier = Modifier.testTag("multiple"), multiple = true)
                }
            }
        }
        compose.onNodeWithText("Uma aposta").assertIsDisplayed()
        compose.onNodeWithTag("single").performClick()
        compose.onNodeWithTag("multiple").performClick()
        compose.runOnIdle {
            assertEquals(true, single.value)
            assertEquals(true, multiple.value)
            assertEquals(listOf("single", "multiple"), calls)
        }
    }

    @Test
    fun emptyStateOnlyOffersActionWhenLabelAndCallbackBothExist() {
        val showAction = mutableStateOf(true)
        var actions = 0
        compose.setContent {
            CaixaDSTheme {
                CaixaDSEmptyState(
                    "Sem apostas",
                    "Escolha uma modalidade",
                    actionLabel = "Começar",
                    onAction = if (showAction.value) {
                        (
                            {
                                actions++
                            }
                            )
                    } else {
                        null
                    }
                )
            }
        }
        compose.onNodeWithText("Sem apostas").assertIsDisplayed()
        compose.onNodeWithText("Escolha uma modalidade").assertIsDisplayed()
        compose.onNodeWithText("Começar").performClick()
        compose.runOnIdle {
            assertEquals(1, actions)
            showAction.value = false
        }
        compose.onNodeWithText("Começar").assertDoesNotExist()
    }

    @Test
    fun sectionHeaderAndCardKeepContentWhileOptionalSubtitleAndActionChange() {
        val subtitle = mutableStateOf<String?>("Veja suas opções")
        var clicks = 0
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSSectionHeader("Modalidades", subtitle.value)
                    CaixaDSDivider()
                    CaixaDSCard(Modifier.testTag("active-card"), { clicks++ }) { Text("Abrir aposta") }
                    CaixaDSCard(Modifier.testTag("static-card")) { Text("Informações") }
                }
            }
        }
        compose.onNodeWithText("Modalidades").assertIsDisplayed()
        compose.onNodeWithText("Veja suas opções").assertIsDisplayed()
        compose.onNodeWithTag("active-card").performClick()
        compose.onNodeWithText("Informações").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, clicks)
            subtitle.value = null
        }
        compose.onNodeWithText("Veja suas opções").assertDoesNotExist()
        compose.onNodeWithText("Modalidades").assertIsDisplayed()
    }

    @Test
    fun tutorialPreferenceTogglesAndPrimaryButtonKeepsItsAction() {
        val checked = mutableStateOf(false)
        var toggles = 0
        var starts = 0
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSTutorialPreference(checked.value, {
                        toggles++
                        checked.value = !checked.value
                    }, modifier = Modifier.testTag("preference"))
                    CaixaDSPoolTutorialPrimary(AnnotatedString("Entendi"), { starts++ })
                }
            }
        }
        compose.onNodeWithText("  Não exibir novamente").assertIsDisplayed()
        compose.onNodeWithTag("preference").assertIsOff().performClick().assertIsOn().performClick().assertIsOff()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertEquals(2, toggles)
            assertEquals(1, starts)
        }
    }
}
