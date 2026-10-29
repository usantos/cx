package br.gov.caixa.loterias.feature.results.presentation.mapper

import br.gov.caixa.loterias.core.presentation.accessibility.PortugueseSpeech
import br.gov.caixa.loterias.feature.results.domain.model.DrawPayout
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsMatch
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsTeam
import java.math.BigInteger

internal fun resultDrawSpokenDescription(result: DrawResult, modality: String): String = buildList {
    add(PortugueseSpeech.expand(resultTitle(result, modalityName(modality)).replace("+", "mais ")))
    add("Concurso ${PortugueseSpeech.number(result.contest)}")
    if (modality == "supersete") {
        val columns = result.firstDrawNumberColumns.takeIf { it.size == SUPER_SEVEN_COLUMN_COUNT }
            ?: result.firstDrawNumbers.map { listOf(it) }
        columns.forEachIndexed { index, numbers ->
            add("Coluna ${PortugueseSpeech.number(index + 1)}: ${spokenNumbers(numbers)}")
        }
    } else {
        val title = if (result.secondDrawNumbers.isEmpty()) "Números sorteados" else "Primeiro sorteio"
        add("$title: ${spokenNumbers(result.firstDrawNumbers.sorted())}")
    }
    if (result.secondDrawNumbers.isNotEmpty()) add("Segundo sorteio: ${spokenNumbers(result.secondDrawNumbers.sorted())}")
    if (result.clovers.isNotEmpty()) add("Trevos sorteados: ${spokenNumbers(result.clovers.sorted())}")
    if (modality == "timemania") result.favoriteTeamReward?.selection?.let { add("Time do coração: ${PortugueseSpeech.expand(it)}") }
    if (modality == "diadesorte") result.luckyMonthReward?.selection?.let { add("Mês da sorte: ${PortugueseSpeech.expand(it)}") }
    if (result.drawDate.isNotBlank()) add(resultDateSpokenDescription(result.drawDate, modality))
}.joinToString(". ", postfix = ".")

internal fun resultDateSpokenDescription(date: String, modality: String): String =
    "${if (modality == "loteca") "Data do resultado" else "Data do sorteio"}: ${PortugueseSpeech.date(date)}"

internal fun resultPayoutSpokenDescription(title: String, modality: String, payout: DrawPayout): String = buildList {
    add(PortugueseSpeech.expand(title))
    add(PortugueseSpeech.expand(resultPayoutDescription(modality, payout.description)))
    add(spokenWinners(payout.winners))
    if (payout.winners > 0 && payout.value.isNotBlank()) add("Prêmio: ${spokenResultMoney(payout.value)}")
}.joinToString(". ", postfix = ".")

internal fun spokenWinners(count: Long): String = when (count) {
    0L -> "Sem ganhadores"
    1L -> "Uma aposta ganhadora"
    else -> PortugueseSpeech.integer(BigInteger.valueOf(count), feminine = true).replaceFirstChar(Char::titlecase) + " apostas ganhadoras"
}

internal fun spokenResultMoney(value: String): String = value.toBigDecimalOrNull()?.let(PortugueseSpeech::currency)
    ?: value.takeIf(String::isNotBlank)?.let(PortugueseSpeech::expand) ?: "não informado"

internal fun resultSportsSpokenDescription(match: DrawSportsMatch, lotogol: Boolean): String = buildList {
    match.number?.let { add("Jogo ${PortugueseSpeech.number(it)}") }
    add(spokenTeam(match.home))
    val homeScore = spokenScore(match.home.score, if (lotogol) match.homeScoreIndex else null)
    val awayScore = spokenScore(match.away.score, if (lotogol) match.awayScoreIndex else null)
    if (homeScore != null || awayScore != null) add("Placar ${homeScore ?: "não informado"} a ${awayScore ?: "não informado"}")
    add(spokenTeam(match.away))
    when {
        match.draw == true -> add("Resultado: empate")
        match.home.won == true -> add("Vencedor: ${PortugueseSpeech.expand(match.home.name)}")
        match.away.won == true -> add("Vencedor: ${PortugueseSpeech.expand(match.away.name)}")
    }
    if (match.legendLetter.isNotBlank()) add("Legenda ${match.legendLetter}")
    if (match.legendDescription.isNotBlank()) add(PortugueseSpeech.expand(match.legendDescription))
}.joinToString(". ", postfix = ".")

private fun spokenNumbers(numbers: List<Int>) = numbers.takeIf(List<Int>::isNotEmpty)
    ?.joinToString(", ") { PortugueseSpeech.number(it) } ?: "não informados"

private fun spokenScore(score: String, index: Int?): String? =
    score.takeIf(String::isNotBlank)?.replace("+", " ou mais")?.let(PortugueseSpeech::expand)
        ?: index?.let { if (it >= LOTOGOL_MAX_SCORE_INDEX) "quatro ou mais" else PortugueseSpeech.number(it) }

private const val SUPER_SEVEN_COLUMN_COUNT = 7
private const val LOTOGOL_MAX_SCORE_INDEX = 4

private fun spokenTeam(team: DrawSportsTeam): String {
    val region = if (team.countryCode.isNotBlank() && (!team.countryCode.equals("BRA", true) || team.name.equals("BRASIL", true))) {
        team.countryCode
    } else {
        team.state
    }
    return listOf(PortugueseSpeech.expand(team.name), region).filter(String::isNotBlank).joinToString(", ")
}
