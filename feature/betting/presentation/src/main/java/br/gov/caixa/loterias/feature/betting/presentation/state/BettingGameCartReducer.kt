package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.presentation.mapper.availableNumberQuantities
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction

internal fun reduceBettingGameCart(state: BettingGameUiState, action: BettingGameAction): BettingGameUiState = when (action) {
    BettingGameAction.DailyLimitCancelled -> reduceBettingGameDailyLimitCancelled(state)
    BettingGameAction.DuplicateCancelled -> reduceBettingGameDuplicateCancelled(state)
    is BettingGameAction.ClosedContestRejected -> reduceBettingGameClosedContestRejected(state, action)
    is BettingGameAction.IncompleteBetRejected -> reduceBettingGameIncompleteBetRejected(state, action)
    BettingGameAction.DailyLimitRequired -> reduceBettingGameDailyLimitRequired(state)
    BettingGameAction.CartSubmissionStarted -> reduceBettingGameCartSubmissionStarted(state)
    is BettingGameAction.CartAdditionConfirmed -> reduceBettingGameCartAdditionConfirmed(state, action)
    else -> state
}

private fun reduceBettingGameDailyLimitCancelled(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(dailyLimitConfirmation = false)
}

private fun reduceBettingGameDuplicateCancelled(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(duplicateConfirmation = false)
}

private fun reduceBettingGameClosedContestRejected(
    state: BettingGameUiState,
    action: BettingGameAction.ClosedContestRejected,
): BettingGameUiState = run {
    val it = state
    it.copy(errorMessage = action.message)
}

private fun reduceBettingGameIncompleteBetRejected(
    state: BettingGameUiState,
    action: BettingGameAction.IncompleteBetRejected,
): BettingGameUiState = run {
    val it = state
    it.copy(errorMessage = action.message)
}

private fun reduceBettingGameDailyLimitRequired(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(dailyLimitConfirmation = true)
}

private fun reduceBettingGameCartSubmissionStarted(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(loading = true, errorMessage = null)
}

private fun reduceBettingGameCartAdditionConfirmed(
    state: BettingGameUiState,
    action: BettingGameAction.CartAdditionConfirmed,
): BettingGameUiState = run {
    val it = state
    BettingGameUiState(
        loading = false,
        game = action.game,
        minimumCartAmount = it.minimumCartAmount,
        dailyLimit = it.dailyLimit,
        numberQuantity = availableNumberQuantities(action.game).firstOrNull() ?: action.game.minimumSelection,
        manualCloverQuantity = action.game.minimumClovers,
        surpriseCloverQuantity = action.game.minimumClovers,
        addedToCart = true,
        confirmedCart = action.cart
    )
}
