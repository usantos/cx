package br.gov.caixa.loterias.feature.tickets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.domain.betting.LotecaMatchSelection
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet

@Composable
internal fun ConfirmedBetSelection(bet: ConfirmedBet, contentColor: Color) {
    when {
        bet.lotecaSelections.isNotEmpty() -> LotecaSelection(bet.lotecaSelections, contentColor)
        bet.lotogolSelections.isNotEmpty() -> LotogolSelection(bet, contentColor)
        bet.numberColumns.size > 1 -> SuperSevenSelection(bet, contentColor)
        else -> NumericSelection(bet, contentColor)
    }
    ConfirmedBetComplements(bet, contentColor)
    ConfirmedBetBadges(bet, contentColor)
}

@Composable
private fun NumericSelection(bet: ConfirmedBet, contentColor: Color) {
    Text("Meus números", Modifier.padding(top = 38.dp), color = contentColor, fontSize = 17.sp)
    FlowRow(
        Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        bet.numbers.forEach { number -> NumberMark(number, contentColor) }
    }
    if (bet.clovers.isNotEmpty()) {
        Text("Trevos", Modifier.padding(top = 18.dp), color = contentColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        FlowRow(
            Modifier.fillMaxWidth().padding(top = 7.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            bet.clovers.forEach { clover -> NumberMark(clover, contentColor) }
        }
    }
}

@Composable
private fun SuperSevenSelection(bet: ConfirmedBet, contentColor: Color) {
    Text("Suas colunas", Modifier.padding(top = 38.dp), color = contentColor, fontSize = 17.sp)
    Row(
        Modifier.fillMaxWidth().padding(top = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        bet.numberColumns.take(7).forEachIndexed { index, column ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${index + 1}", color = contentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                column.forEach { digit ->
                    Box(
                        Modifier.padding(top = 4.dp).size(31.dp)
                            .border(1.dp, contentColor, RoundedCornerShape(3.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(digit.toString(), color = contentColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun LotecaSelection(selections: List<LotecaMatchSelection>, contentColor: Color) {
    Text("Sua aposta", Modifier.padding(top = 28.dp, bottom = 8.dp), color = contentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    Row(Modifier.fillMaxWidth().padding(bottom = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("Coluna", Modifier.width(43.dp), color = contentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text("Time", Modifier.weight(1f), color = contentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text("1     X     2", Modifier.width(82.dp), color = contentColor, fontSize = 11.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
    }
    selections.forEach { selection ->
        Row(
            Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(selection.match.number.toString(), Modifier.width(43.dp), color = contentColor, fontSize = 12.sp)
            Column(Modifier.weight(1f)) {
                Text(selection.match.home.name, color = contentColor, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(selection.match.away.name, color = contentColor, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            OutcomeMark(selection.homeWin, contentColor)
            OutcomeMark(selection.draw, contentColor)
            OutcomeMark(selection.awayWin, contentColor)
        }
    }
}

@Composable
private fun OutcomeMark(selected: Boolean, contentColor: Color) {
    Box(
        Modifier.padding(start = 5.dp).size(22.dp).border(1.dp, contentColor, CircleShape)
            .background(if (selected) contentColor else CaixaDSColorTokens.Transparent, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) Text("✓", color = CaixaDSColorTokens.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LotogolSelection(bet: ConfirmedBet, contentColor: Color) {
    Text("Sua aposta", Modifier.padding(top = 28.dp, bottom = 8.dp), color = contentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    bet.lotogolSelections.forEachIndexed { index, selection ->
        Row(
            Modifier.fillMaxWidth().padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("${index + 1}", Modifier.width(22.dp), color = contentColor, fontSize = 12.sp)
            Text(selection.match.home.name, Modifier.weight(1f), color = contentColor, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            ScoreMark(selection.homeScore, contentColor)
            Text("x", Modifier.padding(horizontal = 5.dp), color = contentColor, fontSize = 12.sp)
            ScoreMark(selection.awayScore, contentColor)
            Text(selection.match.away.name, Modifier.weight(1f).padding(start = 7.dp), color = contentColor, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ScoreMark(score: Int, contentColor: Color) {
    Box(Modifier.size(26.dp).border(1.dp, contentColor, RoundedCornerShape(3.dp)), contentAlignment = Alignment.Center) {
        Text(if (score >= 5) "5+" else score.toString(), color = contentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ConfirmedBetComplements(bet: ConfirmedBet, contentColor: Color) {
    bet.favoriteTeam?.let { team ->
        Text(
            "Time do coração: ${team.name}${team.state.takeIf(String::isNotBlank)?.let { "/$it" }.orEmpty()}",
            Modifier.padding(top = 17.dp),
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
    bet.luckyMonth?.let { month ->
        Text(
            "Mês da sorte: ${month.name}",
            Modifier.padding(top = 17.dp),
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ConfirmedBetBadges(bet: ConfirmedBet, contentColor: Color) {
    val labels = buildList {
        if (bet.exchange) add("Troca")
        if (bet.poolShare) add("Bolão")
        if (bet.combo) add("Combo")
        if (bet.mirror) add("Espelho")
        if (bet.surprise) add("Surpresinha")
        if (bet.recurringDraws > 0) add("Teimosinha")
    }
    if (labels.isEmpty()) return
    FlowRow(
        Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        labels.forEach { label ->
            Text(
                label.uppercase(),
                Modifier.border(1.dp, contentColor, RoundedCornerShape(2.dp)).padding(horizontal = 7.dp, vertical = 3.dp),
                color = contentColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun NumberMark(number: Int, contentColor: Color) {
    Box(
        Modifier.size(34.dp).border(1.dp, contentColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(number.toString().padStart(2, '0'), color = contentColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
