package br.gov.caixa.loterias.feature.results

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.domain.results.*

internal fun sportsResultLabel(match: DrawSportsMatch, lotogol: Boolean): String {
    fun score(value: String, index: Int?) = value.ifBlank {
        when { index == null -> ""; index >= 4 -> "4 ou mais"; else -> index.toString() }
    }
    val homeScore = score(match.home.score, if (lotogol) match.homeScoreIndex else null)
    val awayScore = score(match.away.score, if (lotogol) match.awayScoreIndex else null)
    return listOfNotNull(match.number?.let { "$it." }, match.home.name,
        homeScore.takeIf(String::isNotBlank), "×", awayScore.takeIf(String::isNotBlank), match.away.name).joinToString(" ")
}

internal fun sportsWinnerLabel(match: DrawSportsMatch): String = when {
    match.draw == true -> "Empate"
    match.home.won == true -> "Vencedor: ${match.home.name}"
    match.away.won == true -> "Vencedor: ${match.away.name}"
    else -> ""
}

@Composable
internal fun DrawSportsResults(result: DrawResult, color: Color) {
    (result.lotecaMatches.map { it to false } + result.lotogolMatches.map { it to true }).forEach { (match, lotogol) ->
        Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text(sportsResultLabel(match, lotogol), color = color, fontWeight = FontWeight.Bold)
            sportsWinnerLabel(match).takeIf(String::isNotBlank)?.let { Text(it, color = color) }
            if (match.legendDescription.isNotBlank()) Text(
                listOf(match.legendLetter, match.legendDescription).filter(String::isNotBlank).joinToString(" • "), color = color,
            )
        }
    }
}

@Composable
internal fun DrawPayoutRows(title: String, payouts: List<DrawPayout>, color: Color, modality: String = "") {
    if (payouts.isEmpty()) return
    Text(title, Modifier.padding(top = 16.dp), color = color, fontWeight = FontWeight.Bold)
    payouts.forEach { payout ->
        Text(resultPayoutDescription(modality, payout.description), Modifier.padding(top = 12.dp), color = color, fontWeight = FontWeight.Bold)
        Text(resultWinners(payout.winners), color = color)
        if (payout.winners > 0 && payout.value.isNotBlank()) Text("Prêmio: ${resultMoney(payout.value)}", color = color)
    }
}

@Composable
internal fun DrawResultSupplement(
    result: DrawResult, color: Color, includeFirstPayouts: Boolean = false,
    includeContestSummary: Boolean = true,
) {
    if (includeFirstPayouts) DrawPayoutRows(
        if (result.secondDrawPayouts.isNotEmpty()) "Premiação do 1º sorteio" else "Premiação",
        result.payouts, color, drawModalityCode(result.modality),
    )
    DrawPayoutRows("Premiação do 2º sorteio", result.secondDrawPayouts, color, drawModalityCode(result.modality))
    listOf("Time do coração" to result.favoriteTeamReward, "Mês de sorte" to result.luckyMonthReward).forEach { (title, reward) ->
        reward?.let {
            Text("$title: ${it.selection}", Modifier.padding(top = 16.dp), color = color, fontWeight = FontWeight.Bold)
            Text(resultWinners(it.winners), color = color)
            if (it.winners > 0 && it.value.isNotBlank()) Text("Prêmio: ${resultMoney(it.value)}", color = color)
        }
    }
    listOf("Ganhadores por região" to result.firstDrawRegionalWinners,
        "Ganhadores por região do 2º sorteio" to result.secondDrawRegionalWinners).forEach { (title, winners) ->
        if (winners.isNotEmpty()) Text(title, Modifier.padding(top = 16.dp), color = color, fontWeight = FontWeight.Bold)
        winners.forEach { winner ->
            Text(winner.location, Modifier.padding(top = 10.dp), color = color, fontWeight = FontWeight.Bold)
            Text(winner.description, color = color)
        }
    }
    // VisualizarResultadosActivity renders prize details and revenue only.
    // The service summary belongs to other result presentations in CX.
    if (!includeContestSummary) return
    if (result.specialContest) Text("Concurso especial", Modifier.padding(top = 12.dp), color = color, fontWeight = FontWeight.Bold)
    if (result.drawLocation.isNotBlank()) Text("Local do sorteio: ${result.drawLocation}", Modifier.padding(top = 16.dp), color = color)
    if (result.notice.isNotBlank()) Text(result.notice, Modifier.padding(top = 12.dp), color = color)
    if (result.accumulated) Text("Acumulou", Modifier.padding(top = 12.dp), color = color, fontWeight = FontWeight.Bold)
    if (result.accumulatedNextContest.isNotBlank()) Text("Acumulado para o próximo concurso: ${resultMoney(result.accumulatedNextContest)}", color = color)
    listOf("Próximo concurso" to result.nextContest, "Próximo concurso final zero" to result.nextFinalZeroContest,
        "Próximo concurso final cinco" to result.nextFinalFiveContest, "Próximo concurso especial" to result.nextSpecialContest).forEach { (title, contest) ->
        contest?.let {
            Text("$title: ${it.number}", Modifier.padding(top = 12.dp), color = color, fontWeight = FontWeight.Bold)
            if (it.date.isNotBlank()) Text(it.date, color = color)
            if (it.estimatedPrize.isNotBlank()) Text("Estimativa de prêmio: ${resultMoney(it.estimatedPrize)}", color = color)
        }
    }
}

@Composable
internal fun DrawNumberColumns(columns: List<List<Int>>, color: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        columns.forEachIndexed { index, numbers ->
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Text("${index + 1}ª coluna", color = color)
                numbers.forEach { Text(it.toString(), color = color, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

internal fun drawModalityCode(modality: String): String {
    val normalized = java.text.Normalizer.normalize(modality, java.text.Normalizer.Form.NFD)
        .replace(Regex("\\p{M}"), "").lowercase().replace(Regex("[_\\s-]"), "")
    return when (normalized) { "super7" -> "supersete"; "+milionaria" -> "maismilionaria"; else -> normalized }
}

internal fun drawResultTitle(result: DrawResult, ordinaryName: String): String {
    if (!result.specialContest) return ordinaryName
    return result.specialName.ifBlank { when (drawModalityCode(result.modality)) {
        "megasena" -> "Mega da Virada"
        "quina" -> "Quina de São João"
        "lotofacil" -> "Lotofácil da Independência"
        "duplasena" -> "Dupla de Páscoa"
        "loteca" -> "Loteca Especial"
        else -> ordinaryName
    } }
}

internal fun confirmedBetFirstDrawHitCount(
    bet: br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet,
    result: DrawResult,
): Int = if (bet.modalityId == 7 || drawModalityCode(bet.modality) == "supersete" || drawModalityCode(result.modality) == "supersete") {
    bet.numberColumns.zip(result.firstDrawNumberColumns).count { (selected, drawn) -> selected.any(drawn::contains) }
} else {
    bet.numbers.toSet().intersect(result.firstDrawNumbers.toSet()).size
}
