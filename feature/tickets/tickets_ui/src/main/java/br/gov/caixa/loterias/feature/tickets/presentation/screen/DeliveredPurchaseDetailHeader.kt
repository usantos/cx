package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSPurchaseFavoriteIcon
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR

@Composable
fun LegacyPurchaseDetailHeaderDelivered(purchase: Purchase, statusColor: Color, canFavorite: Boolean, onFavorite: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val dataColor = LoteriasCaixaDSColorTokens.NotificationIntro
    Column(Modifier.fillMaxWidth().background(LoteriasCaixaDSColorTokens.White)) {
        // O grupo compacto é restrito somente pelo topo. Sua margem inferior declarada
        // não acrescenta espaço antes de compra_processada_layout.
        Column(Modifier.fillMaxWidth().padding(start = 8.dp, end = 25.dp, top = 16.dp)) {
            LegacyPurchaseStatusRow(purchase.status, statusColor, canFavorite, onFavorite)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_data_1_s_fa0526, purchase.date),
                    color = dataColor,
                    style = purchaseText(PURCHASE_BODY_SIZE),
                    modifier = Modifier.uiId("tickets.0222")
                )
                Text(
                    formatBrazilianCurrency(purchase.total),
                    color = LoteriasCaixaDSColorTokens.BetAction,
                    style = purchaseText(PURCHASE_STATUS_SIZE),
                    modifier = Modifier.uiId("tickets.0223")
                )
            }
            if (purchase.finalizedTime.isNotBlank()) {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_hora_1_s_d2a782, purchase.finalizedTime),
                    color = dataColor,
                    style = purchaseText(PURCHASE_BODY_SIZE),
                    modifier = Modifier.uiId("tickets.0224")
                )
            }
            if (purchase.paymentMethodId == PIX_PAYMENT_METHOD_ID && purchase.statusId in PURCHASE_STATUSES_WITH_PAYMENT_ID &&
                purchase.endToEndId.isNotBlank()
            ) {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_id_do_pagamento_1_s_f691b7, purchase.endToEndId),
                    color = dataColor,
                    style = purchaseText(PURCHASE_BODY_SIZE),
                    modifier = Modifier.uiId("tickets.0225")
                )
            }
        }
        if (purchase.paymentMethod.isNotBlank()) {
            Text(
                buildAnnotatedString {
                    append(uiStrings.getString(DeliveryR.string.ui_meio_de_pagamento_579abd))
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(purchase.paymentMethod) }
                },
                (Modifier.padding(start = 8.dp, top = 16.dp, bottom = 9.dp)).uiId("tickets.0226"),
                color = dataColor,
                style = purchaseText(PURCHASE_BODY_SIZE)
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
                append(uiStrings.getString(DeliveryR.string.ui_situacao_d07ed5))
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(
                        status.ifBlank { uiStrings.getString(LocalizedR.string.ui_nao_informada_c6f460) }
                    )
                }
            },
            color = statusColor,
            style = purchaseText(PURCHASE_STATUS_SIZE),
            modifier = Modifier.uiId("tickets.0227")
        )
        if (canFavorite) {
            Box(
                Modifier.fillMaxSize()
                    .uiId("tickets.0228").clickable(onClick = onFavorite),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    LoteriasCaixaDSPurchaseFavoriteIcon,
                    uiStrings.getString(DeliveryR.string.ui_salvar_carrinho_favorito_97b999),
                    (Modifier.size(24.dp)).uiId("tickets.0229"),
                    tint = LoteriasCaixaDSColorTokens.BetLink
                )
            }
        }
        Text(
            uiStrings.getString(DeliveryR.string.ui_total_18e872),
            color = LoteriasCaixaDSColorTokens.BetAction,
            style = purchaseText(PURCHASE_TOTAL_LABEL_SIZE, FontWeight.Bold),
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
        val statusShare = (remaining * STATUS_REMAINING_WEIGHT / (STATUS_REMAINING_WEIGHT + TOTAL_REMAINING_WEIGHT)).toInt()
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

private const val PURCHASE_BODY_SIZE = 14f

private const val PURCHASE_STATUS_SIZE = 14.2f

private const val PURCHASE_TOTAL_LABEL_SIZE = 14.4f

private const val STATUS_REMAINING_WEIGHT = .8f

private const val TOTAL_REMAINING_WEIGHT = .1f

private const val PIX_PAYMENT_METHOD_ID = 6L

private val PURCHASE_STATUSES_WITH_PAYMENT_ID = setOf(6L, 8L, 13L, 14L)
