package br.gov.caixa.loterias.feature.tickets.presentation.route

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketScanOrientation
import br.gov.caixa.loterias.feature.tickets.presentation.components.TicketCameraPreview
import br.gov.caixa.loterias.feature.tickets.presentation.screen.TicketScannerScreen
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel

@Composable
fun TicketScannerRoute(viewModel: TicketsViewModel, onBack: () -> Unit, onManual: () -> Unit, onResult: (List<ConfirmedBet>) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val context = LocalContext.current
    val orientation = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> TicketScanOrientation.PORTRAIT
        Configuration.ORIENTATION_LANDSCAPE -> TicketScanOrientation.LANDSCAPE
        else -> null
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    var permissionGranted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permissionGranted = it
    }
    LaunchedEffect(state.loaded, state.bets, state.ticketResult) {
        if (viewModel.consumeTicketResultNavigation()) onResult(state.bets)
    }
    TicketScannerScreen(
        permissionGranted = permissionGranted,
        onBack = onBack,
        onManual = onManual,
        onRequestCameraPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
        cameraPreview = { modifier ->
            TicketCameraPreview(
                onCode = { code -> orientation?.let { viewModel.submitScannedTicketCode(code, it) } },
                modifier = (modifier).uiId("tickets.0151")
            )
        }
    )
    state.errorMessage?.let {
        CaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissTicketError
        )
    }
}
