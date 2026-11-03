package br.gov.caixa.loterias.feature.support

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.support.presentation.screen.SupportScreen
import br.gov.caixa.loterias.feature.support.presentation.state.SupportTab
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import org.junit.Rule
import org.junit.Test

class SupportLoadingStateTest {
    @get:Rule val compose = createComposeRule()

    @Test fun loadingModalEndsInVisibleFailureWithoutInventingContentOrTermsAcceptance() {
        val state = mutableStateOf(SupportUiState(selectedTab = SupportTab.TERMS, isLoading = true))
        compose.setFeatureContent {
            LoteriasCaixaDSTheme(darkTheme = false) {
                SupportScreen(state.value, onAction = {
                    error("No support action should run while this controlled request completes")
                }, faqContent = { _, _ -> })
            }
        }
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        compose.runOnIdle { state.value = state.value.copy(isLoading = false, errorMessage = "Falha controlada de conteúdo") }
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
        compose.onNodeWithText("Falha controlada de conteúdo").assertIsDisplayed()
    }
}
