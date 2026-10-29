// Dimensões, valores e posições de seleção são fixtures fixas do contrato dos campos legados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextField
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextFieldVariant
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSUnifiedTextFieldJvmTest {
    @get:Rule val compose = createComposeRule()

    @Test fun legacyVariantKeepsMaskEditsAndDisabledParentUpdates() {
        val value = mutableStateOf("")
        val enabled = mutableStateOf(true)
        val edits = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSTextField(
                    value.value,
                    {
                        edits += it
                        value.value = it
                    },
                    "CEP",
                    enabled = enabled.value,
                    variant = CaixaDSTextFieldVariant.LEGACY,
                    placeholder = "Somente números",
                    showValidation = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
        compose.onNodeWithText("CEP").assertIsDisplayed()
        compose.onNodeWithText("Somente números").assertIsDisplayed()
        compose.onNode(hasSetTextAction()).performTextReplacement("70000-000")
        compose.runOnIdle {
            assertEquals(listOf("70000-000"), edits)
            value.value = "01001-000"
            enabled.value = false
        }
        compose.onNodeWithText("01001-000").assertIsNotEnabled()
        compose.onNode(hasSetTextAction()).assertDoesNotExist()
    }

    @Test fun legacyCurrencyFormatsDigitsRestoresSelectionAndAcceptsServerReplacement() {
        val value = mutableStateOf("")
        val edits = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSTextField(
                    value.value,
                    {
                        edits += it
                        value.value = it
                    },
                    "",
                    variant = CaixaDSTextFieldVariant.LEGACY,
                    currencyInput = true,
                    placeholder = "R$ 0,00"
                )
            }
        }
        compose.onNodeWithText("R$ 0,00").assertIsDisplayed()
        val field = compose.onNode(hasSetTextAction())
        field.performTextReplacement("12345")
        field.assertTextEquals("R$ 123,45")
        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(9)))
        field.performSemanticsAction(SemanticsActions.SetSelection) { it(0, 1, false) }
        compose.runOnIdle { assertEquals(listOf("R$ 123,45"), edits) }
        field.performTextReplacement("")
        compose.runOnIdle {
            assertEquals(listOf("R$ 123,45", ""), edits)
            value.value = "R$ 50,00"
        }
        field.assertTextEquals("R$ 50,00")
    }

    @Test
    fun currencyParentCanRejectAnEditAndStillReceiveTheSameNextAttempt() {
        val attempts = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSTextField(
                    "R$ 1,00",
                    { attempts += it },
                    "Limite",
                    variant = CaixaDSTextFieldVariant.LEGACY,
                    currencyInput = true
                )
            }
        }
        val field = compose.onNode(hasSetTextAction())
        field.performTextReplacement("200")
        field.assertTextEquals("R$ 1,00")
        field.performSemanticsAction(SemanticsActions.SetSelection) { it(0, 1, false) }
        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(0, 1)))
        field.performTextReplacement("200")
        field.assertTextEquals("R$ 1,00")
        field.performTextReplacement("200")
        field.assertTextEquals("R$ 1,00")
        compose.runOnIdle { assertEquals(listOf("R$ 2,00", "R$ 2,00", "R$ 2,00"), attempts) }
    }

    @Test fun currencyEditsInOneUiTurnNotifyOncePerDistinctAmount() {
        val value = mutableStateOf("R$ 100,00")
        val edits = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSTextField(
                    value.value,
                    {
                        edits += it
                        value.value = it
                    },
                    "Limite",
                    variant = CaixaDSTextFieldVariant.LEGACY,
                    currencyInput = true
                )
            }
        }
        val replaceText = requireNotNull(
            compose.onNode(hasSetTextAction())
                .fetchSemanticsNode().config[SemanticsActions.SetText].action
        )
        compose.runOnUiThread {
            replaceText(AnnotatedString("1234"))
            replaceText(AnnotatedString("1234"))
            replaceText(AnnotatedString("R$ 12,34"))
            replaceText(AnnotatedString("5678"))
            replaceText(AnnotatedString("1234"))
            replaceText(AnnotatedString("10000"))
        }
        compose.runOnIdle {
            assertEquals(listOf("R$ 12,34", "R$ 56,78", "R$ 12,34", "R$ 100,00"), edits)
        }
        compose.onNode(hasSetTextAction()).assertTextEquals("R$ 100,00")
    }

    @Test fun labelledVariantUsesSeparateLabelAndShowsOptionalSupport() {
        compose.setContent {
            CaixaDSTheme {
                CaixaDSTextField(
                    "",
                    {},
                    "Documento",
                    Modifier.width(240.dp),
                    variant = CaixaDSTextFieldVariant.LABELLED,
                    placeholder = "Digite o documento",
                    supportingText = "Ajuda do campo",
                    enabled = false
                )
            }
        }
        compose.onNodeWithText("Documento").assertIsDisplayed()
        compose.onNodeWithText("Digite o documento").assertIsDisplayed()
        compose.onNodeWithText("Ajuda do campo").assertIsDisplayed()
        compose.onNode(hasSetTextAction()).assertDoesNotExist()
    }

    @Test fun bareStringFieldRetainsConsumerImeAndReadOnlyContract() {
        val value = mutableStateOf("inicial")
        val readOnly = mutableStateOf(false)
        var searches = 0
        compose.setContent {
            CaixaDSTheme {
                CaixaDSBasicTextField(
                    value.value,
                    { value.value = it },
                    Modifier.testTag("query"),
                    readOnly = readOnly.value,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { searches++ })
                )
            }
        }
        compose.onNodeWithTag("query").performTextReplacement("consulta")
        compose.onNodeWithTag("query").performImeAction()
        compose.runOnIdle {
            assertEquals("consulta", value.value)
            assertEquals(1, searches)
            readOnly.value = true
        }
        compose.onNodeWithTag("query").assertTextEquals("consulta")
        compose.onNode(hasSetTextAction()).assertDoesNotExist()
    }

    @Test fun bareSelectionAwareFieldPreservesCursorAndParentReplacement() {
        val value = mutableStateOf(TextFieldValue("123", TextRange(1)))
        val changes = mutableListOf<TextFieldValue>()
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSBasicTextField(value.value, {
                        changes += it
                        value.value = it
                    }, Modifier.testTag("selection"))
                }
            }
        }
        val field = compose.onNodeWithTag("selection")
        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(1)))
        field.performTextReplacement("4567")
        compose.runOnIdle {
            assertEquals("4567", changes.single().text)
            value.value = TextFieldValue("89", TextRange(2))
        }
        field.assertTextEquals("89")
        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(2)))
    }
}
