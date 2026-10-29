package br.gov.caixa.loterias.feature.onboarding

import androidx.compose.ui.test.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.CompleteIntroductionUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import br.gov.caixa.loterias.feature.onboarding.presentation.route.OnboardingRoute
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.OnboardingViewModel

@RunWith(AndroidJUnit4::class)
class OnboardingCoordinationUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun acceptingAgeKeepsInitialPageAndSwipeUpdatesCompletionAction() {
        val calls = mutableListOf<String>()
        var completions = 0
        val viewModel = OnboardingViewModel("onboarding/introduction", CompleteIntroductionUseCase { completions++ })
        viewModel.pageChanged(1)
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                OnboardingRoute(viewModel, {}, { calls += "complete" }, { calls += "underage" }, {}, {}, {})
            }
        }
        compose.onNodeWithText("Você tem mais de 18 anos?").assertIsDisplayed()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Você tem mais de 18 anos?").assertDoesNotExist()
        compose.onNodeWithText("Novas funcionalidades").assertIsDisplayed()
        compose.onNodeWithText("Pular introdução").assertIsDisplayed()
        compose.onNodeWithContentDescription("Página 2 de 3").performTouchInput { swipeLeft() }
        compose.onNodeWithContentDescription("Página 3 de 3").assertExists()
        compose.onNodeWithText("Acessar o aplicativo").performClick()
        compose.runOnIdle {
            assertFalse(viewModel.state.value.ageConfirmationVisible)
            assertEquals(2, viewModel.state.value.introductionPage)
            assertEquals(1, completions)
            assertEquals(listOf("complete"), calls)
        }
    }

    @Test fun rejectionNavigatesOnceAndAgeScreenKeepsItsOwnActionsAfterViewModelReplacement() {
        val calls = mutableListOf<String>()
        var completions = 0
        val initial = OnboardingViewModel("onboarding/introduction", CompleteIntroductionUseCase { completions++ })
        val viewModel = mutableStateOf(initial)
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                OnboardingRoute(viewModel.value, { calls += "back" }, { calls += "complete" },
                    { calls += "underage" }, { calls += "responsible" }, {}, {})
            }
        }
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithText("Você tem mais de 18 anos?").assertDoesNotExist()
        compose.runOnIdle {
            assertFalse(initial.state.value.ageConfirmationVisible)
            assertEquals(listOf("underage"), calls)
            assertEquals(0, completions)
            viewModel.value = OnboardingViewModel("onboarding/age", CompleteIntroductionUseCase { completions++ })
        }
        compose.onNodeWithText("O uso do aplicativo é restrito a maiores de 18 anos.").assertIsDisplayed()
        compose.onNodeWithText("Saiba mais sobre o Jogo Responsável.").performClick()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            // O coletor deve acompanhar o ViewModel substituto, não a introdução antiga.
            viewModel.value.complete()
        }
        compose.runOnIdle {
            assertEquals(listOf("underage", "responsible", "back", "complete"), calls)
            assertEquals(0, completions)
        }
    }

    @Test fun tutorialToggleWorksFromLabelAndCheckboxAndOnlyStartPersistsDismissal() {
        val calls = mutableListOf<String>()
        val dismissals = mutableListOf<Pair<BettingTutorial, Boolean>>()
        val viewModel = OnboardingViewModel("onboarding/tutorial", CompleteIntroductionUseCase {},
            SetBettingTutorialDismissedUseCase { tutorial, dismissed -> dismissals += tutorial to dismissed })
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                OnboardingRoute(viewModel, {}, {}, {}, {}, { calls += "start" }, { calls += "skip" })
            }
        }
        compose.onNode(isToggleable()).assertIsOff()
        compose.onNodeWithText("Não mostrar mais esse tutorial").performClick()
        compose.onNode(isToggleable()).assertIsOn().performClick().assertIsOff()
        compose.onNodeWithText("Não mostrar mais esse tutorial").performClick()
        compose.onNodeWithText("Pular").performClick()
        compose.runOnIdle {
            assertTrue(viewModel.state.value.tutorialDoNotShowAgain)
            assertTrue(dismissals.isEmpty())
            assertEquals(listOf("skip"), calls)
        }
        compose.onNodeWithText("Começar").performClick()
        compose.runOnIdle {
            assertEquals(listOf(BettingTutorial.MAIS_MILIONARIA to true), dismissals)
            assertEquals(listOf("skip", "start"), calls)
        }
    }
}
