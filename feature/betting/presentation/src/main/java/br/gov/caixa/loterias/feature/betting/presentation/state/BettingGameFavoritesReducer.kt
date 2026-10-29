package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction

internal fun reduceBettingGameFavorites(state: BettingGameUiState, action: BettingGameAction): BettingGameUiState = when (action) {
    BettingGameAction.FavoriteRequested -> reduceBettingGameFavoriteRequested(state)
    is BettingGameAction.FavoriteNameChanged -> reduceBettingGameFavoriteNameChanged(state, action)
    is BettingGameAction.FavoriteNameMissing -> reduceBettingGameFavoriteNameMissing(state, action)
    is BettingGameAction.FavoriteUnavailable -> reduceBettingGameFavoriteUnavailable(state, action)
    BettingGameAction.FavoriteSavingStarted -> reduceBettingGameFavoriteSavingStarted(state)
    BettingGameAction.FavoriteLoadingFinished -> reduceBettingGameFavoriteLoadingFinished(state)
    is BettingGameAction.FavoriteOverwriteRequired -> reduceBettingGameFavoriteOverwriteRequired(state, action)
    is BettingGameAction.FavoriteFailed -> reduceBettingGameFavoriteFailed(state, action)
    BettingGameAction.FavoriteCancelled -> reduceBettingGameFavoriteCancelled(state)
    BettingGameAction.FavoriteOverwriteStarted -> reduceBettingGameFavoriteOverwriteStarted(state)
    BettingGameAction.FavoriteOverwriteDismissed -> reduceBettingGameFavoriteOverwriteDismissed(state)
    is BettingGameAction.FavoriteSaved -> reduceBettingGameFavoriteSaved(state, action)
    else -> state
}

private fun reduceBettingGameFavoriteRequested(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(favoriteExpanded = true, favoriteName = "", errorMessage = null)
}

private fun reduceBettingGameFavoriteNameChanged(
    state: BettingGameUiState,
    action: BettingGameAction.FavoriteNameChanged,
): BettingGameUiState = run {
    val it = state
    it.copy(favoriteName = action.value, errorMessage = null)
}

private fun reduceBettingGameFavoriteNameMissing(
    state: BettingGameUiState,
    action: BettingGameAction.FavoriteNameMissing,
): BettingGameUiState = run {
    val it = state
    it.copy(favoriteExpanded = false, errorMessage = action.message)
}

private fun reduceBettingGameFavoriteUnavailable(
    state: BettingGameUiState,
    action: BettingGameAction.FavoriteUnavailable,
): BettingGameUiState = run {
    val it = state
    it.copy(errorMessage = action.message)
}

private fun reduceBettingGameFavoriteSavingStarted(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(loading = true, favoriteExpanded = false, errorMessage = null)
}

private fun reduceBettingGameFavoriteLoadingFinished(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(loading = false)
}

private fun reduceBettingGameFavoriteOverwriteRequired(
    state: BettingGameUiState,
    action: BettingGameAction.FavoriteOverwriteRequired,
): BettingGameUiState = run {
    val it = state
    it.copy(
        loading = false,
        favoriteExpanded = false,
        favoriteOverwriteConfirmation = true,
        favoriteOverwriteMessage = action.message
    )
}

private fun reduceBettingGameFavoriteFailed(state: BettingGameUiState, action: BettingGameAction.FavoriteFailed): BettingGameUiState = run {
    val it = state
    it.copy(loading = false, errorMessage = action.message)
}

private fun reduceBettingGameFavoriteCancelled(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(
        favoriteExpanded = false,
        favoriteOverwriteConfirmation = false,
        favoriteOverwriteMessage = null
    )
}

private fun reduceBettingGameFavoriteOverwriteStarted(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(
        loading = true,
        favoriteOverwriteConfirmation = false,
        favoriteOverwriteMessage = null,
        errorMessage = null
    )
}

private fun reduceBettingGameFavoriteOverwriteDismissed(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(favoriteOverwriteConfirmation = false, favoriteOverwriteMessage = null)
}

private fun reduceBettingGameFavoriteSaved(state: BettingGameUiState, action: BettingGameAction.FavoriteSaved): BettingGameUiState = run {
    val it = state
    it.copy(
        loading = false,
        favoriteExpanded = false,
        favoriteSaved = action.currentRevision,
        favoriteOverwriteConfirmation = false,
        favoriteOverwriteMessage = null
    )
}
