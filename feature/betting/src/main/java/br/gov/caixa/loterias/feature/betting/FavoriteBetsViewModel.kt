package br.gov.caixa.loterias.feature.betting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.betting.AddFavoriteBetToCartUseCase
import br.gov.caixa.loterias.core.domain.betting.DeleteFavoriteBetUseCase
import br.gov.caixa.loterias.core.domain.betting.FavoriteBet
import br.gov.caixa.loterias.core.domain.betting.FavoriteBetCartOptions
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import br.gov.caixa.loterias.core.domain.betting.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoriteBetsUiState(
    val loading: Boolean = true,
    val favorites: List<FavoriteBet> = emptyList(),
    val modalities: List<LotteryModality> = emptyList(),
    val games: List<LotteryGameParameters> = emptyList(),
    val selectedModalityId: Int? = null,
    val deleteCandidate: FavoriteBet? = null,
    val addCandidate: FavoriteBet? = null,
    val contestType: Int = 1,
    val recurringDraws: Int = 0,
    val mirror: Boolean = false,
    val addedToCart: Boolean = false,
    val errorMessage: String? = null,
) {
    val selectedModalityName: String
        get() = modalities.firstOrNull { it.id == selectedModalityId }?.name ?: "Todas"
    val recurringOptions: List<Int>
        get() = addCandidate?.let { bet ->
            games.firstOrNull { game ->
                game.modality.id == bet.modalityId &&
                    game.contestType.equals(if (contestType == 2) "ESPECIAL" else "NORMAL", true)
            }?.recurringOptions
        }.orEmpty().distinct()
    val recurringSelectionIndex: Int
        get() = recurringOptions.indexOf(recurringDraws).coerceAtLeast(0)
    val specialAvailable: Boolean
        get() = addCandidate?.let { bet ->
            games.any { it.modality.id == bet.modalityId && it.contestType.equals("ESPECIAL", true) }
        } == true
    val mirrorAvailable: Boolean
        get() = addCandidate?.modalityName?.contains("lotomania", ignoreCase = true) == true
}

class FavoriteBetsViewModel(
    private val getFavorites: GetFavoriteBetsUseCase,
    private val getParameters: GetBettingParametersUseCase,
    private val deleteFavorite: DeleteFavoriteBetUseCase,
    private val addFavoriteToCart: AddFavoriteBetToCartUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = MutableStateFlow(FavoriteBetsUiState())
    val state = mutableState.asStateFlow()

    init { loadInitial() }

    fun loadInitial() = viewModelScope.launch(dispatchers.io) {
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        when (val parameters = getParameters()) {
            is AppResult.Failure -> applyFailure(parameters.error)
            is AppResult.Success -> {
                mutableState.update {
                    it.copy(
                        modalities = parameters.value.games.map(LotteryGameParameters::modality)
                            .distinctBy(LotteryModality::id).filter { modality -> modality.id > 0 },
                        games = parameters.value.games,
                    )
                }
                loadFavorites(null)
            }
        }
    }

    fun selectModality(modalityId: Int?) {
        mutableState.update { it.copy(selectedModalityId = modalityId) }
        viewModelScope.launch(dispatchers.io) { loadFavorites(modalityId) }
    }

    fun requestDelete(favorite: FavoriteBet) = mutableState.update { it.copy(deleteCandidate = favorite) }
    fun cancelDelete() = mutableState.update { it.copy(deleteCandidate = null) }

    fun confirmDelete() {
        val favorite = state.value.deleteCandidate ?: return
        viewModelScope.launch(dispatchers.io) {
            mutableState.update { it.copy(loading = true, deleteCandidate = null, errorMessage = null) }
            when (val result = deleteFavorite(favorite.id)) {
                is AppResult.Failure -> applyFailure(result.error)
                is AppResult.Success -> loadFavorites(state.value.selectedModalityId)
            }
        }
    }

    fun requestAdd(favorite: FavoriteBet) = mutableState.update {
        val firstGame = it.games.firstOrNull { game -> game.modality.id == favorite.modalityId }
        val candidate = it.copy(addCandidate = favorite,
            contestType = if (firstGame?.contestType.equals("ESPECIAL", true)) 2 else 1, mirror = false)
        candidate.copy(recurringDraws = candidate.recurringOptions.firstOrNull() ?: 0)
    }

    fun cancelAdd() = mutableState.update { it.copy(addCandidate = null) }
    fun setContestType(value: Int) = mutableState.update {
        val candidate = it.copy(contestType = value.coerceIn(1, 2))
        if (candidate.contestType == it.contestType) candidate
        else candidate.copy(recurringDraws = candidate.recurringOptions.firstOrNull() ?: 0)
    }
    fun setRecurringDraws(value: Int) = mutableState.update {
        it.copy(recurringDraws = value.takeIf { option -> option in it.recurringOptions }
            ?: it.recurringOptions.firstOrNull() ?: 0)
    }
    fun toggleMirror() = mutableState.update { it.copy(mirror = !it.mirror) }

    fun confirmAdd() {
        val current = state.value
        val favorite = current.addCandidate ?: return
        viewModelScope.launch(dispatchers.io) {
            mutableState.update { it.copy(loading = true, addCandidate = null, errorMessage = null) }
            val options = FavoriteBetCartOptions(current.contestType, current.recurringDraws, current.mirror)
            when (val result = addFavoriteToCart(favorite.id, options)) {
                is AppResult.Failure -> applyFailure(result.error)
                is AppResult.Success -> mutableState.update { it.copy(loading = false, addedToCart = true) }
            }
        }
    }

    fun consumeAdded() = mutableState.update { it.copy(addedToCart = false) }
    fun dismissError() = mutableState.update { it.copy(errorMessage = null) }

    private suspend fun loadFavorites(modalityId: Int?) {
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        when (val result = getFavorites(modalityId)) {
            is AppResult.Failure -> applyFailure(result.error)
            is AppResult.Success -> mutableState.update {
                it.copy(loading = false, favorites = result.value.filter { favorite -> favorite.id > 0 })
            }
        }
    }

    private fun applyFailure(error: AppError) = mutableState.update {
        it.copy(loading = false, errorMessage = when (error) {
            AppError.Network -> "Não foi possível carregar as apostas favoritas. Verifique sua internet."
            is AppError.Validation -> error.reason.ifBlank { "Não foi possível atualizar a aposta favorita." }
            AppError.Unexpected -> "Ocorreu um erro inesperado ao carregar as apostas favoritas."
        })
    }
}
