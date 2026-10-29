package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec

data class BettingFlowUiState(
    val spec: WorkflowSpec,
    val page: Int = 0,
    val doNotShowAgain: Boolean = false,
)

sealed interface BettingFlowEffect {
    data object NavigateBack : BettingFlowEffect
    data object Completed : BettingFlowEffect
}
