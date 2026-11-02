package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSPurchaseFilterIcon
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSPurchaseHelpIcon
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
internal fun PurchaseDetailToolbarActions(state: TicketsUiState, actions: PurchaseDetailActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row {
        Box {
            IconButton(onClick = actions.openPurchaseBetFilters, modifier = Modifier.uiId("tickets.0217")) {
                Icon(
                    LoteriasCaixaDSPurchaseFilterIcon,
                    contentDescription = uiStrings.getString(LocalizedR.string.ui_filtrar_06b52c),
                    tint = LoteriasCaixaDSColorTokens.White,
                    modifier = Modifier.uiId("tickets.0218")
                )
            }
            if (state.purchaseBetFilters.activeCount > 0) {
                Box(
                    Modifier.align(Alignment.TopEnd).size(16.dp)
                        .background(LoteriasCaixaDS.colors.error, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        state.purchaseBetFilters.activeCount.toString(),
                        color = LoteriasCaixaDSColorTokens.White,
                        style = LoteriasCaixaDS.typography.labelSmall,
                        modifier = Modifier.uiId("tickets.0219")
                    )
                }
            }
        }
        actions.openHelp?.let { openHelp ->
            IconButton(onClick = openHelp, modifier = Modifier.uiId("tickets.0220")) {
                LoteriasCaixaDSPurchaseHelpIcon(
                    (Modifier.size(24.dp)).uiId("tickets.0221"),
                    contentDescription = uiStrings.getString(LocalizedR.string.ui_ajuda_fe0add)
                )
            }
        }
    }
}
