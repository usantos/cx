package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction

internal class BettingSuperSevenSelection(private val dispatch: (BettingGameAction) -> Unit) {
    fun toggleSuperSevenDigit(column: Int, digit: Int) = dispatch(BettingGameAction.SuperSevenDigitToggled(column, digit))
}
