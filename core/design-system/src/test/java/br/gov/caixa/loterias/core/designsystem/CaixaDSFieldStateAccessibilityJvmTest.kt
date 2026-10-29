package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
class CaixaDSFieldStateAccessibilityJvmTest {
    @get:Rule val compose = createComposeRule()

    @Test fun legacyPasswordRetainsPrivacyAndDisabledEditingContract() {
        var changes = 0
        compose.setContent {
            CaixaDSTheme {
                CaixaDSTextField(
                    "Segredo",
                    { changes++ },
                    "Senha",
                    Modifier.testTag("password"),
                    enabled = false,
                    visualTransformation = PasswordVisualTransformation(),
                    variant = CaixaDSTextFieldVariant.LEGACY
                )
            }
        }
        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.Password)).assertIsNotEnabled()
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Password))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Senha")))
        compose.runOnIdle { assertEquals(0, changes) }
    }

    @Test fun readOnlyBaseFieldPreservesValueAndRejectsEditing() {
        var changes = 0
        compose.setContent {
            CaixaDSTheme {
                CaixaDSBasicTextField("Somente leitura", { changes++ }, Modifier.testTag("readonly"), readOnly = true)
            }
        }
        compose.onNodeWithTag("readonly")
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.SetText))
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.SetSelection))
        compose.runOnIdle { assertEquals(0, changes) }
    }

    @Test fun fieldErrorUpdatesAndClearsWithoutLosingEditableIdentity() {
        val error = mutableStateOf<String?>(null)
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSTextField(
                        "123",
                        {},
                        "Documento",
                        variant = CaixaDSTextFieldVariant.LEGACY,
                        isError = error.value != null,
                        supportingText = error.value
                    )
                }
            }
        }
        compose.runOnIdle { error.value = "Documento inválido" }
        compose.onNode(hasSetTextAction())
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Documento inválido"))
        compose.runOnIdle { error.value = null }
        compose.onNode(hasSetTextAction())
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Error))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Documento")))
    }
}
