package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import br.gov.caixa.loterias.feature.marketplace.ui.R as DeliveryR

@Composable
fun DetailPurchaseQuantity(state: MarketplaceDetailUiState, decrease: () -> Unit, increase: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val ink = LoteriasCaixaDSColorTokens.MarketplaceFooterText
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp).padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_quantidade_de_cotas_ddb3fd),
            (Modifier.weight(1f)).uiId("pool.0060"),
            style = detailText(PURCHASE_QUANTITY_LABEL_SIZE),
            color = ink
        )
        DetailQuantityButton("-", uiStrings.getString(DeliveryR.string.ui_diminuir_cotas_74fb74), state.canDecrease, decrease)
        Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
            Text(
                state.selectedShares.toString(),
                style = detailText(PURCHASE_QUANTITY_VALUE_SIZE),
                color = ink,
                modifier = Modifier.uiId("pool.0061")
            )
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(1.dp).background(ink))
        }
        DetailQuantityButton("+", uiStrings.getString(DeliveryR.string.ui_aumentar_cotas_203e21), state.canIncrease, increase)
    }
}

@Composable
fun DetailQuantityButton(label: String, description: String, enabled: Boolean, action: () -> Unit) {
    val largeFont = LocalDensity.current.fontScale > 1f
    Box(
        Modifier.then(if (largeFont) Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp) else Modifier.size(48.dp))
            .uiId("pool.0070").clickable(enabled = enabled, role = Role.Button, onClick = action)
            .semantics { contentDescription = description }.padding(8.dp)
            .background(
                if (enabled) LoteriasCaixaDSColorTokens.MarketplaceFooterText else LoteriasCaixaDSColorTokens.MarketplaceFooterDisabled,
                RoundedCornerShape(3.dp)
            ),
        Alignment.Center
    ) {
        Text(
            label,
            style = detailText(PURCHASE_QUANTITY_BUTTON_SIZE),
            color = LoteriasCaixaDSColorTokens.White,
            modifier = Modifier.uiId("pool.0071")
        )
    }
}

private const val PURCHASE_QUANTITY_LABEL_SIZE = 14
private const val PURCHASE_QUANTITY_VALUE_SIZE = 18
private const val PURCHASE_QUANTITY_BUTTON_SIZE = 16
