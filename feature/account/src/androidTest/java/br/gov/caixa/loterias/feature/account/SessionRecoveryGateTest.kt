package br.gov.caixa.loterias.feature.account

import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import org.junit.Rule
import org.junit.Test

class SessionRecoveryGateTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun checkingAndFailureNeverComposeProtectedContentAndRetryReturnsToChecking() {
        val state = mutableStateOf(SessionRecoveryState.CHECKING)
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                SessionRecoveryContent(state.value, { state.value = SessionRecoveryState.CHECKING }, {},
                    loading = { Text("Splash de referência") }, content = { Text("Conteúdo protegido") })
            }
        }
        composeRule.onNodeWithText("Splash de referência").assertIsDisplayed()
        composeRule.onNodeWithText("Conteúdo protegido").assertDoesNotExist()
        composeRule.runOnIdle { state.value = SessionRecoveryState.FAILED }
        composeRule.onNodeWithText("Não foi possível verificar ou concluir a limpeza dos dados da sessão anterior. Tente novamente para continuar com segurança.").assertIsDisplayed()
        composeRule.onNodeWithText("Conteúdo protegido").assertDoesNotExist()
        composeRule.onNodeWithText("Tentar novamente").performClick()
        composeRule.onNodeWithText("Tentar novamente").assertDoesNotExist()
        composeRule.onNodeWithText("Conteúdo protegido").assertDoesNotExist()
    }

    @Test fun recoveredCleanupRequiresAcknowledgmentAndDoesNotClaimBrowserLogout() {
        val state = mutableStateOf(SessionRecoveryState.AWAITING_ACKNOWLEDGMENT)
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                SessionRecoveryContent(state.value, {}, { state.value = SessionRecoveryState.READY },
                    loading = { Text("Splash de referência") }, content = { Text("Conteúdo protegido") })
            }
        }
        composeRule.onNodeWithText("A limpeza dos dados locais da sessão anterior foi concluída. O encerramento da sessão no navegador não foi confirmado.").assertIsDisplayed()
        composeRule.onNodeWithText("Conteúdo protegido").assertDoesNotExist()
        composeRule.onNodeWithText("Entendi").performClick()
        composeRule.onNodeWithText("Conteúdo protegido").assertIsDisplayed()
        composeRule.onNodeWithText("Splash de referência").assertDoesNotExist()
    }

    @Test fun ordinaryReadyEntryDoesNotAddAnAlertOrLoading() {
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                SessionRecoveryContent(SessionRecoveryState.READY, {}, {},
                    loading = { Text("Splash de referência") }, content = { Text("Conteúdo protegido") })
            }
        }
        composeRule.onNodeWithText("Conteúdo protegido").assertIsDisplayed()
        composeRule.onNodeWithText("Atenção").assertDoesNotExist()
        composeRule.onNodeWithText("Splash de referência").assertDoesNotExist()
    }
}
