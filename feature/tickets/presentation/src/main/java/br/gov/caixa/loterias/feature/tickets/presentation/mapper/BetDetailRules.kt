package br.gov.caixa.loterias.feature.tickets.presentation.mapper

import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsMatch
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText

fun shouldShowInvalidTeimosinhaAlert(isOriginalRecurringBet: Boolean, hasPrizeRedemption: Boolean) =
    isOriginalRecurringBet && hasPrizeRedemption

fun betDetailRoute(isOriginalRecurringBet: Boolean, hasPrizeRedemption: Boolean) =
    "tickets/bet-detail?original=$isOriginalRecurringBet&resgate=$hasPrizeRedemption"

internal enum class TeimosinhaNotice { ORIGINAL_PAID, FIRST_CONTEST }

internal fun teimosinhaNotice(bet: br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet): TeimosinhaNotice? = when {
    bet.recurringDraws <= 0 || bet.statusId == 100L -> null
    !bet.exchange && bet.statusId == 11L -> TeimosinhaNotice.ORIGINAL_PAID
    bet.status.contains("não apurado", ignoreCase = true) -> null
    else -> TeimosinhaNotice.FIRST_CONTEST
}

internal fun confirmedBetDetailHitCount(bet: ConfirmedBet, result: DrawResult): Int =
    if (bet.modalityId == 7 || bet.modality.contains("super", ignoreCase = true)) {
        bet.numberColumns.zip(superSevenDrawColumns(result)).count { (selected, drawn) -> selected.any(drawn::contains) }
    } else {
        bet.numbers.toSet().intersect(result.firstDrawNumbers.toSet()).size
    }

internal fun superSevenDrawColumns(result: DrawResult): List<List<Int>> =
    result.firstDrawNumberColumns.takeIf { it.size == 7 } ?: result.firstDrawNumbers.map(::listOf)

internal fun confirmedBetContestStatus(bet: ConfirmedBet, contest: Int): String {
    val award = bet.receipt?.awards?.firstOrNull {
        it.contest == contest && (
            it.value?.signum() == 1 ||
                (it.status.contains("premiad", true) && !it.status.contains("não", true))
            )
    } ?: return messageText("nao_premiado_527d80")
    return if (bet.redeemedPrize) messageText("final_prize_paid") else award.status.ifBlank { messageText("final_awarded") }
}

internal fun detailSportsResultLabel(match: DrawSportsMatch, lotogol: Boolean): String {
    fun score(value: String, index: Int?): String = value.ifBlank {
        if (lotogol && index != null) {
            if (index >= 4) "4 ou mais" else index.toString()
        } else {
            ""
        }
    }
    return listOfNotNull(
        match.number?.let { "$it." },
        match.home.name,
        score(match.home.score, match.homeScoreIndex),
        "×",
        score(match.away.score, match.awayScoreIndex),
        match.away.name
    )
        .filter(String::isNotBlank).joinToString(" ")
}
