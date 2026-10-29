package br.gov.caixa.loterias.core.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.core.presentation.state.ServiceFailurePresentation
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_LOGIN_REQUIRED
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_UPDATED_TERMS
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_OFFLINE
import br.gov.caixa.loterias.core.presentation.components.ServiceFailureOverlay

class ServiceFailureOverlayTest {
    @get:Rule val compose = createComposeRule()
    private val redirects = mutableListOf<ServiceRedirect>()
    private var connection = ConnectionAvailability.CONNECTED

    @Test fun termsRequireAcknowledgementThenNavigateOnlyOnce() {
        val state = mutableStateOf(ServiceFailurePresentation(notice = ServiceNoticeUiState(LEGACY_UPDATED_TERMS, ServiceRedirect.ACCEPT_TERMS)))
        show(state)
        compose.onNodeWithText(LEGACY_UPDATED_TERMS).assertIsDisplayed()
        compose.runOnIdle { assertTrue(redirects.isEmpty()) }
        compose.onNodeWithText("Entendi").performClick()
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(listOf(ServiceRedirect.ACCEPT_TERMS), redirects); assertFalse(state.value.pending) }
    }

    @Test fun expiredSessionAcknowledgementChecksConnectivityAgainAndNeverLaunchesARequest() {
        val state = mutableStateOf(ServiceFailurePresentation(notice = ServiceNoticeUiState(LEGACY_LOGIN_REQUIRED, ServiceRedirect.LOGIN)))
        show(state)
        compose.onNodeWithText(LEGACY_LOGIN_REQUIRED).assertIsDisplayed()
        compose.onNodeWithText("Não").assertDoesNotExist()
        compose.onNodeWithText("Sim").assertDoesNotExist()
        compose.runOnIdle {
            assertTrue(redirects.isEmpty())
            connection = ConnectionAvailability.DISCONNECTED
        }
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText(LEGACY_OFFLINE).assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertTrue(redirects.isEmpty()); assertFalse(state.value.pending) }
    }

    @Test fun moreBetsOffersDismissalOrExplicitHomeNavigation() {
        val state = mutableStateOf(ServiceFailurePresentation(notice = ServiceNoticeUiState("Aposte mais", ServiceRedirect.HOME)))
        show(state)
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertTrue(redirects.isEmpty())
            state.value = ServiceFailurePresentation(notice = ServiceNoticeUiState("Aposte mais", ServiceRedirect.HOME))
        }
        compose.onNodeWithText("Apostar mais").performClick()
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(listOf(ServiceRedirect.HOME), redirects) }
    }

    private fun show(state: MutableState<ServiceFailurePresentation>) {
        val network = NetworkAvailability { connection }
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                ServiceFailureOverlay(state.value, onAccept = { state.value = state.value.accept(it, network) },
                    onDismiss = { state.value = state.value.dismiss(it) }, consumeRedirect = { expected ->
                        if (state.value.redirect !== expected) false
                        else { state.value = state.value.copy(redirect = null); true }
                    }, onRedirect = { redirects += it })
            }
        }
    }
}
