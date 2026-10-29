package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality

internal fun reduceFavoriteBetsLoading(state: FavoriteBetsUiState, action: FavoriteBetsAction): FavoriteBetsUiState = when (action) {
    FavoriteBetsAction.LoadingStarted -> reduceFavoriteBetsLoadingStarted(state)
    is FavoriteBetsAction.ParametersLoaded -> reduceFavoriteBetsParametersLoaded(state, action)
    FavoriteBetsAction.MoreLoading -> reduceFavoriteBetsMoreLoading(state)
    is FavoriteBetsAction.MoreLoaded -> reduceFavoriteBetsMoreLoaded(state, action)
    FavoriteBetsAction.LoadingFinished -> reduceFavoriteBetsLoadingFinished(state)
    FavoriteBetsAction.FirstPageLoading -> reduceFavoriteBetsFirstPageLoading(state)
    is FavoriteBetsAction.FirstPageLoaded -> reduceFavoriteBetsFirstPageLoaded(state, action)
    is FavoriteBetsAction.Failed -> reduceFavoriteBetsFailed(state, action)
    else -> state
}

private fun reduceFavoriteBetsLoadingStarted(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(loading = true, errorMessage = null)
}

private fun reduceFavoriteBetsParametersLoaded(
    state: FavoriteBetsUiState,
    action: FavoriteBetsAction.ParametersLoaded,
): FavoriteBetsUiState = run {
    val it = state
    it.copy(
        modalities = action.modalities.distinctBy(LotteryModality::id)
            .filter { modality -> modality.id > 0 },
        games = action.parameters.games,
        dailyLimit = action.parameters.dailyLimit
    )
}

private fun reduceFavoriteBetsMoreLoading(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(loadingMore = true)
}

private fun reduceFavoriteBetsMoreLoaded(state: FavoriteBetsUiState, action: FavoriteBetsAction.MoreLoaded): FavoriteBetsUiState = run {
    val it = state
    it.copy(
        loadingMore = false,
        canLoadMore = action.favorites.isNotEmpty(),
        favorites = (it.favorites + action.favorites.filter { bet -> bet.id > 0 }).distinctBy(FavoriteBet::id)
    )
}

private fun reduceFavoriteBetsLoadingFinished(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(loading = false)
}

private fun reduceFavoriteBetsFirstPageLoading(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(loading = true, loadingMore = false, canLoadMore = false, errorMessage = null)
}

private fun reduceFavoriteBetsFirstPageLoaded(state: FavoriteBetsUiState, action: FavoriteBetsAction.FirstPageLoaded): FavoriteBetsUiState =
    run {
        val it = state
        it.copy(
            loading = false,
            favorites = action.favorites.filter { favorite -> favorite.id > 0 },
            canLoadMore = action.filtered && action.favorites.isNotEmpty()
        )
    }

private fun reduceFavoriteBetsFailed(state: FavoriteBetsUiState, action: FavoriteBetsAction.Failed): FavoriteBetsUiState = run {
    val it = state
    it.copy(
        loading = false,
        loadingMore = false,
        errorMessage = action.message
    )
}
