package br.gov.caixa.loterias.feature.home.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryRequest
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingAction
import br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryRequest
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingActionUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PreparePoolEntryUseCase
import br.gov.caixa.loterias.feature.home.presentation.state.HomeStateAction
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class HomeEntryCoordinator(
    private val mutableState: ReduxStateStore<HomeUiState, HomeStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val prepareBetEntry: PrepareBetEntryUseCase,
    private val preparePoolEntry: PreparePoolEntryUseCase,
    private val prepareHomeBettingAction: PrepareHomeBettingActionUseCase
) {
    fun startBet(modalityId: Int, contestType: String, contestNumber: Int) {
        if (entryInProgress()) return
        mutableState.dispatch(HomeStateAction.BetEntryStarted)
        scope.launch(dispatchers.io) {
            when (val result = prepareBetEntry(BetEntryRequest(modalityId, contestType, contestNumber))) {
                is AppResult.Success -> mutableState.dispatch(HomeStateAction.BetEntryPrepared(result.value))
                is AppResult.Failure -> failEntry(result.error)
            }
        }
    }

    fun consumeBetEntryDecision() = mutableState.dispatch(HomeStateAction.BetEntryConsumed)

    fun dismissBetEntryError() = mutableState.dispatch(HomeStateAction.BetEntryErrorDismissed)

    fun startPools(modalityId: Int, contestType: String) {
        if (entryInProgress()) return
        mutableState.dispatch(HomeStateAction.PoolEntryStarted)
        scope.launch(dispatchers.io) {
            when (val result = preparePoolEntry(PoolEntryRequest(modalityId, contestType))) {
                is AppResult.Success -> mutableState.dispatch(HomeStateAction.PoolEntryPrepared(result.value))
                is AppResult.Failure -> failEntry(result.error)
            }
        }
    }

    fun consumePoolEntryDecision() = mutableState.dispatch(HomeStateAction.PoolEntryConsumed)

    fun startOtherBetting(action: HomeBettingAction) {
        if (entryInProgress()) return
        mutableState.dispatch(HomeStateAction.OtherBettingEntryStarted)
        scope.launch(dispatchers.io) {
            when (val result = prepareHomeBettingAction(action)) {
                is AppResult.Success -> mutableState.dispatch(HomeStateAction.OtherBettingEntryPrepared(result.value))
                is AppResult.Failure -> failEntry(result.error)
            }
        }
    }

    fun consumeOtherBettingDecision() = mutableState.dispatch(HomeStateAction.OtherBettingEntryConsumed)

    fun externalBettingFailed() = mutableState.dispatch(HomeStateAction.ExternalBettingFailed())

    private fun entryInProgress(): Boolean = mutableState.value.let {
        it.parametersUnavailable || it.loading || it.preparingBet || it.betEntryDecision != null || it.poolEntryDecision != null ||
            it.otherBettingDecision != null
    }

    private fun failEntry(error: AppError) = mutableState.dispatch(HomeStateAction.BetEntryFailed(error))
}
