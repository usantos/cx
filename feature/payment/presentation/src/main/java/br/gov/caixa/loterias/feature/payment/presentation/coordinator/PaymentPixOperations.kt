package br.gov.caixa.loterias.feature.payment.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.effect.ForegroundReadOperation
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseProgress
import br.gov.caixa.loterias.feature.payment.domain.usecase.GeneratePixChargeUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.presentation.mapper.purchaseNoticeUi
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState
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

    private val statusRead = ForegroundReadOperation<AppResult<PurchaseProgress>>(
        viewModelScope,
        dispatchers.io,
        onCancelled = { mutableState.dispatch(PaymentAction.PixStatusCheckCancelled) }
    )

    fun setScreenActive(active: Boolean) = statusRead.setActive(active)

    fun checkPixStatus() {
        val pix = mutableState.value.pix ?: return
        statusRead.launch(
            allowed = {
                val state = mutableState.value
                !state.checkingPix && !state.pixApproved && state.redirect == null && !state.redirectPaused &&
                    !state.registrationPending && state.notice == null && state.pix?.purchaseId == pix.purchaseId
            },
            onStarted = { mutableState.dispatch(PaymentAction.PixStatusCheckStarted) },
            read = { getPurchaseProgress(pix.purchaseId) },
            onResult = { result ->
                when (result) {
                    is AppResult.Failure -> applyFailure(result.error)
                    is AppResult.Success -> applyProgress(pix, result.value)
                }
            }
        )
    }

    private fun applyProgress(pix: br.gov.caixa.loterias.feature.payment.domain.model.PixCharge, progress: PurchaseProgress) {
        val redirect = progress.redirect
        val notice = progress.notice
        when {
            redirect != null -> mutableState.dispatch(PaymentAction.PixRedirectReceived(redirect))

            notice != null -> mutableState.dispatch(
                PaymentAction.PixNoticeReceived(purchaseNoticeUi(notice, java.time.LocalDateTime.now(clock)))
            )

            progress.completed -> viewModelScope.launch(dispatchers.io) {
                // Confirmação conhecida: mantém a escrita local independente da leitura cancelável.
                recordRecentPurchase(pix.amount)
                mutableState.dispatch(PaymentAction.PixStatusReceived(progress.completed, progress.rejected))
            }

            else -> mutableState.dispatch(PaymentAction.PixStatusReceived(progress.completed, progress.rejected))
        }
    }
}
