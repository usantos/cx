package br.gov.caixa.loterias.feature.betting.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingBettingSportsContentEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome

internal class BettingBettingSportsContentRenderer : BettingBettingSportsContentEntry {
    @Composable
    override fun BettingLegacyTeams(
        state: BettingGameUiState,
        onQueryChanged: (String) -> Unit,
        onTeamSelected: (br.gov.caixa.loterias.core.domain.contract.betting.model.SportsTeam) -> Unit,
    ) {
        br.gov.caixa.loterias.feature.betting.presentation.screen.BettingLegacyTeamsDelivered(state, onQueryChanged, onTeamSelected)
    }

    @Composable
    override fun BettingLegacyLoteca(
        state: BettingGameUiState,
        onOutcomeSelected: (Int, br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome) -> Unit,
        onQuantity: () -> Unit,
        onHelp: () -> Unit,
        descriptionBindingKey: Int,
    ) {
        br.gov.caixa.loterias.feature.betting.presentation.screen.BettingLegacyLotecaDelivered(
            state,
            onOutcomeSelected,
            onQuantity,
            onHelp,
            descriptionBindingKey
        )
    }
}
