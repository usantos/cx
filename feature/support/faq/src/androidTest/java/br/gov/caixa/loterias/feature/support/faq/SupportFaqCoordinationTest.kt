package br.gov.caixa.loterias.feature.support.faq

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.feature.support.domain.model.SupportQuestion
import br.gov.caixa.loterias.feature.support.presentation.screen.SupportScreen
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SupportFaqCoordinationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun pendingLinkSurvivesRecompositionButResetsWhenTheAnswerChanges() {
        val answer = "www.caixa.gov.br"
        val state = mutableStateOf(
            SupportUiState(
                selectedSectionId = 1,
                selectedSectionTitle = "PERGUNTAS",
                questions = listOf(SupportQuestion(1, "Portal", answer)),
                expandedQuestionIds = setOf(1)
            )
        )
        compose.setContent {
            LoteriasCaixaDSTheme {
                SupportScreen(state.value, {
                    error("A link must not dispatch a support action")
                }, faqContent = { state, onAction -> SupportFaqEntryImpl().Content(state, onAction) })
            }
        }
        compose.onNodeWithText(answer).performTouchInput { click() }
        compose.onNodeWithText("Você será direcionado para a WEB. Deseja continuar?").assertIsDisplayed()
        compose.runOnIdle { state.value = state.value.copy(selectedSectionTitle = "PORTAIS") }
        compose.onNodeWithText("Você será direcionado para a WEB. Deseja continuar?").assertIsDisplayed()
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithText("Você será direcionado para a WEB. Deseja continuar?").assertDoesNotExist()

        compose.onNodeWithText(answer).performTouchInput { click() }
        compose.onNodeWithText("Você será direcionado para a WEB. Deseja continuar?").assertIsDisplayed()
        compose.runOnIdle {
            state.value = state.value.copy(questions = listOf(SupportQuestion(1, "Portal", "Resposta atualizada")))
        }
        compose.onNodeWithText("Você será direcionado para a WEB. Deseja continuar?").assertDoesNotExist()
        compose.onNodeWithText("Resposta atualizada").assertIsDisplayed()
    }

    @Test fun faqClosedExpandedLoadingAndFailureRemainLocal() {
        val state = mutableStateOf(
            SupportUiState(
                selectedSectionId = 1,
                selectedSectionTitle = "PERGUNTAS",
                questions = listOf(SupportQuestion(1, "Pergunta de Teste", "Resposta sintética de suporte, sem informações pessoais."))
            )
        )
        compose.setContent {
            LoteriasCaixaDSTheme(darkTheme = false) {
                SupportScreen(state.value, { action ->
                    if (action is SupportAction.ToggleQuestion) {
                        state.value = state.value.copy(expandedQuestionIds = setOf(action.id))
                    } else {
                        error("No external support action allowed")
                    }
                }, faqContent = { currentState, onAction -> SupportFaqEntryImpl().Content(currentState, onAction) })
            }
        }
        compose.onNodeWithText("Pergunta de Teste").assertIsDisplayed()
        capture("faq-closed")
        compose.onNodeWithText("Pergunta de Teste").performClick()
        compose.onNodeWithText("Resposta sintética de suporte, sem informações pessoais.").assertIsDisplayed()
        capture("faq-expanded")
        compose.runOnIdle { state.value = state.value.copy(isLoading = true) }
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        capture("faq-loading")
        compose.runOnIdle { state.value = state.value.copy(isLoading = false, errorMessage = "Falha controlada de conteúdo") }
        compose.onNodeWithText("Falha controlada de conteúdo").assertIsDisplayed()
        capture("faq-failure")
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val directory = File(requireNotNull(instrumentation.targetContext.externalCacheDir), "remaining-support-parity").apply { mkdirs() }
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(directory, "$name.png").outputStream().use { assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, FAQ_CAPTURE_QUALITY, it)) }
        bitmap.recycle()
    }
}

private const val FAQ_CAPTURE_QUALITY = 100
