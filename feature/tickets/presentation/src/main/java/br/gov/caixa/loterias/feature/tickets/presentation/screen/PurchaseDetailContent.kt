package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSEmptyState
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSPurchaseFilterIcon
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSPurchaseHelpIcon
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.isPendingPix
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetDataCard
import br.gov.caixa.loterias.feature.tickets.presentation.components.ErrorData
import br.gov.caixa.loterias.feature.tickets.presentation.components.LoadingData
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
internal fun PurchaseDetailContent(state: TicketsUiState, purchase: Purchase, onBack: () -> Unit, actions: PurchaseDetailActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_apostas_da_compra_37ee79),
        contentUsesTruncatedToolbarGuideline = true,

        onBack = onBack,
        containerColor = CaixaDSColorTokens.White,
        topBarActionWidth = if (actions.openHelp != null) 96.dp else 48.dp,
        topBarAction = {
            PurchaseDetailToolbarActions(state, actions)
        },
        modifier = Modifier.uiId("tickets.0212")
    ) { padding ->
        when {
            state.isLoading || state.selectedPurchase?.id != purchase.id -> LoadingData((Modifier.padding(padding)).uiId("tickets.0213"))

            state.errorMessage != null -> ErrorData(state.errorMessage.orEmpty(), {
                actions.loadPurchase(purchase)
            }, (Modifier.padding(padding)).uiId("tickets.0214"))

            else -> androidx.compose.foundation.lazy.LazyColumn(
                (Modifier.fillMaxSize().padding(padding)).uiId("tickets.0215"),
                contentPadding = PaddingValues(0.dp)
            ) {
                val detail = state.selectedPurchase
                val displayedBets = state.bets.filter(state.purchaseBetFilters::matches)
                item {
                    PurchaseLegacyHeader(
                        purchase = detail,
                        canFavorite = state.bets.none(ConfirmedBet::poolShare),
                        onFavorite = actions.openFavoriteDialog
                    )
                }
                if (detail.isPendingPix()) {
                    item {
                        PendingPurchasePix(
                            detail,
                            state.checkingPixPurchase,
                            actions.pendingPixSecondsRemaining,
                            actions.checkPendingPixStatus
                        )
                    }
                }
                if (displayedBets.isEmpty()) {
                    item {
                        CaixaDSEmptyState(
                            title = uiStrings.getString(LocalizedR.string.ui_nenhuma_aposta_encontrada_60307f),
                            message = uiStrings.getString(LocalizedR.string.ui_revise_os_filtros_aplicados_973628),
                            actionLabel = uiStrings.getString(LocalizedR.string.ui_limpar_filtros_fade92),
                            onAction = actions.clearPurchaseBetFilters,
                            modifier = Modifier.uiId("tickets.0216")
                        )
                    }
                } else {
                    items(displayedBets.size, key = { displayedBets[it].id }) { index ->
                        val bet = displayedBets[index]
                        if (bet.usesLegacyNumericRow()) {
                            LegacyNumericPurchaseBetRow(bet)
                        } else {
                            BetDataCard(bet) {}
                        }
                    }
                }
            }
        }
    }
}

internal data class PurchaseDetailActions(
    val openPurchaseBetFilters: () -> Unit,
    val loadPurchase: (Purchase) -> Unit,
    val openFavoriteDialog: () -> Unit,
    val pendingPixSecondsRemaining: () -> Long,
    val checkPendingPixStatus: () -> Unit,
    val clearPurchaseBetFilters: () -> Unit,
    val openHelp: (() -> Unit)? = null,
)

@Composable
private fun PurchaseLegacyHeader(purchase: Purchase, canFavorite: Boolean, onFavorite: () -> Unit) {
    LegacyPurchaseDetailHeader(purchase, purchaseStatusColor(purchase), canFavorite, onFavorite)
}

@Composable
private fun purchaseStatusColor(purchase: Purchase) = when (purchase.statusId) {
    6L, 8L, 13L, 14L -> CaixaDSColorTokens.PurchaseStatus
    3L, 4L, 5L, 11L, 12L -> CaixaDSColorTokens.NotificationDeleteAction
    else -> CaixaDSColorTokens.ReservationWarning
}

@Composable
private fun PurchaseDetailToolbarActions(state: TicketsUiState, actions: PurchaseDetailActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row {
        Box {
            IconButton(onClick = actions.openPurchaseBetFilters, modifier = Modifier.uiId("tickets.0217")) {
                Icon(
                    CaixaDSPurchaseFilterIcon,
                    contentDescription = uiStrings.getString(LocalizedR.string.ui_filtrar_06b52c),
                    tint = CaixaDSColorTokens.White,
                    modifier = Modifier.uiId("tickets.0218")
                )
            }
            if (state.purchaseBetFilters.activeCount > 0) {
                Box(
                    Modifier.align(Alignment.TopEnd).size(16.dp)
                        .background(CaixaDS.colors.error, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        state.purchaseBetFilters.activeCount.toString(),
                        color = CaixaDSColorTokens.White,
                        style = CaixaDS.typography.labelSmall,
                        modifier = Modifier.uiId("tickets.0219")
                    )
                }
            }
        }
        actions.openHelp?.let { openHelp ->
            IconButton(onClick = openHelp, modifier = Modifier.uiId("tickets.0220")) {
                CaixaDSPurchaseHelpIcon(
                    (Modifier.size(24.dp)).uiId("tickets.0221"),
                    contentDescription = uiStrings.getString(LocalizedR.string.ui_ajuda_fe0add)
                )
            }
        }
    }
}

private fun ConfirmedBet.usesLegacyNumericRow(): Boolean = numbers.isNotEmpty() && numberColumns.size <= 1 &&
    clovers.isEmpty() && !poolShare && lotecaSelections.isEmpty() &&
    lotogolSelections.isEmpty() && favoriteTeam == null && luckyMonth == null &&
    !combo && !mirror && !exchange && exchangeBetId == null && !surprise
