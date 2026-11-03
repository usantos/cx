package br.gov.caixa.loterias.feature.support

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.support.presentation.screen.SupportScreen
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportMode
import br.gov.caixa.loterias.feature.support.presentation.state.SupportTab
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SupportScreenCoordinationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun mandatoryTermsRenderWithoutResolvingFaqContent() {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                SupportScreen(
                    state = SupportUiState(
                        selectedTab = SupportTab.TERMS,
                        acceptanceRequired = true,
                        htmlContent = "<p>Termos disponíveis na base</p>"
                    ),
                    onAction = {},
                    faqContent = { _, _ -> error("Terms must not resolve the optional FAQ split") }
                )
            }
        }
        compose.onNodeWithTag("support_html").assertExists()
        compose.onNodeWithText("Continuar").assertIsDisplayed().assertIsNotEnabled()
    }

    @Test fun termsAcceptanceAndTabsUseCurrentActionsAndKeepTheirGuards() {
        val state = mutableStateOf(
            SupportUiState(
                selectedTab = SupportTab.TERMS,
                showTermsTabs = true,
                acceptanceRequired = true,
                htmlContent = "<p>Termos sintéticos</p>"
            )
        )
        val revision = mutableIntStateOf(0)
        val actions = mutableListOf<Pair<Int, SupportAction>>()
        compose.setFeatureContent {
            val currentRevision = revision.intValue
            LoteriasCaixaDSTheme {
                SupportScreen(state.value, { action ->
                    actions += currentRevision to action
                    state.value = when (action) {
                        SupportAction.ToggleTermsAcceptance -> state.value.copy(termsAccepted = !state.value.termsAccepted)
                        is SupportAction.SelectTab -> state.value.copy(selectedTab = action.tab)
                        else -> state.value
                    }
                }, faqContent = { _, _ -> })
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
            assertEquals(
                listOf(
                    0 to SupportAction.ToggleTermsAcceptance,
                    1 to SupportAction.AcceptTerms,
                    1 to SupportAction.SelectTab(SupportTab.FAQ),
                    1 to SupportAction.SelectTab(SupportTab.TERMS)
                ),
                actions
            )
        }
    }

    @Test fun specialModesKeepGlobalLoadingMetadataAndBack() {
        val state = mutableStateOf(
            SupportUiState(
                mode = SupportMode.GAME_HELP,
                gameHelpModalityId = 16,
                gameHelpModalityName = "Lotomania",
                gameHelpContestNumber = 123,
                gameHelpDrawDate = "2026-10-18",
                gameHelpClosingDateTime = "2026-10-18T23:30:00Z",
                htmlContent = "<b>Regra sintética</b>",
                isLoading = true
            )
        )
        val actions = mutableListOf<SupportAction>()
        compose.setFeatureContent { LoteriasCaixaDSTheme { SupportScreen(state.value, { actions += it }, faqContent = { _, _ -> }) } }
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
