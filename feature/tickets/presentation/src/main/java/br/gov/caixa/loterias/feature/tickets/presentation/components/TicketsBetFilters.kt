package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSAlertDialog as AlertDialog
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSHorizontalDivider
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSNumberPicker
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSModalWidth
import br.gov.caixa.loterias.feature.tickets.R
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketPeriod
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
internal fun BetFiltersDialog(state: TicketsUiState, actions: BetFiltersActions) {
    val configuration = state.betConfiguration ?: return
    AlertDialog(
        modifier = (Modifier.caixaDSModalWidth()).uiId("tickets.0113"),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = actions.closeBetFilters,
        title = { Text(stringResource(R.string.tickets_bet_filters_title), modifier = Modifier.uiId("tickets.0114")) },
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
            ) { Text(stringResource(R.string.tickets_filter_apply), modifier = Modifier.uiId("tickets.0117")) }
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
private fun BetFilterWheel(label: String, options: List<TicketFilterOption>, selected: Long, onSelect: (Long) -> Unit) {
    CaixaDSNumberPicker(
        label = label,
        options = options.map { it.description },
        selectedIndex = options.indexOfFirst { it.value == selected },
        onSelect = { index -> options.getOrNull(index)?.let { onSelect(it.value) } },
        modifier = Modifier.uiId("tickets.0122")
    )
}

@Composable
private fun BetFilterGroup(title: String, content: @Composable () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var expanded by rememberSaveable(title) { mutableStateOf(false) }
    val description = stringResource(
        if (expanded) R.string.tickets_filter_expanded else R.string.tickets_filter_collapsed
    )
    Column {
        Row(
            Modifier.fillMaxWidth().semantics { stateDescription = description }
                .uiId("tickets.0123").clickable { expanded = !expanded }.padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, modifier = Modifier.uiId("tickets.0124"))
            Text(
                if (expanded) {
                    uiStrings.getString(
                        LocalizedR.string.ui_texto_afba24
                    )
                } else {
                    uiStrings.getString(LocalizedR.string.ui_texto_a318c2)
                },
                modifier = Modifier.uiId("tickets.0125")
            )
        }
        if (expanded) content()
        CaixaDSHorizontalDivider(modifier = Modifier.uiId("tickets.0126"))
    }
}

internal data class BetFiltersActions(
    val closeBetFilters: () -> Unit,
    val selectBetPeriod: (TicketPeriod) -> Unit,
    val selectBetStatus: (Long) -> Unit,
    val selectBetOrdering: (Long) -> Unit,
    val selectBetType: (Long) -> Unit,
    val selectContestType: (Long) -> Unit,
    val selectModality: (Int) -> Unit,
    val toggleSurprise: () -> Unit,
    val toggleRecurring: () -> Unit,
    val toggleCombo: () -> Unit,
    val applyBetFilters: () -> Unit,
    val clearBetFilters: () -> Unit,
)

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
