package br.gov.caixa.loterias.feature.responsiblegaming

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentQuestion
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.AssessmentFormScreen
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.SuspensionScreen
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
class ResponsibleGamingAccessibilityTest {
    @get:Rule val compose = createComposeRule()
    private fun role(value: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, value)

    @Test fun assessmentAnswersHaveNamesAndExclusiveSelectedState() {
        val state = mutableStateOf(ResponsibleGamingUiState(questions = listOf(SelfAssessmentQuestion(1, "Pergunta", 1))))
        val actions = mutableListOf<ResponsibleGamingAction>()
        compose.setContent {
            CaixaDSTheme {
                AssessmentFormScreen(state.value, { action ->
                    actions += action
                    if (action is ResponsibleGamingAction.Answer) state.value = state.value.copy(answers = mapOf(1 to action.affirmative))
                })
            }
        }
        compose.onNode(role(Role.RadioButton) and hasText("Sim")).performScrollTo().assertIsNotSelected().performClick().assertIsSelected()
        compose.onNode(role(Role.RadioButton) and hasText("Não")).assertIsNotSelected()
        compose.runOnIdle { assertEquals(listOf(ResponsibleGamingAction.Answer(true)), actions) }
    }

    @Test fun suspensionPeriodAndAcknowledgementRetainTheirNames() {
        compose.setContent { CaixaDSTheme { SuspensionScreen(ResponsibleGamingUiState(), {}, {}) } }
        compose.onNode(role(Role.RadioButton) and hasText("24 horas")).performScrollTo().assertIsNotSelected()
        compose.onNode(role(Role.Checkbox) and hasText("Estou ciente", substring = true)).performScrollTo().assertIsOff()
    }

    @Test fun suspensionChoicesFitNarrowViewportAtDoubleFontScale() {
        compose.setContent {
            val density = LocalDensity.current.density
            CompositionLocalProvider(LocalDensity provides Density(density, 2f)) {
                Box(Modifier.width(NARROW_WIDTH.dp).height(COMPACT_HEIGHT.dp).testTag("suspension-viewport")) {
                    CaixaDSTheme { SuspensionScreen(ResponsibleGamingUiState(), {}, {}) }
                }
            }
        }
        val viewport = compose.onNodeWithTag("suspension-viewport").getUnclippedBoundsInRoot()
        listOf("24 horas", "48 horas", "7 dias", "15 dias", "30 dias").forEach { label ->
            val choice = compose.onNode(role(Role.RadioButton) and hasText(label)).performScrollTo()
            val bounds = choice.getUnclippedBoundsInRoot()
            assertTrue("$label extends outside the narrow viewport: $bounds versus $viewport", bounds.right <= viewport.right)
            assertTrue("$label starts outside the narrow viewport", bounds.left >= viewport.left)
        }
    }

    @Test fun keyboardTabMovesOncePerLabelledAssessmentChoice() {
        val actions = mutableListOf<ResponsibleGamingAction>()
        compose.setContent {
            CaixaDSTheme {
                AssessmentFormScreen(
                    ResponsibleGamingUiState(assessmentRequired = true, questions = listOf(SelfAssessmentQuestion(1, "Pergunta", 1))),
                    actions::add
                )
            }
        }
        compose.onNode(role(Role.RadioButton) and hasText("Sim")).performScrollTo()
        compose.onRoot().performKeyInput {
            pressKey(Key.Tab)
            pressKey(Key.Enter)
            pressKey(Key.Tab)
            pressKey(Key.Enter)
        }
        compose.runOnIdle { assertEquals(listOf(ResponsibleGamingAction.Answer(true), ResponsibleGamingAction.Answer(false)), actions) }
    }

    private companion object {
        const val NARROW_WIDTH = 320
        const val COMPACT_HEIGHT = 640
    }
}
