package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment
import androidx.compose.ui.test.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.gov.caixa.loterias.feature.account.presentation.route.LoginEnvironmentPicker
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HostEnvironmentFlowUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    @Test fun failedChoiceRetriesBeforeTheClosingAcknowledgement() {
        var changes = 0; var closed = 0
        compose.setContent { CaixaDSTheme {
            LoginEnvironmentPicker(TestEnvironment.EXTERNO_ESTEIRA, true, {
                changes++; if (changes == 1) error("storage unavailable") else true
            }, { closed++ })
        } }
        compose.onNodeWithTag("environment-picker").performClick()
        compose.onNodeWithText("PILOTO").performClick()
        compose.onNodeWithText("Não foi possível salvar a mudança de ambiente. Tente novamente.").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, closed); assertEquals(1, changes) }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("O aplicativo será encerrado para aplicar as mudanças de ambiente.").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, closed); assertEquals(2, changes) }
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertEquals(1, closed) }
    }
    @Test fun pendingCommitDisablesThePickerUntilTheNotice() {
        val commit = CompletableDeferred<Boolean>(); var changes = 0
        compose.setContent { CaixaDSTheme {
            LoginEnvironmentPicker(TestEnvironment.EXTERNO_ESTEIRA, true, { changes++; commit.await() }, {})
        } }
        compose.onNodeWithTag("environment-picker").performClick()
        compose.onNodeWithText("PILOTO").performClick()
        compose.onNodeWithTag("environment-picker").assertIsNotEnabled()
        compose.onNodeWithText("Mudança de Ambiente").assertDoesNotExist()
        compose.runOnIdle { assertEquals(1, changes); commit.complete(true) }
        compose.onNodeWithText("Mudança de Ambiente").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, changes) }
    }
    @Test fun callbackRecompositionDoesNotRepeatACompletedNoChange() {
        var generation by mutableStateOf(0)
        var changes = 0
        compose.setContent { CaixaDSTheme {
            val current = generation
            LoginEnvironmentPicker(TestEnvironment.EXTERNO_ESTEIRA, true, {
                check(current >= 0)
                changes++
                false
            }, {})
        } }
        compose.onNodeWithTag("environment-picker").performClick()
        compose.onNodeWithText("PILOTO").performClick()
        compose.runOnIdle { assertEquals(1, changes); generation++ }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals("Only an explicit retry or restored request may save again", 1, changes) }
    }
}
