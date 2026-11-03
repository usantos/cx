package br.gov.caixa.loterias.feature.tickets.presentation.delivery

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface TicketsTicketScannerScreenEntry {
    @Composable
    fun TicketScannerScreen(
        permissionGranted: Boolean,
        onBack: () -> Unit,
        onRequestCameraPermission: () -> Unit,
        cameraPreview: @Composable (Modifier) -> Unit,
        onManual: (() -> Unit)? = null,
    )
}
