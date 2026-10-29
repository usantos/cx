package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingSportsCallbacks
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingTeamsContent

@Composable
internal fun BettingLegacyTeams(state: BettingGameUiState, viewModel: BettingGameViewModel) {
    val callbacks = BettingSportsCallbacks(viewModel)
    BettingTeamsContent(state, callbacks.onQueryChanged, callbacks.onTeamSelected)
}

@Composable
internal fun BettingLegacyLoteca(state: BettingGameUiState, viewModel: BettingGameViewModel, onQuantity: () -> Unit, onHelp: () -> Unit, descriptionBindingKey: Int = 0) {
    val callbacks = BettingSportsCallbacks(viewModel)
    BettingLotecaContent(state, callbacks.onOutcomeSelected, onQuantity, onHelp, descriptionBindingKey)
}
