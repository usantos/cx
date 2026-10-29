package br.gov.caixa.loterias.feature.betting.presentation.state

internal fun reduceFavoriteBetsDeletion(state: FavoriteBetsUiState, action: FavoriteBetsAction): FavoriteBetsUiState = when (action) {
    is FavoriteBetsAction.DeletionRequested -> reduceFavoriteBetsDeletionRequested(state, action)
    FavoriteBetsAction.DeletionCancelled -> reduceFavoriteBetsDeletionCancelled(state)
    FavoriteBetsAction.DeletionStarted -> reduceFavoriteBetsDeletionStarted(state)
    is FavoriteBetsAction.Deleted -> reduceFavoriteBetsDeleted(state, action)
    else -> state
}

private fun reduceFavoriteBetsDeletionRequested(
    state: FavoriteBetsUiState,
    action: FavoriteBetsAction.DeletionRequested,
): FavoriteBetsUiState = run {
    val it = state
    it.copy(deleteCandidate = action.favorite)
}

private fun reduceFavoriteBetsDeletionCancelled(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(deleteCandidate = null)
}

private fun reduceFavoriteBetsDeletionStarted(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(loading = true, deleteCandidate = null, errorMessage = null)
}

private fun reduceFavoriteBetsDeleted(state: FavoriteBetsUiState, action: FavoriteBetsAction.Deleted): FavoriteBetsUiState = run {
    val it = state
    it.copy(loading = false, favorites = action.remaining)
}
