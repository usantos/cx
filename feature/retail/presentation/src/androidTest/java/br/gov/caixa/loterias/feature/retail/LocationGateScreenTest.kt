package br.gov.caixa.loterias.feature.retail

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.retail.presentation.screen.LocationGateScreen
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateStatus
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateUiState

/** Rótulos de estado errados, falta de ação de permissão/configurações ou callback obsoleto fazem esta suíte falhar. */
class LocationGateScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun permissionSettingsAndRetryStatesKeepTheirMessagesAndCurrentAction() {
        val state = mutableStateOf(LocationGateUiState(LocationGateStatus.PERMISSION_REQUIRED))
        val selected = mutableListOf<LocationGateStatus>()
        compose.setContent { CaixaDSTheme {
            LocationGateScreen(state.value, "characterization", 0) { selected += state.value.status }
        } }
        compose.onNodeWithText("vcharacterization").assertIsDisplayed()
        compose.onNodeWithText("Precisamos conferir se você está em território brasileiro.\nHabilite sua localização.").assertIsDisplayed()
        compose.onNodeWithText("Permitir acesso a localização").performClick()
        compose.runOnIdle { state.value = LocationGateUiState(LocationGateStatus.PERMISSION_PERMANENTLY_DENIED) }
        compose.onNodeWithText("Permitir acesso a localização").performClick()
        compose.runOnIdle { state.value = LocationGateUiState(LocationGateStatus.PROVIDER_DISABLED) }
        compose.onNodeWithText("Ir para configurações").performClick()
        compose.runOnIdle { state.value = LocationGateUiState(LocationGateStatus.ERROR) }
        compose.onNodeWithText("Não foi possível recuperar sua localização. Por favor, tente novamente").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle { assertEquals(listOf(
            LocationGateStatus.PERMISSION_REQUIRED, LocationGateStatus.PERMISSION_PERMANENTLY_DENIED,
            LocationGateStatus.PROVIDER_DISABLED, LocationGateStatus.ERROR,
        ), selected) }
    }

    @Test fun checkingAndOutsideBrazilDoNotOfferAnAction() {
        val state = mutableStateOf(LocationGateUiState())
        compose.setContent { CaixaDSTheme {
            LocationGateScreen(state.value, "1.0", 0) { error("No action is offered") }
        } }
        compose.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()
        compose.onAllNodes(hasClickAction()).assertCountEquals(0)
        compose.runOnIdle { state.value = LocationGateUiState(LocationGateStatus.OUTSIDE_BRAZIL) }
        compose.onNodeWithText("As apostas de Loterias só podem ser realizadas em território brasileiro.").assertIsDisplayed()
        compose.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertDoesNotExist()
        compose.onAllNodes(hasClickAction()).assertCountEquals(0)
    }
}
