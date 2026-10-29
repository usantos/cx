package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.DIA_DE_SORTE_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.TIMEMANIA_ID

internal class BettingSelectionReset(private val dispatch: (BettingGameAction) -> Unit) {
    fun selectMode(mode: BetEntryMode) = dispatch(BettingGameAction.ModeSelected(mode))

    fun clearSelection() = dispatch(BettingGameAction.SelectionCleared)

    fun clearCurrentStep() = dispatch(BettingGameAction.CurrentStepCleared)
}
