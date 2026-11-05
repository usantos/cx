package br.gov.caixa.loterias.feature.betting.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome

interface BettingBettingSportsContentEntry {
    @Composable
    fun BettingLegacyTeams(
        state: BettingGameUiState,
        onQueryChanged: (String) -> Unit,
        onTeamSelected: (br.gov.caixa.loterias.core.domain.contract.betting.model.SportsTeam) -> Unit,
    )

    @Composable
    fun BettingLegacyLoteca(
        state: BettingGameUiState,
        onOutcomeSelected: (Int, br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome) -> Unit,
        onQuantity: () -> Unit,
        onHelp: () -> Unit,
        descriptionBindingKey: Int,
    )
}
