package br.gov.caixa.loterias.feature.responsiblegaming

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.AccountSuspension
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentQuestion
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.AssessmentFormScreen
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.AssessmentHelpText
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.SuspensionScreen
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ResponsibleGamingScreensJvmTest {
    @get:Rule
    val compose = createComposeRule()
    private val questions =
        listOf(
            SelfAssessmentQuestion(1, "Você perdeu o controle das apostas?", 1),
            SelfAssessmentQuestion(2, "Você precisou pedir dinheiro?", 2)
        )

    @Test
    fun assessmentRequiresAnswerAndDispatchesNavigationAndTypedAnswers() {
        val state = mutableStateOf(ResponsibleGamingUiState(questions = questions))
        val actions = mutableListOf<ResponsibleGamingAction>()
        compose.setContent {
            CaixaDSTheme {
                AssessmentFormScreen(state.value, { action ->
                    actions.add(action)
                    state.value = when (action) {
                        is ResponsibleGamingAction.Answer -> state.value.copy(
                            answers =
                                state.value.answers + (state.value.currentQuestion!!.id to action.affirmative)
                        )

                        ResponsibleGamingAction.NextQuestion -> state.value.copy(currentQuestionIndex = 1)

                        ResponsibleGamingAction.PreviousQuestion -> state.value.copy(currentQuestionIndex = 0)

                        else -> state.value
                    }
                })
            }
        }
        compose.onNodeWithText("Anterior").assertIsNotEnabled()
        compose.onNodeWithText("Próximo").assertIsNotEnabled()
        compose.onNodeWithText(questions[0].description).performScrollTo().assertIsDisplayed()
        compose.onAllNodes(role(Role.RadioButton))[0].performScrollTo().performClick()
        compose.onNodeWithText("Próximo").assertIsEnabled().performClick()
        compose.onNodeWithText(questions[1].description).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Concluido").assertIsNotEnabled()
        compose.onAllNodes(role(Role.RadioButton))[1].performScrollTo().performClick()
        compose.onNodeWithText("Concluido").assertIsEnabled()
        compose.onNodeWithText("Anterior").performClick()
        compose.onNodeWithText("Fechar").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(
                listOf(
                    ResponsibleGamingAction.Answer(true),
                    ResponsibleGamingAction.NextQuestion,
                    ResponsibleGamingAction.Answer(false),
                    ResponsibleGamingAction.PreviousQuestion,
                    ResponsibleGamingAction.Back
                ),
                actions
            )
        }
    }

    @Test
    fun assessmentLoadingErrorAndMissingQuestionsOfferRetryWithoutShowingAnswers() {
        val state = mutableStateOf(ResponsibleGamingUiState(loading = true))
        val actions = mutableListOf<ResponsibleGamingAction>()
        compose.setContent { CaixaDSTheme { AssessmentFormScreen(state.value, actions::add) } }
        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)).assertIsDisplayed()
        compose.runOnIdle { state.value = state.value.copy(loading = false, errorMessage = "Falha ao carregar perguntas") }
        compose.onNodeWithText("Falha ao carregar perguntas").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle { state.value = state.value.copy(errorMessage = null) }
        compose.onNodeWithText("Autoavaliação sem perguntas.").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle { assertEquals(listOf(ResponsibleGamingAction.Retry, ResponsibleGamingAction.Retry), actions) }
    }

    @Test
    fun affirmativeAssessmentOffersHelpContactsAndCompletion() {
        val state = ResponsibleGamingUiState(questions = questions, answers = mapOf(1 to true), assessmentSubmitted = true)
        val actions = mutableListOf<ResponsibleGamingAction>()
        compose.setContent { CaixaDSTheme { AssessmentFormScreen(state, actions::add) } }
        compose.onNodeWithText("Muita atenção!", substring = true).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Ajuda").performClick()
        compose.onNodeWithText(AssessmentHelpText.Title).assertIsDisplayed()
        compose.onNodeWithText(AssessmentHelpText.CaixaMetropolitanPhone).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(AssessmentHelpText.ProamjoEmail).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(AssessmentHelpText.AnonymousGamblersLink).performScrollTo().assertIsDisplayed()
        compose.runOnIdle { assertEquals(emptyList<ResponsibleGamingAction>(), actions) }
    }

    @Test
    fun negativeAssessmentCongratulatesAndCompletesWithoutHelpAction() {
        val actions = mutableListOf<ResponsibleGamingAction>()
        compose.setContent {
            CaixaDSTheme {
                AssessmentFormScreen(
                    ResponsibleGamingUiState(
                        questions = questions,
                        assessmentRequired = true,
                        answers = mapOf(1 to false),
                        assessmentSubmitted = true
                    ),
                    actions::add
                )
            }
        }
        compose.onNodeWithText("Parabéns!", substring = true).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Ajuda").assertDoesNotExist()
        compose.onNodeWithText("Fechar").performClick()
        compose.runOnIdle { assertEquals(listOf(ResponsibleGamingAction.Complete), actions) }
    }

    @Test
    fun suspensionRequiresPeriodAndAcknowledgementBeforeConfirmationAndSubmission() {
        val state = mutableStateOf(ResponsibleGamingUiState())
        val actions = mutableListOf<ResponsibleGamingAction>()
        var submissions = 0
        compose.setContent {
            CaixaDSTheme {
                SuspensionScreen(state.value, { action ->
                    actions.add(action)
                    state.value = when (action) {
                        is ResponsibleGamingAction.SelectSuspensionPeriod -> state.value.copy(selectedSuspensionPeriod = action.label)

                        ResponsibleGamingAction.ToggleSuspensionAcknowledgement -> state.value.copy(
                            suspensionAcknowledged = !state.value.suspensionAcknowledged
                        )

                        ResponsibleGamingAction.ConfirmSuspension -> state.value.copy(suspensionConfirmation = true)

                        ResponsibleGamingAction.Back -> state.value.copy(suspensionConfirmation = false)

                        else -> state.value
                    }
                }, { submissions++ })
            }
        }
        compose.onNodeWithText("Confirmar").performScrollTo().assertIsNotEnabled()
        compose.onAllNodes(role(Role.RadioButton))[0].performScrollTo().performClick()
        compose.onNodeWithText("Confirmar").performScrollTo().assertIsNotEnabled()
        compose.onNode(role(Role.Checkbox)).performScrollTo().performClick()
        compose.onNodeWithText("Confirmar").performScrollTo().assertIsEnabled().performClick()
        compose.onNodeWithText("Você confirma a suspensão por", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Desistir").performClick()
        compose.onNodeWithText("Confirmar").performScrollTo().performClick()
        compose.onNodeWithText("Confirmar Suspensão").performClick()
        compose.runOnIdle {
            assertEquals(1, submissions)
            assertEquals(
                listOf(
                    ResponsibleGamingAction.SelectSuspensionPeriod("24 horas"),
                    ResponsibleGamingAction.ToggleSuspensionAcknowledgement,
                    ResponsibleGamingAction.ConfirmSuspension,
                    ResponsibleGamingAction.Back,
                    ResponsibleGamingAction.ConfirmSuspension
                ),
                actions
            )
        }
    }

    @Test
    fun suspensionLoadingErrorSuccessAndBackKeepTheirSpecificActions() {
        val state = mutableStateOf(ResponsibleGamingUiState(loading = true))
        val actions = mutableListOf<ResponsibleGamingAction>()
        compose.setContent { CaixaDSTheme { SuspensionScreen(state.value, actions::add, {}) } }
        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)).assertIsDisplayed()
        compose.runOnIdle { state.value = state.value.copy(loading = false, errorMessage = "Não foi possível suspender") }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle {
            state.value =
                state.value.copy(errorMessage = null, suspension = AccountSuspension(true, "24 horas"), suspensionEndsAt = "Até amanhã")
        }
        compose.onNodeWithText("Até amanhã", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { state.value = ResponsibleGamingUiState() }
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(listOf(ResponsibleGamingAction.Retry, ResponsibleGamingAction.Complete, ResponsibleGamingAction.Back), actions)
        }
    }

    private fun role(value: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, value)
}
