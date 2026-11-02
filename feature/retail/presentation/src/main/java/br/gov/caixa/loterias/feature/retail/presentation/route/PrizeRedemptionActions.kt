package br.gov.caixa.loterias.feature.retail.presentation.route

import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeRedemptionScreenActions
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizePixActions
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionViewModel

internal fun prizeRedemptionScreenActions(
    viewModel: PrizeRedemptionViewModel,
    uiState: PrizeRedemptionUiState,
    receipts: PrizeReceiptManager,
) = PrizeRedemptionScreenActions(
    select = viewModel::select,
    betNow = viewModel::betNow,
    loadCurrentContest = viewModel::loadCurrentContest,
    refreshLotteryCode = viewModel::refreshLotteryCode,
    downloadBetReceipt = {
        val fileName = "comprovante-aposta-${uiState.details?.betId}.pdf"
        receipts.openOrDownload(fileName, viewModel::downloadBetReceipt)
    },
    confirmMercadoPago = viewModel::confirmMercadoPago,
    changeEmail = viewModel::changeEmail,
    downloadPrizeReceipt = {
        val fileName = "comprovante-premio-${uiState.details?.betId}.pdf"
        receipts.openOrDownload(fileName, viewModel::downloadPrizeReceipt)
    },
    pix = PrizePixActions(
        cancelPixAndExit = viewModel::cancelPixAndExit,
        consultPix = viewModel::consultPix,
        confirmPix = viewModel::confirmPix,
        selectCpfPixKey = viewModel::selectCpfPixKey,
        selectOtherPixKey = viewModel::selectOtherPixKey,
        changePixKey = viewModel::changePixKey,
        togglePixOwnership = viewModel::togglePixOwnership
    )
)
