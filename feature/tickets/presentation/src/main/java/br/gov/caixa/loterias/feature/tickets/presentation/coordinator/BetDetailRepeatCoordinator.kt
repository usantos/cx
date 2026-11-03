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
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailRepeatAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

internal class BetDetailRepeatCoordinator(
    private val mutableState: ReduxStateStore<BetDetailUiState, BetDetailStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val addBetToCart: AddBetToCartUseCase,
    private val getBettingParameters: GetBettingParametersUseCase?,
    private val canStartOperation: () -> Boolean = { true },
) {
    fun dismissCartAddition() = mutableState.dispatch(BetDetailStateAction.CartAdditionDismissed)

    private enum class RepeatPhase { IDLE, LOADING, CONTEST, CONVERSION, SUBMITTING }

    private val phase = AtomicReference(RepeatPhase.IDLE)

    @Synchronized
    fun addCurrentBetToCart(): Job {
        if (!canStartOperation()) return completedOperation()
        if (!mutableState.value.detailsReady || !phase.compareAndSet(RepeatPhase.IDLE, RepeatPhase.LOADING)) return completedOperation()
        val bet = mutableState.value.bet
        return scope.launch(dispatchers.io) {
            var awaitingConfirmation = false
            try {
                awaitingConfirmation = prepareRepeat(bet)
            } finally {
                if (!awaitingConfirmation) phase.set(RepeatPhase.IDLE)
            }
        }
    }

    private suspend fun prepareRepeat(bet: ConfirmedBet): Boolean {
        if (!canStartOperation()) return false
        val hasSelection = bet.numbers.isNotEmpty() || bet.numberColumns.any(List<Int>::isNotEmpty) ||
            bet.lotecaSelections.isNotEmpty() || bet.lotogolSelections.isNotEmpty() || bet.surprise
        if (bet.modalityId <= 0 || !hasSelection) {
            mutableState.dispatch(BetDetailStateAction.RepeatSelectionMissing())
            return false
        }
        mutableState.dispatch(BetDetailStateAction.RepeatLoading)
        val parameters = getBettingParameters
        if (parameters == null) {
            submitRepeat(bet, bet.contestType())
            return false
        }
        val result = parameters()
        currentCoroutineContext().ensureActive()
        if (!canStartOperation()) {
            mutableState.dispatch(BetDetailStateAction.RepeatPreparationCancelled)
            return false
        }
        return when (result) {
            is AppResult.Failure -> {
                mutableState.dispatch(BetDetailStateAction.RepeatParametersFailed(result.error.toCartMessage()))
                false
            }

            is AppResult.Success -> resolveRepeatContest(bet, result.value.games)
        }
    }

    fun selectRepeatContest(index: Int) = mutableState.dispatch(BetDetailStateAction.RepeatContestSelected(index))

    @Synchronized
    fun confirmRepeatContest(): Job {
        if (!canStartOperation()) return completedOperation()
        val current = mutableState.value
        val option = current.repeatContestOptions.getOrNull(current.selectedRepeatContestIndex) ?: return completedOperation()
        if (!phase.compareAndSet(RepeatPhase.CONTEST, RepeatPhase.SUBMITTING)) return completedOperation()
        return launchSubmission(
            current.bet,
            option.contestType,
            RepeatPhase.CONTEST,
            BetDetailStateAction.RepeatContestConfirmed,
            BetDetailStateAction.RepeatContestsReceived(current.repeatContestOptions, current.selectedRepeatContestIndex)
        )
    }

    @Synchronized
    fun dismissRepeatContest() {
        val current = phase.get()
        if (current == RepeatPhase.LOADING || current == RepeatPhase.SUBMITTING) return
        if (!phase.compareAndSet(current, RepeatPhase.IDLE)) return
        mutableState.dispatch(BetDetailStateAction.RepeatContestDismissed)
    }

    @Synchronized
    fun confirmRepeatConversion(): Job {
        if (!canStartOperation()) return completedOperation()
        val current = mutableState.value
        val option = current.repeatConversion ?: return completedOperation()
        if (!phase.compareAndSet(RepeatPhase.CONVERSION, RepeatPhase.SUBMITTING)) return completedOperation()
        return launchSubmission(
            current.bet,
            option.contestType,
            RepeatPhase.CONVERSION,
            BetDetailStateAction.RepeatConversionConfirmed,
            BetDetailStateAction.RepeatConversionRequired(option)
        )
    }

    private fun completedOperation(): Job = Job().apply { complete() }

    private fun launchSubmission(
        bet: ConfirmedBet,
        contestType: String,
        previous: RepeatPhase,
        started: BetDetailRepeatAction,
        restore: BetDetailRepeatAction,
    ) = scope.launch(dispatchers.io) {
        try {
            if (!canStartOperation()) {
                phase.set(previous)
                return@launch
            }
            mutableState.dispatch(started)
            if (!submitRepeat(bet, contestType)) {
                mutableState.dispatch(restore)
                phase.set(previous)
            }
        } finally {
            phase.compareAndSet(RepeatPhase.SUBMITTING, RepeatPhase.IDLE)
        }
    }

    private suspend fun resolveRepeatContest(bet: ConfirmedBet, games: List<LotteryGameParameters>): Boolean {
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
                    awaitConfirmation(RepeatPhase.CONVERSION, BetDetailStateAction.RepeatConversionRequired(option))
                    return true
                }
            }

            else -> {
                val currentType = bet.contestType()
                val selected = available.indexOfFirst { it.contestType == currentType }.coerceAtLeast(0)
                awaitConfirmation(RepeatPhase.CONTEST, BetDetailStateAction.RepeatContestsReceived(available, selected))
                return true
            }
        }
        return false
    }

    @Synchronized
    private fun awaitConfirmation(next: RepeatPhase, action: BetDetailStateAction) {
        phase.set(next)
        mutableState.dispatch(action)
    }

    private suspend fun submitRepeat(bet: ConfirmedBet, contestType: String): Boolean {
        currentCoroutineContext().ensureActive()
        if (!canStartOperation()) {
            mutableState.dispatch(BetDetailStateAction.RepeatPreparationCancelled)
            return false
        }
        val draft = bet.toRepeatDraft(contestType)
        when (val result = addBetToCart(draft)) {
            is AppResult.Success -> {
                mutableState.dispatch(BetDetailStateAction.RepeatAddedToCart)
            }

            is AppResult.Failure -> mutableState.dispatch(BetDetailStateAction.RepeatAdditionFailed(result.error.toCartMessage()))
        }
        return true
    }
}
