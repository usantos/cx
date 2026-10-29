package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.tickets.presentation.screen.TicketScannerScreen

@Preview(name = "Leitor: permissão", showBackground = true)
@Composable
private fun TicketScannerPermissionPreview() {
    CaixaDSTheme(darkTheme = false) {
        TicketScannerScreen(false, {}, {}, cameraPreview = { Box(it) })
    }
}

@Preview(name = "Leitor: máscara sem câmera", showBackground = true)
@Composable
private fun TicketScannerOverlayPreview() {
    CaixaDSTheme(darkTheme = false) {
        TicketScannerScreen(true, {}, {}, cameraPreview = { Box(it) })
    }
}
