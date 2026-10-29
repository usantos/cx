package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSWheel
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
class CaixaDSWheelAccessibilityJvmTest {
    @get:Rule val compose = createComposeRule()

    @Test fun wrappedWheelHasOneFiniteAdjustableOwnerAndNoRepeatedCycleActions() {
        val value = mutableStateOf(0)
        val changes = mutableListOf<Int>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSWheel(
                    listOf("Uma", "Duas"),
                    value.value,
                    {
                        changes += it
                        value.value = it
                    },
                    Modifier.size(180.dp, 160.dp).testTag("wheel"),
                    wrap = true,
                    accessibilityLabel = "Quantidade de apostas"
                )
            }
        }
        val progress = SemanticsMatcher.keyIsDefined(SemanticsActions.SetProgress)
        compose.onAllNodes(progress).assertCountEquals(1)
        compose.onNodeWithTag("wheel")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Quantidade de apostas")))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo(0f, 0f..1f)))
            .performSemanticsAction(SemanticsActions.SetProgress) { assertTrue(it(1f)) }
        compose.runOnIdle { assertEquals(listOf(1), changes) }
        compose.onNodeWithTag("wheel")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Duas"))
        // No máximo as duas opções finitas permanecem acionáveis; os ciclos virtuais repetidos não.
        assertTrue(compose.onAllNodes(hasClickAction()).fetchSemanticsNodes().size <= 2)
    }

    @Test fun keyboardAdvancesAndActivatingAlreadyCenteredOptionDispatchesExactlyOnce() {
        val value = mutableStateOf(0)
        val changes = mutableListOf<Int>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSWheel(
                    listOf("Uma", "Duas"),
                    value.value,
                    {
                        changes += it
                        value.value = it
                    },
                    Modifier.size(180.dp, 160.dp).testTag("wheel")
                )
            }
        }
        compose.onNodeWithTag("wheel").performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        compose.onNodeWithTag("wheel").performKeyInput {
            keyDown(Key.DirectionDown)
            keyUp(Key.DirectionDown)
        }
        compose.runOnIdle { assertEquals(listOf(1), changes) }
        compose.onNodeWithText("Duas").performClick()
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(listOf(1, 1), changes) }
    }
}
