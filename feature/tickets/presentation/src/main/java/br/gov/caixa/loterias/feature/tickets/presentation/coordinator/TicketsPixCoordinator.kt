package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.effect.ForegroundReadOperation
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseProgress
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.isPendingPix
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseDetailsUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.TicketsPixNoticeUiState
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.ticketsPixNoticeUi
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import kotlinx.coroutines.launch

internal class TicketsPixCoordinator(
    private val mutableState: ReduxStateStore<TicketsUiState, TicketsStateAction>,
    private val scope: kotlinx.coroutines.CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getPixPurchaseStatus: br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase?,
    private val getPurchaseDetails: GetPurchaseDetailsUseCase?,
    private val pixNoticeClock: java.time.Clock,
    private val deadline: TicketsPixDeadlineTracker,
    private val showPurchaseDetails: suspend (Purchase, List<ConfirmedBet>, String) -> Unit,
) {
    private val statusRead = ForegroundReadOperation<AppResult<PurchaseProgress>>(
        scope,
        dispatchers.io,
        onCancelled = { mutableState.dispatch(TicketsStateAction.PixStatusCancelled) }
    )

    fun setScreenActive(active: Boolean) = statusRead.setActive(active)

    fun checkPendingPixStatus() {
        val purchase = mutableState.value.selectedPurchase ?: return
        val query = getPixPurchaseStatus ?: return
        statusRead.launch(
            allowed = {
                val current = mutableState.value
                !current.isLoading && !current.checkingPixPurchase && !current.pixPurchaseCompleted &&
                    !current.pixPurchasePaused && current.pixPurchaseRedirect == null && current.pixPurchaseNotice == null &&
                    current.selectedPurchase?.id == purchase.id && purchase.isPendingPix() && pendingPixSecondsRemaining() > 0
            },
            onStarted = { mutableState.dispatch(TicketsStateAction.PixStatusLoading) },
            read = { query(purchase.id) },
            onResult = { result ->
                if (mutableState.value.selectedPurchase?.id == purchase.id) {
                    when (result) {
                        is AppResult.Failure -> mutableState.dispatch(TicketsStateAction.PixStatusFailed(purchase.id, result.error))
                        is AppResult.Success -> applyProgress(purchase, result.value)
                    }
                }
            }
        )
    }

    private fun applyProgress(purchase: Purchase, progress: PurchaseProgress) {
        mutableState.dispatch(
            TicketsStateAction.PixStatusReceived(
                purchase.id,
                progress,
                progress.notice?.let { ticketsPixNoticeUi(it, java.time.LocalDateTime.now(pixNoticeClock)) }
            )
        )
        if (progress.completed || progress.rejected) {
            val details = getPurchaseDetails ?: return
            scope.launch(dispatchers.io) {
                // Uma situação confirmada atualiza a mesma compra, sem repetir pagamento.
                when (val refreshed = details(purchase)) {
                    is AppResult.Success -> if (mutableState.value.selectedPurchase?.id == purchase.id) {
                        showPurchaseDetails(refreshed.value.purchase, refreshed.value.bets, refreshed.value.serverDateTime)
                    }

                    is AppResult.Failure -> if (mutableState.value.selectedPurchase?.id == purchase.id) {
                        mutableState.dispatch(TicketsStateAction.PixDetailsRefreshFailed(purchase.id))
                    }
                }
            }
        }
    }

    fun consumePixPurchaseRedirect(expected: br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect): Boolean =
        synchronized(mutableState) {
            if (mutableState.value.pixPurchaseRedirect != expected) return false
            mutableState.dispatch(TicketsStateAction.PixRedirectConsumed(expected))
            return true
        }

    fun consumePixPurchaseNotice(expected: TicketsPixNoticeUiState, pause: Boolean = false): Boolean = synchronized(mutableState) {
        if (mutableState.value.pixPurchaseNotice !== expected) return false
        mutableState.dispatch(TicketsStateAction.PixNoticeConsumed(pause, expected))
        return true
    }

    fun resumePixPurchaseAfterRedirect() {
        mutableState.dispatch(TicketsStateAction.PixRedirectResumed)
    }

    fun pendingPixSecondsRemaining(): Long = deadline.remainingSeconds(mutableState.value.selectedPurchase)
}
