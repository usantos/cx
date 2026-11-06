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
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountChoice
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSChoiceRow
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterNumberCell
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterSelection
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceTab
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSTextField
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSTextFieldVariant
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSWheel
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSTextInputDialog
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSCheckSwitch
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private const val SELECTED_NUMBER = 7

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LoteriasCaixaDSAccessibilityJvmTest {
    @get:Rule val compose = createComposeRule()

    @Test fun labelledFilledInputRetainsNameAndSpecificError() {
        assertFieldContract(LoteriasCaixaDSTextFieldVariant.LABELLED)
    }

    @Test fun legacyFilledInputRetainsNameAndSpecificError() {
        assertFieldContract(LoteriasCaixaDSTextFieldVariant.LEGACY)
    }

    private fun assertFieldContract(variant: LoteriasCaixaDSTextFieldVariant) {
        compose.setContent {
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSTextField(
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
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSTextInputDialog("Salvar", "A", "Nome", {}, {}, {}, errorMessage = "Informe dois caracteres")
            }
        }
        compose.onNode(hasSetTextAction())
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Informe dois caracteres"))
    }

    @Test fun choiceHasOneNamedRadioTargetWithSelectedStateAndOneCallback() {
        var clicks = 0
        compose.setContent {
            LoteriasCaixaDSTheme { LoteriasCaixaDSChoiceRow("Seis números", selected = true, onClick = { clicks++ }) }
        }
        compose.onAllNodes(hasClickAction()).assertCountEquals(1)
        compose.onNodeWithText("Seis números")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
            .assertIsSelected().performClick()
        compose.runOnIdle { assertEquals(1, clicks) }
    }

    @Test fun multipleChoiceHasOneNamedCheckedTarget() {
        compose.setContent {
            LoteriasCaixaDSTheme { LoteriasCaixaDSChoiceRow("Aceitar termos", selected = true, onClick = {}, multiple = true) }
        }
        compose.onAllNodes(hasClickAction()).assertCountEquals(1)
        compose.onNodeWithText("Aceitar termos")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.On))
    }

    @Test fun accountChoiceAnnouncesSelectedOption() {
        compose.setContent {
            LoteriasCaixaDSTheme { LoteriasCaixaDSAccountChoice(1, listOf("Feminino" to 1, "Masculino" to 2), {}) }
        }
        compose.onNodeWithText("Feminino")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)).assertIsSelected()
    }

    @Test fun marketplaceTabExposesTabRoleAndSelection() {
        compose.setContent {
            LoteriasCaixaDSTheme { Column(Modifier.height(48.dp)) { LoteriasCaixaDSMarketplaceTab("Bolões", true, {}) } }
        }
        compose.onNodeWithText("Bolões")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)).assertIsSelected()
    }

    @Test fun numberSelectionExposesCheckedState() {
        compose.setContent {
            LoteriasCaixaDSTheme { LoteriasCaixaDSMarketplaceFilterNumberCell(SELECTED_NUMBER, true, onClick = {}) }
        }
        compose.onNodeWithText("07")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.On))
    }

    @Test fun shakeSwitchAnnouncesStateAndActivatesOnce() {
        val checked = mutableStateOf(true)
        var changes = 0
        compose.setContent {
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSCheckSwitch(checked.value, Color.Blue, Color.White, Color.Gray, false) {
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
            LoteriasCaixaDSTheme { LoteriasCaixaDSMarketplaceFilterSelection(listOf(SELECTED_NUMBER), "Limpar", { removals++ }, {}) }
        }
        // Uma ação de remoção e a ação de limpar, sem um segundo alvo de remoção aninhado.
        compose.onAllNodes(hasClickAction()).assertCountEquals(2)
        compose.onNodeWithText("07").performClick()
        compose.runOnIdle { assertEquals(1, removals) }
    }

    @Test fun wheelExposesItsCurrentSelectedOption() {
        compose.setContent {
            LoteriasCaixaDSTheme { LoteriasCaixaDSWheel(listOf("Uma aposta", "Duas apostas"), 0, {}, Modifier.height(160.dp)) }
        }
        compose.onNodeWithText("Uma aposta").assertIsSelected()
    }
}
