package br.gov.caixa.loterias.feature.betting.presentation.state

internal fun reduceFavoriteBetsCart(state: FavoriteBetsUiState, action: FavoriteBetsAction): FavoriteBetsUiState = when (action) {
    FavoriteBetsAction.SuspensionReceived -> reduceFavoriteBetsSuspensionReceived(state)
    FavoriteBetsAction.DailyLimitConfirmationRequired -> reduceFavoriteBetsDailyLimitConfirmationRequired(state)
    FavoriteBetsAction.DailyLimitConfirmationCancelled -> reduceFavoriteBetsDailyLimitConfirmationCancelled(state)
    FavoriteBetsAction.DuplicateConfirmationRequired -> reduceFavoriteBetsDuplicateConfirmationRequired(state)
    FavoriteBetsAction.DuplicateConfirmationCancelled -> reduceFavoriteBetsDuplicateConfirmationCancelled(state)
    FavoriteBetsAction.ConfirmationsCancelled -> reduceFavoriteBetsConfirmationsCancelled(state)
    FavoriteBetsAction.AdditionStarted -> reduceFavoriteBetsAdditionStarted(state)
    is FavoriteBetsAction.RecoveryRequired -> reduceFavoriteBetsRecoveryRequired(state, action)
    FavoriteBetsAction.AddedToCart -> reduceFavoriteBetsAddedToCart(state)
    FavoriteBetsAction.AdditionConsumed -> reduceFavoriteBetsAdditionConsumed(state)
    FavoriteBetsAction.ErrorDismissed -> reduceFavoriteBetsErrorDismissed(state)
    else -> state
}

private fun reduceFavoriteBetsSuspensionReceived(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(suspensionNotice = true)
}

private fun reduceFavoriteBetsDailyLimitConfirmationRequired(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(dailyLimitConfirmation = true)
}

private fun reduceFavoriteBetsDailyLimitConfirmationCancelled(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(dailyLimitConfirmation = false)
}

private fun reduceFavoriteBetsDuplicateConfirmationRequired(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(duplicateConfirmation = true)
}

private fun reduceFavoriteBetsDuplicateConfirmationCancelled(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(duplicateConfirmation = false)
}

private fun reduceFavoriteBetsConfirmationsCancelled(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(dailyLimitConfirmation = false, duplicateConfirmation = false)
}

private fun reduceFavoriteBetsAdditionStarted(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(loading = true, addCandidate = null, errorMessage = null)
}

private fun reduceFavoriteBetsRecoveryRequired(
    state: FavoriteBetsUiState,
    action: FavoriteBetsAction.RecoveryRequired,
): FavoriteBetsUiState = run {
    val it = state
    it.copy(loading = false, errorMessage = action.message)
}

private fun reduceFavoriteBetsAddedToCart(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(loading = false, addedToCart = true)
}

private fun reduceFavoriteBetsAdditionConsumed(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(addedToCart = false)
}

private fun reduceFavoriteBetsErrorDismissed(state: FavoriteBetsUiState): FavoriteBetsUiState = run {
    val it = state
    it.copy(errorMessage = null, suspensionNotice = false)
}
