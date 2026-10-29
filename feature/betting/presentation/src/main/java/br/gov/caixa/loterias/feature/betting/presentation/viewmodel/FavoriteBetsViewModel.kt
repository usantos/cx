package br.gov.caixa.loterias.feature.betting.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetCartOptions
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSession
import br.gov.caixa.loterias.feature.betting.domain.rule.favoriteBetAlreadyInCart
import br.gov.caixa.loterias.feature.betting.domain.rule.favoriteBetPrice
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddFavoriteBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.DeleteFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingMutationBarrier
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingRecoveryMessage
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingRecoveryMessages
import br.gov.caixa.loterias.feature.betting.presentation.state.FavoriteBetsAction
import br.gov.caixa.loterias.feature.betting.presentation.state.FavoriteBetsUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceFavoriteBetsState
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
class FavoriteBetsViewModel(
    private val getFavorites: GetFavoriteBetsUseCase,
    private val getParameters: GetBettingParametersUseCase,
    private val deleteFavorite: DeleteFavoriteBetUseCase,
    private val addFavoriteToCart: AddFavoriteBetToCartUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    autoLoad: Boolean = true,
    private val getModalities: GetLotteryModalitiesUseCase? = null,
    private val getCurrentCart: GetCurrentCartUseCase? = null,
    private val verifyAccountAccess: VerifyAccountAccessUseCase? = null,
    private val isSessionCurrent: () -> Boolean = { true },
    private val recoveryMessages: BettingRecoveryMessages = BettingRecoveryMessages.Identifiers,
    private val checkpointSession: BettingCheckpointSession? = null,
) : ViewModel() {
    private val mutableState = reduxStateStore<FavoriteBetsUiState, FavoriteBetsAction>(
        FavoriteBetsUiState(loading = autoLoad),
        ::reduceFavoriteBetsState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state
    private val mutation = BettingMutationBarrier(checkpointSession)
    private var favoritesRequest = 0L
    private var nextOffset = 0
    private var pendingAdd: Pair<FavoriteBet, ShoppingCart?>? = null

    init {
        if (autoLoad) loadInitial()
    }

    fun loadInitial() = viewModelScope.launch(dispatchers.io) {
        if (!isSessionCurrent()) return@launch
        mutableState.dispatch(FavoriteBetsAction.LoadingStarted)
        val parameters = getParameters()
        if (!isSessionCurrent()) return@launch
        when (parameters) {
            is AppResult.Failure -> applyFailure(parameters.error)

            is AppResult.Success -> {
                val catalog = getModalities?.invoke()
                    ?: AppResult.Success(parameters.value.games.map(LotteryGameParameters::modality))
                if (!isSessionCurrent()) return@launch
                val modalities = when (catalog) {
                    is AppResult.Failure -> {
                        applyFailure(catalog.error)
                        return@launch
                    }

                    is AppResult.Success -> catalog.value
                }
                mutableState.dispatch(FavoriteBetsAction.ParametersLoaded(modalities, parameters.value))
                loadFavorites(state.value.selectedModalityId)
            }
        }
    }

    fun selectModality(modalityId: Int?) {
        if (!isSessionCurrent()) return
        mutableState.dispatch(FavoriteBetsAction.ModalitySelected(modalityId))
        viewModelScope.launch(dispatchers.io) { loadFavorites(modalityId) }
    }

    fun loadMore() {
        val current = state.value
        val modality = current.selectedModalityId ?: return
        if (!isSessionCurrent() || current.loading || current.loadingMore || !current.canLoadMore) return
        val request = favoritesRequest
        val offset = nextOffset
        mutableState.dispatch(FavoriteBetsAction.MoreLoading)
        viewModelScope.launch(dispatchers.io) {
            val result = getFavorites.page(modality, offset)
            if (!isSessionCurrent() || request != favoritesRequest || state.value.selectedModalityId != modality) return@launch
            when (result) {
                is AppResult.Failure -> applyFailure(result.error)

                is AppResult.Success -> {
                    nextOffset += result.value.size
                    mutableState.dispatch(FavoriteBetsAction.MoreLoaded(result.value))
                }
            }
        }
    }

    fun requestDelete(favorite: FavoriteBet) {
        if (isSessionCurrent() && !state.value.loading) mutableState.dispatch(FavoriteBetsAction.DeletionRequested(favorite))
    }
    fun cancelDelete() = mutableState.dispatch(FavoriteBetsAction.DeletionCancelled)

    fun confirmDelete() {
        if (!isSessionCurrent() || state.value.loading) return
        val favorite = state.value.deleteCandidate ?: return
        mutableState.dispatch(FavoriteBetsAction.DeletionStarted)
        viewModelScope.launch(dispatchers.io) {
            if (!isSessionCurrent()) return@launch
            val result = deleteFavorite(favorite.id)
            if (!isSessionCurrent()) return@launch
            when (result) {
                is AppResult.Failure -> applyFailure(result.error)

                is AppResult.Success -> {
                    val remaining = state.value.favorites.filterNot { it.id == favorite.id }
                    if (remaining.isNotEmpty()) {
                        mutableState.dispatch(FavoriteBetsAction.Deleted(remaining))
                    } else {
                        mutableState.dispatch(FavoriteBetsAction.ModalityCleared)
                        loadFavorites(null)
                    }
                }
            }
        }
    }

    fun requestAdd(favorite: FavoriteBet) {
        val current = state.value
        if (!isSessionCurrent() || current.loading || current.addedToCart || current.addCandidate != null || pendingAdd != null) return
        mutableState.dispatch(FavoriteBetsAction.LoadingStarted)
        viewModelScope.launch(dispatchers.io) {
            if (!isSessionCurrent()) return@launch
            verifyAccountAccess?.let { verify ->
                val result = verify()
                if (!isSessionCurrent()) return@launch
                when (result) {
                    is AppResult.Failure -> {
                        applyFailure(result.error)
                        return@launch
                    }

                    is AppResult.Success -> if (result.value.temporarySuspension) {
                        mutableState.dispatch(FavoriteBetsAction.SuspensionReceived)
                        applyFailure(
                            AppError.Validation(
                                messageText("ha_uma_suspensao_temporaria_ativ_3db999") +
                                    "no Portal Loterias Online " +
                                    messageText("e_no_aplicativo_loterias_caixa_a_d4a054")
                            )
                        )
                        return@launch
                    }
                }
            }
            val cart = getCurrentCart?.let { get ->
                val result = get()
                if (!isSessionCurrent()) return@launch
                when (result) {
                    is AppResult.Failure -> {
                        applyFailure(result.error)
                        return@launch
                    }

                    is AppResult.Success -> result.value
                }
            }
            if (state.value.games.none { it.modality.id == favorite.modalityId }) {
                applyFailure(AppError.Validation(messageText("modalidade_nao_disponivel_no_mom_5f4037")))
                return@launch
            }
            pendingAdd = favorite to cart
            mutableState.dispatch(FavoriteBetsAction.LoadingFinished)
            if (cart != null && cart.total.add(favoriteBetPrice(favorite, state.value.games)) > state.value.dailyLimit) {
                mutableState.dispatch(FavoriteBetsAction.DailyLimitConfirmationRequired)
            } else {
                checkDuplicate()
            }
        }
    }

    fun confirmDailyLimit() {
        if (!state.value.dailyLimitConfirmation || !isSessionCurrent()) return
        mutableState.dispatch(FavoriteBetsAction.DailyLimitConfirmationCancelled)
        checkDuplicate()
    }

    private fun checkDuplicate() {
        if (!isSessionCurrent()) return
        val (favorite, cart) = pendingAdd ?: return
        if (favoriteBetAlreadyInCart(favorite, cart, state.value.games)) {
            mutableState.dispatch(FavoriteBetsAction.DuplicateConfirmationRequired)
        } else {
            configureAdd()
        }
    }

    fun confirmDuplicate() {
        if (!state.value.duplicateConfirmation || !isSessionCurrent()) return
        mutableState.dispatch(FavoriteBetsAction.DuplicateConfirmationCancelled)
        configureAdd()
    }

    fun cancelAddConfirmation() {
        pendingAdd = null
        mutableState.dispatch(FavoriteBetsAction.ConfirmationsCancelled)
    }

    private fun configureAdd() {
        if (!isSessionCurrent()) return
        val favorite = pendingAdd?.first ?: return
        pendingAdd = null
        val games = state.value.games.filter { it.modality.id == favorite.modalityId }
        if (games.size == 1 && games.single().contestType.equals("ESPECIAL", true)) {
            submit(favorite, FavoriteBetCartOptions(2, 0, null))
            return
        }
        mutableState.dispatch(FavoriteBetsAction.AdditionRequested(favorite, games.firstOrNull()?.contestType.equals("ESPECIAL", true)))
    }

    fun cancelAdd() = mutableState.dispatch(FavoriteBetsAction.AdditionCancelled)
    fun setContestType(value: Int) = mutableState.dispatch(FavoriteBetsAction.ContestTypeChanged(value))
    fun setRecurringDraws(value: Int) = mutableState.dispatch(FavoriteBetsAction.RecurringDrawsChanged(value))
    fun toggleMirror() = mutableState.dispatch(FavoriteBetsAction.MirrorToggled)

    fun confirmAdd() {
        if (!isSessionCurrent() || state.value.loading) return
        val current = state.value
        val favorite = current.addCandidate ?: return
        submit(favorite, FavoriteBetCartOptions(current.contestType, current.recurringDraws, current.mirror))
    }

    private fun submit(favorite: FavoriteBet, options: FavoriteBetCartOptions) {
        mutableState.dispatch(FavoriteBetsAction.AdditionStarted)
        viewModelScope.launch(dispatchers.io) {
            if (!isSessionCurrent()) return@launch
            val result = try {
                mutation.run("ADD_FAVORITE", { it: AppResult<*> -> it is AppResult.Success }) { addFavoriteToCart(favorite.id, options) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                if (isSessionCurrent() && checkpointSession?.isCurrent() != false) {
                    mutableState.dispatch(
                        FavoriteBetsAction.RecoveryRequired(recoveryMessages.text(BettingRecoveryMessage.RECONCILE_REQUIRED))
                    )
                }
                return@launch
            }
            if (!isSessionCurrent()) return@launch
            when (result) {
                is AppResult.Failure -> applyFailure(result.error)
                is AppResult.Success -> mutableState.dispatch(FavoriteBetsAction.AddedToCart)
            }
        }
    }

    fun consumeAdded() = mutableState.dispatch(FavoriteBetsAction.AdditionConsumed)
    fun dismissError() = mutableState.dispatch(FavoriteBetsAction.ErrorDismissed)

    private suspend fun loadFavorites(modalityId: Int?) {
        if (!isSessionCurrent()) return
        val request = ++favoritesRequest
        mutableState.dispatch(FavoriteBetsAction.FirstPageLoading)
        val result = getFavorites(modalityId)
        if (!isSessionCurrent() || request != favoritesRequest || state.value.selectedModalityId != modalityId) return
        when (result) {
            is AppResult.Failure -> applyFailure(result.error)

            is AppResult.Success -> {
                nextOffset = 8 // A primeira página de modalidades do legado contém oito itens; as seguintes, cinco.
                mutableState.dispatch(FavoriteBetsAction.FirstPageLoaded(result.value, modalityId != null))
            }
        }
    }

    private fun applyFailure(error: AppError) = mutableState.dispatch(FavoriteBetsAction.Failed(error))
}
