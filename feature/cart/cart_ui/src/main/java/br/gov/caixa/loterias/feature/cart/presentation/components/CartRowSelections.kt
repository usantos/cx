package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSSurface as Surface
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItemBadge
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.presentation.mapper.label
import br.gov.caixa.loterias.feature.cart.presentation.mapper.quantityLabel
import br.gov.caixa.loterias.feature.cart.ui.R as DeliveryR

@Composable
internal fun CartRowSelections(item: CartItem, modalityColor: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (item.badges.isNotEmpty()) {
        Row(
            Modifier.fillMaxWidth().uiId("cart.0021").horizontalScroll(rememberScrollState()).padding(top = 7.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item.badges.forEach { CartBadge(it, modalityColor) }
        }
    }
    val rows = item.selectedNumbers.filter(List<Int>::isNotEmpty)
    if (rows.isNotEmpty()) {
        rows.forEach { numbers ->
            Text(
                numbers.joinToString(" - ") { it.toString().padStart(2, '0') },
                (Modifier.padding(top = 8.dp)).uiId("cart.0022"),
                color = modalityColor,
                fontWeight = FontWeight.Bold
            )
        }
    } else if (item.description.isNotBlank()) {
        Text(item.description, (Modifier.padding(top = 7.dp)).uiId("cart.0023"), color = LoteriasCaixaDS.colors.mutedText)
    }
    if (item.selectedClovers.isNotEmpty()) {
        Text(
            uiStrings.getString(
                DeliveryR.string.ui_trevos_1_s_946f5c,
                item.selectedClovers.joinToString(" - ") { it.toString().padStart(2, '0') }
            ),
            (Modifier.padding(top = 5.dp)).uiId("cart.0024"),
            color = modalityColor,
            fontWeight = FontWeight.Bold
        )
    }
    if (item.kind == CartItemKind.INDIVIDUAL_BET) {
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(28.dp)) {
            CartQuantity(
                uiStrings.getString(DeliveryR.string.final_you_bet),
                quantityLabel(
                    item.numberQuantity,
                    uiStrings.getString(DeliveryR.string.ui_numero_84871a),
                    uiStrings.getString(DeliveryR.string.ui_numeros_5b88ef)
                )
            )
            CartQuantity("Para", quantityLabel(item.recurringDraws, "concurso", "concursos"))
        }
    }
}

@Composable
private fun CartQuantity(label: String, value: String) {
    Column {
        Text(label, color = LoteriasCaixaDS.colors.mutedText, fontSize = 12.sp, modifier = Modifier.uiId("cart.0036"))
        Text(
            value,
            color = LoteriasCaixaDS.colors.text,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.uiId("cart.0037")
        )
    }
}

@Composable
private fun CartBadge(badge: CartItemBadge, color: Color) {
    Surface(color = color, shape = RoundedCornerShape(2.dp)) {
        Text(
            badge.label,
            (Modifier.padding(horizontal = 7.dp, vertical = 3.dp)).uiId("cart.0038"),
            color = LoteriasCaixaDSColorTokens.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
