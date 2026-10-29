package br.gov.caixa.loterias.feature.retail.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.event.SingleConsumerEventQueue
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadPrizeReceiptUseCase
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeReceipt
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStateAction
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class PrizeRedemptionReceipts(
    private val betId: Long,
    private val mutableState: ReduxStateStore<PrizeRedemptionUiState, PrizeRedemptionStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val mutableReceipts: SingleConsumerEventQueue<PrizeReceipt>,
    private val downloadBetReceiptUseCase: DownloadBetReceiptUseCase,
    private val downloadPrizeReceiptUseCase: DownloadPrizeReceiptUseCase,
    private val interactionBlocked: () -> Boolean,
    private val fail: (AppError) -> Unit
) {
    fun downloadBetReceipt() = download("comprovante-aposta-$betId.pdf") { downloadBetReceiptUseCase(it) }

    fun downloadPrizeReceipt() = download("comprovante-premio-$betId.pdf") { downloadPrizeReceiptUseCase(it) }

    fun recoverReceipt(fileName: String, onFailure: () -> Unit): Boolean = when (fileName) {
        "comprovante-aposta-$betId.pdf" -> {
            download(fileName, onFailure) { downloadBetReceiptUseCase(it) }
            true
        }

        "comprovante-premio-$betId.pdf" -> {
            download(fileName, onFailure) { downloadPrizeReceiptUseCase(it) }
            true
        }

        else -> false
    }

    private fun download(fileName: String, recoveryFailure: (() -> Unit)? = null, useCase: suspend (Long) -> AppResult<ByteArray>) =
        scope.launch(dispatchers.io) {
            if (interactionBlocked()) {
                recoveryFailure?.invoke()
                return@launch
            }
            mutableState.dispatch(PrizeRedemptionStateAction.ReceiptLoading)
            when (val result = useCase(betId)) {
                is AppResult.Success -> {
                    mutableReceipts.emit(PrizeReceipt(result.value, fileName))
                    mutableState.dispatch(PrizeRedemptionStateAction.ReceiptDelivered)
                }

                is AppResult.Failure -> {
                    if (recoveryFailure == null) {
                        fail(result.error)
                    } else {
                        mutableState.dispatch(PrizeRedemptionStateAction.ReceiptRecoveryFailed)
                        recoveryFailure()
                    }
                }
            }
        }
}
