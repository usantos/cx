package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.event.SingleConsumerEventQueue
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.toReceiptMessage
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetReceipt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class BetDetailReceiptCoordinator(
    private val mutableState: ReduxStateStore<BetDetailUiState, BetDetailStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val mutableReceipts: SingleConsumerEventQueue<BetReceipt>,
    private val downloadBetReceipt: DownloadBetReceiptUseCase,
    private val downloadPrizeReceipt: br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadPrizeReceiptUseCase?
) {
    fun downloadReceipt() = scope.launch(dispatchers.io) {
        val betId = mutableState.value.bet.id
        if (betId <= 0) {
            mutableState.dispatch(BetDetailStateAction.ReceiptUnavailable())
            return@launch
        }
        mutableState.dispatch(BetDetailStateAction.ReceiptLoading)
        val prizeReceipt = mutableState.value.bet.redeemedPrize && downloadPrizeReceipt != null
        val result = if (prizeReceipt) requireNotNull(downloadPrizeReceipt)(betId) else downloadBetReceipt(betId)
        when (result) {
            is AppResult.Success -> {
                mutableReceipts.emit(BetReceipt(result.value, "comprovante-${if (prizeReceipt) "premio" else "aposta"}-$betId.pdf"))
                mutableState.dispatch(BetDetailStateAction.ReceiptDelivered)
            }

            is AppResult.Failure -> mutableState.dispatch(BetDetailStateAction.ReceiptFailed(result.error.toReceiptMessage()))
        }
    }

    fun reportReceiptOpenFailure() = mutableState.dispatch(BetDetailStateAction.ReceiptOpenFailed())
}
