package br.gov.caixa.loterias.feature.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.text.font.FontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSBetTypeIcons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import br.gov.caixa.loterias.core.designsystem.CaixaDSCurrentCartDeleteIcon
import br.gov.caixa.loterias.core.designsystem.CaixaDSCurrentCartIcon
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSCurrentCartColors
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.domain.cart.*
import java.math.BigDecimal
import java.time.Instant
import java.util.Locale

internal val CurrentCartBlue = CaixaDSCurrentCartColors.Primary
private val CurrentCartText = CaixaDSCurrentCartColors.Text
private val CurrentCartLine = CaixaDSCurrentCartColors.Divider

internal fun cartText(size: Int = 14, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = CaixaDSFontFamily, fontSize = size.sp, fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true),
)

/** activity_carrinho's elevated white footer; its zero summary also remains present. */
@Composable
internal fun CurrentCartFooter(cart: ShoppingCart?, paymentEnabled: Boolean, onAdd: () -> Unit, onPay: () -> Unit) {
    Surface(color = CaixaDSColorTokens.White, shadowElevation = 12.dp) {
        Column(Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 24.dp)) {
            OutlinedButton(onClick = onAdd, modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(4.dp), border = androidx.compose.foundation.BorderStroke(1.dp, CaixaDSCurrentCartColors.ActionBorder),
                contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = CurrentCartBlue)) {
                Text("Adicionar nova aposta", style = cartText(16, FontWeight.Bold))
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onPay, enabled = paymentEnabled, modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(4.dp), contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CurrentCartBlue, contentColor = CaixaDSColorTokens.White,
                    disabledContainerColor = CaixaDSCurrentCartColors.DisabledAction, disabledContentColor = CaixaDSColorTokens.White)) {
                Text("Ir para pagamento", style = cartText(16, FontWeight.Bold))
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth().height(43.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(0.6f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total do carrinho", style = cartText(), color = CurrentCartText)
                    Text(formatCartAmount(cart?.total), style = cartText(14, FontWeight.SemiBold), color = CurrentCartText)
                }
                Box(Modifier.width(1.dp).fillMaxHeight().background(CaixaDSCurrentCartColors.SummaryDivider))
                BoxWithConstraints(Modifier.weight(0.4f).fillMaxHeight()) {
                    Box(Modifier.padding(start = (maxWidth - 41.dp) * 0.35f).align(Alignment.CenterStart).size(41.dp, 32.dp)) {
                        Icon(CaixaDSCurrentCartIcon, "Carrinho", Modifier.fillMaxSize(), tint = Color.Unspecified)
                        Box(Modifier.align(Alignment.TopEnd).size(15.dp).background(CaixaDSCurrentCartColors.Badge, CircleShape),
                            contentAlignment = Alignment.Center) {
                            Text((cart?.items?.size ?: 0).toString(), color = CurrentCartText,
                                style = cartText(12, FontWeight.SemiBold).copy(platformStyle = PlatformTextStyle(includeFontPadding = false)))
                        }
                    }
                }
            }
        }
    }
}

/** item_aposta_carrinho uses a 75/25 split and six numbers on each regular-text line. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CurrentCartRow(item: CartItem, now: Instant, onDelete: () -> Unit, onDetail: (() -> Unit)?) {
    if (item.kind != CartItemKind.INDIVIDUAL_BET) {
        CurrentCartGroupedRow(item, now, onDelete, onDetail)
        return
    }
    Column(Modifier.fillMaxWidth()) {
        // CX anchors the following rows to the TextView, while the 25dp delete icon overlays it.
        Box(Modifier.fillMaxWidth()) {
            Text(currentCartModalityName(item), Modifier.fillMaxWidth().padding(end = 33.dp),
                style = cartText(16, FontWeight.Bold), color = currentCartModalityColor(item.modalityCode))
            Box(Modifier.matchParentSize()) {
                Box(Modifier.align(Alignment.TopEnd)) { CurrentCartDelete(onDelete, 25) }
            }
        }
        val superSeven = item.modalityCode.uppercase(Locale.ROOT) in setOf("SUPER_7", "SUPER_SETE", "SUPERSETE", "SUPER-SETE")
        val cloverText = when {
            item.modalityCode.uppercase(Locale.ROOT) in setOf("MAIS_MILIONARIA", "MAISMILIONARIA", "+MILIONARIA") && CartItemBadge.SURPRISE in item.badges -> "trevos: XX - XX"
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
            addAll(item.gameRows)
        }
        val indicators = listOf(CartItemBadge.MIRROR, CartItemBadge.SURPRISE, CartItemBadge.RECURRING).filter { it in item.badges }
        Layout(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), content = {
            Column {
                if (superSeven) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(7) { column ->
                            Column(Modifier.width(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text((column + 1).toString(), Modifier.fillMaxWidth().background(CaixaDSCurrentCartColors.Text),
                                    color = CaixaDSColorTokens.White, style = cartText(12, FontWeight.Bold), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                val numbers = if (CartItemBadge.SURPRISE in item.badges) listOf("XX")
                                    else item.selectedNumbers.getOrNull(column).orEmpty().map(Int::toString)
                                numbers.forEach { Text(it, style = cartText(12), color = CurrentCartText) }
                            }
                        }
                    }
                } else {
                    val lines = if (CartItemBadge.SURPRISE in item.badges) listOf(List(6) { "XX" }.joinToString(" - "))
                    else item.selectedNumbers.flatten().chunked(6).map { numbers -> numbers.joinToString(" - ") { it.toString().padStart(2, '0') } }
                    lines.forEach { Text(it, style = cartText().copy(letterSpacing = 1.4.sp), color = CurrentCartText) }
                    if (lines.isEmpty() && item.description.isNotBlank()) Text(item.description, style = cartText(), color = CurrentCartText)
                }
            }
            Text(item.contestNumber?.let { "Conc. $it" }.orEmpty(), style = cartText(), color = CurrentCartText, maxLines = 1)
            Text(formatCartAmount(item.amount), style = cartText().copy(platformStyle = PlatformTextStyle(includeFontPadding = false)), color = CurrentCartText)
            Column {
                extras.forEach { Text(it, style = cartText().copy(platformStyle = PlatformTextStyle(includeFontPadding = false)), color = CurrentCartText) }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                indicators.forEach { badge ->
                    val icon = when (badge) {
                        CartItemBadge.MIRROR -> CaixaDSBetTypeIcons.Mirror
                        CartItemBadge.SURPRISE -> CaixaDSBetTypeIcons.Surprise
                        else -> CaixaDSBetTypeIcons.Recurring
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, null, Modifier.size(icon.defaultWidth, icon.defaultHeight), tint = CurrentCartText)
                        Spacer(Modifier.width(6.dp))
                        Text(when (badge) {
                            CartItemBadge.MIRROR -> "Espelho"
                            CartItemBadge.SURPRISE -> "Surpresinha"
                            else -> "Teimosinha"
                        }, style = cartText(14, FontWeight.Bold).copy(fontFamily = FontFamily.Default), color = CurrentCartText)
                    }
                }
            }
        }) { measurables, constraints ->
            // Match CX's independent ConstraintLayout anchors; the price column cannot push extras or indicators down.
            val split = (constraints.maxWidth * 0.75f).toInt()
            val left = constraints.copy(minWidth = 0, minHeight = 0, maxWidth = split - 8.dp.roundToPx())
            val right = constraints.copy(minWidth = 0, minHeight = 0, maxWidth = constraints.maxWidth - split)
            val numbers = measurables[0].measure(left)
            val contest = measurables[1].measure(right)
            val price = measurables[2].measure(right)
            val extra = measurables[3].measure(left.copy(maxWidth = split))
            val badges = measurables[4].measure(left.copy(maxWidth = split))
            val extraY = numbers.height
            val priceY = if (extras.isNotEmpty()) extraY + extra[FirstBaseline] - price[FirstBaseline]
                else contest.height + 12.dp.roundToPx()
            val badgeY = when {
                extras.isNotEmpty() -> extraY + extra.height
                superSeven || (CartItemBadge.SURPRISE !in item.badges && item.selectedNumbers.flatten().size > 6) -> numbers.height
                else -> priceY + (price.height - badges.height) / 2
            }
            val height = maxOf(numbers.height, contest.height, priceY + price.height,
                extraY + extra.height, if (indicators.isNotEmpty()) badgeY + badges.height else 0)
            layout(constraints.maxWidth, height) {
                numbers.placeRelative(0, 0)
                contest.placeRelative(constraints.maxWidth - contest.width, 0)
                price.placeRelative(constraints.maxWidth - price.width, priceY)
                extra.placeRelative(0, extraY)
                badges.placeRelative(0, badgeY)
            }
        }
        CurrentCartDivider()
    }
}

@Composable
private fun CurrentCartGroupedRow(item: CartItem, now: Instant, onDelete: () -> Unit, onDetail: (() -> Unit)?) {
    val share = item.poolShare
    val expiry = share?.let { poolExpiry(it.expiresAt, now) }
    Column(Modifier.fillMaxWidth().padding(top = if (share != null) 16.dp else 0.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(if (share != null) currentCartModalityName(item.copy(title = item.title.substringBefore(" • "))) else item.title,
                Modifier.weight(1f), style = cartText(14, if (share != null) FontWeight.Bold else FontWeight.SemiBold),
                color = if (share != null) currentCartModalityColor(item.modalityCode) else CurrentCartText)
            CurrentCartDelete(onDelete, if (share != null) 24 else 32)
        }
        if (share != null) {
            Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text("cota reservada nº ${share.reservedShare} de ${share.totalShares}",
                    Modifier.weight(1f).alignByBaseline(), style = cartText(), color = CurrentCartText)
                item.contestNumber?.let {
                    Text("Conc. $it", Modifier.alignByBaseline(), style = cartText(), color = CurrentCartText)
                }
            }
            Text(formatCartAmount(item.amount), Modifier.align(Alignment.End).padding(top = 12.dp),
                style = cartText(), color = CurrentCartText)
        } else {
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text("${item.comboBetCount} apostas", style = cartText(), color = CurrentCartText)
                    item.contestNumber?.let { Text("Conc. $it", style = cartText(), color = CurrentCartText) }
                }
                Text(formatCartAmount(item.amount), style = cartText(), color = CurrentCartText)
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = if (share != null) 16.dp else 8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (expiry != null) {
                Text("Expira em", style = cartText(), color = CurrentCartBlue)
                Icon(CaixaDSIcons.Time, null, Modifier.padding(start = 4.dp).size(16.dp), tint = CurrentCartBlue)
                Text(expiry.remainingLabel, Modifier.padding(start = 4.dp), style = cartText(14, FontWeight.Bold), color = CurrentCartBlue)
            }
            Spacer(Modifier.weight(1f))
            onDetail?.let {
                Row(Modifier.clickable(onClick = it), verticalAlignment = Alignment.CenterVertically) {
                    Text("mais detalhes", style = cartText(14, FontWeight.SemiBold), color = CurrentCartBlue)
                    Icon(CaixaDSIcons.Forward, null, Modifier.size(16.dp), tint = CurrentCartBlue)
                }
            }
        }
        CurrentCartDivider()
    }
}

@Composable
private fun CurrentCartDelete(onDelete: () -> Unit, size: Int) {
    IconButton(onClick = onDelete, modifier = Modifier.size(size.dp)) {
        Icon(CaixaDSCurrentCartDeleteIcon, "Excluir", Modifier.size(20.dp), tint = CaixaDSCurrentCartColors.Delete)
    }
}

@Composable
private fun CurrentCartDivider() {
    Spacer(Modifier.height(14.dp))
    Box(Modifier.fillMaxWidth().height(1.dp).background(CurrentCartLine))
    Spacer(Modifier.height(12.dp))
}

internal fun currentCartModalityName(item: CartItem): String {
    val title = item.title.lowercase(Locale.ROOT)
    // Special contest descriptions supplied by the backend retain their own labels.
    if (listOf("virada", "independência", "são joão", "páscoa", "especial").any(title::contains)) return title
    return when (item.modalityCode.uppercase(Locale.ROOT)) {
        "MEGA_SENA", "MEGASENA", "MEGA-SENA" -> "mega-sena"
        "LOTOFACIL", "LOTOFÁCIL" -> "lotofácil"
        "MAIS_MILIONARIA", "MAISMILIONARIA", "+MILIONARIA" -> "+milionária"
        "DUPLA_SENA", "DUPLASENA", "DUPLA-SENA" -> "dupla sena"
        "DIA_DE_SORTE", "DIADESORTE", "DIA-DE-SORTE" -> "dia de sorte"
        "SUPER_7", "SUPER_SETE", "SUPERSETE", "SUPER-SETE" -> "super sete"
        "QUINA" -> "quina"
        "LOTOMANIA" -> "lotomania"
        "TIMEMANIA" -> "timemania"
        "LOTECA" -> "loteca"
        else -> title
    }
}

private fun currentCartModalityColor(code: String): Color = when (code.uppercase(Locale.ROOT)) {
    "MEGA_SENA", "MEGASENA", "MEGA-SENA" -> CaixaDSCurrentCartColors.MegaSena
    "LOTOFACIL", "LOTOFÁCIL" -> CaixaDSCurrentCartColors.Lotofacil
    "QUINA" -> CaixaDSCurrentCartColors.Quina
    "LOTOMANIA" -> CaixaDSCurrentCartColors.Lotomania
    "TIMEMANIA" -> CaixaDSCurrentCartColors.Timemania
    "DUPLA_SENA", "DUPLASENA", "DUPLA-SENA" -> CaixaDSCurrentCartColors.DuplaSena
    "DIA_DE_SORTE", "DIADESORTE", "DIA-DE-SORTE" -> CaixaDSCurrentCartColors.DiaDeSorte
    "SUPER_7", "SUPER_SETE", "SUPERSETE", "SUPER-SETE" -> CaixaDSCurrentCartColors.SuperSete
    "MAIS_MILIONARIA", "MAISMILIONARIA", "+MILIONARIA" -> CaixaDSCurrentCartColors.MaisMilionaria
    "LOTECA" -> CaixaDSCurrentCartColors.Loteca
    else -> CurrentCartBlue
}
