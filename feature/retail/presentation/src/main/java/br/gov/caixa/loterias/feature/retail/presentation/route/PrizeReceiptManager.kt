package br.gov.caixa.loterias.feature.retail.presentation.route

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionViewModel
import java.io.IOException

internal fun interface PrizeReceiptManager {
    fun openOrDownload(fileName: String, download: () -> Unit)
}

@Composable
internal fun rememberPrizeReceiptManager(viewModel: PrizeRedemptionViewModel): PrizeReceiptManager {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val context = LocalContext.current
    val delivery = viewModel.receiptDelivery
    val deliveryState by delivery.state.collectAsState()
    // Somente metadados de recuperação são salvos. Um ViewModel retido controla os bytes e as gravações em andamento.
    var pendingFileName by rememberSaveable { mutableStateOf<String?>(null) }
    var destination by rememberSaveable { mutableStateOf<String?>(null) }
    fun openPdf(uri: Uri) {
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW)
                    .setDataAndType(uri, "application/pdf")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            )
            delivery.dismissViewerFailure()
        } catch (_: ActivityNotFoundException) {
            delivery.viewerFailed(uri.toString())
        } catch (_: SecurityException) {
            delivery.viewerFailed(uri.toString())
        }
    }
    fun save(uri: Uri) {
        // A operação retida captura a fronteira do provedor, nunca esta Activity ou sua interface.
        val resolver = context.applicationContext.contentResolver
        delivery.save(uri.toString()) { bytes ->
            val output = resolver.openOutputStream(uri)
                ?: throw IOException(messageText("nao_foi_possivel_abrir_o_comprov_c2df10"))
            output.use { it.write(bytes) }
        }
    }
    fun recover(fileName: String) {
        if (!delivery.beginRecovery()) return
        if (!viewModel.recoverReceipt(fileName)) {
            delivery.cancel()
            pendingFileName = null
            destination = null
        }
    }
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
    // Após a perda do ViewModel, busca novamente apenas o recibo pendente identificado, usando o resultado
    // original do seletor. Isso também se aplica quando os metadados salvos descrevem uma gravação interrompida.
    LaunchedEffect(viewModel, pendingFileName, destination) {
        val fileName = pendingFileName
        if (fileName != null && destination != null && delivery.state.value.canRecover) {
            recover(fileName)
        }
    }
    LaunchedEffect(viewModel) {
        viewModel.receipts.collect { receipt ->
            delivery.state.value.saved[receipt.fileName]?.let { openPdf(it.toUri()) } ?: run {
                if (delivery.accept(receipt)) {
                    val restoredDestination = destination
                    if (pendingFileName == receipt.fileName && restoredDestination != null) {
                        save(restoredDestination.toUri())
                    } else {
                        pendingFileName = receipt.fileName
                        destination = null
                        createPdf.launch(receipt.fileName)
                    }
                }
            }
        }
    }
    LaunchedEffect(deliveryState.completion) {
        delivery.state.value.completion?.let { completion ->
            if (delivery.claimCompletion(completion)) {
                pendingFileName = null
                destination = null
                openPdf(completion.destination.toUri())
            }
        }
    }
    if (deliveryState.recoveryFailure) {
        CaixaDSDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_recuperar_o_comprov_f02c59),
            uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            onPositive = { pendingFileName?.let(::recover) },
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
            onNegative = {
                if (delivery.state.value.recoveryFailure && !delivery.state.value.recovering) {
                    delivery.cancel()
                    pendingFileName = null
                    destination = null
                }
            }
        )
    }
    deliveryState.saveFailure?.let { destinationId ->
        CaixaDSDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_salvar_o_comprovant_e315d0),
            uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            onPositive = { save(destinationId.toUri()) },
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
            onNegative = {
                if (delivery.state.value.saveFailure != null && !delivery.state.value.saving) {
                    delivery.cancel()
                    pendingFileName = null
                    destination = null
                }
            }
        )
    }
    deliveryState.viewerFailure?.let { destinationId ->
        CaixaDSDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_abrir_o_comprovante_8b45b2),
            uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            onPositive = { openPdf(destinationId.toUri()) },
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
            onNegative = { delivery.dismissViewerFailure() }
        )
    }
    return PrizeReceiptManager { fileName, download ->
        delivery.state.value.saved[fileName]?.let { openPdf(it.toUri()) } ?: run {
            if (delivery.state.value.awaitingBytes && (pendingFileName == null || destination != null)) {
                download()
            }
        }
    }
}
