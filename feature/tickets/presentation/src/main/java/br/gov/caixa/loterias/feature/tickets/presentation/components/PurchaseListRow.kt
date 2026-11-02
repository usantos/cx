package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.tickets.R
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.presentation.screen.paymentSecondsRemaining
import kotlinx.coroutines.delay

/** list_group_compras + CompraViewHolder; esta linha preserva a ação de detalhes da compra. */
@Composable
internal fun LegacyPurchaseRow(purchase: Purchase, onOpen: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val statusColor = when (purchase.statusId) {
        DEBIT_NOT_AUTHORIZED,
        REVERSED_PURCHASE,
        CANCELLED_PURCHASE,
        AWAITING_PIX_REFUND,
        PAYMENT_DEADLINE_CANCELLED,
        -> LoteriasCaixaDSColorTokens.NotificationDeleteAction

        CART_PURCHASE,
        DEBIT_STARTED,
        PROCESSING_PURCHASE,
        HELD_PURCHASE,
        AWAITING_PIX_PAYMENT,
        -> LoteriasCaixaDSColorTokens.ReservationWarning

        else -> LoteriasCaixaDSColorTokens.PurchaseStatus
    }
    var remaining by remember(purchase.paymentExpiresAt) { mutableLongStateOf(purchase.paymentSecondsRemaining()) }
    LaunchedEffect(purchase.paymentExpiresAt) {
        while (remaining > 0) {
            delay(COUNTDOWN_INTERVAL_MILLIS)
            remaining = purchase.paymentSecondsRemaining()
        }
    }
    Box(Modifier.fillMaxWidth().uiId("tickets.0092").clickable(onClick = onOpen)) {
        Column(Modifier.fillMaxWidth().padding(start = 8.dp, end = 25.dp, top = 8.dp, bottom = 8.dp)) {
            PurchaseRowStatus(purchase, statusColor)
            Row(Modifier.fillMaxWidth()) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_data_1_s_fa0526, purchase.date),
                    (Modifier.weight(1f)).uiId("tickets.0095"),
                    color = LoteriasCaixaDSColorTokens.BetAction,
                    style = ticketListText(14.sp)
                )
                Text(
                    formatBrazilianCurrency(purchase.total),
                    color = LoteriasCaixaDSColorTokens.BetAction,
                    style = ticketListText(14.2.sp),
                    modifier = Modifier.uiId("tickets.0096")
                )
            }
            if (purchase.finalizedTime.isNotBlank()) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_hora_1_s_d2a782, purchase.finalizedTime),
                    color = LoteriasCaixaDSColorTokens.BetAction,
                    style = ticketListText(14.sp),
                    modifier = Modifier.uiId("tickets.0097")
                )
            }
            Row(Modifier.fillMaxWidth()) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_meio_de_pagamento_1_s_826d04, purchase.paymentMethod),
                    (Modifier.weight(1f)).uiId("tickets.0098"),
                    color = LoteriasCaixaDSColorTokens.BetAction,
                    style = ticketListText(14.sp)
                )
                if (purchase.paymentMethodId == PIX_PAYMENT_METHOD_ID && purchase.statusId == AWAITING_PIX_PAYMENT) {
                    Text(
                        uiStrings.getString(
                            LocalizedR.string.ui_1_s_2_s_069c89,
                            (remaining / SECONDS_PER_MINUTE).toString(),
                            (remaining % SECONDS_PER_MINUTE).toString().padStart(2, '0')
                        ),
                        color = LoteriasCaixaDSColorTokens.ReservationWarning,
                        style = ticketListText(14.2.sp, FontWeight.Bold),
                        modifier = Modifier.uiId("tickets.0099")
                    )
                }
            }
        }
        Image(
            painterResource(R.drawable.legacy_tickets_seta_direita_cinza),
            null,
            Modifier.align(Alignment.TopEnd).padding(top = 18.dp, end = 8.dp).size(11.dp, 20.dp)
        )
    }
}

@Composable
private fun PurchaseRowStatus(purchase: Purchase, statusColor: androidx.compose.ui.graphics.Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth()) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_situacao_1_s_796e19, purchase.status),
            (Modifier.weight(1f)).uiId("tickets.0093"),
            color = statusColor,
            style = ticketListText(14.2.sp)
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_total_c9b3c3),
            color = LoteriasCaixaDSColorTokens.BetAction,
            style = ticketListText(14.4.sp, FontWeight.Bold),
            modifier = Modifier.uiId("tickets.0094")
        )
    }
}

private const val CART_PURCHASE = 1L
private const val DEBIT_STARTED = 2L
private const val DEBIT_NOT_AUTHORIZED = 3L
private const val REVERSED_PURCHASE = 4L
private const val CANCELLED_PURCHASE = 5L
private const val PROCESSING_PURCHASE = 7L
private const val HELD_PURCHASE = 9L
private const val AWAITING_PIX_PAYMENT = 10L
private const val AWAITING_PIX_REFUND = 11L
private const val PAYMENT_DEADLINE_CANCELLED = 12L
private const val PIX_PAYMENT_METHOD_ID = 6L
private const val COUNTDOWN_INTERVAL_MILLIS = 1_000L
private const val SECONDS_PER_MINUTE = 60
