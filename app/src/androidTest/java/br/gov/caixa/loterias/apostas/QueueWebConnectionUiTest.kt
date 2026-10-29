package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.apostas.composition.startup.QueueWebConnectionGate
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class QueueWebConnectionUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun failedPageWithInternetShowsServiceFailureAndRetryRecreatesOnlyThePage() {
        var connection = ConnectionAvailability.CONNECTED
        var pageInstances = 0
        val network = NetworkAvailability { connection }
        compose.setContent {
            CaixaDSTheme {
                QueueWebConnectionGate(network) { failed ->
                    DisposableEffect(Unit) {
                        pageInstances++
                        onDispose { }
                    }
                    CaixaDSButton(onClick = failed) { CaixaDSText("FAIL PAGE") }
                }
            }
        }
        compose.onNodeWithText("FAIL PAGE").performClick()
        compose.onNodeWithText("Código do erro: LTR-503").assertExists()
        compose.onNodeWithText("Verifique sua conexão\ne tente novamente").assertDoesNotExist()
        compose.onNodeWithText("FAIL PAGE").assertDoesNotExist()
        compose.runOnIdle { connection = ConnectionAvailability.DISCONNECTED }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("Código do erro: LTR-001").assertExists()
        compose.onAllNodes(isDialog()).assertCountEquals(0)
        compose.runOnIdle {
            assertEquals(1, pageInstances)
            connection = ConnectionAvailability.CONNECTED
        }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("FAIL PAGE").assertIsDisplayed()
        compose.runOnIdle { assertEquals(2, pageInstances) }
    }
}
