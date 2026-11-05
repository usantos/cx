package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.loteriasCaixaDSMarketplaceFilterClover
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.loteriasCaixaDSLotteryClover
import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.detailSportsResultLabel
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.superSevenDrawColumns
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR

@Composable
fun ConfirmedBetDrawnResults(bet: ConfirmedBet, contentColor: Color, background: Color, result: DrawResult?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    result?.takeIf { it.firstDrawNumbers.isNotEmpty() }?.let { draw ->
        Text(
            uiStrings.getString(DeliveryR.string.ui_numeros_sorteados_513d0e),
            (Modifier.padding(top = 40.dp)).uiId("tickets.0002"),
            color = contentColor,
            fontSize = 18.sp
        )
        if (draw.secondDrawNumbers.isNotEmpty()) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_1o_sorteio_a7dc6e),
                color = contentColor,
                fontSize = 18.sp,
                modifier = Modifier.uiId("tickets.0003")
            )
        }
        if (bet.modalityId == 7 || bet.modality.contains("super", true)) {
            SuperSevenColumns(superSevenDrawColumns(draw), contentColor, background)
        } else {
            NativeNumberGrid(draw.firstDrawNumbers, contentColor, background, plain = true)
        }
        if (draw.secondDrawNumbers.isNotEmpty()) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_2o_sorteio_679b24),
                (Modifier.padding(top = 8.dp)).uiId("tickets.0004"),
                color = contentColor,
                fontSize = 18.sp
            )
            NativeNumberGrid(draw.secondDrawNumbers, contentColor, background, plain = true)
        }
        ConfirmedBetExtraDrawResults(bet, draw, contentColor, background)
    }
    result?.let { draw ->
        val matches = draw.lotecaMatches.map { it to false } + draw.lotogolMatches.map { it to true }
        if (matches.isNotEmpty()) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_resultado_dos_jogos_b19143),
                (Modifier.padding(top = 24.dp)).uiId("tickets.0010"),
                color = contentColor,
                fontSize = 18.sp
            )
            matches.forEach { (match, lotogol) ->
                Text(
                    detailSportsResultLabel(match, lotogol),
                    color = contentColor,
                    fontSize = 14.sp,
                    modifier = Modifier.uiId("tickets.0011")
                )
            }
        }
    }
}

@Composable
fun ConfirmedBetNumbers(bet: ConfirmedBet, contentColor: Color, background: Color, result: DrawResult?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(DeliveryR.string.ui_meus_numeros_f099c2),
        (Modifier.padding(top = if (result != null) 10.dp else 40.dp)).uiId("tickets.0012"),
        color = contentColor,
        fontSize = 18.sp
    )
    NativeNumberGrid(bet.numbers, contentColor, background, result?.firstDrawNumbers.orEmpty().toSet())
    if (bet.clovers.isNotEmpty()) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_trevos_0f4867),
            (Modifier.padding(top = 8.dp)).uiId("tickets.0013"),
            color = contentColor,
            fontSize = 18.sp
        )
        NativeNumberGrid(bet.clovers, contentColor, background, result?.clovers.orEmpty().toSet())
    }
    if (result?.secondDrawNumbers?.isNotEmpty() == true) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_2o_sorteio_679b24),
            (Modifier.padding(top = 8.dp)).uiId("tickets.0014"),
            color = contentColor,
            fontSize = 18.sp
        )
        NativeNumberGrid(bet.numbers, contentColor, background, result.secondDrawNumbers.toSet())
    }
}

@Composable
fun ConfirmedBetComplements(bet: ConfirmedBet, contentColor: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    bet.favoriteTeam?.let { team ->
        Text(
            uiStrings.getString(
                DeliveryR.string.ui_time_do_coracao_1_s_2_s_3b5c68,
                team.name,
                team.state.takeIf(String::isNotBlank)?.let { "/$it" }.orEmpty()
            ),
            (Modifier.padding(top = 17.dp)).uiId("tickets.0033"),
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
    bet.luckyMonth?.let { month ->
        Text(
            uiStrings.getString(DeliveryR.string.ui_mes_da_sorte_1_s_35a139, month.name),
            (Modifier.padding(top = 17.dp)).uiId("tickets.0034"),
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ConfirmedBetExtraDrawResults(bet: ConfirmedBet, draw: DrawResult, contentColor: Color, background: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (draw.clovers.isNotEmpty()) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_trevos_0f4867),
            color = contentColor,
            fontSize = 18.sp,
            modifier = Modifier
                .uiId("tickets.0005")
        )
        NativeNumberGrid(draw.clovers, contentColor, background, plain = true)
    }
    draw.favoriteTeamReward?.let {
        Text(
            uiStrings.getString(DeliveryR.string.ui_time_sorteado_1_s_ca30eb, it.selection),
            color = contentColor,
            fontSize = 18.sp,
            modifier = Modifier.uiId("tickets.0006")
        )
    }
    draw.luckyMonthReward?.let {
        Text(
            uiStrings.getString(DeliveryR.string.ui_mes_sorteado_1_s_af82b8, it.selection),
            color = contentColor,
            fontSize = 18.sp,
            modifier = Modifier.uiId("tickets.0007")
        )
    }
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(Modifier.width(212.dp).height(28.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.fillMaxWidth().height(1.dp).background(contentColor))
            Box(Modifier.size(20.dp).background(contentColor, CircleShape), contentAlignment = Alignment.Center) {
                val clover = loteriasCaixaDSMarketplaceFilterClover(bet.modalityId, bet.modality, selected = false)
                if (clover != null) {
                    Icon(painterResource(clover), null, (Modifier.size(12.dp)).uiId("tickets.0008"), tint = Color.Unspecified)
                } else {
                    Icon(
                        loteriasCaixaDSLotteryClover(bet.modalityId, onDark = false),
                        null,
                        (Modifier.size(12.dp)).uiId("tickets.0009"),
                        tint = background
                    )
                }
            }
        }
    }
}
