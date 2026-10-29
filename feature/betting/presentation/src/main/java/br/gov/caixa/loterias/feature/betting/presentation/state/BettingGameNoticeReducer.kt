package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction

internal fun reduceBettingGameNotice(state: BettingGameUiState, action: BettingGameAction): BettingGameUiState = when (action) {
    is BettingGameAction.FailureReceived -> reduceBettingGameFailureReceived(state, action)
    else -> state
}

private fun reduceBettingGameFailureReceived(state: BettingGameUiState, action: BettingGameAction.FailureReceived): BettingGameUiState =
    run {
        val it = state
        if (action.error is AppError.Validation &&
            action.error.reason == br.gov.caixa.loterias.feature.cart.domain.model.VISITOR_DUPLICATE_CONFIRMATION
        ) {
            it.copy(loading = false, duplicateConfirmation = true, errorMessage = null)
        } else {
            it.copy(loading = false, errorMessage = action.message)
        }
    }
