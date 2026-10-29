package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction

internal fun reduceBettingGameSteps(state: BettingGameUiState, action: BettingGameAction): BettingGameUiState = when (action) {
    BettingGameAction.ComplementStarted -> reduceBettingGameComplementStarted(state)
    BettingGameAction.NumbersStarted -> reduceBettingGameNumbersStarted(state)
    BettingGameAction.CartAdditionAcknowledged -> reduceBettingGameCartAdditionAcknowledged(state)
    BettingGameAction.MessageDismissed -> reduceBettingGameMessageDismissed(state)
    else -> state
}

private fun reduceBettingGameComplementStarted(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(selectionStep = BetSelectionStep.COMPLEMENT, errorMessage = null)
}

private fun reduceBettingGameNumbersStarted(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(selectionStep = BetSelectionStep.NUMBERS, errorMessage = null)
}

private fun reduceBettingGameCartAdditionAcknowledged(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(addedToCart = false)
}

private fun reduceBettingGameMessageDismissed(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(warningMessage = null, errorMessage = null)
}
