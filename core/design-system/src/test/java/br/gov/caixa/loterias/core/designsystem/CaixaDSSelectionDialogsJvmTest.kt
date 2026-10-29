// Literais numéricos descrevem geometria fixa de teste, IDs das fixtures, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.AnnotatedString
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSOptionDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSRapidAmountDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSSelectionDialog
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import java.util.Locale
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSSelectionDialogsJvmTest {
    private val originalLocale = Locale.getDefault()

    @get:Rule
    val compose = createComposeRule()

    @Before
    fun useBrazilianCurrency() {
        Locale.setDefault(Locale.forLanguageTag("pt-BR"))
    }

    @After
    fun restoreLocale() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun dialogDispatchesPositiveNegativeAndNeutralIndependentlyWithExtraContent() {
        val calls = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSDialog(
                    "Atenção", "Selecione uma ação", "Entrar", { calls.add("positive") },
                    negativeLabel = "Voltar", onNegative = { calls.add("negative") },
                    neutralLabel = "Visitante", onNeutral = { calls.add("neutral") },
                    extraContent = { Text("Dados adicionais") }, visitorPrompt = true
                )
            }
        }
        compose.onNodeWithText("Atenção").assertIsDisplayed()
        compose.onNodeWithText("Selecione uma ação").assertIsDisplayed()
        compose.onNodeWithText("Dados adicionais").assertIsDisplayed()
        compose.onNodeWithText("Entrar").performClick()
        compose.onNodeWithText("Voltar").performClick()
        compose.onNodeWithText("Visitante").performClick()
        compose.runOnIdle { assertEquals(listOf("positive", "negative", "neutral"), calls) }
    }

    @Test
    fun annotatedDialogWithoutTitleOrNegativeStillOffersPositiveAction() {
        var confirms = 0
        compose.setContent {
            CaixaDSTheme { CaixaDSDialog("", AnnotatedString("Informação"), "Entendi", { confirms++ }) }
        }
        compose.onNodeWithText("Informação").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertEquals(1, confirms) }
    }

    @Test
    fun selectionDialogReportsOptionIndexAndKeepsConfirmSeparateFromDismiss() {
        val selected = mutableStateOf(0)
        val calls = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSSelectionDialog(
                    "Modalidade",
                    "Escolha uma",
                    listOf("Mega-Sena", "Quina"),
                    selected.value,
                    {
                        selected.value = it
                        calls.add("select-$it")
                    },
                    { calls.add("confirm") },
                    { calls.add("dismiss") }
                )
            }
        }
        compose.onNodeWithText("Modalidade").assertIsDisplayed()
        compose.onNodeWithText("Escolha uma").assertIsDisplayed()
        compose.onNodeWithText("Quina").performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle {
            assertEquals(1, selected.value)
            assertEquals(listOf("select-1", "confirm", "dismiss"), calls)
        }
    }

    @Test
    fun wheelRequiresExplicitChoiceAndResetsPendingWhenOptionsChange() {
        val options = mutableStateOf(listOf(1, 2, 3))
        val calls = mutableListOf<Int>()
        var dismisses = 0
        compose.setContent {
            CaixaDSTheme {
                CaixaDSOptionDialog(
                    "Repetições",
                    options.value,
                    1,
                    { "$it concursos" },
                    { calls.add(it) },
                    { dismisses++ },
                    description = "Escolha a recorrência",
                    subtitle = "Concursos seguintes"
                )
            }
        }
        compose.onNodeWithText("Escolha a recorrência").assertIsDisplayed()
        compose.onNodeWithText("Concursos seguintes").assertIsDisplayed()
        compose.onNodeWithTag("betting-option-confirm").assertIsNotEnabled()
        compose.onNodeWithTag("betting-option-item-1").performClick().assertIsSelected()
        compose.onNodeWithTag("betting-option-confirm").assertIsEnabled()
        compose.runOnIdle {
            assertEquals(emptyList<Int>(), calls)
            options.value = listOf(4, 5)
        }
        compose.onNodeWithTag("betting-option-confirm").assertIsNotEnabled()
        compose.onNodeWithTag("betting-option-item-0").performClick()
        compose.onNodeWithTag("betting-option-confirm").performClick()
        compose.runOnIdle {
            assertEquals(listOf(4), calls)
            assertEquals(1, dismisses)
        }
    }

    @Test
    fun rapidAmountFormatsEditsShowsErrorAndUsesSeparateSaveCancelCallbacks() {
        val value = mutableStateOf("")
        val error = mutableStateOf<String?>("Valor abaixo do mínimo")
        val changes = mutableListOf<String>()
        val actions = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSRapidAmountDialog(
                    "Valor da aposta",
                    value.value,
                    "Digite um valor",
                    "De R$ 5,00 a R$ 100,00",
                    error.value,
                    {
                        changes.add(it)
                        value.value = it
                    },
                    { actions.add("save") },
                    { actions.add("cancel") }
                )
            }
        }
        compose.onNodeWithText("Digite um valor").assertIsDisplayed()
        compose.onNodeWithText("Valor abaixo do mínimo").assertIsDisplayed()
        compose.onNode(hasSetTextAction()).performTextReplacement("1234")
        val formatted = java.text.NumberFormat.getCurrencyInstance().format(java.math.BigDecimal("12.34"))
        compose.onNode(hasSetTextAction()).assertTextEquals(formatted)
        compose.runOnIdle {
            assertEquals(listOf(formatted), changes)
            value.value = "50,00"
            error.value = null
        }
        compose.onNode(hasSetTextAction()).assertTextEquals("50,00")
        compose.onNodeWithText("Valor abaixo do mínimo").assertDoesNotExist()
        compose.onNodeWithText("Salvar").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle { assertEquals(listOf("save", "cancel"), actions) }
    }
}
