package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.feature.tickets.presentation.screen.TicketScannerScreenDelivered

@Preview(name = "Leitor: permissão", showBackground = true)
@Composable
private fun TicketScannerPermissionPreview() {
    LoteriasCaixaDSTheme(darkTheme = false) {
        TicketScannerScreenDelivered(false, {}, {}, cameraPreview = { Box(it) })
    }
}

@Preview(name = "Leitor: máscara sem câmera", showBackground = true)
@Composable
private fun TicketScannerOverlayPreview() {
    LoteriasCaixaDSTheme(darkTheme = false) {
        TicketScannerScreenDelivered(true, {}, {}, cameraPreview = { Box(it) })
    }
}
