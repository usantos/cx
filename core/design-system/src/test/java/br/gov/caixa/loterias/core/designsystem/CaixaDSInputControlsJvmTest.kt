// Literais numéricos descrevem geometria fixa de teste, IDs das fixtures, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSAccountCheckbox
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSCheckSwitch
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSCheckbox
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSNumberPicker
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSRadioButton
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSTutorialCheckbox
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSInputControlsJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun selectionControlsExposeStateAndDispatchOneActionPerClick() {
        val selected = mutableStateOf(false)
        var clicks = 0
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSRadioButton(selected.value, {
                        clicks++
                        selected.value = !selected.value
                    }, Modifier.testTag("radio"))
                    CaixaDSCheckbox(selected.value, {
                        clicks++
                        selected.value = !selected.value
                    }, Modifier.testTag("checkbox"))
                    CaixaDSTutorialCheckbox(selected.value, {
                        clicks++
                        selected.value = !selected.value
                    }, Modifier.testTag("tutorial"))
                }
            }
        }

        listOf("radio", "checkbox", "tutorial").forEach { compose.onNodeWithTag(it).assertIsOff() }
        compose.onNodeWithTag("radio").performClick()
        listOf("radio", "checkbox", "tutorial").forEach { compose.onNodeWithTag(it).assertIsOn() }
        compose.onNodeWithTag("checkbox").performClick()
        compose.onNodeWithTag("tutorial").assertIsOff().performClick().assertIsOn()
        compose.runOnIdle { assertEquals(3, clicks) }
    }

    @Test
    fun accountCheckboxReportsParentControlledState() {
        val checked = mutableStateOf(false)
        var calls = 0
        compose.setContent {
            CaixaDSTheme {
                CaixaDSAccountCheckbox(checked.value, {
                    calls++
                    checked.value = !checked.value
                }, 48.dp)
            }
        }
        val checkbox = androidx.compose.ui.test.SemanticsMatcher.keyIsDefined(SemanticsProperties.ToggleableState)
        compose.onNode(checkbox).assertIsOff().performClick().assertIsOn().performClick().assertIsOff()
        compose.runOnIdle { assertEquals(2, calls) }
    }

    @Test
    fun shakeSwitchAllowsChangingBothCurrentStatesAndInvertedPresentation() {
        val enabled = mutableStateOf(false)
        val invert = mutableStateOf(false)
        var clicks = 0
        compose.setContent {
            CaixaDSTheme {
                CaixaDSCheckSwitch(enabled.value, Color.Blue, Color.White, Color.Gray, invert.value) {
                    clicks++
                    enabled.value = !enabled.value
                }
            }
        }
        compose.onNodeWithContentDescription("Aposta Shake").assertIsDisplayed().performClick()
        compose.runOnIdle {
            assertTrue(enabled.value)
            invert.value = true
        }
        compose.onNodeWithContentDescription("Aposta Shake").performClick()
        compose.runOnIdle { assertEquals(false, enabled.value) }
        compose.onNodeWithContentDescription("Aposta Shake").performClick()
        compose.runOnIdle {
            assertEquals(3, clicks)
            assertTrue(enabled.value)
        }
    }

    @Test
    fun nativePickerClampsExternalSelectionAndOffersEveryOptionToAccessibility() {
        val options = mutableStateOf(listOf("Um", "Dois", "Três"))
        val selected = mutableStateOf(-8)
        val choices = mutableListOf<Int>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSNumberPicker("Quantidade", options.value, selected.value, {
                    choices.add(it)
                    selected.value = it
                })
            }
        }
        val picker = compose.onNodeWithContentDescription("Quantidade")
        picker.assertContentDescriptionEquals("Quantidade")
        assertEquals("Um", picker.fetchSemanticsNode().config[SemanticsProperties.StateDescription])
        val actions = picker.fetchSemanticsNode().config[SemanticsActions.CustomActions]
        assertEquals(listOf("Um", "Dois", "Três"), actions.map { it.label })
        compose.runOnIdle { assertTrue(actions[2].action()) }
        assertEquals("Três", picker.fetchSemanticsNode().config[SemanticsProperties.StateDescription])
        compose.runOnIdle {
            selected.value = 99
            options.value = listOf("Nova", "Última")
        }
        assertEquals("Última", picker.fetchSemanticsNode().config[SemanticsProperties.StateDescription])
        assertEquals(listOf("Nova", "Última"), picker.fetchSemanticsNode().config[SemanticsActions.CustomActions].map { it.label })
        compose.runOnIdle {
            assertEquals(listOf(2), choices)
            options.value = emptyList()
        }
        picker.assertDoesNotExist()
    }
}
