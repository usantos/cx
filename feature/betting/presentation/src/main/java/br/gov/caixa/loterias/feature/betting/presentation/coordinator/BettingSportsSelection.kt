package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.core.domain.contract.betting.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome

internal class BettingSportsSelection(private val dispatch: (BettingGameAction) -> Unit) {
    fun updateTeamQuery(value: String) = dispatch(BettingGameAction.TeamQueryChanged(value))

    fun selectTeam(team: SportsTeam) = dispatch(BettingGameAction.TeamSelected(team))

    fun toggleLotecaOutcome(matchNumber: Int, outcome: LotecaOutcome) =
        dispatch(BettingGameAction.LotecaOutcomeToggled(matchNumber, outcome))

    fun selectLotogolScore(matchNumber: Int, home: Boolean, score: Int) =
        dispatch(BettingGameAction.LotogolScoreSelected(matchNumber, home, score))
}
