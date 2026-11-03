package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction

internal class BettingSelectionReset(private val dispatch: (BettingGameAction) -> Unit) {
    fun selectMode(mode: BetEntryMode) = dispatch(BettingGameAction.ModeSelected(mode))

    fun clearSelection() = dispatch(BettingGameAction.SelectionCleared)

    fun clearCurrentStep() = dispatch(BettingGameAction.CurrentStepCleared)
}
