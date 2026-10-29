package br.gov.caixa.loterias.feature.betting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.betting.presentation.components.AnimatedLotteryTutorial
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialDefinition
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingRoute
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowUiState
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingFlowViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xhdpi")
class AnimatedTutorialAccessibilityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun poolIntroductionPublishesItsInstructionsAndKeepsActionsSeparate() {
        var closes = 0
        var skips = 0
        show("betting/tutorial", close = { closes++ }, skip = { skips++ })
        compose.onNodeWithContentDescription(
            "Etapa um de cinco. Chegaram os novos bolões da CAIXA. Venha ver como funcionam os bolões no aplicativo Loterias CAIXA."
        ).assertIsDisplayed()
        compose.onNodeWithText("Fechar").performClick()
        compose.onNodeWithTag("pool-tutorial-skip").performClick()
        compose.runOnIdle {
            assertEquals(1, closes)
            assertEquals(1, skips)
        }
    }

    @Test fun everyAnimatedPageHasAnEquivalentInstructionInsteadOfOnlyAnAssetName() {
        listOf("betting/tutorial", "betting/tutorial-plus").forEach { route ->
            tutorialDefinition(route).pages.forEach { page ->
                assertTrue("Missing equivalent instruction for ${page.media}", !page.heading.isNullOrBlank())
            }
        }
    }

    @Test fun plusIntroductionIsAvailableEvenBeforeTheAnimationFinishesLoading() {
        show("betting/tutorial-plus")
        compose.onNodeWithContentDescription(
            "Etapa um de sete. Chegou a Mais Milionária. Venha ver como este novo jogo funciona."
        ).assertIsDisplayed()
    }

    @Test fun offscreenPagesDoNotCreateDuplicateInstructionFocusTargets() {
        show("betting/tutorial", page = 1)
        compose.onAllNodesWithContentDescription("Etapa ", substring = true).assertCountEquals(1)
    }

    @Test fun poolPreferenceIsOneNamedCheckboxWithOneCallback() = verifyFinalPreference("betting/tutorial")

    @Test fun plusPreferenceIsOneNamedCheckboxWithOneCallback() = verifyFinalPreference("betting/tutorial-plus")

    private fun verifyFinalPreference(route: String) {
        val definition = tutorialDefinition(route)
        val state = mutableStateOf(BettingFlowUiState(WorkflowSpec(route, "", "", primaryAction = ""), page = definition.pages.lastIndex))
        var changes = 0
        compose.setContent {
            LargeFontTheme {
                AnimatedLotteryTutorial(state.value, definition, {}, {}, {}, {}, {
                    changes++
                    state.value = state.value.copy(doNotShowAgain = !state.value.doNotShowAgain)
                })
            }
        }
        val role = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox)
        compose.onAllNodes(role).assertCountEquals(1)
        compose.onNode(role and hasText("Não exibir novamente", substring = true)).assertIsOff().performClick().assertIsOn()
        compose.runOnIdle { assertEquals(1, changes) }
    }

    @Test fun poolPageControlsRemainReachableWithFontAtTwoHundredPercent() = verifyPageNavigation("betting/tutorial")

    @Test fun plusPageControlsRemainReachableWithFontAtTwoHundredPercent() = verifyPageNavigation("betting/tutorial-plus")

    private fun verifyPageNavigation(route: String) {
        val definition = tutorialDefinition(route)
        val viewModel = BettingFlowViewModel(WorkflowSpec(route, "", "", primaryAction = ""))
        var exits = 0
        compose.setContent {
            LargeFontTheme {
                Box(Modifier.requiredSize(360.dp, 640.dp).testTag("tutorial-test-viewport")) {
                    BettingRoute(viewModel, { exits++ }, { exits++ })
                }
            }
        }
        compose.onNodeWithText("Anterior").assertIsNotEnabled()
        compose.onNodeWithText("Próximo").assertIsEnabled().assertIsDisplayed()
        assertWholeLabel("Anterior")
        assertWholeLabel("Próximo")
        val viewport = compose.onNodeWithTag("tutorial-test-viewport").fetchSemanticsNode().boundsInRoot
        val next = compose.onNodeWithText("Próximo").fetchSemanticsNode().boundsInRoot
        assertTrue("Navigation remains inside the compact viewport", next.bottom <= viewport.bottom)
        listOf("Próximo" to 1, "Próximo" to 2, "Anterior" to 1, "Anterior" to 0).forEach { (label, page) ->
            compose.onNodeWithText(label).performClick()
            compose.runOnIdle {
                assertEquals(page, viewModel.state.value.page)
                assertEquals(0, exits)
            }
        }
        compose.onNodeWithText("Anterior").assertIsNotEnabled()
        compose.runOnIdle { viewModel.selectPage(definition.pages.lastIndex) }
        compose.onNodeWithText("Próximo").assertIsNotEnabled()
        compose.onNodeWithText("Anterior").assertIsEnabled().assertIsDisplayed()
        val finalAction = if (route == "betting/tutorial") {
            compose.onNodeWithTag("pool-tutorial-primary").fetchSemanticsNode().boundsInRoot
        } else {
            compose.onNodeWithText(definition.finalPrimaryLabel).fetchSemanticsNode().boundsInRoot
        }
        val footer = compose.onNodeWithText("Anterior").fetchSemanticsNode().boundsInRoot
        assertTrue("Final action stays above page navigation", finalAction.bottom <= footer.top)
    }

    private fun assertWholeLabel(label: String) {
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(label, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val layout = layouts.single()
        assertFalse(
            "Whole label: $label; size=${layout.size}; width=${layout.didOverflowWidth}; height=${layout.didOverflowHeight}; " +
                "lines=${layout.lineCount}; right=${layout.getLineRight(0)}; constraints=${layout.layoutInput.constraints}",
            layout.hasVisualOverflow
        )
    }

    @Composable
    private fun LargeFontTheme(content: @Composable () -> Unit) {
        val density = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2f)) {
            CaixaDSTheme(content = content)
        }
    }

    private fun show(route: String, page: Int = 0, close: () -> Unit = {}, skip: () -> Unit = {}) {
        compose.setContent {
            LargeFontTheme {
                AnimatedLotteryTutorial(
                    BettingFlowUiState(
                        WorkflowSpec(route, "", "", primaryAction = ""),
                        page = page
                    ),
                    tutorialDefinition(route),
                    {
                    },
                    close,
                    {},
                    skip,
                    {}
                )
            }
        }
    }
}
