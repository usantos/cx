package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingSelectionWarnings
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome

internal class BettingGameSelection(
    private val state: kotlinx.coroutines.flow.StateFlow<BettingGameUiState>,
    private val dispatch: (br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction) -> Unit,
) {
    private val warnings = BettingSelectionWarnings()
    private val numbers = BettingNumberSelection(state, dispatch, warnings)
    private val superSeven = BettingSuperSevenSelection(dispatch)
    private val sports = BettingSportsSelection(dispatch)
    private val quantities = BettingSelectionQuantities(state, dispatch, warnings)
    private val reset = BettingSelectionReset(dispatch)

    fun fillShake(renew: Boolean) = numbers.fillShake(renew)

    fun toggleNumber(number: Int) = numbers.toggleNumber(number)

    fun selectLuckyMonth(month: LuckyMonth) = numbers.selectLuckyMonth(month)

    fun toggleMirror() = numbers.toggleMirror()

    fun toggleClover(clover: Int) = numbers.toggleClover(clover)

    fun chooseCloverQuantity(quantity: Int) = numbers.chooseCloverQuantity(quantity)

    fun toggleSuperSevenDigit(column: Int, digit: Int) = superSeven.toggleSuperSevenDigit(column, digit)

    fun updateTeamQuery(value: String) = sports.updateTeamQuery(value)

    fun selectTeam(team: SportsTeam) = sports.selectTeam(team)

    fun toggleLotecaOutcome(matchNumber: Int, outcome: LotecaOutcome) = sports.toggleLotecaOutcome(matchNumber, outcome)

    fun selectLotogolScore(matchNumber: Int, home: Boolean, score: Int) = sports.selectLotogolScore(matchNumber, home, score)

    fun chooseNumberQuantity(quantity: Int) = quantities.chooseNumberQuantity(quantity)

    fun chooseRecurringDraws(draws: Int) = quantities.chooseRecurringDraws(draws)

    fun warnSpecialRecurringUnavailable() = quantities.warnSpecialRecurringUnavailable()

    fun increaseSurpriseQuantity() = quantities.increaseSurpriseQuantity()

    fun decreaseSurpriseQuantity() = quantities.decreaseSurpriseQuantity()

    fun increaseSurpriseCloverQuantity() = quantities.increaseSurpriseCloverQuantity()

    fun decreaseSurpriseCloverQuantity() = quantities.decreaseSurpriseCloverQuantity()

    fun selectMode(mode: BetEntryMode) = reset.selectMode(mode)

    fun clearSelection() = reset.clearSelection()

    fun clearCurrentStep() = reset.clearCurrentStep()
}
