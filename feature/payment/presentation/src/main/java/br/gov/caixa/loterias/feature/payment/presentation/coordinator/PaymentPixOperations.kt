package br.gov.caixa.loterias.feature.payment.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GeneratePixChargeUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.presentation.mapper.purchaseNoticeUi
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentState
import kotlinx.coroutines.launch

internal class PaymentPixOperations(
    private val mutableState: ReduxStateStore<PaymentUiState, PaymentAction>,
    private val viewModelScope: kotlinx.coroutines.CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val pixRestoration: SavedPixRestoration?,
    private val generatePixCharge: GeneratePixChargeUseCase,
    private val getPurchaseProgress: GetPurchaseProgressUseCase,
    private val recordRecentPurchase: RecordRecentPurchaseUseCase,
    private val clock: java.time.Clock,
    private val applyFailure: (AppError) -> Unit,
) {

    suspend fun generatePix() {
        mutableState.dispatch(PaymentAction.PixGenerationStarted)
        pixRestoration?.recordAttempt()
        when (val result = generatePixCharge()) {
            is AppResult.Failure -> {
                // Uma resposta de falha não comprova que o POST da compra não foi aceito.
                // Hospedeiros de estado salvo recuperam pelas compras em vez de enviar outro POST.
                if (pixRestoration != null) mutableState.dispatch(PaymentAction.PixRecoveryRequired)
                applyFailure(result.error)
            }

            is AppResult.Success -> {
                pixRestoration?.recordCharge(result.value)
                mutableState.dispatch(PaymentAction.PixGenerated(result.value))
            }
        }
    }

    fun checkPixStatus() {
        synchronized(mutableState) {
            val pix = mutableState.value.pix ?: return
            if (mutableState.value.checkingPix || mutableState.value.pixApproved || mutableState.value.redirect != null ||
                mutableState.value.redirectPaused ||
                mutableState.value.registrationPending ||
                mutableState.value.notice != null
            ) {
                return
            }
            mutableState.dispatch(PaymentAction.PixStatusCheckStarted)
            viewModelScope.launch(dispatchers.io) {
                when (val result = getPurchaseProgress(pix.purchaseId)) {
                    is AppResult.Failure -> applyFailure(result.error)

                    is AppResult.Success -> {
                        result.value.redirect?.let { destination ->
                            mutableState.dispatch(PaymentAction.PixRedirectReceived(destination))
                            return@launch
                        }
                        result.value.notice?.let { notice ->
                            val presentation = purchaseNoticeUi(notice, java.time.LocalDateTime.now(clock))
                            mutableState.dispatch(PaymentAction.PixNoticeReceived(presentation))
                            return@launch
                        }
                        // A mera geração de um código Pix não deve criar um registro de compra bem-sucedida.
                        if (result.value.completed) recordRecentPurchase(pix.amount)
                        mutableState.dispatch(PaymentAction.PixStatusReceived(result.value.completed, result.value.rejected))
                    }
                }
            }
        }
    }
}
