package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSAlertDialog as AlertDialog
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.layout.loteriasCaixaDSModalWidth
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
internal fun PurchaseBetFiltersDialog(state: TicketsUiState, actions: PurchaseBetFiltersActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    AlertDialog(
        modifier = (Modifier.loteriasCaixaDSModalWidth()).uiId("tickets.0053"),
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
