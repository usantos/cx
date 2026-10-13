package br.gov.caixa.loterias.feature.support

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import org.junit.Rule
import org.junit.Test

class SupportLoadingStateTest {
    @get:Rule val compose = createComposeRule()

    @Test fun loadingModalEndsInVisibleFailureWithoutInventingContentOrTermsAcceptance() {
        val state = mutableStateOf(SupportUiState(isLoading = true))
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                SupportScreen(state.value, onAction = { error("No support action should run while this controlled request completes") })
            }
        }
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        compose.runOnIdle { state.value = state.value.copy(isLoading = false, errorMessage = "Falha controlada de conteúdo") }
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
        compose.onNodeWithText("Falha controlada de conteúdo").assertIsDisplayed()
    }
}
