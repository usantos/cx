package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingModeToolbar
import br.gov.caixa.loterias.feature.betting.presentation.screen.BettingLegacyLoteca
import br.gov.caixa.loterias.feature.betting.presentation.screen.BettingLegacyTeams
import br.gov.caixa.loterias.feature.betting.presentation.screen.LotogolBetContent
import br.gov.caixa.loterias.feature.betting.presentation.screen.ManualBetContent
import br.gov.caixa.loterias.feature.betting.presentation.screen.SurpriseBetContent
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTOGOL_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel

@Composable
internal fun BettingGameModalityContent(
    state: BettingGameUiState,
    actions: BettingGameActions,
    viewModel: BettingGameViewModel,
    lotecaDescriptionBindingKey: Int,
    onNumberQuantity: () -> Unit,
    onCloverQuantity: () -> Unit,
    onHelp: () -> Unit,
    onRecurring: () -> Unit,
) {
    val game = requireNotNull(state.game)
    if (game.modality.id == LOTECA_ID) {
        BettingLegacyLoteca(state, viewModel, onQuantity = {
            onNumberQuantity()
        }, onHelp = { onHelp() }, descriptionBindingKey = lotecaDescriptionBindingKey)
    } else if (game.modality.id == LOTOGOL_ID) {
        LotogolBetContent(state, actions)
    } else {
        BettingModeToolbar(
            state = state,
            onManual = { viewModel.selectMode(BetEntryMode.MANUAL) },
            onSurprise = { viewModel.selectMode(BetEntryMode.SURPRISE) },
            onNumberQuantity = {
                if (state.game?.modality?.id == MAIS_MILIONARIA_ID && state.selectionStep == BetSelectionStep.COMPLEMENT &&
                    state.mode == BetEntryMode.MANUAL
                ) {
                    onCloverQuantity()
                } else {
                    onNumberQuantity()
                }
            },
            onRecurring = {
                if (state.game?.contestType.equals("ESPECIAL", ignoreCase = true)) {
                    viewModel.warnSpecialRecurringUnavailable()
                } else {
                    onRecurring()
                }
            },
            onTutorial = { onHelp() }
        )
        if (state.mode == BetEntryMode.MANUAL) {
            ManualBetContent(state, actions, teamContent = { TeamOfTheHeartContent(state, viewModel) })
        } else {
            SurpriseBetContent(state, actions, teamContent = { TeamOfTheHeartContent(state, viewModel) })
        }
    }
}

@Composable
internal fun TeamOfTheHeartContent(state: BettingGameUiState, viewModel: BettingGameViewModel) = BettingLegacyTeams(state, viewModel)
