package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.mapper.superSevenMaximumPerColumn
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.SUPER_SEVEN_ID

internal class BettingSuperSevenSelection(private val dispatch: (BettingGameAction) -> Unit) {
    fun toggleSuperSevenDigit(column: Int, digit: Int) = dispatch(BettingGameAction.SuperSevenDigitToggled(column, digit))
}
