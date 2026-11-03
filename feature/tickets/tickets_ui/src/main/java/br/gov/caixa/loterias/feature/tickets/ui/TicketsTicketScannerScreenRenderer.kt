package br.gov.caixa.loterias.feature.tickets.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsTicketScannerScreenEntry

internal class TicketsTicketScannerScreenRenderer : TicketsTicketScannerScreenEntry {
    @Composable
    override fun TicketScannerScreen(
        permissionGranted: Boolean,
        onBack: () -> Unit,
        onRequestCameraPermission: () -> Unit,
        cameraPreview: @Composable (Modifier) -> Unit,
        onManual: (() -> Unit)?,
    ) {
        br.gov.caixa.loterias.feature.tickets.presentation.screen.TicketScannerScreenDelivered(
            permissionGranted,
            onBack,
            onRequestCameraPermission,
            cameraPreview,
            onManual
        )
    }
}
