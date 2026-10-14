package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.*
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet
import br.gov.caixa.loterias.core.domain.tickets.Purchase
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

private val ListTextColor = CaixaDSColorTokens.LegacyNotificationText
private fun ticketListText(size: TextUnit, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = CaixaDSFontFamily, fontSize = size, fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true),
)

@Composable
internal fun LegacyTicketsFilterAction(count: Int, onClick: () -> Unit) {
    Box(Modifier.size(48.dp).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(painterResource(R.drawable.legacy_tickets_ic_funil), "Filtrar", Modifier.size(24.dp), tint = CaixaDSColorTokens.White)
        if (count > 0) Box(Modifier.align(Alignment.TopEnd).padding(3.dp).size(12.dp)
            .background(CaixaDSColorTokens.LegacyTicketNoticeBadge, RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
            Text(count.toString(), color = CaixaDSColorTokens.White, style = ticketListText(8.sp))
        }
    }
}

@Composable
internal fun LegacyConfirmedBetsIntro(historyBackend: br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsBackend) {
    Column {
        Image(painterResource(when (historyBackend) {
            br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsBackend.NOVA_API -> R.drawable.legacy_tickets_trevo_nova_api
            br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsBackend.MICROSERVICE -> R.drawable.legacy_tickets_trevo_micro_servico
            else -> R.drawable.legacy_tickets_trevo_silce
        }), null, Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp).height(28.dp))
        Column(Modifier.padding(16.dp)) {
            Text("O próximo ganhador pode ser você. Já pensou?", color = CaixaDSColorTokens.LegacyTicketIntro,
                style = ticketListText(14.sp, FontWeight.Bold))
            Text("Aqui você pode acompanhar todas suas apostas realizadas nos últimos 6 meses. Boa sorte!",
                Modifier.padding(top = 20.dp), color = CaixaDSColorTokens.LegacyBetAction, style = ticketListText(14.sp))
        }
        Box(Modifier.fillMaxWidth().height(56.dp).background(CaixaDSColorTokens.LegacyTealAction), contentAlignment = Alignment.Center) {
            Text("Ver resultados", color = CaixaDSColorTokens.White, style = ticketListText(12.sp, FontWeight.Bold))
            Image(painterResource(R.drawable.legacy_tickets_tab_seta), null, Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
internal fun LegacyPurchaseIntro() {
    Text("Acompanhe todas as compras que você já realizou em nosso aplicativo.",
        Modifier.fillMaxWidth().padding(start = 10.dp, end = 17.dp, top = 20.dp),
        color = CaixaDS.colors.brand, style = ticketListText(15.5.sp, FontWeight.Bold))
}

/** spinner_date/content_lista_compras; selection keeps the existing ViewModel callbacks. */
@Composable
internal fun LegacyTicketPeriodSelector(label: String, value: String, options: List<String>,
    modifier: Modifier = Modifier, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier.fillMaxWidth().height(48.dp)) {
        Text(label, color = CaixaDSColorTokens.LegacyTicketPeriodLabel, style = ticketListText(12.sp))
        Box(Modifier.weight(1f).fillMaxWidth().clickable { expanded = true }) {
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                Text(value, Modifier.weight(1f), color = CaixaDSColorTokens.LegacyTicketPeriodSelection, style = ticketListText(16.sp))
                Image(painterResource(R.drawable.legacy_tickets_combo), null)
            }
            DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(text = { Text(option, style = ticketListText(16.sp), color = ListTextColor) },
                        onClick = { expanded = false; onSelect(index) })
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.LegacyTicketPeriodDivider))
    }
}

@Composable
internal fun LegacyEmptyBets(onBetNow: (() -> Unit)?) {
    Column(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Você ainda não fez apostas no período selecionado. Aproveite para apostar agora!",
            Modifier.padding(horizontal = 16.dp), color = CaixaDSColorTokens.LegacyTicketIntro,
            style = ticketListText(14.sp, FontWeight.Bold), textAlign = TextAlign.Center)
        if (onBetNow != null) Box(Modifier.padding(top = 26.dp).width(200.dp).height(54.dp)
            .background(CaixaDSColorTokens.LegacyTealAction, RoundedCornerShape(4.dp)).clickable(onClick = onBetNow),
            contentAlignment = Alignment.Center) {
            Text("Aposte aqui", color = CaixaDSColorTokens.White, style = ticketListText(14.sp, FontWeight.Bold))
        }
    }
}

@Composable
internal fun LegacyEmptyPurchases() {
    Text("Você ainda não possui compras neste mês.", Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 30.dp),
        color = CaixaDSColorTokens.LegacyTicketIntro, style = ticketListText(14.sp, FontWeight.Bold), textAlign = TextAlign.Center)
}

/** item_apostas_confirmadas + ApostaConfirmadaHolder runtime 14sp situation override. */
@Composable
internal fun LegacyConfirmedBetRow(bet: ConfirmedBet, onOpen: () -> Unit, onAddToCart: (() -> Unit)?) {
    val lottery = caixaDSLotteryStyle(bet.modalityId, bet.modality)
    val listColor = if (bet.modalityId in setOf(7, 11, 20)) lottery.content else lottery.light
    Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White).drawBehind {
        drawRect(listColor, size = androidx.compose.ui.geometry.Size(5.dp.toPx(), size.height))
    }) {
        Row(Modifier.fillMaxWidth().padding(start = 21.dp, end = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f).padding(top = 16.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Text(bet.modality.lowercase(Locale.forLanguageTag("pt-BR")), Modifier.weight(1f).padding(end = 8.dp),
                        color = listColor, style = ticketListText(16.sp, FontWeight.Bold).copy(fontFamily = CaixaDSLegacyFuturaFontFamily))
                    Text(if (bet.recurringDraws > 0) "Conc. ${bet.contest} a ${bet.contest + bet.recurringDraws - 1}" else "Concurso ${bet.contest}", Modifier.padding(end = 42.dp), color = ListTextColor, style = ticketListText(12.sp))
                }
                Text(bet.date, Modifier.padding(top = 2.dp), color = ListTextColor, style = ticketListText(12.sp))
                Text("situação:\u00a0${legacyConfirmedBetStatus(bet).lowercase(Locale.forLanguageTag("pt-BR"))}",
                    Modifier.padding(end = 8.dp, bottom = 8.dp), color = CaixaDSColorTokens.LegacySecondaryText,
                    style = ticketListText(14.sp, if (bet.statusId == 100L) FontWeight.Bold else FontWeight.Normal))
            }
            Icon(painterResource(R.drawable.legacy_tickets_ic_numero_bilhete), "Detalhe da aposta",
                Modifier.padding(end = 10.dp).size(26.dp).clickable(onClick = onOpen), tint = CaixaDSColorTokens.LegacyTicketListAction)
            Box(Modifier.size(26.dp)) {
                if (!bet.poolShare && bet.modalityId != 19 && onAddToCart != null) {
                    Icon(painterResource(R.drawable.legacy_tickets_ic_adicionar_carrinho), "Adicionar ao carrinho",
                        Modifier.fillMaxSize().clickable(onClick = onAddToCart), tint = CaixaDSColorTokens.LegacyTicketListAction)
                }
            }
        }
        val badges = buildList {
            if (bet.exchange || bet.exchangeBetId != null) add(CaixaDSBetTypeItem("Troca", CaixaDSBetTypeIcons.Exchange))
            if (bet.poolShare) add(CaixaDSBetTypeItem("Bolão", CaixaDSBetTypeIcons.Pool))
            if (bet.combo) add(CaixaDSBetTypeItem("Combo", CaixaDSBetTypeIcons.Combo))
            if (bet.mirror) add(CaixaDSBetTypeItem("Espelho", CaixaDSBetTypeIcons.Mirror))
            if (bet.surprise) add(CaixaDSBetTypeItem("Surpresinha", CaixaDSBetTypeIcons.Surprise))
            if (bet.originalRecurringBet || bet.recurringDraws > 0) add(CaixaDSBetTypeItem("Teimosinha", CaixaDSBetTypeIcons.Recurring))
        }
        if (badges.isNotEmpty()) CaixaDSBetTypeStrip(badges, backgroundColor = CaixaDSColorTokens.White, contentColor = listColor)
        Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.LegacyFavoriteDivider))
    }
}

private fun legacyConfirmedBetStatus(bet: ConfirmedBet): String = when (bet.statusId) {
    100L -> "Premiada"
    11L -> "Prêmio pago"
    9L -> "Aposta não premiada"
    101L -> "Concurso não apurado"
    102L -> "Aposta não premiada concorrendo em concursos futuros"
    10L -> "Aposta prescrita"
    4L -> "Aposta efetivada"
    else -> bet.status
}

@Composable
internal fun LegacyTicketsHelpAction(onClick: () -> Unit) {
    Box(Modifier.size(45.dp).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Image(painterResource(R.drawable.legacy_tickets_icon_duvida2), "Ajuda", Modifier.size(24.dp))
    }
}

/** list_group_compras + CompraViewHolder; this row retains the purchase-detail action. */
@Composable
internal fun LegacyPurchaseRow(purchase: Purchase, onOpen: () -> Unit) {
    val statusColor = when (purchase.statusId) {
        3L, 4L, 5L, 11L, 12L -> CaixaDSColorTokens.LegacyNotificationDeleteAction
        1L, 2L, 7L, 9L, 10L -> CaixaDSColorTokens.LegacyReservationWarning
        else -> CaixaDSColorTokens.LegacyPurchaseStatus
    }
    var remaining by remember(purchase.paymentExpiresAt) { mutableLongStateOf(purchase.paymentSecondsRemaining()) }
    LaunchedEffect(purchase.paymentExpiresAt) {
        while (remaining > 0) { delay(1_000); remaining = purchase.paymentSecondsRemaining() }
    }
    Box(Modifier.fillMaxWidth().clickable(onClick = onOpen)) {
        Column(Modifier.fillMaxWidth().padding(start = 8.dp, end = 25.dp, top = 8.dp, bottom = 8.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Text("Situação: ${purchase.status}", Modifier.weight(1f), color = statusColor, style = ticketListText(14.2.sp))
                Text("Total", color = CaixaDSColorTokens.LegacyBetAction, style = ticketListText(14.4.sp, FontWeight.Bold))
            }
            Row(Modifier.fillMaxWidth()) {
                Text("Data: ${purchase.date}", Modifier.weight(1f), color = CaixaDSColorTokens.LegacyBetAction, style = ticketListText(14.sp))
                Text(NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(purchase.total),
                    color = CaixaDSColorTokens.LegacyBetAction, style = ticketListText(14.2.sp))
            }
            if (purchase.finalizedTime.isNotBlank()) Text("Hora: ${purchase.finalizedTime}", color = CaixaDSColorTokens.LegacyBetAction, style = ticketListText(14.sp))
            Row(Modifier.fillMaxWidth()) {
                Text("Meio de pagamento: ${purchase.paymentMethod}", Modifier.weight(1f), color = CaixaDSColorTokens.LegacyBetAction, style = ticketListText(14.sp))
                if (purchase.paymentMethodId == 6L && purchase.statusId == 10L) Text(
                    "${remaining / 60}:${(remaining % 60).toString().padStart(2, '0')}",
                    color = CaixaDSColorTokens.LegacyReservationWarning, style = ticketListText(14.2.sp, FontWeight.Bold))
            }
        }
        Image(painterResource(R.drawable.legacy_tickets_seta_direita_cinza), null,
            Modifier.align(Alignment.TopEnd).padding(top = 18.dp, end = 8.dp).size(11.dp, 20.dp))
    }
}
