package br.gov.caixa.loterias.feature.betting.presentation.route

import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel

internal class BettingSportsCallbacks(viewModel: BettingGameViewModel) {
    val onQueryChanged: (String) -> Unit = viewModel::updateTeamQuery
    val onTeamSelected: (SportsTeam) -> Unit = viewModel::selectTeam
    val onOutcomeSelected: (Int, LotecaOutcome) -> Unit = viewModel::toggleLotecaOutcome
}
