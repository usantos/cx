package br.gov.caixa.loterias.feature.account

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.account.presentation.components.LogoutStatus
import br.gov.caixa.loterias.feature.account.presentation.state.LogoutNotice
import br.gov.caixa.loterias.feature.account.presentation.state.LogoutUiState

class LogoutStatusTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun serverFailureRetainsAccessAndOffersExplicitRetry() {
        val state = mutableStateOf(LogoutUiState(notice = LogoutNotice.SERVER_FAILURE))
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                LogoutStatus(state.value) { state.value = LogoutUiState(signingOut = true) }
            }
        }
        composeRule.onNodeWithText("Não foi possível encerrar a sessão no serviço de loterias. Seu acesso foi mantido. Tente novamente.").assertIsDisplayed()
        composeRule.onNodeWithText("Entendi").assertDoesNotExist()
        composeRule.onNodeWithText("Tentar novamente").performClick()
        composeRule.onNodeWithText("Carregando…").assertIsDisplayed()
    }

    @Test fun incompleteCleanupOffersExplicitRetryInsteadOfDismissal() {
        val state = mutableStateOf(LogoutUiState(notice = LogoutNotice.LOCAL_DATA_FAILURE))
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                LogoutStatus(state.value) { state.value = LogoutUiState(clearingLocalData = true) }
            }
        }
        composeRule.onNodeWithText("O acesso foi encerrado, mas não foi possível limpar todos os dados locais da sessão. Tente novamente para continuar com segurança.").assertIsDisplayed()
        composeRule.onNodeWithText("Entendi").assertDoesNotExist()
        composeRule.onNodeWithText("Tentar novamente").performClick()
        composeRule.onNodeWithText("Tentar novamente").assertDoesNotExist()
        composeRule.onNodeWithText("Carregando…").assertIsDisplayed()
    }

    @Test fun cancellationExplainsLocalAndBrowserSessionsSeparatelyAndEntendiDismisses() {
        val state = mutableStateOf(LogoutUiState(notice = LogoutNotice.CANCELLED))
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                LogoutStatus(state.value) { state.value = LogoutUiState() }
            }
        }
        composeRule.onNodeWithText("Atenção").assertIsDisplayed()
        composeRule.onNodeWithText("Você saiu deste aplicativo. O encerramento da sessão no navegador foi cancelado e não pôde ser confirmado.").assertIsDisplayed()
        composeRule.onNodeWithText("Entendi").performClick()
        composeRule.onNodeWithText("Atenção").assertDoesNotExist()
        composeRule.onNodeWithText("Carregando…").assertDoesNotExist()
    }

    @Test fun pendingBrowserUsesSharedLoadingAndStopsWhenOutcomeIsUnconfirmed() {
        val state = mutableStateOf(LogoutUiState(waitingForBrowser = true))
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) { LogoutStatus(state.value) {} }
        }
        composeRule.mainClock.advanceTimeBy(900)
        composeRule.onNodeWithText("Carregando…").assertIsDisplayed()
        composeRule.runOnIdle { state.value = LogoutUiState(notice = LogoutNotice.UNCONFIRMED) }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.onNodeWithText("Carregando…").assertDoesNotExist()
        composeRule.onNodeWithText("Você saiu deste aplicativo, mas não foi possível confirmar o encerramento da sessão no navegador.").assertIsDisplayed()
    }

    @Test fun localFailureDoesNotSayThatUserHasSignedOut() {
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                LogoutStatus(LogoutUiState(notice = LogoutNotice.LOCAL_FAILURE)) {}
            }
        }
        composeRule.onNodeWithText("Não foi possível encerrar a sessão neste aplicativo. Tente novamente.").assertIsDisplayed()
        composeRule.onNodeWithText("Entendi").assertIsDisplayed()
    }
}
