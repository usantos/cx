package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartSummaryTokens
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSBetCartIcon
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSBetTypeIcons
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSPurchaseFavoriteIcon
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSLotteryStyle
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.confirmedBetContestStatus
import java.math.BigDecimal

@Composable
internal fun NativeBetDetailHeader(bet: ConfirmedBet, style: CaixaDSLotteryStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val color = style.darkContent
    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Text(
            if (bet.recurringDraws > 0) {
                uiStrings.getString(
                    LocalizedR.string.ui_concursos_80935e
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_concurso_1997f5)
            },
            (Modifier.padding(top = 8.dp)).uiId("tickets.0183"),
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            if (bet.recurringDraws > 0) {
                uiStrings.getString(
                    LocalizedR.string.ui_1_s_2_s_3_s_apostas_be142d,
                    bet.contest.toString(),
                    (bet.contest + bet.recurringDraws - 1).toString(),
                    bet.recurringDraws.toString()
                )
            } else {
                bet.contest.toString()
            },
            color = color,
            fontSize = 18.sp,
            modifier = Modifier.uiId("tickets.0184")
        )
        Row(Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.padding(start = 16.dp)) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_data_aposta_59182a),
                    color = color,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("tickets.0185")
                )
                Text(bet.date, color = color, fontSize = 18.sp, modifier = Modifier.uiId("tickets.0186"))
                if (bet.time.isNotBlank()) Text(bet.time, color = color, fontSize = 18.sp, modifier = Modifier.uiId("tickets.0187"))
            }
            Box(Modifier.padding(horizontal = 20.dp).width(1.dp).height(if (bet.time.isBlank()) 47.dp else 72.dp).background(color))
            Column(Modifier.weight(1f)) {
                val status = when {
                    bet.statusId in setOf(
                        4L,
                        101L
                    ) || bet.status.contains(
                        "não apurado",
                        true
                    ) -> uiStrings.getString(LocalizedR.string.ui_concurso_nao_apurado_ff0687)

                    bet.status.contains("não premiad", true) -> uiStrings.getString(LocalizedR.string.ui_aposta_nao_premiada_b3d311)

                    else -> bet.status
                }
                Text(status, color = color, fontSize = 18.sp, modifier = Modifier.uiId("tickets.0188"))
                val prize = bet.receipt?.netPrize ?: bet.prize
                if (prize.signum() > 0) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_valor_do_premio_940005),
                        color = color,
                        fontSize = 18.sp,
                        modifier = Modifier.uiId("tickets.0189")
                    )
                    Text(
                        formatBrazilianCurrency(prize),
                        color = color,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .uiId("tickets.0190")
                    )
                }
            }
        }
    }
}

@Composable
internal fun NativeBetTypeBanner(bet: ConfirmedBet, style: CaixaDSLotteryStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val items = buildList {
        if (bet.exchange) add("Troca" to CaixaDSBetTypeIcons.Exchange)
        if (bet.poolShare) add(uiStrings.getString(LocalizedR.string.ui_bolao_99927e) to CaixaDSBetTypeIcons.Pool)
        if (bet.combo) add("Combo" to CaixaDSBetTypeIcons.Combo)
        if (bet.mirror) add("Espelho" to CaixaDSBetTypeIcons.Mirror)
        if (bet.surprise) add("Surpresinha" to CaixaDSBetTypeIcons.Surprise)
        if (bet.recurringDraws > 0) add("Teimosinha" to CaixaDSBetTypeIcons.Recurring)
    }
    if (items.isEmpty()) return
    Column(Modifier.padding(top = 0.dp)) {
        FlowRow(Modifier.fillMaxWidth().background(style.light).padding(horizontal = 8.dp, vertical = 6.dp)) {
            items.forEach { (label, icon) ->
                Row(Modifier.padding(end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, null, (Modifier.size(20.dp)).uiId("tickets.0191"), tint = style.content)
                    Text(
                        label,
                        (Modifier.padding(start = 8.dp)).uiId("tickets.0192"),
                        color = style.content,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Canvas(Modifier.align(Alignment.CenterHorizontally).size(24.dp, 12.dp)) {
            drawPath(
                Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2f, size.height)
                    close()
                },
                style.light
            )
        }
    }
}

@Composable
internal fun NativeDetailButton(
    label: String,
    style: CaixaDSLotteryStyle,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    download: Boolean = false,
) {
    val darkTextGame = style.darkContent != CaixaDSColorTokens.White
    val background = if (darkTextGame) style.listText else style.darkContent
    val foreground = if (darkTextGame) style.darkContent else style.dark
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Row(
            Modifier.width(246.dp).height(44.dp).background(background, RoundedCornerShape(5.dp))
                .semantics(mergeDescendants = true) { contentDescription = label }
                .uiId("tickets.0193").clickable(enabled = onClick != null) { onClick?.invoke() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (download) Icon(CaixaDSIcons.Download, null, (Modifier.size(17.dp)).uiId("tickets.0194"), tint = foreground)
            Text(
                label,
                (Modifier.padding(start = if (download) 8.dp else 0.dp)).uiId("tickets.0195"),
                color = foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
internal fun NativeBetDetailFooter(
    bet: ConfirmedBet,
    style: CaixaDSLotteryStyle,
    canRepeat: Boolean,
    onEdit: (() -> Unit)?,
    onCart: () -> Unit,
    onAddToCart: (() -> Unit)?,
    onFavorite: () -> Unit,
    saved: Boolean,
    total: BigDecimal,
    count: Int,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val gray = CaixaDSColorTokens.SocialGraphite
    Column(
        Modifier.fillMaxWidth().height(if (canRepeat) 172.dp else 70.dp).background(CaixaDSColorTokens.White)
            .legacyAutomationId("bet-detail-footer")
    ) {
        if (canRepeat) {
            NativeBetRepeatActions(style, saved, onFavorite, onEdit, onAddToCart)
        }
        Spacer(Modifier.weight(1f))
        Row(
            Modifier.fillMaxWidth().height(54.dp).padding(start = 60.dp, end = 40.dp)
                .semantics(mergeDescendants = true) {
                    contentDescription = uiStrings.getString(
                        LocalizedR.string.ui_carrinho_de_apostas_1_s_itens_e4b649,
                        count.toString()
                    )
                }
                .uiId("tickets.0200").clickable(onClick = onCart),
            verticalAlignment = Alignment.Top
        ) {
            Column(Modifier.weight(1f).padding(end = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_total_do_carrinho_b01305),
                    color = gray,
                    fontSize = 11.sp,
                    modifier = Modifier.uiId("tickets.0201")
                )
                Text(
                    formatBrazilianCurrency(total),
                    color = gray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .uiId("tickets.0202")
                )
            }
            Box(Modifier.width(1.dp).height(32.dp).background(CaixaDSColorTokens.SimulationMuted))
            Box(Modifier.width(120.dp).height(42.dp).padding(start = 30.dp)) {
                Icon(CaixaDSBetCartIcon, null, (Modifier.size(34.dp, 30.dp)).uiId("tickets.0203"), tint = gray)
                Box(
                    Modifier.padding(start = 19.dp).size(15.dp).background(CaixaDSCartSummaryTokens.BadgeColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        count.toString(),
                        color = gray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .uiId("tickets.0204")
                    )
                }
            }
        }
    }
}

@Composable
private fun NativeFooterOutline(label: String, modifier: Modifier, action: (() -> Unit)?, star: Boolean = false) {
    val gray = CaixaDSColorTokens.SocialGraphite
    Row(
        modifier.height(44.dp).border(1.dp, CaixaDSColorTokens.BetIcon, RoundedCornerShape(8.dp))
            .uiId("tickets.0205").clickable(enabled = action != null) { action?.invoke() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (star) Icon(CaixaDSPurchaseFavoriteIcon, null, (Modifier.padding(end = 6.dp).size(18.dp)).uiId("tickets.0206"), tint = gray)
        Text(label, color = gray, fontSize = 13.sp, modifier = Modifier.uiId("tickets.0207"))
    }
}

@Composable
internal fun NativeBetDetailSelections(bet: ConfirmedBet, results: List<DrawResult>, style: CaixaDSLotteryStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (bet.recurringDraws > 0 && results.isNotEmpty()) {
        results.forEach { result ->
            var expanded by remember(bet.id, result.contest) { mutableStateOf(false) }
            Column(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Row(
                    Modifier.fillMaxWidth()
                        .uiId("tickets.0208").clickable { expanded = !expanded },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            uiStrings.getString(LocalizedR.string.ui_resultado_do_concurso_1_s_98d81e, result.contest.toString()),
                            color = style.darkContent,
                            fontSize = 18.sp,
                            modifier = Modifier.uiId("tickets.0209")
                        )
                        Text(
                            uiStrings.getString(LocalizedR.string.ui_situacao_1_s_796e19, confirmedBetContestStatus(bet, result.contest)),
                            color = style.darkContent,
                            fontSize = 16.sp,
                            modifier = Modifier.uiId("tickets.0210")
                        )
                    }
                    Text(
                        if (expanded) {
                            uiStrings.getString(
                                LocalizedR.string.ui_texto_07d452
                            )
                        } else {
                            uiStrings.getString(LocalizedR.string.ui_texto_641b9b)
                        },
                        color = style.darkContent,
                        fontSize = 24.sp,
                        modifier = Modifier.uiId("tickets.0211")
                    )
                }
                if (expanded) ConfirmedBetSelection(bet, style.darkContent, result, style.dark)
                Box(Modifier.fillMaxWidth().padding(top = 18.dp).height(1.dp).background(style.darkContent))
            }
        }
    } else {
        ConfirmedBetSelection(bet, style.darkContent, results.firstOrNull(), style.dark)
    }
}

@Composable
private fun NativeBetRepeatActions(
    style: CaixaDSLotteryStyle,
    saved: Boolean,
    onFavorite: () -> Unit,
    onEdit: (() -> Unit)?,
    onAddToCart: (() -> Unit)?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NativeFooterOutline(
                if (saved) {
                    uiStrings.getString(
                        LocalizedR.string.ui_aposta_favorita_5aae3e
                    )
                } else {
                    uiStrings.getString(
                        LocalizedR.string.ui_favoritar_aposta_a34b1e
                    )
                },
                (Modifier.weight(1f)).uiId("tickets.0196"),
                onFavorite,
                star = true
            )
            NativeFooterOutline(
                uiStrings.getString(LocalizedR.string.ui_editar_aposta_d694dd),
                (Modifier.weight(1f))
                    .uiId("tickets.0197"),
                onEdit
            )
        }
        Box(
            Modifier.padding(top = 8.dp).fillMaxWidth().height(44.dp).background(style.dark, RoundedCornerShape(5.dp))
                .uiId("tickets.0198").clickable(enabled = onAddToCart != null) { onAddToCart?.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_adicionar_ao_carrinho_bf86b8),
                color = style.darkContent,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.uiId("tickets.0199")
            )
        }
    }
}
