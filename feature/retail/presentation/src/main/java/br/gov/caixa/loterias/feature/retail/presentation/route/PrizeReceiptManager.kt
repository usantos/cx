package br.gov.caixa.loterias.feature.retail.presentation.route

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionViewModel

internal fun interface PrizeReceiptManager {
    fun openOrDownload(fileName: String, download: () -> Unit)
}

@Composable
internal fun rememberPrizeReceiptManager(viewModel: PrizeRedemptionViewModel): PrizeReceiptManager {
    val context = LocalContext.current
    val delivery = viewModel.receiptDelivery
    val deliveryState by delivery.state.collectAsState()
    // Somente metadados de recuperação são salvos. Um ViewModel retido controla os bytes e as gravações em andamento.
    var pendingFileName by rememberSaveable { mutableStateOf<String?>(null) }
    var destination by rememberSaveable { mutableStateOf<String?>(null) }
    fun clearPendingReceipt() {
        pendingFileName = null
        destination = null
    }
    fun openPdf(uri: Uri) = openPrizeReceiptPdf(context, delivery, uri)
    fun save(uri: Uri) = savePrizeReceiptPdf(context.applicationContext.contentResolver, delivery, uri)
    fun recover(fileName: String) = recoverPrizeReceipt(viewModel, fileName, ::clearPendingReceipt)
    val createPdf = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (pendingFileName != null && delivery.state.value.canReceiveDestination) {
            if (uri == null) {
                delivery.cancel()
                pendingFileName = null
                destination = null
            } else {
                destination = uri.toString()
                save(uri)
            }
        }
    }
    PrizeReceiptRecoveryEffect(
        viewModel,
        pendingFileName,
        destination,
        currentPendingFileName = { pendingFileName },
        currentDestination = { destination },
        recover = ::recover
    )
    PrizeReceiptCollectionEffect(
        viewModel = viewModel,
        pendingFileName = { pendingFileName },
        destination = { destination },
        onPendingReceipt = { fileName ->
            pendingFileName = fileName
            destination = null
        },
        launchCreatePdf = { createPdf.launch(it) },
        save = ::save,
        openPdf = ::openPdf
    )
    PrizeReceiptCompletionEffect(delivery, deliveryState.completion, ::clearPendingReceipt, ::openPdf)
    PrizeReceiptFailureDialogs(
        deliveryState = deliveryState,
        onRecover = { pendingFileName?.let(::recover) },
        onCancelRecovery = {
            if (delivery.state.value.recoveryFailure && !delivery.state.value.recovering) {
                delivery.cancel()
                clearPendingReceipt()
            }
        },
        onSave = ::save,
        onCancelSave = {
            if (delivery.state.value.saveFailure != null && !delivery.state.value.saving) {
                delivery.cancel()
                clearPendingReceipt()
            }
        },
        onOpenPdf = ::openPdf,
        onDismissViewerFailure = { delivery.dismissViewerFailure() }
    )
    return PrizeReceiptManager { fileName, download ->
        delivery.state.value.saved[fileName]?.let { openPdf(it.toUri()) } ?: run {
            if (delivery.state.value.awaitingBytes && (pendingFileName == null || destination != null)) {
                download()
            }
        }
    }
}
