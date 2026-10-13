package br.gov.caixa.loterias.feature.tickets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import br.gov.caixa.loterias.core.designsystem.*
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet
import br.gov.caixa.loterias.core.domain.tickets.Purchase
import java.text.NumberFormat
import java.util.Locale

private fun purchaseText(size: Float, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = CaixaDSFontFamily, fontSize = size.sp, fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true),
)

/** Packed list_group_compras followed by compra_processada_layout; no financial behavior. */
@Composable
internal fun LegacyPurchaseDetailHeader(purchase: Purchase, statusColor: Color, canFavorite: Boolean, onFavorite: () -> Unit) {
    val dataColor = CaixaDSColorTokens.LegacyNotificationIntro
    Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White)) {
        // The packed group is constrained only at its top. Its declared bottom
        // margin contributes no space before compra_processada_layout.
        Column(Modifier.fillMaxWidth().padding(start = 8.dp, end = 25.dp, top = 16.dp)) {
            LegacyPurchaseStatusRow(purchase.status, statusColor, canFavorite, onFavorite)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Data: ${purchase.date}", color = dataColor, style = purchaseText(14f))
                Text(NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(purchase.total),
                    color = CaixaDSColorTokens.LegacyBetAction, style = purchaseText(14.2f))
            }
            if (purchase.finalizedTime.isNotBlank()) Text("Hora: ${purchase.finalizedTime}", color = dataColor, style = purchaseText(14f))
            if (purchase.paymentMethodId == 6L && purchase.statusId in setOf(6L, 8L, 13L, 14L) && purchase.endToEndId.isNotBlank()) {
                Text("ID do Pagamento:\n${purchase.endToEndId}", color = dataColor, style = purchaseText(14f))
            }
        }
        if (purchase.paymentMethod.isNotBlank()) Text(buildAnnotatedString {
            append("Meio de pagamento: ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(purchase.paymentMethod) }
        }, Modifier.padding(start = 8.dp, top = 16.dp, bottom = 9.dp), color = dataColor, style = purchaseText(14f))
    }
}

/** list_group_compras' wrap-content children, then LinearLayout's .8/.1 weights. */
@Composable
private fun LegacyPurchaseStatusRow(status: String, statusColor: Color, canFavorite: Boolean, onFavorite: () -> Unit) {
    Layout(modifier = Modifier.fillMaxWidth(), content = {
        Text(buildAnnotatedString {
            append("Situação: ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(status.ifBlank { "Não informada" }) }
        }, color = statusColor, style = purchaseText(14.2f))
        if (canFavorite) Box(Modifier.fillMaxSize().clickable(onClick = onFavorite), contentAlignment = Alignment.Center) {
            Icon(CaixaDSLegacyPurchaseFavoriteIcon, "Salvar carrinho favorito",
                Modifier.size(24.dp), tint = CaixaDSColorTokens.LegacyBetLink)
        }
        Text("Total:", color = CaixaDSColorTokens.LegacyBetAction,
            style = purchaseText(14.4f, FontWeight.Bold), textAlign = TextAlign.End)
    }) { children, constraints ->
        val totalIndex = if (canFavorite) 2 else 1
        val width = constraints.maxWidth
        val favoriteWidth = if (canFavorite) 48.dp.roundToPx() else 0
        val favoriteEndMargin = if (canFavorite) 5.dp.roundToPx() else 0
        // Intrinsics give the first wrap-content pass without measuring a
        // Compose child twice. The status TextView initially caps at150dp.
        val statusNatural = minOf(children[0].maxIntrinsicWidth(Constraints.Infinity), 150.dp.roundToPx(), width)
        val totalNatural = minOf(children[totalIndex].maxIntrinsicWidth(Constraints.Infinity), width)
        val remaining = width - statusNatural - totalNatural - favoriteWidth - favoriteEndMargin
        // LinearLayout consumes the integer .8 share first, then the residual
        // .1 share. This also handles shrinking on a narrower viewport.
        val statusShare = (remaining * .8f / (.8f + .1f)).toInt()
        val statusWidth = (statusNatural + statusShare).coerceAtLeast(0)
        val totalWidth = (width - statusWidth - favoriteWidth - favoriteEndMargin).coerceAtLeast(0)
        val statusPlaceable = children[0].measure(Constraints(minWidth = statusWidth, maxWidth = statusWidth, maxHeight = constraints.maxHeight))
        val totalPlaceable = children[totalIndex].measure(Constraints(minWidth = totalWidth, maxWidth = totalWidth, maxHeight = constraints.maxHeight))
        val height = constraints.constrainHeight(maxOf(statusPlaceable.height, totalPlaceable.height))
        val favoritePlaceable = if (canFavorite) children[1].measure(Constraints.fixed(favoriteWidth, height)) else null
        layout(width, height) {
            statusPlaceable.placeRelative(0, 0)
            favoritePlaceable?.placeRelative(statusWidth, 0)
            totalPlaceable.placeRelative(statusWidth + favoriteWidth + favoriteEndMargin, 0)
        }
    }
}

/** Packed list_item_compras + item_dezena_detalhe for ordinary numeric purchase rows. */
@Composable
internal fun LegacyNumericPurchaseBetRow(bet: ConfirmedBet) {
    val style = caixaDSLotteryStyle(bet.modalityId, bet.modality)
    Column(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().background(style.light)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 15.dp)) {
                Text(bet.modality.lowercase(Locale.forLanguageTag("pt-BR")), Modifier.weight(.5f), color = style.content,
                    style = purchaseText(14.2f, FontWeight.Bold).copy(fontFamily = CaixaDSLegacyFuturaFontFamily))
                Text("Seus números", Modifier.weight(.5f), color = style.content, style = purchaseText(12.1f))
            }
            Row(Modifier.fillMaxWidth().padding(top = 17.dp).padding(horizontal = 13.dp, vertical = 15.dp)) {
                Column(Modifier.weight(.3f)) {
                    Text(bet.date, color = style.content, style = purchaseText(14.2f))
                    Text(buildAnnotatedString {
                        append("Conc. ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(bet.contest.toString()) }
                    }, color = style.content, style = purchaseText(14.2f))
                    if (bet.recurringDraws > 0) Text("${bet.recurringDraws} ${if (bet.recurringDraws > 1) "Teimosinhas" else "Teimosinha"}",
                        Modifier.padding(top = 30.dp), color = style.content, style = purchaseText(12.2f))
                }
                Column(Modifier.weight(.7f)) {
                    bet.numbers.chunked(6).forEach { row ->
                        Row(Modifier.fillMaxWidth()) {
                            repeat(6) { index ->
                                Box(Modifier.weight(1f).height(51.dp), contentAlignment = Alignment.Center) {
                                    row.getOrNull(index)?.let { number ->
                                        // DezenaConfig default is cinza110, even on a purple row.
                                        Box(Modifier.size(35.dp).border((35f / 40.1168f).dp, CaixaDSColorTokens.LegacySocialGraphite, CircleShape),
                                            contentAlignment = Alignment.Center) {
                                            Text(number.toString(), color = CaixaDSColorTokens.LegacySocialGraphite,
                                                style = purchaseText(14f, FontWeight.Bold))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(65.dp).background(style.dark).padding(start = 13.dp, top = 7.dp)) {
            Text(bet.status.ifBlank { "Em acompanhamento" }, color = style.content, style = purchaseText(14.2f))
        }
    }
}
