package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSAlertDialog as AlertDialog
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSModalWidth
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
internal fun PurchaseFiltersDialog(state: TicketsUiState, actions: PurchaseFiltersActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    AlertDialog(
        modifier = (Modifier.caixaDSModalWidth()).uiId("tickets.0044"),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = actions.closePurchaseFilters,
        title = { Text(uiStrings.getString(LocalizedR.string.ui_filtrar_compras_752066), modifier = Modifier.uiId("tickets.0045")) },
        text = {
            Column(
                Modifier.uiId("tickets.0046").verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                FilterSection(uiStrings.getString(LocalizedR.string.ui_meio_de_pagamento_83501f)) {
                    FilterRadio(stringResource(LocalizedR.string.format_todos), state.purchaseFilters.paymentMethodId == null) {
                        actions.selectPaymentMethod(null)
                    }
                    state.paymentMethods.forEach { option ->
                        FilterRadio(option.description, state.purchaseFilters.paymentMethodId == option.value) {
                            actions.selectPaymentMethod(option.value)
                        }
                    }
                }
                FilterSection(uiStrings.getString(LocalizedR.string.ui_situacao_cf7d46)) {
                    FilterRadio(stringResource(LocalizedR.string.tickets_filter_all), state.purchaseFilters.statusId == null) {
                        actions.selectPurchaseStatus(null)
                    }
                    state.purchaseStatuses.forEach { option ->
                        FilterRadio(option.description, state.purchaseFilters.statusId == option.value) {
                            actions.selectPurchaseStatus(option.value)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = actions.applyPurchaseFilters, modifier = Modifier.uiId("tickets.0047")) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_aplicar_3a0489),
                    modifier = Modifier.uiId("tickets.0048")
                )
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = actions.clearPurchaseFilters,
                    modifier = Modifier
                        .uiId("tickets.0049")
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_limpar_5585ca),
                        modifier = Modifier
                            .uiId("tickets.0050")
                    )
                }
                TextButton(
                    onClick = actions.closePurchaseFilters,
                    modifier = Modifier
                        .uiId("tickets.0051")
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
                        modifier = Modifier
                            .uiId("tickets.0052")
                    )
                }
            }
        }
    )
}

internal data class PurchaseFiltersActions(
    val closePurchaseFilters: () -> Unit,
    val selectPaymentMethod: (Long?) -> Unit,
    val selectPurchaseStatus: (Long?) -> Unit,
    val applyPurchaseFilters: () -> Unit,
    val clearPurchaseFilters: () -> Unit,
)

@Composable
internal fun PurchaseBetFiltersDialog(state: TicketsUiState, actions: PurchaseBetFiltersActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    AlertDialog(
        modifier = (Modifier.caixaDSModalWidth()).uiId("tickets.0053"),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = actions.closePurchaseBetFilters,
        title = { Text(uiStrings.getString(LocalizedR.string.ui_filtrar_apostas_210e4a), modifier = Modifier.uiId("tickets.0054")) },
        text = {
            PurchaseBetFilterOptions(state, actions)
        },
        confirmButton = {
            TextButton(onClick = actions.applyPurchaseBetFilters, modifier = Modifier.uiId("tickets.0059")) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_aplicar_3a0489),
                    modifier = Modifier.uiId("tickets.0060")
                )
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = actions.clearPurchaseBetFilters,
                    modifier = Modifier
                        .uiId("tickets.0061")
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_limpar_5585ca),
                        modifier = Modifier
                            .uiId("tickets.0062")
                    )
                }
                TextButton(
                    onClick = actions.closePurchaseBetFilters,
                    modifier = Modifier
                        .uiId("tickets.0063")
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
                        modifier = Modifier
                            .uiId("tickets.0064")
                    )
                }
            }
        }
    )
}

internal data class PurchaseBetFiltersActions(
    val closePurchaseBetFilters: () -> Unit,
    val selectPurchaseBetContest: (Int?) -> Unit,
    val selectPurchaseBetStatus: (Long?) -> Unit,
    val selectPurchaseBetModality: (Int?) -> Unit,
    val applyPurchaseBetFilters: () -> Unit,
    val clearPurchaseBetFilters: () -> Unit,
)

@Composable
private fun PurchaseBetFilterOptions(state: TicketsUiState, actions: PurchaseBetFiltersActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.uiId("tickets.0055").verticalScroll(rememberScrollState())) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_concurso_1997f5),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .uiId("tickets.0056")
        )
        FilterRadio(stringResource(LocalizedR.string.format_todos), state.purchaseBetFilters.contest == null) {
            actions.selectPurchaseBetContest(null)
        }
        state.bets.map(ConfirmedBet::contest).filter { it > 0 }.distinct().sorted().forEach { contest ->
            FilterRadio(contest.toString(), state.purchaseBetFilters.contest == contest) {
                actions.selectPurchaseBetContest(contest)
            }
        }
        Text(
            uiStrings.getString(LocalizedR.string.ui_situacao_cf7d46),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .uiId("tickets.0057")
        )
        FilterRadio(stringResource(LocalizedR.string.tickets_filter_all), state.purchaseBetFilters.statusId == null) {
            actions.selectPurchaseBetStatus(null)
        }
        state.bets.distinctBy { it.statusId to it.status }.forEach { bet ->
            FilterRadio(
                bet.status.ifBlank { uiStrings.getString(LocalizedR.string.ui_nao_informada_c6f460) },
                state.purchaseBetFilters.statusId == bet.statusId
            ) {
                actions.selectPurchaseBetStatus(bet.statusId)
            }
        }
        Text(
            uiStrings.getString(LocalizedR.string.ui_modalidade_0121ad),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .uiId("tickets.0058")
        )
        FilterRadio(stringResource(LocalizedR.string.tickets_filter_all), state.purchaseBetFilters.modalityId == null) {
            actions.selectPurchaseBetModality(null)
        }
        state.bets.distinctBy { it.modalityId to it.modality }.forEach { bet ->
            FilterRadio(bet.modality, state.purchaseBetFilters.modalityId == bet.modalityId) {
                actions.selectPurchaseBetModality(bet.modalityId)
            }
        }
    }
}
