package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.BetDetailViewModel
import br.gov.caixa.loterias.feature.tickets.presentation.route.deliverBetReceipt
import br.gov.caixa.loterias.feature.tickets.presentation.route.openBetReceipt
import br.gov.caixa.loterias.feature.tickets.presentation.route.writeBetReceipt

@Composable
internal fun BetDetailReceiptEffect(viewModel: BetDetailViewModel) {
    val context = LocalContext.current
    LaunchedEffect(viewModel) {
        viewModel.receipts.collect { receipt ->
            deliverBetReceipt(
                receipt = receipt,
                write = { writeBetReceipt(context, it) },
                open = { openBetReceipt(context, it) },
                onFailure = viewModel::reportReceiptOpenFailure,
            )
        }
    }
}
