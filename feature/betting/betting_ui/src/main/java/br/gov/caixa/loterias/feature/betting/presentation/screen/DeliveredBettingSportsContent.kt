package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingTeamsContent
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

@Composable
fun BettingLegacyTeamsDelivered(
    state: BettingGameUiState,
    onQueryChanged: (String) -> Unit,
    onTeamSelected: (br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam) -> Unit,
) {
    BettingTeamsContent(state, onQueryChanged, onTeamSelected)
}

@Composable
fun BettingLegacyLotecaDelivered(
    state: BettingGameUiState,
    onOutcomeSelected: (Int, br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome) -> Unit,
    onQuantity: () -> Unit,
    onHelp: () -> Unit,
    descriptionBindingKey: Int = 0,
) {
    BettingLotecaContentDelivered(state, onOutcomeSelected, onQuantity, onHelp, descriptionBindingKey)
}
