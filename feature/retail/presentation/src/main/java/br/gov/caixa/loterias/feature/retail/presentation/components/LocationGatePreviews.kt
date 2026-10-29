package br.gov.caixa.loterias.feature.retail.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.retail.presentation.screen.LocationGateScreen
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateStatus
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateUiState

@Preview(name = "Localização: permissão", showBackground = true)
@Composable
private fun LocationPermissionPreview() {
    CaixaDSTheme(darkTheme = false) {
        LocationGateScreen(
            state = LocationGateUiState(LocationGateStatus.PERMISSION_REQUIRED),
            versionName = "1.0",
            lotteryIconResourceId = 0,
            onAction = {},
        )
    }
}

@Preview(name = "Localização: falha", showBackground = true)
@Composable
private fun LocationFailurePreview() {
    CaixaDSTheme(darkTheme = false) {
        LocationGateScreen(LocationGateUiState(LocationGateStatus.ERROR), "1.0", 0, {})
    }
}
