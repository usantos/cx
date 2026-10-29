package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.gov.caixa.loterias.apostas.composition.startup.AppStartupStatusPanel
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class StartupConnectionRecoveryUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun retryStaysInlineAndRechecksConnectionAfterFailure() {
        val loading = mutableStateOf(false)
        var connection = ConnectionAvailability.CONNECTED
        var retries = 0
        val network = NetworkAvailability { connection }
        compose.setContent { CaixaDSTheme {
            AppStartupStatusPanel(StartupStatusKind.UNAVAILABLE, { retries++; loading.value = true }, network, loading.value)
        } }
        compose.onNodeWithText("Código do erro: LTR-503").assertExists()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onAllNodes(isDialog()).assertCountEquals(0)
        compose.runOnIdle { assertEquals(1, retries); connection = ConnectionAvailability.DISCONNECTED; loading.value = false }
        compose.onNodeWithText("Código do erro: LTR-001").assertExists()
        compose.onNodeWithText("Verifique sua conexão\ne tente novamente").assertExists()
        compose.onAllNodes(isDialog()).assertCountEquals(0)
    }

    @Test fun backAsksBeforeClosingAndCancelKeepsScreen() {
        compose.setContent { CaixaDSTheme {
            AppStartupStatusPanel(StartupStatusKind.UNAVAILABLE, {}, NetworkAvailability { ConnectionAvailability.DISCONNECTED })
        } }
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("Deseja sair do aplicativo?").assertIsDisplayed()
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithText("Deseja sair do aplicativo?").assertDoesNotExist()
        compose.onNodeWithText("Tentar novamente").assertIsDisplayed()
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("Sair").performClick()
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        assertTrue(compose.activity.isFinishing)
    }
}
