package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR

@Composable
fun LotecaSelection(selections: List<LotecaMatchSelection>, contentColor: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(DeliveryR.string.ui_sua_aposta_3bcfc2),
        (Modifier.padding(top = 28.dp, bottom = 8.dp)).uiId("tickets.0019"),
        color = contentColor,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    Row(Modifier.fillMaxWidth().padding(bottom = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_coluna_862f07),
            (Modifier.width(43.dp)).uiId("tickets.0020"),
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            uiStrings.getString(DeliveryR.string.ui_time_33b934),
            (Modifier.weight(1f)).uiId("tickets.0021"),
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            uiStrings.getString(DeliveryR.string.ui_1_x_2_e8072e),
            (Modifier.width(82.dp)).uiId("tickets.0022"),
            color = contentColor,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
    selections.forEach { selection ->
        Row(
            Modifier.fillMaxWidth().padding(vertical = 4.dp).semantics {
                isTraversalGroup = true
                val outcomes = listOfNotNull(
                    selection.match.home.name.takeIf { selection.homeWin },
                    "Empate".takeIf { selection.draw },
                    selection.match.away.name.takeIf { selection.awayWin }
                )
                val match = selection.match
                contentDescription = "Jogo ${match.number}: ${match.home.name} x ${match.away.name}; " +
                    "selecionados: ${outcomes.joinToString(", ")}"
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selection.match.number.toString(), (Modifier.width(43.dp)).uiId("tickets.0023"), color = contentColor, fontSize = 12.sp)
            Column(Modifier.weight(1f)) {
                Text(
                    selection.match.home.name,
                    color = contentColor,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.uiId("tickets.0024")
                )
                Text(
                    selection.match.away.name,
                    color = contentColor,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.uiId("tickets.0025")
                )
            }
            OutcomeMark(selection.homeWin, contentColor)
            OutcomeMark(selection.draw, contentColor)
            OutcomeMark(selection.awayWin, contentColor)
        }
    }
}

@Composable
private fun OutcomeMark(selected: Boolean, contentColor: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.padding(start = 5.dp).size(22.dp).border(1.dp, contentColor, CircleShape)
            .background(if (selected) contentColor else LoteriasCaixaDSColorTokens.Transparent, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_texto_1dabba),
                color = LoteriasCaixaDSColorTokens.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.uiId("tickets.0026")
            )
        }
    }
}

@Composable
fun LotogolSelection(bet: ConfirmedBet, contentColor: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(DeliveryR.string.ui_sua_aposta_3bcfc2),
        (Modifier.padding(top = 28.dp, bottom = 8.dp)).uiId("tickets.0027"),
        color = contentColor,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    bet.lotogolSelections.forEachIndexed { index, selection ->
        Row(
            Modifier.fillMaxWidth().padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_1_s_d020a2, (index + 1).toString()),
                (Modifier.width(22.dp)).uiId("tickets.0028"),
                color = contentColor,
                fontSize = 12.sp
            )
            Text(
                selection.match.home.name,
                (Modifier.weight(1f)).uiId("tickets.0029"),
                color = contentColor,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            ScoreMark(selection.homeScore, contentColor)
            Text(
                uiStrings.getString(DeliveryR.string.ui_x_2d7116),
                (Modifier.padding(horizontal = 5.dp)).uiId("tickets.0030"),
                color = contentColor,
                fontSize = 12.sp
            )
            ScoreMark(selection.awayScore, contentColor)
            Text(
                selection.match.away.name,
                (Modifier.weight(1f).padding(start = 7.dp)).uiId("tickets.0031"),
                color = contentColor,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ScoreMark(score: Int, contentColor: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.size(26.dp).border(1.dp, contentColor, RoundedCornerShape(3.dp)), contentAlignment = Alignment.Center) {
        Text(
            if (score >= LOTOGOL_MAXIMUM_EXACT_SCORE) uiStrings.getString(DeliveryR.string.ui_5_fa676d) else score.toString(),
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.uiId("tickets.0032")
        )
    }
}

private const val LOTOGOL_MAXIMUM_EXACT_SCORE = 5
