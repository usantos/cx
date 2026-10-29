package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSPurchaseFavoriteIcon
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import java.util.Locale

private fun purchaseText(size: Float, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = CaixaDSFontFamily,
    fontSize = size.sp,
    fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

/** Grupo compacto list_group_compras seguido de compra_processada_layout; sem comportamento financeiro. */
@Composable
internal fun LegacyPurchaseDetailHeader(purchase: Purchase, statusColor: Color, canFavorite: Boolean, onFavorite: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val dataColor = CaixaDSColorTokens.NotificationIntro
    Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White)) {
        // O grupo compacto é restrito somente pelo topo. Sua margem inferior declarada
        // não acrescenta espaço antes de compra_processada_layout.
        Column(Modifier.fillMaxWidth().padding(start = 8.dp, end = 25.dp, top = 16.dp)) {
            LegacyPurchaseStatusRow(purchase.status, statusColor, canFavorite, onFavorite)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_data_1_s_fa0526, purchase.date),
                    color = dataColor,
                    style = purchaseText(14f),
                    modifier = Modifier.uiId("tickets.0222")
                )
                Text(
                    formatBrazilianCurrency(purchase.total),
                    color = CaixaDSColorTokens.BetAction,
                    style = purchaseText(14.2f),
                    modifier = Modifier.uiId("tickets.0223")
                )
            }
            if (purchase.finalizedTime.isNotBlank()) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_hora_1_s_d2a782, purchase.finalizedTime),
                    color = dataColor,
                    style = purchaseText(14f),
                    modifier = Modifier.uiId("tickets.0224")
                )
            }
            if (purchase.paymentMethodId == 6L && purchase.statusId in setOf(6L, 8L, 13L, 14L) && purchase.endToEndId.isNotBlank()) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_id_do_pagamento_1_s_f691b7, purchase.endToEndId),
                    color = dataColor,
                    style = purchaseText(14f),
                    modifier = Modifier.uiId("tickets.0225")
                )
            }
        }
        if (purchase.paymentMethod.isNotBlank()) {
            Text(
                buildAnnotatedString {
                    append(uiStrings.getString(LocalizedR.string.ui_meio_de_pagamento_579abd))
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(purchase.paymentMethod) }
                },
                (Modifier.padding(start = 8.dp, top = 16.dp, bottom = 9.dp)).uiId("tickets.0226"),
                color = dataColor,
                style = purchaseText(14f)
            )
        }
    }
}

/** Filhos de list_group_compras com tamanho conforme o conteúdo, seguidos dos pesos .8/.1 de LinearLayout. */
@Composable
private fun LegacyPurchaseStatusRow(status: String, statusColor: Color, canFavorite: Boolean, onFavorite: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Layout(modifier = Modifier.fillMaxWidth(), content = {
        Text(
            buildAnnotatedString {
                append(uiStrings.getString(LocalizedR.string.ui_situacao_d07ed5))
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(
                        status.ifBlank { uiStrings.getString(LocalizedR.string.ui_nao_informada_c6f460) }
                    )
                }
            },
            color = statusColor,
            style = purchaseText(14.2f),
            modifier = Modifier.uiId("tickets.0227")
        )
        if (canFavorite) {
            Box(
                Modifier.fillMaxSize()
                    .uiId("tickets.0228").clickable(onClick = onFavorite),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    CaixaDSPurchaseFavoriteIcon,
                    uiStrings.getString(LocalizedR.string.ui_salvar_carrinho_favorito_97b999),
                    (Modifier.size(24.dp)).uiId("tickets.0229"),
                    tint = CaixaDSColorTokens.BetLink
                )
            }
        }
        Text(
            uiStrings.getString(LocalizedR.string.ui_total_18e872),
            color = CaixaDSColorTokens.BetAction,
            style = purchaseText(14.4f, FontWeight.Bold),
            textAlign = TextAlign.End,
            modifier = Modifier.uiId("tickets.0230")
        )
    }) { children, constraints ->
        val totalIndex = if (canFavorite) 2 else 1
        val width = constraints.maxWidth
        val favoriteWidth = if (canFavorite) 48.dp.roundToPx() else 0
        val favoriteEndMargin = if (canFavorite) 5.dp.roundToPx() else 0
        // Medidas intrínsecas fornecem a primeira passagem conforme o conteúdo sem medir
        // um filho Compose duas vezes. O TextView de situação limita-se inicialmente a 150dp.
        val statusNatural = minOf(children[0].maxIntrinsicWidth(Constraints.Infinity), 150.dp.roundToPx(), width)
        val totalNatural = minOf(children[totalIndex].maxIntrinsicWidth(Constraints.Infinity), width)
        val remaining = width - statusNatural - totalNatural - favoriteWidth - favoriteEndMargin
        // LinearLayout consome primeiro a parcela inteira de .8 e depois a parcela residual
        // de .1. Isso também trata a redução em uma área de visualização mais estreita.
        val statusShare = (remaining * .8f / (.8f + .1f)).toInt()
        val statusWidth = (statusNatural + statusShare).coerceAtLeast(0)
        val totalWidth = (width - statusWidth - favoriteWidth - favoriteEndMargin).coerceAtLeast(0)
        val statusPlaceable = children[0].measure(
            Constraints(minWidth = statusWidth, maxWidth = statusWidth, maxHeight = constraints.maxHeight)
        )
        val totalPlaceable = children[totalIndex].measure(
            Constraints(minWidth = totalWidth, maxWidth = totalWidth, maxHeight = constraints.maxHeight)
        )
        val height = constraints.constrainHeight(maxOf(statusPlaceable.height, totalPlaceable.height))
        val favoritePlaceable = if (canFavorite) children[1].measure(Constraints.fixed(favoriteWidth, height)) else null
        layout(width, height) {
            statusPlaceable.placeRelative(0, 0)
            favoritePlaceable?.placeRelative(statusWidth, 0)
            totalPlaceable.placeRelative(statusWidth + favoriteWidth + favoriteEndMargin, 0)
        }
    }
}

/** Linhas compactas list_item_compras + item_dezena_detalhe para compras numéricas comuns. */
@Composable
internal fun LegacyNumericPurchaseBetRow(bet: ConfirmedBet) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val style = caixaDSLotteryStyle(bet.modalityId, bet.modality)
    Column(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().background(style.light)) {
            NumericPurchaseLabels(bet, style)
            Row(Modifier.fillMaxWidth().padding(top = 17.dp).padding(horizontal = 13.dp, vertical = 15.dp)) {
                Column(Modifier.weight(.3f)) {
                    Text(bet.date, color = style.content, style = purchaseText(14.2f), modifier = Modifier.uiId("tickets.0233"))
                    Text(
                        buildAnnotatedString {
                            append("Conc. ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(bet.contest.toString()) }
                        },
                        color = style.content,
                        style = purchaseText(14.2f),
                        modifier = Modifier.uiId("tickets.0234")
                    )
                    if (bet.recurringDraws > 0) {
                        Text(
                            uiStrings.getQuantityString(
                                LocalizedR.plurals.format_purchase_recurring_draws,
                                bet.recurringDraws,
                                bet.recurringDraws
                            ),
                            (Modifier.padding(top = 30.dp)).uiId("tickets.0235"),
                            color = style.content,
                            style = purchaseText(12.2f)
                        )
                    }
                }
                Column(Modifier.weight(.7f)) {
                    bet.numbers.chunked(6).forEach { row ->
                        Row(Modifier.fillMaxWidth()) {
                            repeat(6) { index ->
                                Box(Modifier.weight(1f).height(51.dp), contentAlignment = Alignment.Center) {
                                    row.getOrNull(index)?.let { number ->
                                        // O padrão de DezenaConfig é cinza110, mesmo em uma linha roxa.
                                        Box(
                                            Modifier.size(
                                                35.dp
                                            ).border((35f / 40.1168f).dp, CaixaDSColorTokens.SocialGraphite, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                number.toString(),
                                                color = CaixaDSColorTokens.SocialGraphite,
                                                style = purchaseText(14f, FontWeight.Bold),
                                                modifier = Modifier.uiId("tickets.0236")
                                            )
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
            Text(
                bet.status.ifBlank { uiStrings.getString(LocalizedR.string.ui_em_acompanhamento_9afa47) },
                color = style.darkContent,
                style = purchaseText(14.2f),
                modifier = Modifier.uiId("tickets.0237")
            )
        }
    }
}

@Composable
private fun NumericPurchaseLabels(bet: ConfirmedBet, style: br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSLotteryStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 15.dp)) {
        Text(
            bet.modality.lowercase(Locale.forLanguageTag("pt-BR")),
            (Modifier.weight(.5f)).uiId("tickets.0231"),
            color = style.content,
            style = purchaseText(14.2f, FontWeight.Bold).copy(fontFamily = CaixaDSFuturaFontFamily)
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_seus_numeros_ac6cc9),
            (Modifier.weight(.5f)).uiId("tickets.0232"),
            color = style.content,
            style = purchaseText(12.1f)
        )
    }
}
