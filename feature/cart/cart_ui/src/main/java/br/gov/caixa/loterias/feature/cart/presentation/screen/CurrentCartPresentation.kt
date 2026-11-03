package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemBadge
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.presentation.components.CartLotecaContent
import br.gov.caixa.loterias.feature.cart.presentation.components.cartWholeBlockReadout
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartNativeText
import br.gov.caixa.loterias.feature.cart.presentation.mapper.currentCartModalityName
import br.gov.caixa.loterias.feature.cart.presentation.mapper.formatCartAmount
import java.time.Instant
import java.util.Locale

@Composable
fun CurrentCartRowDelivered(item: CartItem, now: Instant, onDelete: () -> Unit, onDetail: (() -> Unit)?) {
    if (item.kind != CartItemKind.INDIVIDUAL_BET) {
        CurrentCartGroupedRowDelivered(item, now, onDelete, onDetail)
        return
    }
    Column(Modifier.fillMaxWidth().cartWholeBlockReadout(item, now)) {
        // A CX ancora as linhas seguintes no TextView, enquanto o ícone de exclusão de 25dp se sobrepõe a ele.
        Box(Modifier.fillMaxWidth()) {
            Text(
                currentCartModalityName(item),
                (Modifier.fillMaxWidth().padding(end = 33.dp)).uiId("cart.0112"),
                style = cartNativeText(16, FontWeight.Bold),
                color = currentCartModalityColor(item.modalityCode)
            )
            Box(Modifier.matchParentSize()) {
                Box(Modifier.align(Alignment.TopEnd)) { CurrentCartDeleteDelivered(onDelete, 25) }
            }
        }
        val loteca = item.modalityCode.equals("LOTECA", ignoreCase = true)
        val superSeven = item.modalityCode.uppercase(Locale.ROOT) in setOf("SUPER_7", "SUPER_SETE", "SUPERSETE", "SUPER-SETE")
        val cloverText = when {
            item.modalityCode.uppercase(Locale.ROOT) in setOf("MAIS_MILIONARIA", "MAISMILIONARIA", "+MILIONARIA") &&
                CartItemBadge.SURPRISE in item.badges -> "trevos: XX - XX"

            item.selectedClovers.isNotEmpty() -> "trevos: ${item.selectedClovers.joinToString(" - ") { it.toString().padStart(2, '0') }}"

            else -> null
        }
        val extras = buildList {
            cloverText?.let(::add)
            item.complementaryLabel
                ?.takeIf { it.isNotBlank() && (cloverText == null || !it.startsWith("Trevos:", ignoreCase = true)) }
                ?.let { label ->
                    val nativeExtra = label.startsWith("Mês da sorte:", ignoreCase = true) ||
                        label.startsWith("Time do coração:", ignoreCase = true)
                    add(if (nativeExtra) label.lowercase(Locale.ROOT) else label)
                }
        }
        val indicators = listOf(CartItemBadge.MIRROR, CartItemBadge.SURPRISE, CartItemBadge.RECURRING).filter { it in item.badges }
        Layout(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), content = {
            CurrentCartNumbersDelivered(item, loteca, superSeven)
            Text(
                item.contestNumber?.let { "Conc. $it" }.orEmpty(),
                style = cartNativeText(),
                color = CurrentCartText,
                maxLines = 1,
                modifier = Modifier.uiId("cart.0117")
            )
            Text(
                formatCartAmount(item.amount),
                style = cartNativeText().copy(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                color = CurrentCartText,
                modifier = Modifier.uiId("cart.0118")
            )
            Column {
                extras.forEach {
                    Text(
                        it,
                        style = cartNativeText().copy(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                        color = CurrentCartText,
                        modifier = Modifier.uiId("cart.0119")
                    )
                }
            }
            CurrentCartBadgesDelivered(indicators)
        }, measurePolicy = currentCartIndividualMeasurePolicy(item, superSeven, extras.isNotEmpty(), indicators.isNotEmpty()))
        if (loteca) CartLotecaContent(item.lotecaSelections, groupedReadout = true)
        CurrentCartDividerDelivered()
    }
}
