package br.gov.caixa.loterias.feature.betting.presentation.state

internal sealed interface BettingFlowAction {
    data class PageSelected(val page: Int) : BettingFlowAction
    data object DismissalToggled : BettingFlowAction
}

internal fun reduceBettingFlowState(state: BettingFlowUiState, action: BettingFlowAction): BettingFlowUiState = when (action) {
    is BettingFlowAction.PageSelected -> state.copy(page = action.page)
    BettingFlowAction.DismissalToggled -> state.copy(doNotShowAgain = !state.doNotShowAgain)
}
