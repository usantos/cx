package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSBetTypeItem
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSBetTypeStrip
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSBetTypeIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.resolveLoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR
import java.util.Locale

/** item_apostas_confirmadas + substituição da situação por 14sp em execução em ApostaConfirmadaHolder. */
@Composable
fun LegacyConfirmedBetRow(bet: ConfirmedBet, onOpen: () -> Unit, onAddToCart: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val lottery = loteriasCaixaDSLotteryStyle(bet.modalityId, bet.modality)
    val listColor = if (lottery != resolveLoteriasCaixaDSLotteryStyle(bet.modalityId, bet.modality, LoteriasCaixaDSCampaign.DEFAULT)) {
        lottery.textOnSurface
    } else {
        lottery.listText
    }
    Column(
        Modifier.fillMaxWidth().background(LoteriasCaixaDSColorTokens.White).drawBehind {
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
                        style = ticketListText(16.sp, FontWeight.Bold).copy(fontFamily = LoteriasCaixaDSFuturaFontFamily)
                    )
                    Text(
                        if (bet.recurringDraws > 0) {
                            uiStrings.getString(
                                DeliveryR.string.ui_conc_1_s_a_2_s_a6ec3a,
                                bet.contest.toString(),
                                (bet.contest + bet.recurringDraws - 1).toString()
                            )
                        } else {
                            uiStrings.getString(DeliveryR.string.ui_concurso_1_s_32ee4c, bet.contest.toString())
                        },
                        (Modifier.padding(end = 42.dp)).uiId("tickets.0083"),
                        color = ListTextColor,
                        style = ticketListText(12.sp)
                    )
                }
                Text(bet.date, (Modifier.padding(top = 2.dp)).uiId("tickets.0084"), color = ListTextColor, style = ticketListText(12.sp))
                Text(
                    uiStrings.getString(
                        DeliveryR.string.ui_situacao_1_s_48ba95,
                        legacyConfirmedBetStatus(bet).lowercase(Locale.forLanguageTag("pt-BR"))
                    ),
                    (Modifier.padding(end = 8.dp, bottom = 8.dp)).uiId("tickets.0085"),
                    color = LoteriasCaixaDSColorTokens.SecondaryText,
                    style = ticketListText(14.sp, if (bet.statusId == AWARDED_BET_STATUS) FontWeight.Bold else FontWeight.Normal)
                )
            }
            Icon(
                painterResource(DeliveryR.drawable.legacy_tickets_ic_numero_bilhete),
                uiStrings.getString(DeliveryR.string.ui_detalhe_da_aposta_b67444),
                (Modifier.padding(end = 10.dp).size(26.dp).uiId("tickets.0086").clickable(onClick = onOpen)).uiId("tickets.0087"),
                tint = LoteriasCaixaDSColorTokens.TicketListAction
            )
            Box(Modifier.size(26.dp)) {
                if (!bet.poolShare && bet.modalityId != LOTECA_MODALITY_ID && onAddToCart != null) {
                    Icon(
                        painterResource(DeliveryR.drawable.legacy_tickets_ic_adicionar_carrinho),
                        uiStrings.getString(DeliveryR.string.ui_adicionar_ao_carrinho_bf86b8),
                        (Modifier.fillMaxSize().uiId("tickets.0088").clickable(onClick = onAddToCart)).uiId("tickets.0089"),
                        tint = LoteriasCaixaDSColorTokens.TicketListAction
                    )
                }
            }
        }
        ConfirmedBetBadges(bet, listColor)
        Box(Modifier.fillMaxWidth().height(1.dp).background(LoteriasCaixaDSColorTokens.FavoriteDivider))
    }
}

private fun legacyConfirmedBetStatus(bet: ConfirmedBet): String = when (bet.statusId) {
    AWARDED_BET_STATUS -> "Premiada"
    PAID_PRIZE_STATUS -> messageText("final_prize_paid")
    NOT_AWARDED_STATUS -> messageText("aposta_nao_premiada_1218e0")
    UNSETTLED_CONTEST_STATUS -> messageText("concurso_nao_apurado_b96550")
    NOT_AWARDED_STILL_COMPETING_STATUS -> messageText("aposta_nao_premiada_concorrendo_ffe743")
    EXPIRED_BET_STATUS -> "Aposta prescrita"
    REGISTERED_BET_STATUS -> "Aposta efetivada"
    else -> bet.status
}

@Composable
private fun ConfirmedBetBadges(bet: ConfirmedBet, listColor: androidx.compose.ui.graphics.Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val badges = buildList {
        if (bet.exchange || bet.exchangeBetId != null) add(LoteriasCaixaDSBetTypeItem("Troca", LoteriasCaixaDSBetTypeIcons.Exchange))
        if (bet.poolShare) {
            add(
                LoteriasCaixaDSBetTypeItem(uiStrings.getString(DeliveryR.string.ui_bolao_99927e), LoteriasCaixaDSBetTypeIcons.Pool)
            )
        }
        if (bet.combo) add(LoteriasCaixaDSBetTypeItem("Combo", LoteriasCaixaDSBetTypeIcons.Combo))
        if (bet.mirror) add(LoteriasCaixaDSBetTypeItem("Espelho", LoteriasCaixaDSBetTypeIcons.Mirror))
        if (bet.surprise) add(LoteriasCaixaDSBetTypeItem("Surpresinha", LoteriasCaixaDSBetTypeIcons.Surprise))
        if (bet.originalRecurringBet ||
            bet.recurringDraws > 0
        ) {
            add(LoteriasCaixaDSBetTypeItem("Teimosinha", LoteriasCaixaDSBetTypeIcons.Recurring))
        }
    }
    if (badges.isNotEmpty()) {
        LoteriasCaixaDSBetTypeStrip(
            badges,
            backgroundColor = LoteriasCaixaDSColorTokens.White,
            contentColor = listColor,
            modifier = Modifier.uiId("tickets.0090")
        )
    }
}

private const val AWARDED_BET_STATUS = 100L
private const val PAID_PRIZE_STATUS = 11L
private const val NOT_AWARDED_STATUS = 9L
private const val UNSETTLED_CONTEST_STATUS = 101L
private const val NOT_AWARDED_STILL_COMPETING_STATUS = 102L
private const val EXPIRED_BET_STATUS = 10L
private const val REGISTERED_BET_STATUS = 4L
private const val LOTECA_MODALITY_ID = 19
