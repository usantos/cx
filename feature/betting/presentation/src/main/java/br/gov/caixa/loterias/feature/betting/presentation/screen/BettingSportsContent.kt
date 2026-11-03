package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingUiEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel

@Composable
fun BettingLegacyTeams(state: BettingGameUiState, viewModel: BettingGameViewModel) {
    FeatureModuleContent(
        module = AppModule.BETTING,
        entryType = BettingUiEntry::class.java,
        embedded = true,
        preserveActive = true
    ) { entry ->
        entry.BettingLegacyTeams(state, viewModel::updateTeamQuery, viewModel::selectTeam)
    }
}

@Composable
fun BettingLegacyLoteca(
    state: BettingGameUiState,
    viewModel: BettingGameViewModel,
    onQuantity: () -> Unit,
    onHelp: () -> Unit,
    descriptionBindingKey: Int = 0,
) {
    FeatureModuleContent(
        module = AppModule.BETTING,
        entryType = BettingUiEntry::class.java,
        embedded = true,
        preserveActive = true
    ) { entry ->
        entry.BettingLegacyLoteca(state, viewModel::toggleLotecaOutcome, onQuantity, onHelp, descriptionBindingKey)
    }
}
