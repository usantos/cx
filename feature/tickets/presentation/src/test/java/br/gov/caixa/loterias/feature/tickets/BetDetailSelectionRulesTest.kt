package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsMatch
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsTeam
import br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketAward
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetReceipt
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.confirmedBetContestStatus
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.confirmedBetDetailHitCount
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.detailSportsResultLabel
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test

class BetDetailSelectionRulesTest {
    private fun result(numbers: List<Int>) = DrawResult(
        "SUPER_7", 1, "", "", numbers, emptyList(), emptyList(), false, "", "", "", emptyList(),
        firstDrawNumberColumns = numbers.map(
            ::listOf
        )
    )
    private val superBet = ConfirmedBet(1, "SUPER_7", 1, "", "", List(7) { 1 }, modalityId = 7, numberColumns = List(7) { listOf(1) })

    @Test fun repeatedSuperSevenDigitsCountEachMatchingColumn() {
        assertEquals(7, confirmedBetDetailHitCount(superBet, result(List(7) { 1 })))
        val swapped = superBet.copy(numbers = listOf(1, 2, 3, 4, 5, 6, 7), numberColumns = (1..7).map(::listOf))
        assertEquals(0, confirmedBetDetailHitCount(swapped, result(listOf(2, 3, 4, 5, 6, 7, 1))))
    }

    @Test fun recurringContestStatusDoesNotPropagateOnePrizeToLosingContests() {
        val bet = superBet.copy(
            status = "Premiada",
            receipt = ConfirmedBetReceipt(awards = listOf(CheckedTicketAward(1, "Premiado", value = BigDecimal.TEN)))
        )
        assertEquals("Premiado", confirmedBetContestStatus(bet, 1))
        assertEquals("Não premiado", confirmedBetContestStatus(bet, 2))
        assertEquals("Prêmio pago", confirmedBetContestStatus(bet.copy(redeemedPrize = true), 1))
    }

    @Test fun lotogolResultsRetainBothActualScoresAndLegacyScoreBuckets() {
        val match = DrawSportsMatch(1, DrawSportsTeam("Casa", score = "2"), DrawSportsTeam("Fora", score = "3"))
        assertEquals("1. Casa 2 × 3 Fora", detailSportsResultLabel(match, true))
        assertEquals(
            "1. Casa 4 ou mais × 0 Fora",
            detailSportsResultLabel(
                match.copy(home = match.home.copy(score = ""), away = match.away.copy(score = ""), homeScoreIndex = 4, awayScoreIndex = 0),
                true
            )
        )
    }
}
