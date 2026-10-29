package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import kotlinx.coroutines.flow.StateFlow

internal class BettingGameStepsCoordinator(
    private val state: StateFlow<BettingGameUiState>,
    private val dispatch: (BettingGameAction) -> Unit,
    private val incompleteBetMessage: (BettingGameUiState) -> String,
    private val addToCart: () -> Unit,
) {
    fun primaryAction() {
        val current = state.value
        if (current.loading || current.addedToCart) return
        if (current.selectionStep == BetSelectionStep.NUMBERS && current.requiresManualComplement) {
            if (current.numbersAreComplete) {
                dispatch(BettingGameAction.ComplementStarted)
            } else {
                dispatch(BettingGameAction.IncompleteBetRejected(incompleteBetMessage(state.value)))
            }
            return
        }
        addToCart()
    }

    fun returnToNumbers(): Boolean {
        if (state.value.selectionStep != BetSelectionStep.COMPLEMENT) return false
        dispatch(BettingGameAction.NumbersStarted)
        return true
    }

    fun acknowledgeCartAddition() = dispatch(BettingGameAction.CartAdditionAcknowledged)

    fun dismissMessage() = dispatch(BettingGameAction.MessageDismissed)
}
