package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.contestType
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.normalizedContestType
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.toCartMessage
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.toRepeatDraft
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.toRepeatOption
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class BetDetailRepeatCoordinator(
    private val mutableState: ReduxStateStore<BetDetailUiState, BetDetailStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val addBetToCart: AddBetToCartUseCase,
    private val getBettingParameters: GetBettingParametersUseCase?
) {
    fun dismissCartAddition() = mutableState.dispatch(BetDetailStateAction.CartAdditionDismissed)

    fun addCurrentBetToCart() = scope.launch(dispatchers.io) {
        if (!mutableState.value.detailsReady) return@launch
        val bet = mutableState.value.bet
        val hasSelection = bet.numbers.isNotEmpty() || bet.numberColumns.any(List<Int>::isNotEmpty) ||
            bet.lotecaSelections.isNotEmpty() || bet.lotogolSelections.isNotEmpty() || bet.surprise
        if (bet.modalityId <= 0 || !hasSelection) {
            mutableState.dispatch(BetDetailStateAction.RepeatSelectionMissing())
            return@launch
        }
        mutableState.dispatch(BetDetailStateAction.RepeatLoading)
        val parameters = getBettingParameters
        if (parameters == null) {
            submitRepeat(bet, bet.contestType())
            return@launch
        }
        when (val result = parameters()) {
            is AppResult.Failure -> mutableState.dispatch(BetDetailStateAction.RepeatParametersFailed(result.error.toCartMessage()))
            is AppResult.Success -> resolveRepeatContest(bet, result.value.games)
        }
    }

    fun selectRepeatContest(index: Int) = mutableState.dispatch(BetDetailStateAction.RepeatContestSelected(index))

    fun confirmRepeatContest() = scope.launch(dispatchers.io) {
        val current = mutableState.value
        val option = current.repeatContestOptions.getOrNull(current.selectedRepeatContestIndex) ?: return@launch
        mutableState.dispatch(BetDetailStateAction.RepeatContestConfirmed)
        submitRepeat(current.bet, option.contestType)
    }

    fun dismissRepeatContest() = mutableState.dispatch(BetDetailStateAction.RepeatContestDismissed)

    fun confirmRepeatConversion() = scope.launch(dispatchers.io) {
        val current = mutableState.value
        val option = current.repeatConversion ?: return@launch
        mutableState.dispatch(BetDetailStateAction.RepeatConversionConfirmed)
        submitRepeat(current.bet, option.contestType)
    }

    private suspend fun resolveRepeatContest(bet: ConfirmedBet, games: List<LotteryGameParameters>) {
        val available = games.filter { it.modality.id == bet.modalityId && it.open }
            .distinctBy { it.contestType.normalizedContestType() }
            .map { game -> game.toRepeatOption(bet) }
        when (available.size) {
            0 -> mutableState.dispatch(BetDetailStateAction.RepeatUnavailable())

            1 -> {
                val option = available.single()
                if (option.contestType == bet.contestType()) {
                    submitRepeat(bet, option.contestType)
                } else {
                    mutableState.dispatch(BetDetailStateAction.RepeatConversionRequired(option))
                }
            }

            else -> {
                val currentType = bet.contestType()
                val selected = available.indexOfFirst { it.contestType == currentType }.coerceAtLeast(0)
                mutableState.dispatch(BetDetailStateAction.RepeatContestsReceived(available, selected))
            }
        }
    }

    private suspend fun submitRepeat(bet: ConfirmedBet, contestType: String) {
        val draft = bet.toRepeatDraft(contestType)
        when (val result = addBetToCart(draft)) {
            is AppResult.Success -> {
                mutableState.dispatch(BetDetailStateAction.RepeatAddedToCart)
            }

            is AppResult.Failure -> mutableState.dispatch(BetDetailStateAction.RepeatAdditionFailed(result.error.toCartMessage()))
        }
    }
}
