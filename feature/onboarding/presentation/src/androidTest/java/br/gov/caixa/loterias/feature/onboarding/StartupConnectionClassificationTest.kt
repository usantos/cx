package br.gov.caixa.loterias.feature.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.onboarding.presentation.components.StartupStatusPanelContent
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusKind
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class StartupConnectionClassificationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun queueTransportFailureWithConnectedNetworkDoesNotClaimDeviceIsOffline() = checkFailure(
        ConnectionAvailability.CONNECTED,
        "LTR-503"
    )

    @Test fun queueTransportFailureWithUnknownNetworkDoesNotInventAnOfflineDiagnosis() = checkFailure(
        ConnectionAvailability.UNKNOWN,
        "LTR-503"
    )

    @Test fun confirmedDisconnectionKeepsOfflineMessageAndRetry() = checkFailure(ConnectionAvailability.DISCONNECTED, "LTR-001")

    @Test fun transportFailureNeverReplacesMandatoryUpdate() {
        show(ConnectionAvailability.CONNECTED, StartupStatusKind.UPDATE_REQUIRED)
        compose.onNodeWithText("Atualizar").assertIsDisplayed()
        compose.onNodeWithText("Código do erro: LTR-001").assertDoesNotExist()
        compose.onNodeWithText("Código do erro: LTR-503").assertDoesNotExist()
    }

    private fun checkFailure(connection: ConnectionAvailability, code: String) {
        var retries = 0
        show(connection, StartupStatusKind.OFFLINE) { retries++ }
        compose.onNodeWithText("Código do erro: $code").assertIsDisplayed()
        if (connection != ConnectionAvailability.DISCONNECTED) {
            compose.onNodeWithText("Verifique sua conexão\ne tente novamente").assertDoesNotExist()
        }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    private fun show(connection: ConnectionAvailability, kind: StartupStatusKind, retry: () -> Unit = {}) {
        compose.setContent {
            CaixaDSTheme {
                StartupStatusPanelContent(
                    kind = kind,
                    onRetry = retry,
                    networkAvailability = NetworkAvailability { connection },
                    loading = false,
                    appName = "Loterias",
                    versionName = "teste",
                    toolbarIcon = {},
                    illustration = {},
                    onOpenStore = { true },
                    connectionFailure = true
                )
            }
        }
    }
}
