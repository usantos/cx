package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.betting.LotecaMatchSelection
import br.gov.caixa.loterias.core.domain.betting.LotogolMatchSelection
import br.gov.caixa.loterias.core.domain.betting.LuckyMonth
import br.gov.caixa.loterias.core.domain.betting.SportsMatch
import br.gov.caixa.loterias.core.domain.betting.SportsTeam
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet

/** Fixtures determinísticas exclusivas do APK mock para homologar estados que dependiam de Serializable extras. */
internal fun mockConfirmedBet(
    variant: String,
    originalRecurringBet: Boolean = false,
    redeemedPrize: Boolean = false,
): ConfirmedBet {
    val base = ConfirmedBet(
        id = -1,
        modality = "Mega-Sena",
        contest = 2800,
        date = "06/09/2026",
        time = "12:30:00",
        status = if (redeemedPrize) "Prêmio resgatado" else "Em acompanhamento",
        numbers = listOf(5, 10, 23, 34, 42, 58),
        originalRecurringBet = originalRecurringBet,
        redeemedPrize = redeemedPrize,
        exchangeBetId = if (redeemedPrize) 2 else null,
        modalityId = 2,
        contestTypeId = 1,
    )
    return when (variant.lowercase()) {
        "plus", "mais-milionaria" -> base.copy(
            id = -9,
            modality = "+Milionária",
            modalityId = 9,
            numbers = listOf(3, 9, 18, 27, 35, 49),
            numberColumns = listOf(listOf(3, 9, 18, 27, 35, 49)),
            clovers = listOf(2, 5),
            recurringDraws = 3,
        )
        "super-seven" -> base.copy(
            id = -7,
            modality = "Super Sete",
            modalityId = 7,
            numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8),
            numberColumns = listOf(listOf(1), listOf(2, 3), listOf(4), listOf(5), listOf(6), listOf(7), listOf(8)),
        )
        "timemania" -> base.copy(
            id = -20,
            modality = "Timemania",
            modalityId = 20,
            numbers = (1..10).toList(),
            numberColumns = listOf((1..10).toList()),
            favoriteTeam = SportsTeam(101, "Time Azul", "DF"),
        )
        "dia-de-sorte" -> base.copy(
            id = -11,
            modality = "Dia de Sorte",
            modalityId = 11,
            numbers = listOf(1, 4, 9, 14, 18, 23, 30),
            numberColumns = listOf(listOf(1, 4, 9, 14, 18, 23, 30)),
            luckyMonth = LuckyMonth(9, "Setembro", "SET"),
        )
        "loteca" -> sportsBet(base, loteca = true)
        "lotogol" -> sportsBet(base, loteca = false)
        "pool" -> base.copy(id = -30, poolShare = true, quantity = 4)
        else -> base
    }
}

private fun sportsBet(base: ConfirmedBet, loteca: Boolean): ConfirmedBet {
    val teams = listOf(
        SportsTeam(1, "Time da Casa", "SP"),
        SportsTeam(2, "Time Visitante", "RJ"),
        SportsTeam(3, "Clube Azul", "MG"),
        SportsTeam(4, "Clube Verde", "BA"),
    )
    val matches = List(if (loteca) 14 else 5) { index ->
        SportsMatch(index + 1, teams[(index * 2) % teams.size], teams[(index * 2 + 1) % teams.size])
    }
    return if (loteca) {
        base.copy(
            id = -19,
            modality = "Loteca",
            modalityId = 19,
            numbers = emptyList(),
            numberColumns = emptyList(),
            lotecaSelections = matches.mapIndexed { index, match ->
                LotecaMatchSelection(match, homeWin = index % 3 == 0, draw = index % 3 == 1, awayWin = index % 3 == 2)
            },
        )
    } else {
        base.copy(
            id = -14,
            modality = "Lotogol",
            modalityId = 14,
            numbers = emptyList(),
            numberColumns = emptyList(),
            lotogolSelections = matches.mapIndexed { index, match -> LotogolMatchSelection(match, index % 6, (index + 1) % 6) },
        )
    }
}
