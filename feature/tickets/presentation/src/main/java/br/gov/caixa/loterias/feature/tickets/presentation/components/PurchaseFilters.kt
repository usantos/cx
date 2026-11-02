package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
internal fun PurchaseFiltersDialog(state: TicketsUiState, actions: PurchaseFiltersActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    AlertDialog(
        modifier = (Modifier.loteriasCaixaDSModalWidth()).uiId("tickets.0044"),
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
