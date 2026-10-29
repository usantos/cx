package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSBetTypeItem
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSBetTypeStrip
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSDropdownMenu as DropdownMenu
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSDropdownMenuItem as DropdownMenuItem
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSBetTypeIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.resolveCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.tickets.R
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsBackend
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.presentation.screen.paymentSecondsRemaining
import kotlinx.coroutines.delay
import java.util.Locale

private val ListTextColor = CaixaDSColorTokens.NotificationText
private fun ticketListText(size: TextUnit, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = CaixaDSFontFamily,
    fontSize = size,
    fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

@Composable
internal fun LegacyTicketsFilterAction(count: Int, onClick: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.size(48.dp).uiId("tickets.0065").clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(
            painterResource(R.drawable.legacy_tickets_ic_funil),
            uiStrings.getString(LocalizedR.string.ui_filtrar_06b52c),
            (Modifier.size(24.dp)).uiId("tickets.0066"),
            tint = CaixaDSColorTokens.White
        )
        if (count > 0) {
            Box(
                Modifier.align(Alignment.TopEnd).padding(3.dp).size(12.dp)
                    .background(CaixaDSColorTokens.TicketNoticeBadge, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    count.toString(),
                    color = CaixaDSColorTokens.White,
                    style = ticketListText(8.sp),
                    modifier = Modifier
                        .uiId("tickets.0067")
                )
            }
        }
    }
}

@Composable
internal fun LegacyConfirmedBetsIntro(historyBackend: ConfirmedBetsBackend) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column {
        Image(
            painterResource(
                when (historyBackend) {
                    ConfirmedBetsBackend.NOVA_API -> R.drawable.legacy_tickets_trevo_nova_api
                    ConfirmedBetsBackend.MICROSERVICE -> R.drawable.legacy_tickets_trevo_micro_servico
                    else -> R.drawable.legacy_tickets_trevo_silce
                }
            ),
            null,
            Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp).height(28.dp)
        )
        Column(Modifier.padding(16.dp)) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_o_proximo_ganhador_pode_ser_voce_ja_492506),
                color = CaixaDSColorTokens.TicketIntro,
                style = ticketListText(14.sp, FontWeight.Bold),
                modifier = Modifier.uiId("tickets.0068")
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_aqui_voce_pode_acompanhar_todas_suas_312271),
                (Modifier.padding(top = 20.dp)).uiId("tickets.0069"),
                color = CaixaDSColorTokens.BetAction,
                style = ticketListText(14.sp)
            )
        }
        Box(Modifier.fillMaxWidth().height(56.dp).background(CaixaDSColorTokens.TealAction), contentAlignment = Alignment.Center) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_ver_resultados_08bb7b),
                color = CaixaDSColorTokens.White,
                style = ticketListText(12.sp, FontWeight.Bold),
                modifier = Modifier.uiId("tickets.0070")
            )
            Image(painterResource(R.drawable.legacy_tickets_tab_seta), null, Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
internal fun LegacyPurchaseIntro() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(LocalizedR.string.ui_acompanhe_todas_as_compras_que_voce_b96f1f),
        (Modifier.fillMaxWidth().padding(start = 10.dp, end = 17.dp, top = 20.dp)).uiId("tickets.0071"),
        color = CaixaDS.colors.brand,
        style = ticketListText(15.5.sp, FontWeight.Bold)
    )
}

/** spinner_date/content_lista_compras; a seleção mantém os callbacks existentes do ViewModel. */
@Composable
internal fun LegacyTicketPeriodSelector(
    label: String,
    value: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    onSelect: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val largeFont = androidx.compose.ui.platform.LocalDensity.current.fontScale > 1f
    Column(modifier.fillMaxWidth().then(if (largeFont) Modifier.heightIn(min = 48.dp) else Modifier.height(48.dp))) {
        Text(label, color = CaixaDSColorTokens.TicketPeriodLabel, style = ticketListText(12.sp), modifier = Modifier.uiId("tickets.0072"))
        Box(
            Modifier.then(if (largeFont) Modifier else Modifier.weight(1f)).fillMaxWidth()
                .uiId("tickets.0073").clickable { expanded = true }
        ) {
            Row(if (largeFont) Modifier.fillMaxWidth() else Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    value,
                    (Modifier.weight(1f)).uiId("tickets.0074"),
                    color = CaixaDSColorTokens.TicketPeriodSelection,
                    style = ticketListText(16.sp)
                )
                Image(painterResource(R.drawable.legacy_tickets_combo), null)
            }
            DropdownMenu(expanded, onDismissRequest = { expanded = false }, modifier = Modifier.uiId("tickets.0075")) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                style = ticketListText(16.sp),
                                color = ListTextColor,
                                modifier = Modifier
                                    .uiId("tickets.0076")
                            )
                        },
                        onClick = {
                            expanded = false
                            onSelect(index)
                        },
                        modifier = Modifier.uiId("tickets.0077")
                    )
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.TicketPeriodDivider))
    }
}

@Composable
internal fun LegacyEmptyBets(onBetNow: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_voce_ainda_nao_fez_apostas_no_period_002a42),
            (Modifier.padding(horizontal = 16.dp)).uiId("tickets.0078"),
            color = CaixaDSColorTokens.TicketIntro,
            style = ticketListText(14.sp, FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        if (onBetNow != null) {
            Box(
                Modifier.padding(top = 26.dp).width(200.dp).height(54.dp)
                    .background(CaixaDSColorTokens.TealAction, RoundedCornerShape(4.dp)).uiId("tickets.0079").clickable(onClick = onBetNow),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_aposte_aqui_a5080d),
                    color = CaixaDSColorTokens.White,
                    style = ticketListText(14.sp, FontWeight.Bold),
                    modifier = Modifier.uiId("tickets.0080")
                )
            }
        }
    }
}

@Composable
internal fun LegacyEmptyPurchases() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(LocalizedR.string.ui_voce_ainda_nao_possui_compras_neste_0585aa),
        (Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 30.dp)).uiId("tickets.0081"),
        color = CaixaDSColorTokens.TicketIntro,
        style = ticketListText(14.sp, FontWeight.Bold),
        textAlign = TextAlign.Center
    )
}

/** item_apostas_confirmadas + substituição da situação por 14sp em execução em ApostaConfirmadaHolder. */
@Composable
internal fun LegacyConfirmedBetRow(bet: ConfirmedBet, onOpen: () -> Unit, onAddToCart: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val lottery = caixaDSLotteryStyle(bet.modalityId, bet.modality)
    val listColor = if (lottery != resolveCaixaDSLotteryStyle(bet.modalityId, bet.modality, CaixaDSCampaign.DEFAULT)) {
        lottery.textOnSurface
    } else {
        lottery.listText
    }
    Column(
        Modifier.fillMaxWidth().background(CaixaDSColorTokens.White).drawBehind {
            drawRect(lottery.listText, size = androidx.compose.ui.geometry.Size(5.dp.toPx(), size.height))
        }
    ) {
        Row(Modifier.fillMaxWidth().padding(start = 21.dp, end = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f).padding(top = 16.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Text(
                        bet.modality.lowercase(Locale.forLanguageTag("pt-BR")),
                        (Modifier.weight(1f).padding(end = 8.dp)).uiId("tickets.0082"),
                        color = listColor,
                        style = ticketListText(16.sp, FontWeight.Bold).copy(fontFamily = CaixaDSFuturaFontFamily)
                    )
                    Text(
                        if (bet.recurringDraws > 0) {
                            uiStrings.getString(
                                LocalizedR.string.ui_conc_1_s_a_2_s_a6ec3a,
                                bet.contest.toString(),
                                (bet.contest + bet.recurringDraws - 1).toString()
                            )
                        } else {
                            uiStrings.getString(LocalizedR.string.ui_concurso_1_s_32ee4c, bet.contest.toString())
                        },
                        (Modifier.padding(end = 42.dp)).uiId("tickets.0083"),
                        color = ListTextColor,
                        style = ticketListText(12.sp)
                    )
                }
                Text(bet.date, (Modifier.padding(top = 2.dp)).uiId("tickets.0084"), color = ListTextColor, style = ticketListText(12.sp))
                Text(
                    uiStrings.getString(
                        LocalizedR.string.ui_situacao_1_s_48ba95,
                        legacyConfirmedBetStatus(bet).lowercase(Locale.forLanguageTag("pt-BR"))
                    ),
                    (Modifier.padding(end = 8.dp, bottom = 8.dp)).uiId("tickets.0085"),
                    color = CaixaDSColorTokens.SecondaryText,
                    style = ticketListText(14.sp, if (bet.statusId == 100L) FontWeight.Bold else FontWeight.Normal)
                )
            }
            Icon(
                painterResource(R.drawable.legacy_tickets_ic_numero_bilhete),
                uiStrings.getString(LocalizedR.string.ui_detalhe_da_aposta_b67444),
                (Modifier.padding(end = 10.dp).size(26.dp).uiId("tickets.0086").clickable(onClick = onOpen)).uiId("tickets.0087"),
                tint = CaixaDSColorTokens.TicketListAction
            )
            Box(Modifier.size(26.dp)) {
                if (!bet.poolShare && bet.modalityId != 19 && onAddToCart != null) {
                    Icon(
                        painterResource(R.drawable.legacy_tickets_ic_adicionar_carrinho),
                        uiStrings.getString(LocalizedR.string.ui_adicionar_ao_carrinho_bf86b8),
                        (Modifier.fillMaxSize().uiId("tickets.0088").clickable(onClick = onAddToCart)).uiId("tickets.0089"),
                        tint = CaixaDSColorTokens.TicketListAction
                    )
                }
            }
        }
        ConfirmedBetBadges(bet, listColor)
        Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.FavoriteDivider))
    }
}

private fun legacyConfirmedBetStatus(bet: ConfirmedBet): String = when (bet.statusId) {
    100L -> "Premiada"
    11L -> messageText("final_prize_paid")
    9L -> messageText("aposta_nao_premiada_1218e0")
    101L -> messageText("concurso_nao_apurado_b96550")
    102L -> messageText("aposta_nao_premiada_concorrendo_ffe743")
    10L -> "Aposta prescrita"
    4L -> "Aposta efetivada"
    else -> bet.status
}

@Composable
internal fun LegacyTicketsHelpAction(onClick: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.size(45.dp).uiId("tickets.0091").clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Image(
            painterResource(R.drawable.legacy_tickets_icon_duvida2),
            uiStrings.getString(LocalizedR.string.ui_ajuda_fe0add),
            Modifier.size(24.dp)
        )
    }
}

/** list_group_compras + CompraViewHolder; esta linha preserva a ação de detalhes da compra. */
@Composable
internal fun LegacyPurchaseRow(purchase: Purchase, onOpen: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val statusColor = when (purchase.statusId) {
        3L, 4L, 5L, 11L, 12L -> CaixaDSColorTokens.NotificationDeleteAction
        1L, 2L, 7L, 9L, 10L -> CaixaDSColorTokens.ReservationWarning
        else -> CaixaDSColorTokens.PurchaseStatus
    }
    var remaining by remember(purchase.paymentExpiresAt) { mutableLongStateOf(purchase.paymentSecondsRemaining()) }
    LaunchedEffect(purchase.paymentExpiresAt) {
        while (remaining > 0) {
            delay(1_000)
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
                    color = CaixaDSColorTokens.BetAction,
                    style = ticketListText(14.sp)
                )
                Text(
                    formatBrazilianCurrency(purchase.total),
                    color = CaixaDSColorTokens.BetAction,
                    style = ticketListText(14.2.sp),
                    modifier = Modifier.uiId("tickets.0096")
                )
            }
            if (purchase.finalizedTime.isNotBlank()) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_hora_1_s_d2a782, purchase.finalizedTime),
                    color = CaixaDSColorTokens.BetAction,
                    style = ticketListText(14.sp),
                    modifier = Modifier.uiId("tickets.0097")
                )
            }
            Row(Modifier.fillMaxWidth()) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_meio_de_pagamento_1_s_826d04, purchase.paymentMethod),
                    (Modifier.weight(1f)).uiId("tickets.0098"),
                    color = CaixaDSColorTokens.BetAction,
                    style = ticketListText(14.sp)
                )
                if (purchase.paymentMethodId == 6L && purchase.statusId == 10L) {
                    Text(
                        uiStrings.getString(
                            LocalizedR.string.ui_1_s_2_s_069c89,
                            (remaining / 60).toString(),
                            (remaining % 60).toString().padStart(2, '0')
                        ),
                        color = CaixaDSColorTokens.ReservationWarning,
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
private fun ConfirmedBetBadges(bet: ConfirmedBet, listColor: androidx.compose.ui.graphics.Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val badges = buildList {
        if (bet.exchange || bet.exchangeBetId != null) add(CaixaDSBetTypeItem("Troca", CaixaDSBetTypeIcons.Exchange))
        if (bet.poolShare) add(CaixaDSBetTypeItem(uiStrings.getString(LocalizedR.string.ui_bolao_99927e), CaixaDSBetTypeIcons.Pool))
        if (bet.combo) add(CaixaDSBetTypeItem("Combo", CaixaDSBetTypeIcons.Combo))
        if (bet.mirror) add(CaixaDSBetTypeItem("Espelho", CaixaDSBetTypeIcons.Mirror))
        if (bet.surprise) add(CaixaDSBetTypeItem("Surpresinha", CaixaDSBetTypeIcons.Surprise))
        if (bet.originalRecurringBet || bet.recurringDraws > 0) add(CaixaDSBetTypeItem("Teimosinha", CaixaDSBetTypeIcons.Recurring))
    }
    if (badges.isNotEmpty()) {
        CaixaDSBetTypeStrip(
            badges,
            backgroundColor = CaixaDSColorTokens.White,
            contentColor = listColor,
            modifier = Modifier.uiId("tickets.0090")
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
            color = CaixaDSColorTokens.BetAction,
            style = ticketListText(14.4.sp, FontWeight.Bold),
            modifier = Modifier.uiId("tickets.0094")
        )
    }
}
