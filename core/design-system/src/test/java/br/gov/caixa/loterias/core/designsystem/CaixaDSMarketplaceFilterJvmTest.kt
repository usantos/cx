// Literais numéricos descrevem geometria fixa de teste, IDs das fixtures, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.input.KeyboardType
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterAccordionHeader
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterAccordionKind
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterBetGrid
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterButton
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterInput
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterSelection
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSMarketplaceFilterJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun numericInputLimitsFourteenCharactersAndRestoresPlaceholderWhenCleared() {
        val value = mutableStateOf("")
        val updates = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSMarketplaceFilterInput(value.value, {
                    value.value = it
                    updates.add(it)
                }, "Concurso", KeyboardType.Number)
            }
        }
        compose.onNodeWithText("Concurso").assertIsDisplayed()
        val field = compose.onNode(hasSetTextAction())
        field.performTextReplacement("123456789012345678")
        field.assertTextEquals("12345678901234")
        field.performTextInputSelection(androidx.compose.ui.text.TextRange(3))
        compose.runOnIdle { assertEquals(listOf("12345678901234"), updates) }
        compose.onNodeWithText("Concurso").assertDoesNotExist()
        field.performTextReplacement("")
        compose.onNodeWithText("Concurso").assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf("12345678901234", ""), updates) }
    }

    @Test
    fun currencyInputFormatsDecimalCentsAndShowsCurrencyDescription() {
        val value = mutableStateOf("")
        compose.setContent {
            CaixaDSTheme {
                CaixaDSMarketplaceFilterInput(value.value, { value.value = it }, "Valor", KeyboardType.Decimal, currency = true)
            }
        }
        compose.onNodeWithContentDescription("R$").assertIsDisplayed()
        val field = compose.onNode(hasSetTextAction())
        field.performTextReplacement("123456")
        field.assertTextEquals("1.234,56")
        compose.runOnIdle { value.value = "9,99" }
        field.assertTextEquals("9,99")
        field.performTextReplacement("")
        compose.onNodeWithText("Valor").assertIsDisplayed()
    }

    @Test
    fun textInputKeepsLongNamesWithoutNumericLengthLimit() {
        val value = mutableStateOf("")
        compose.setContent {
            CaixaDSTheme { CaixaDSMarketplaceFilterInput(value.value, { value.value = it }, "Nome", KeyboardType.Text) }
        }
        val text = "Lotérica central da cidade"
        compose.onNode(hasSetTextAction()).performTextReplacement(text)
        compose.onNode(hasSetTextAction()).assertTextEquals(text)
        compose.runOnIdle { assertEquals(text, value.value) }
    }

    @Test
    fun betGridDispatchesFormattedNumberAndSelectionSupportsRemoveAndClear() {
        val selected = mutableStateOf(setOf(2))
        val removed = mutableListOf<Int>()
        val bets = mutableListOf<Int>()
        var cleared = 0
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSMarketplaceFilterBetGrid(listOf(1, 2, 3), selected.value, Modifier.testTag("grid")) {
                        bets.add(it)
                        selected.value = selected.value + it
                    }
                    CaixaDSMarketplaceFilterSelection(listOf(7, 11), "Limpar", { removed.add(it) }, { cleared++ })
                }
            }
        }
        compose.onNodeWithText("01").assertIsDisplayed().performClick()
        compose.onNodeWithText("02").performClick()
        compose.onNodeWithContentDescription("Remover 07", useUnmergedTree = true).performClick()
        compose.onNodeWithText("11").performClick()
        compose.onNodeWithText("LIMPAR").performClick()
        compose.runOnIdle {
            assertEquals(listOf(1, 2), bets)
            assertEquals(setOf(1, 2), selected.value)
            assertEquals(listOf(7, 11), removed)
            assertEquals(1, cleared)
        }
    }

    @Test
    fun accordionAndButtonsKeepIndependentActionsAndDisabledContract() {
        val actions = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSMarketplaceFilterAccordionHeader("Quantidade", "quantity", { actions.add("quantity") })
                    CaixaDSMarketplaceFilterAccordionHeader("Dezenas", "numbers", {
                        actions.add("numbers")
                    }, CaixaDSMarketplaceFilterAccordionKind.NUMBERS)
                    CaixaDSMarketplaceFilterButton("Aplicar", false, { actions.add("apply") })
                    CaixaDSMarketplaceFilterButton("Cancelar", true, { actions.add("cancel") })
                    CaixaDSMarketplaceFilterButton("Indisponível", false, { actions.add("disabled") }, enabled = false)
                }
            }
        }
        compose.onNodeWithTag("quantity").performClick()
        compose.onNodeWithTag("numbers").performClick()
        compose.onNodeWithText("Aplicar").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithText("Indisponível").assertIsNotEnabled()
        compose.runOnIdle { assertEquals(listOf("quantity", "numbers", "apply", "cancel"), actions) }
    }
}
