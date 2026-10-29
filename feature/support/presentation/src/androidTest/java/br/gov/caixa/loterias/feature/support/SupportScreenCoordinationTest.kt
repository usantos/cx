package br.gov.caixa.loterias.feature.support

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.support.domain.model.SupportQuestion
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportMode
import br.gov.caixa.loterias.feature.support.presentation.state.SupportTab
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import br.gov.caixa.loterias.feature.support.presentation.screen.SupportScreen

class SupportScreenCoordinationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun pendingLinkSurvivesRecompositionButResetsWhenTheAnswerChanges() {
        val answer = "www.caixa.gov.br"
        val state = mutableStateOf(SupportUiState(
            selectedSectionId = 1,
            selectedSectionTitle = "PERGUNTAS",
            questions = listOf(SupportQuestion(1, "Portal", answer)),
            expandedQuestionIds = setOf(1),
        ))
        compose.setContent {
            CaixaDSTheme { SupportScreen(state.value, { error("A link must not dispatch a support action") }) }
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

    @Test fun termsAcceptanceAndTabsUseCurrentActionsAndKeepTheirGuards() {
        val state = mutableStateOf(SupportUiState(
            selectedTab = SupportTab.TERMS,
            showTermsTabs = true,
            acceptanceRequired = true,
            htmlContent = "<p>Termos sintéticos</p>",
        ))
        val revision = mutableIntStateOf(0)
        val actions = mutableListOf<Pair<Int, SupportAction>>()
        compose.setContent {
            val currentRevision = revision.intValue
            CaixaDSTheme {
                SupportScreen(state.value, { action ->
                    actions += currentRevision to action
                    state.value = when (action) {
                        SupportAction.ToggleTermsAcceptance -> state.value.copy(termsAccepted = !state.value.termsAccepted)
                        is SupportAction.SelectTab -> state.value.copy(selectedTab = action.tab)
                        else -> state.value
                    }
                })
            }
        }
        compose.onNodeWithText("Continuar").assertIsNotEnabled()
        compose.onNodeWithText("Declaro ter lido e concordado com os\ntermos de uso").performClick()
        compose.onNodeWithText("Continuar").assertIsEnabled()
        compose.runOnIdle { revision.intValue = 1 }
        compose.onNodeWithText("Continuar").performClick()
        compose.onNodeWithText("Dúvidas").performClick()
        compose.onNodeWithText("Continuar").assertDoesNotExist()
        compose.onNodeWithText("Termo de uso").performClick()
        compose.onNodeWithText("Continuar").assertIsEnabled()
        compose.runOnIdle {
            assertEquals(listOf(
                0 to SupportAction.ToggleTermsAcceptance,
                1 to SupportAction.AcceptTerms,
                1 to SupportAction.SelectTab(SupportTab.FAQ),
                1 to SupportAction.SelectTab(SupportTab.TERMS),
            ), actions)
        }
    }

    @Test fun specialModesKeepGlobalLoadingMetadataAndBack() {
        val state = mutableStateOf(SupportUiState(
            mode = SupportMode.GAME_HELP,
            gameHelpModalityId = 16,
            gameHelpModalityName = "Lotomania",
            gameHelpContestNumber = 123,
            gameHelpDrawDate = "2026-10-18",
            gameHelpClosingDateTime = "2026-10-18T23:30:00Z",
            htmlContent = "<b>Regra sintética</b>",
            isLoading = true,
        ))
        val actions = mutableListOf<SupportAction>()
        compose.setContent { CaixaDSTheme { SupportScreen(state.value, { actions += it }) } }
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        compose.runOnIdle { state.value = state.value.copy(isLoading = false) }
        compose.onNodeWithTag("support_game_help").assertExists()
        compose.onNodeWithTag("support_screen").assertDoesNotExist()
        compose.onNodeWithText("Nº 123").assertIsDisplayed()
        compose.onNodeWithText("18/10").assertIsDisplayed()
        compose.onNodeWithText("18/10 | 20h30").assertIsDisplayed()
        compose.onNodeWithText("Regra sintética").assertExists()
        compose.onNodeWithContentDescription("Fechar").performClick()
        compose.runOnIdle {
            assertEquals(listOf(SupportAction.Back), actions)
            state.value = state.value.copy(mode = SupportMode.TERMS_READ_ONLY, isLoading = true)
        }
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        compose.runOnIdle { state.value = state.value.copy(isLoading = false, errorMessage = "Falha controlada") }
        compose.onNodeWithTag("support_terms_view").assertExists()
        compose.onNodeWithTag("support_screen").assertDoesNotExist()
        compose.onNodeWithText("Falha controlada").assertIsDisplayed()
    }
}
