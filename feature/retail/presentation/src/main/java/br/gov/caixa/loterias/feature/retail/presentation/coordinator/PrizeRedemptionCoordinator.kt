package br.gov.caixa.loterias.feature.retail.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetPrizeDetailsUseCase
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStateAction
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class PrizeRedemptionCoordinator(
    private val betId: Long,
    private val mutableState: ReduxStateStore<PrizeRedemptionUiState, PrizeRedemptionStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getDetails: GetPrizeDetailsUseCase,
    private val getBettingParameters: GetBettingParametersUseCase,
    private val prepareBetEntry: PrepareBetEntryUseCase,
    private val interactionBlocked: () -> Boolean,
    private val fail: (AppError) -> Unit,
) {
    fun load() = scope.launch(dispatchers.io) {
        if (interactionBlocked()) return@launch
        mutableState.dispatch(PrizeRedemptionStateAction.DetailsLoading)
        when (val result = getDetails(betId)) {
            is AppResult.Success -> {
                mutableState.dispatch(PrizeRedemptionStateAction.DetailsReceived(result.value))
                loadCurrentContest()
            }

            is AppResult.Failure -> fail(result.error)
        }
    }

    fun loadCurrentContest() {
        val modalityId = mutableState.value.details?.modalityId ?: return
        if (mutableState.value.currentContestLoading) return
        mutableState.dispatch(PrizeRedemptionStateAction.ContestLoading)
        scope.launch(dispatchers.io) {
            when (val result = getBettingParameters()) {
                is AppResult.Success -> mutableState.dispatch(PrizeRedemptionStateAction.ContestReceived(result.value, modalityId))
                is AppResult.Failure -> mutableState.dispatch(PrizeRedemptionStateAction.ContestFailed())
            }
        }
    }

    fun betNow() {
        val modalityId = mutableState.value.details?.modalityId ?: return
        if (mutableState.value.loading || mutableState.value.betEntryDecision != null || interactionBlocked()) return
        mutableState.dispatch(PrizeRedemptionStateAction.BetEntryStarted)
        scope.launch(dispatchers.io) {
            when (val result = prepareBetEntry(modalityId)) {
                is AppResult.Success -> mutableState.dispatch(PrizeRedemptionStateAction.BetEntryPrepared(result.value))
                is AppResult.Failure -> fail(result.error)
            }
        }
    }
}
