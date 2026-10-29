package br.gov.caixa.loterias.feature.betting.presentation.state

internal fun reduceFavoriteBetsSelection(state: FavoriteBetsUiState, action: FavoriteBetsAction): FavoriteBetsUiState = when (action) {
    is FavoriteBetsAction.ModalitySelected -> reduceFavoriteBetsModalitySelected(state, action)
    FavoriteBetsAction.ModalityCleared -> reduceFavoriteBetsModalityCleared(state)
    is FavoriteBetsAction.AdditionRequested -> reduceFavoriteBetsAdditionRequested(state, action)
    FavoriteBetsAction.AdditionCancelled -> reduceFavoriteBetsAdditionCancelled(state)
    is FavoriteBetsAction.ContestTypeChanged -> reduceFavoriteBetsContestTypeChanged(state, action)
    is FavoriteBetsAction.RecurringDrawsChanged -> reduceFavoriteBetsRecurringDrawsChanged(state, action)
    FavoriteBetsAction.MirrorToggled -> reduceFavoriteBetsMirrorToggled(state)
    else -> state
}

private fun reduceFavoriteBetsModalitySelected(
    state: FavoriteBetsUiState,
    action: FavoriteBetsAction.ModalitySelected,
): FavoriteBetsUiState = run {
    val it = state
    it.copy(selectedModalityId = action.modalityId)
}

private fun reduceFavoriteBetsModalityCleared(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(selectedModalityId = null)
}

private fun reduceFavoriteBetsAdditionRequested(
    state: FavoriteBetsUiState,
    action: FavoriteBetsAction.AdditionRequested,
): FavoriteBetsUiState = run {
    val it = state
    val candidate = it.copy(
        addCandidate = action.favorite,
        contestType = if (action.special) 2 else 1,
        mirror = false
    )
    candidate.copy(recurringDraws = candidate.recurringOptions.firstOrNull() ?: 0)
}

private fun reduceFavoriteBetsAdditionCancelled(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(addCandidate = null)
}

private fun reduceFavoriteBetsContestTypeChanged(
    state: FavoriteBetsUiState,
    action: FavoriteBetsAction.ContestTypeChanged,
): FavoriteBetsUiState = run {
    val it = state
    val candidate = it.copy(contestType = action.value.coerceIn(1, 2))
    if (candidate.contestType == it.contestType) {
        candidate
    } else {
        candidate.copy(recurringDraws = candidate.recurringOptions.firstOrNull() ?: 0)
    }
}

private fun reduceFavoriteBetsRecurringDrawsChanged(
    state: FavoriteBetsUiState,
    action: FavoriteBetsAction.RecurringDrawsChanged,
): FavoriteBetsUiState = run {
    val it = state
    it.copy(
        recurringDraws = action.value.takeIf { option -> option in it.recurringOptions }
            ?: it.recurringOptions.firstOrNull() ?: 0
    )
}

private fun reduceFavoriteBetsMirrorToggled(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(mirror = !it.mirror)
}
