package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountChoice
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSChoiceRow
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterNumberCell
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterSelection
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceTab
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextField
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextFieldVariant
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSWheel
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSTextInputDialog
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSCheckSwitch
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private const val SELECTED_NUMBER = 7

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSAccessibilityJvmTest {
    @get:Rule val compose = createComposeRule()

    @Test fun labelledFilledInputRetainsNameAndSpecificError() {
        assertFieldContract(CaixaDSTextFieldVariant.LABELLED)
    }

    @Test fun legacyFilledInputRetainsNameAndSpecificError() {
        assertFieldContract(CaixaDSTextFieldVariant.LEGACY)
    }

    private fun assertFieldContract(variant: CaixaDSTextFieldVariant) {
        compose.setContent {
            CaixaDSTheme {
                CaixaDSTextField(
                    "123",
                    {},
                    "Documento",
                    variant = variant,
                    isError = true,
                    supportingText = "Documento inválido"
                )
            }
        }
        compose.onNode(hasSetTextAction())
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Documento")))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Documento inválido"))
    }

    @Test fun inputDialogAssociatesValidationErrorWithEditableValue() {
        compose.setContent {
            CaixaDSTheme {
                CaixaDSTextInputDialog("Salvar", "A", "Nome", {}, {}, {}, errorMessage = "Informe dois caracteres")
            }
        }
        compose.onNode(hasSetTextAction())
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Informe dois caracteres"))
    }

    @Test fun choiceHasOneNamedRadioTargetWithSelectedStateAndOneCallback() {
        var clicks = 0
        compose.setContent {
            CaixaDSTheme { CaixaDSChoiceRow("Seis números", selected = true, onClick = { clicks++ }) }
        }
        compose.onAllNodes(hasClickAction()).assertCountEquals(1)
        compose.onNodeWithText("Seis números")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
            .assertIsSelected().performClick()
        compose.runOnIdle { assertEquals(1, clicks) }
    }

    @Test fun multipleChoiceHasOneNamedCheckedTarget() {
        compose.setContent {
            CaixaDSTheme { CaixaDSChoiceRow("Aceitar termos", selected = true, onClick = {}, multiple = true) }
        }
        compose.onAllNodes(hasClickAction()).assertCountEquals(1)
        compose.onNodeWithText("Aceitar termos")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.On))
    }

    @Test fun accountChoiceAnnouncesSelectedOption() {
        compose.setContent {
            CaixaDSTheme { CaixaDSAccountChoice(1, listOf("Feminino" to 1, "Masculino" to 2), {}) }
        }
        compose.onNodeWithText("Feminino")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)).assertIsSelected()
    }

    @Test fun marketplaceTabExposesTabRoleAndSelection() {
        compose.setContent {
            CaixaDSTheme { Column(Modifier.height(48.dp)) { CaixaDSMarketplaceTab("Bolões", true, {}) } }
        }
        compose.onNodeWithText("Bolões")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)).assertIsSelected()
    }

    @Test fun numberSelectionExposesCheckedState() {
        compose.setContent {
            CaixaDSTheme { CaixaDSMarketplaceFilterNumberCell(SELECTED_NUMBER, true, onClick = {}) }
        }
        compose.onNodeWithText("07")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.On))
    }

    @Test fun shakeSwitchAnnouncesStateAndActivatesOnce() {
        val checked = mutableStateOf(true)
        var changes = 0
        compose.setContent {
            CaixaDSTheme {
                CaixaDSCheckSwitch(checked.value, Color.Blue, Color.White, Color.Gray, false) {
                    checked.value = !checked.value
                    changes++
                }
            }
        }
        compose.onNode(hasClickAction())
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.On))
            .performClick()
        compose.onNode(hasClickAction())
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.Off))
        compose.runOnIdle { assertEquals(1, changes) }
    }

    @Test fun removalOffersOneAccessibleActionPerValue() {
        var removals = 0
        compose.setContent {
            CaixaDSTheme { CaixaDSMarketplaceFilterSelection(listOf(SELECTED_NUMBER), "Limpar", { removals++ }, {}) }
        }
        // Uma ação de remoção e a ação de limpar, sem um segundo alvo de remoção aninhado.
        compose.onAllNodes(hasClickAction()).assertCountEquals(2)
        compose.onNodeWithText("07").performClick()
        compose.runOnIdle { assertEquals(1, removals) }
    }

    @Test fun wheelExposesItsCurrentSelectedOption() {
        compose.setContent {
            CaixaDSTheme { CaixaDSWheel(listOf("Uma aposta", "Duas apostas"), 0, {}, Modifier.height(160.dp)) }
        }
        compose.onNodeWithText("Uma aposta").assertIsSelected()
    }
}
