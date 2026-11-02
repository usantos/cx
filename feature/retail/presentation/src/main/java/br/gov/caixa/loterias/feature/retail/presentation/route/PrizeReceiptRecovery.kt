package br.gov.caixa.loterias.feature.retail.presentation.route

import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionViewModel

internal fun recoverPrizeReceipt(viewModel: PrizeRedemptionViewModel, fileName: String, clearPendingReceipt: () -> Unit) {
    val delivery = viewModel.receiptDelivery
    if (!delivery.beginRecovery()) return
    if (!viewModel.recoverReceipt(fileName)) {
        delivery.cancel()
        clearPendingReceipt()
    }
}
