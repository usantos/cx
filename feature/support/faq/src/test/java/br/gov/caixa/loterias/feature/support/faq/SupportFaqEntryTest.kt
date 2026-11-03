package br.gov.caixa.loterias.feature.support.faq

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.support.domain.model.SupportQuestion
import br.gov.caixa.loterias.feature.support.presentation.dynamic.SupportFaqEntry
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.ResourcesMode

@RunWith(DynamicFeatureRobolectricTestRunner::class)
@ResourcesMode(ResourcesMode.Mode.NATIVE)
@Config(sdk = [FAQ_TEST_SDK], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
class SupportFaqEntryTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun erroAtualSubstituiAsPerguntasAnterioresNaEntradaInstalada() {
        compose.setContent {
            LoteriasCaixaDSTheme {
                SupportFaqEntryImpl().Content(
                    SupportUiState(
                        selectedSectionId = 1,
                        questions = listOf(SupportQuestion(FAQ_QUESTION_ID, "Pergunta anterior", "Resposta anterior")),
                        errorMessage = "Falha ao carregar perguntas"
                    ),
                    onAction = { error("Falha de conteúdo não dispara ação") }
                )
            }
        }
        compose.onNodeWithTag("simlo.help.0025").assertIsDisplayed()
        compose.onNodeWithText("Falha ao carregar perguntas").assertIsDisplayed()
        compose.onNodeWithText("Pergunta anterior").assertDoesNotExist()
    }

    @Test fun typedEntryPreservesQuestionExpansionAndCollapseActions() {
        val entry: SupportFaqEntry = SupportFaqEntryImpl::class.java.getConstructor().newInstance()
        val state = mutableStateOf(
            SupportUiState(
                selectedSectionId = 1,
                questions = listOf(SupportQuestion(FAQ_QUESTION_ID, "Como consultar?", "Consulte o portal da CAIXA."))
            )
        )
        val actions = mutableListOf<SupportAction>()
        compose.setContent {
            LoteriasCaixaDSTheme {
                entry.Content(state.value) { action ->
                    actions += action
                    if (action is SupportAction.ToggleQuestion) {
                        state.value = state.value.copy(
                            expandedQuestionIds = if (action.id in state.value.expandedQuestionIds) emptySet() else setOf(action.id)
                        )
                    }
                }
            }
        }
        compose.onNodeWithText("Consulte o portal da CAIXA.").assertDoesNotExist()
        compose.onNodeWithContentDescription("Expandir resposta").assertIsDisplayed()
        compose.onNodeWithText("Como consultar?").performClick()
        compose.onNodeWithText("Consulte o portal da CAIXA.").assertIsDisplayed()
        compose.onNodeWithContentDescription("Recolher resposta").assertIsDisplayed()
        compose.onNodeWithText("Como consultar?").performClick()
        compose.onNodeWithText("Consulte o portal da CAIXA.").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(listOf(SupportAction.ToggleQuestion(FAQ_QUESTION_ID), SupportAction.ToggleQuestion(FAQ_QUESTION_ID)), actions)
        }
    }
}

private const val FAQ_TEST_SDK = 36
private const val FAQ_QUESTION_ID = 9L
