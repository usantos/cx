package br.gov.caixa.loterias.feature.account.presentation.state

sealed interface AccountCompletionAction {
    data object CheckStarted : AccountCompletionAction
    data class Verified(val outcome: AccountCompletionOutcome) : AccountCompletionAction
    data class Failed(val message: String) : AccountCompletionAction
    data object CheckFinished : AccountCompletionAction
    data object ErrorDismissed : AccountCompletionAction
    data class Acknowledged(val outcome: AccountCompletionOutcome) : AccountCompletionAction
    data object Reset : AccountCompletionAction
}

fun reduceAccountCompletionState(state: AccountCompletionState, action: AccountCompletionAction): AccountCompletionState = when (action) {
    AccountCompletionAction.CheckStarted -> AccountCompletionState(checking = true)
    is AccountCompletionAction.Verified -> AccountCompletionState(checking = true, outcome = action.outcome)
    is AccountCompletionAction.Failed -> AccountCompletionState(error = action.message)
    AccountCompletionAction.CheckFinished -> state.copy(checking = false)
    AccountCompletionAction.ErrorDismissed -> state.copy(error = null)
    is AccountCompletionAction.Acknowledged -> if (state.outcome === action.outcome) AccountCompletionState() else state
    AccountCompletionAction.Reset -> AccountCompletionState()
}
