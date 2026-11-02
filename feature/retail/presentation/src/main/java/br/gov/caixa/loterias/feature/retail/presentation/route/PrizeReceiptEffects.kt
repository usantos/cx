package br.gov.caixa.loterias.feature.retail.presentation.route

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.net.toUri
import br.gov.caixa.loterias.feature.retail.presentation.coordinator.PrizeReceiptDelivery
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeReceiptCompletion
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionViewModel

@Composable
internal fun PrizeReceiptRecoveryEffect(
    viewModel: PrizeRedemptionViewModel,
    pendingFileName: String?,
    destination: String?,
    currentPendingFileName: () -> String?,
    currentDestination: () -> String?,
    recover: (String) -> Unit,
) {
    val delivery = viewModel.receiptDelivery
    // Após a perda do ViewModel, busca novamente apenas o recibo pendente identificado, usando o resultado
    // original do seletor. Isso também se aplica quando os metadados salvos descrevem uma gravação interrompida.
    LaunchedEffect(viewModel, pendingFileName, destination) {
        val fileName = currentPendingFileName()
        if (fileName != null && currentDestination() != null && delivery.state.value.canRecover) {
            recover(fileName)
        }
    }
}

@Composable
internal fun PrizeReceiptCollectionEffect(
    viewModel: PrizeRedemptionViewModel,
    pendingFileName: () -> String?,
    destination: () -> String?,
    onPendingReceipt: (String) -> Unit,
    launchCreatePdf: (String) -> Unit,
    save: (Uri) -> Unit,
    openPdf: (Uri) -> Unit,
) {
    val delivery = viewModel.receiptDelivery
    LaunchedEffect(viewModel) {
        viewModel.receipts.collect { receipt ->
            delivery.state.value.saved[receipt.fileName]?.let { openPdf(it.toUri()) } ?: run {
                if (delivery.accept(receipt)) {
                    val restoredDestination = destination()
                    if (pendingFileName() == receipt.fileName && restoredDestination != null) {
                        save(restoredDestination.toUri())
                    } else {
                        onPendingReceipt(receipt.fileName)
                        launchCreatePdf(receipt.fileName)
                    }
                }
            }
        }
    }
}

@Composable
internal fun PrizeReceiptCompletionEffect(
    delivery: PrizeReceiptDelivery,
    completion: PrizeReceiptCompletion?,
    clearPendingReceipt: () -> Unit,
    openPdf: (Uri) -> Unit,
) {
    LaunchedEffect(completion) {
        delivery.state.value.completion?.let { completion ->
            if (delivery.claimCompletion(completion)) {
                clearPendingReceipt()
                openPdf(completion.destination.toUri())
            }
        }
    }
}
