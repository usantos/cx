package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction

internal fun reduceBettingGame(state: BettingGameUiState, action: BettingGameAction): BettingGameUiState = when (action) {
    is BettingGameAction.SpecialContestsLoaded -> reduceBettingGameSpecialContestsLoaded(state, action)
    is BettingGameAction.RecoveryOperationFailed -> reduceBettingGameRecoveryOperationFailed(state, action)
    else -> state
}

private fun reduceBettingGameSpecialContestsLoaded(
    state: BettingGameUiState,
    action: BettingGameAction.SpecialContestsLoaded,
): BettingGameUiState = run {
    val it = state
    it.copy(specialContests = action.contests)
}

private fun reduceBettingGameRecoveryOperationFailed(
    state: BettingGameUiState,
    action: BettingGameAction.RecoveryOperationFailed,
): BettingGameUiState = run {
    val it = state
    it.copy(loading = false, errorMessage = action.message)
}
