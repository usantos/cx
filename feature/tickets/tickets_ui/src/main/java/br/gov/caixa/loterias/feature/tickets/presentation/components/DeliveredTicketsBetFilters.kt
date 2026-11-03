package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSAlertDialog as AlertDialog
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.layout.loteriasCaixaDSModalWidth
import br.gov.caixa.loterias.feature.tickets.R
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR

@Composable
internal fun BetFiltersDialogDelivered(state: TicketsUiState, actions: BetFiltersActions) {
    val configuration = state.betConfiguration ?: return
    AlertDialog(
        modifier = (Modifier.loteriasCaixaDSModalWidth()).uiId("tickets.0113"),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = actions.closeBetFilters,
        title = { Text(stringResource(DeliveryR.string.tickets_bet_filters_title), modifier = Modifier.uiId("tickets.0114")) },
        text = {
            Column(
                Modifier.uiId("tickets.0115").verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BetFilterGroup(stringResource(R.string.tickets_filter_bet_type)) {
                    BetFilterWheel(
                        stringResource(R.string.tickets_filter_bet_type),
                        configuration.betTypes,
                        state.betFilters.betType,
                        actions.selectBetType
                    )
                }
                BetModalityFilterOptions(state, actions)
                BetFilterGroup(stringResource(R.string.tickets_filter_contest_type)) {
                    BetFilterWheel(
                        stringResource(R.string.tickets_filter_contest_type),
                        configuration.contestTypes,
                        state.betFilters.contestType,
                        actions.selectContestType
                    )
                }
                BetFilterGroup(stringResource(R.string.tickets_filter_bet_status)) {
                    BetFilterWheel(
                        stringResource(R.string.tickets_filter_bet_status),
                        configuration.statuses,
                        state.betFilters.status,
                        actions.selectBetStatus
                    )
                }
                BetFilterGroup(stringResource(R.string.tickets_filter_ordering)) {
                    BetFilterWheel(
                        stringResource(R.string.tickets_filter_ordering),
                        configuration.orderings,
                        state.betFilters.ordering,
                        actions.selectBetOrdering
                    )
                }
                FilterCheck(stringResource(R.string.tickets_filter_surprise), state.betFilters.surprise, actions.toggleSurprise)
                FilterCheck(stringResource(R.string.tickets_filter_recurring), state.betFilters.recurring, actions.toggleRecurring)
                FilterCheck(stringResource(R.string.tickets_filter_combos), state.betFilters.combo, actions.toggleCombo)
            }
        },
        confirmButton = {
            TextButton(
                onClick = actions.applyBetFilters,
                modifier = Modifier
                    .uiId("tickets.0116")
            ) { Text(stringResource(DeliveryR.string.tickets_filter_apply), modifier = Modifier.uiId("tickets.0117")) }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = actions.clearBetFilters,
                    modifier = Modifier
                        .uiId("tickets.0118")
                ) { Text(stringResource(R.string.tickets_filter_clear), modifier = Modifier.uiId("tickets.0119")) }
                TextButton(
                    onClick = actions.closeBetFilters,
                    modifier = Modifier
                        .uiId("tickets.0120")
                ) {
                    Text(
                        stringResource(R.string.tickets_filter_cancel),
                        modifier = Modifier
                            .uiId("tickets.0121")
                    )
                }
            }
        }
    )
}

@Composable
private fun BetModalityFilterOptions(state: TicketsUiState, actions: BetFiltersActions) {
    BetFilterGroup(stringResource(R.string.tickets_filter_modalities)) {
        FilterRadio(stringResource(R.string.tickets_filter_all), state.betFilters.modality == 0) {
            actions.selectModality(0)
        }
        state.modalities.chunked(2).forEach { modalities ->
            Row(Modifier.fillMaxWidth()) {
                modalities.forEach { modality ->
                    Column(Modifier.weight(1f)) {
                        FilterRadio(modality.name, state.betFilters.modality == modality.id) {
                            actions.selectModality(if (state.betFilters.modality == modality.id) 0 else modality.id)
                        }
                    }
                }
                if (modalities.size == 1) Column(Modifier.weight(1f)) {}
            }
        }
    }
}
