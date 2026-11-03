package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsUiEntry

/** A rota fornece o conteúdo da câmera; testes podem renderizar a sobreposição sem hardware. */

@Composable
fun TicketScannerScreen(
    permissionGranted: Boolean,
    onBack: () -> Unit,
    onRequestCameraPermission: () -> Unit,
    cameraPreview: @Composable (Modifier) -> Unit,
    onManual: (() -> Unit)? = null,
) {
    FeatureModuleContent(
        module = AppModule.TICKETS,
        entryType = TicketsUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.TicketScannerScreen(permissionGranted, onBack, onRequestCameraPermission, cameraPreview, onManual)
    }
}
