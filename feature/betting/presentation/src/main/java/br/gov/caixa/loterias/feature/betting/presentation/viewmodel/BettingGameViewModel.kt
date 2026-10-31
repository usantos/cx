package br.gov.caixa.loterias.feature.betting.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.time.LotteryDateTimes
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotogolMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSession
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingSpecialContestsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.presentation.coordinator.BettingGameCartCoordinator
import br.gov.caixa.loterias.feature.betting.presentation.coordinator.BettingGameFavorites
import br.gov.caixa.loterias.feature.betting.presentation.coordinator.BettingGameParametersCoordinator
import br.gov.caixa.loterias.feature.betting.presentation.coordinator.BettingGameSelection
import br.gov.caixa.loterias.feature.betting.presentation.coordinator.BettingGameStepsCoordinator
import br.gov.caixa.loterias.feature.betting.presentation.mapper.BettingGameDraftMapper
import br.gov.caixa.loterias.feature.betting.presentation.mapper.BettingGameNoticeMapper
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingDraftCommands
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingRecoveryMessage
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingRecoveryMessages
import br.gov.caixa.loterias.feature.betting.presentation.recovery.restoreInto
import br.gov.caixa.loterias.feature.betting.presentation.recovery.toCheckpoint
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceBettingGameState
import java.time.Instant
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
class BettingGameViewModel(
    private val modalityId: Int?,
    private val getParameters: GetBettingParametersUseCase,
    private val addBet: AddBetToCartUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val randomTeam: (List<SportsTeam>) -> SportsTeam? = { it.randomOrNull() },
    private val initialNumbers: List<Int> = emptyList(),
    private val initialContestTypeId: Long = 0,
    private val initialClovers: List<Int> = emptyList(),
    private val initialTeam: SportsTeam? = null,
    private val initialLuckyMonth: LuckyMonth? = null,
    private val initialLotecaSelections: List<LotecaMatchSelection> = emptyList(),
    private val initialLotogolSelections: List<LotogolMatchSelection> = emptyList(),
    private val initialSuperSevenColumns: List<List<Int>> = emptyList(),
    private val initialRecurringDraws: Int = 0,
    private val initialMirror: Boolean = false,
    private val expectedContestNumber: Int? = null,
    private val validateFavoriteBet: ValidateFavoriteBetUseCase? = null,
    private val saveFavoriteBet: SaveFavoriteBetUseCase? = null,
    private val initialParameters: BettingParameters? = null,
    private val checkpointSession: BettingCheckpointSession? = null,
    private val restoreCheckpoint: Boolean = true,
    private val recoveryMessages: BettingRecoveryMessages = BettingRecoveryMessages.Identifiers,
    private val serverTime: ServerTimeSource = ServerTimeSource {
        Instant.now()
    },
    private val getSpecialContests: GetBettingSpecialContestsUseCase? = null,
) : ViewModel() {
    private var acknowledgedCheckpoint: BettingCheckpoint? = null
    private var operationId: String? = null
    private var recoveryLocked = checkpointSession != null
    private val mutableState = reduxStateStore<BettingGameUiState, BettingGameAction>(BettingGameUiState(), ::reduceBettingGameState).also {
        addCloseable(it)
    }
    private val commands: BettingDraftCommands? = checkpointSession?.let { session ->
        BettingDraftCommands(
            viewModelScope,
            BettingGameUiState(),
            ::persistCandidate,
            session::isCurrent,
            recoveryMessages,
            mutableState,
            ::recordSelectionChange
        )
    }
    val state = commands?.state ?: mutableState.state
    private val favorites: BettingGameFavorites = BettingGameFavorites(
        mutableState, viewModelScope, dispatchers,
        validateFavoriteBet, saveFavoriteBet, commands?.let { queue -> { action -> queue.edit(action) } },
        { beforeRemote(it, "SAVE_FAVORITE") }, ::afterRemote, ::isCurrent,
        enqueueCommand = commands?.let { queue -> queue::execute }
    )
    private val selection = BettingGameSelection(mutableState.state, favorites::dispatch)
    private val draftMapper = BettingGameDraftMapper(randomTeam)

    private val notices = BettingGameNoticeMapper(favorites::dispatch)
    private val parameters = BettingGameParametersCoordinator(
        modalityId, getParameters, initialContestTypeId,
        br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameSeed(
            initialNumbers, initialClovers, initialTeam, initialLuckyMonth, initialLotecaSelections,
            initialLotogolSelections, initialSuperSevenColumns, initialRecurringDraws, initialMirror
        ),
        expectedContestNumber, viewModelScope, dispatchers, favorites::dispatch, notices::applyFailure
    )
    private val cart = BettingGameCartCoordinator(
        state, viewModelScope, dispatchers, addBet, draftMapper,
        favorites::dispatch, notices::incompleteBetMessage, notices::applyFailure,
        { beforeRemote(it, "ADD_BET") }, ::afterRemote, ::isCurrent
    )
    private val steps = BettingGameStepsCoordinator(
        state,
        favorites::dispatch,
        notices::incompleteBetMessage,
        { addToCart() }
    )

    private fun recordSelectionChange(before: BettingGameUiState, after: BettingGameUiState): Unit =
        favorites.recordSelectionChange(before, after)

    init {
        if (getSpecialContests != null) {
            viewModelScope.launch(dispatchers.io) {
                when (val result = getSpecialContests.invoke()) {
                    is AppResult.Success -> favorites.dispatch(BettingGameAction.SpecialContestsLoaded(result.value))
                    is AppResult.Failure -> Unit
                }
            }
        }
        if (checkpointSession != null) {
            recover()
        } else {
            val prepared = initialParameters?.takeIf { selectGame(it) != null }
            if (prepared != null) applyParameters(prepared) else load()
        }
    }

    fun load() {
        if (checkpointSession == null) parameters.load() else recover()
    }

    private fun recover() = viewModelScope.launch(dispatchers.io) {
        val session = checkpointSession ?: return@launch
        try {
            recoverSession(session)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            recoveryError(recoveryMessages.text(BettingRecoveryMessage.READ_FAILED))
        }
    }

    private suspend fun recoverSession(session: BettingCheckpointSession) {
        if (!session.isCurrent()) return
        val record = session.read()
        if (!session.isCurrent()) return
        if (record?.uncertainOperation != null) {
            recoveryError(recoveryMessages.text(BettingRecoveryMessage.RECONCILE_REQUIRED))
            return
        }
        val checkpoint = record?.checkpoint?.takeIf {
            !record.reconcileCart && restoreCheckpoint && (modalityId == null || it.modalityId == modalityId) &&
                (expectedContestNumber == null || it.contestNumber == expectedContestNumber)
        }
        when (val result = getParameters()) {
            is AppResult.Failure -> recoveryError(recoveryMessages.text(BettingRecoveryMessage.RULES_UNAVAILABLE))
            is AppResult.Success -> if (session.isCurrent()) installRecovery(result.value, checkpoint)
        }
    }

    private fun installRecovery(fresh: BettingParameters, checkpoint: BettingCheckpoint?) {
        val game = checkpoint?.let { draft -> fresh.games.firstOrNull { matches(it, draft) } }
        if (checkpoint != null && game == null) {
            recoveryError(recoveryMessages.text(BettingRecoveryMessage.CONTEST_CHANGED), checkpoint)
            return
        }
        commands?.initialize {
            if (checkpoint != null && game != null) {
                commands?.edit(BettingGameAction.CheckpointRestored(restoredState(fresh, game, checkpoint)))
                acknowledgedCheckpoint = checkpoint
            } else {
                parameters.applyParameters(fresh)
            }
            recoveryLocked = mutableState.value.recoveryReview != null
        }
    }

    private fun matches(game: LotteryGameParameters, checkpoint: BettingCheckpoint): Boolean =
        game.modality.id == checkpoint.modalityId && game.contestNumber == checkpoint.contestNumber &&
            game.contestType.equals(checkpoint.contestType, true)

    private fun restoredState(fresh: BettingParameters, game: LotteryGameParameters, checkpoint: BettingCheckpoint): BettingGameUiState {
        val base = BettingGameUiState(
            loading = false,
            game = game.copy(open = eligible(game)),
            minimumCartAmount = fresh.minimumCartAmount,
            dailyLimit = fresh.dailyLimit
        )
        return try {
            checkpoint.restoreInto(base)
        } catch (_: IllegalArgumentException) {
            BettingGameUiState(
                loading = false,
                recoveryReview = checkpoint,
                errorMessage = recoveryMessages.text(BettingRecoveryMessage.CONTEST_CHANGED)
            )
        }
    }

    private fun recoveryError(message: String, review: BettingCheckpoint? = null) {
        recoveryLocked = true
        commands?.initialize { commands?.edit(BettingGameAction.RecoveryRejected(message, review)) }
    }

    private fun isCurrent() = checkpointSession?.isCurrent() ?: true

    private fun eligible(game: LotteryGameParameters): Boolean = game.open &&
        (
            game.closingDateTime.isBlank() || LotteryDateTimes.parse(game.closingDateTime)
                ?.let { serverTime.now() <= it } == true
            )

    private suspend fun persistCandidate(candidate: BettingGameUiState) {
        val session = checkpointSession ?: return
        check(session.isCurrent())
        if (candidate.addedToCart || (candidate.favoriteSaved && operationId == null && acknowledgedCheckpoint == null)) return
        val checkpoint = candidate.toCheckpoint() ?: return
        if (checkpoint != acknowledgedCheckpoint) {
            session.save(checkpoint)
            check(session.isCurrent())
            acknowledgedCheckpoint = checkpoint
        }
    }

    private fun command(action: () -> Unit) {
        val queue = commands
        if (queue == null) synchronized(mutableState) { action() } else queue.execute { if (!recoveryLocked) action() }
    }

    private fun healthyAction(action: () -> Unit) {
        val queue = commands
        if (queue == null) synchronized(mutableState) { action() } else queue.action { if (!recoveryLocked) action() }
    }

    /** A navegação efetiva entra na fila após cada seleção e etapa confirmada. */
    fun checkpointNavigation(action: () -> Unit) {
        if (recoveryLocked) {
            // Sem cartela editável, sair não deve gravar por cima do rascunho preservado.
            if (!state.value.loading && state.value.game == null && isCurrent()) action()
            return
        }
        if (commands == null) action() else commands.navigate { if (!recoveryLocked) action() }
    }

    /** Resolve o retorno somente após os toques e gravações anteriores, incluindo uma transição complementar pendente. */
    fun requestBack(onLeave: () -> Unit) {
        if (recoveryLocked) {
            checkpointNavigation(onLeave)
            return
        }
        val queue = commands
        if (queue == null) {
            if (!returnToNumbers()) onLeave()
        } else {
            queue.navigate {
                if (!recoveryLocked) {
                    if (state.value.selectionStep == BetSelectionStep.COMPLEMENT) {
                        command { steps.returnToNumbers() }
                    } else {
                        onLeave()
                    }
                }
            }
        }
    }

    private suspend fun beforeRemote(current: BettingGameUiState, kind: String): Boolean {
        val session = checkpointSession ?: return true
        return try {
            check(!recoveryLocked && session.isCurrent())
            check(current.game?.let(::eligible) == true)
            val checkpoint = requireNotNull(current.toCheckpoint())
            val id = java.util.UUID.randomUUID().toString()
            session.beginOperation(id, kind, checkpoint)
            check(session.isCurrent())
            operationId = id
            true
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            favorites.dispatch(BettingGameAction.RecoveryOperationFailed(recoveryMessages.text(BettingRecoveryMessage.INTENT_FAILED)))
            false
        }
    }

    private suspend fun afterRemote(): Boolean {
        val session = checkpointSession ?: return true
        return try {
            session.completeOperation(requireNotNull(operationId))
            check(session.isCurrent())
            operationId = null
            acknowledgedCheckpoint = null
            true
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            recoveryLocked = true
            favorites.dispatch(BettingGameAction.RecoveryOperationFailed(recoveryMessages.text(BettingRecoveryMessage.RECONCILE_REQUIRED)))
            false
        }
    }

    private fun applyParameters(parameters: BettingParameters) = this.parameters.applyParameters(parameters)

    private fun selectGame(parameters: BettingParameters): LotteryGameParameters? = this.parameters.selectGame(parameters)

    fun requestFavorite() = command { favorites.requestFavorite() }

    fun changeFavoriteName(value: String) = command { favorites.changeFavoriteName(value) }

    fun submitFavorite() = healthyAction { favorites.submitFavorite() }

    fun cancelFavorite() = command { favorites.cancelFavorite() }

    fun confirmFavoriteOverwrite() = healthyAction { favorites.confirmFavoriteOverwrite() }

    fun dismissFavoriteOverwrite() = command { favorites.dismissFavoriteOverwrite() }

    fun fillShake(renew: Boolean) = command { selection.fillShake(renew) }

    fun selectMode(mode: BetEntryMode) = command { selection.selectMode(mode) }

    fun toggleNumber(number: Int) = command { selection.toggleNumber(number) }

    fun toggleSuperSevenDigit(column: Int, digit: Int) = command { selection.toggleSuperSevenDigit(column, digit) }

    fun selectLuckyMonth(month: LuckyMonth) = command { selection.selectLuckyMonth(month) }

    fun toggleMirror() = command { selection.toggleMirror() }

    fun toggleClover(clover: Int) = command { selection.toggleClover(clover) }

    fun chooseCloverQuantity(quantity: Int) = command { selection.chooseCloverQuantity(quantity) }

    fun updateTeamQuery(value: String) = command { selection.updateTeamQuery(value) }

    fun selectTeam(team: SportsTeam) = command { selection.selectTeam(team) }

    fun toggleLotecaOutcome(matchNumber: Int, outcome: LotecaOutcome) = command { selection.toggleLotecaOutcome(matchNumber, outcome) }

    fun selectLotogolScore(matchNumber: Int, home: Boolean, score: Int) = command { selection.selectLotogolScore(matchNumber, home, score) }

    fun chooseNumberQuantity(quantity: Int) = command { selection.chooseNumberQuantity(quantity) }

    fun chooseRecurringDraws(draws: Int) = command { selection.chooseRecurringDraws(draws) }

    fun warnSpecialRecurringUnavailable() = command { selection.warnSpecialRecurringUnavailable() }

    fun increaseSurpriseQuantity() = command { selection.increaseSurpriseQuantity() }

    fun decreaseSurpriseQuantity() = command { selection.decreaseSurpriseQuantity() }

    fun increaseSurpriseCloverQuantity() = command { selection.increaseSurpriseCloverQuantity() }

    fun decreaseSurpriseCloverQuantity() = command { selection.decreaseSurpriseCloverQuantity() }

    fun clearSelection() = command { selection.clearSelection() }

    fun clearCurrentStep() = command { selection.clearCurrentStep() }

    fun primaryAction() = healthyAction { steps.primaryAction() }

    /** Retorna true quando a tela consumiu o retorno para sair de uma seleção complementar. */
    fun returnToNumbers(): Boolean {
        if (state.value.selectionStep != BetSelectionStep.COMPLEMENT) return false
        command { steps.returnToNumbers() }
        return true
    }

    fun acknowledgeCartAddition() = command { steps.acknowledgeCartAddition() }

    fun dismissMessage() = command { steps.dismissMessage() }

    fun updateCartContext(authenticated: Boolean, total: java.math.BigDecimal?) = command { cart.updateCartContext(authenticated, total) }
    fun confirmDailyLimit() = healthyAction { cart.confirmDailyLimit() }
    fun cancelDailyLimit() = command { cart.cancelDailyLimit() }

    fun confirmLocalDuplicate() = healthyAction { cart.confirmLocalDuplicate() }
    fun cancelLocalDuplicate() = command { cart.cancelLocalDuplicate() }
    fun addToCart(allowLocalDuplicate: Boolean = false) = healthyAction { cart.addToCart(allowLocalDuplicate) }

    override fun onCleared() {
        commands?.close()
    }
}
