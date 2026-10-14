package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.results.*
import br.gov.caixa.loterias.core.network.DrawSportsMatchDto
import br.gov.caixa.loterias.core.network.DrawSportsTeamDto
import br.gov.caixa.loterias.core.network.DrawContestDto
import br.gov.caixa.loterias.core.network.DrawSelectionRewardDto
import br.gov.caixa.loterias.core.network.DrawRegionalWinnerDto
import br.gov.caixa.loterias.core.network.DrawResultDto
import com.google.gson.JsonElement

class RemoteResultsRepository(
    private val remote: ResultsRemoteDataSource,
) : ResultsRepository {
    override suspend fun latest(modality: String): DrawResult {
        require(modality.isNotBlank()) { "Modalidade obrigatória" }
        return requireNotNull(remote.latestResult(modality)) { "Resultado sem payload" }.toDomain()
    }

    override suspend fun byContest(modality: String, contest: Int): DrawResult {
        require(modality.isNotBlank()) { "Modalidade obrigatória" }
        require(contest > 0) { "Concurso inválido" }
        return requireNotNull(remote.resultByContest(modality, contest)) { "Resultado sem payload" }.toDomain()
    }

    override suspend fun forConfirmedBet(bet: br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet) = serviceResultOf {
        require(bet.contest > 0) { "Concurso inválido" }
        val modality = when (bet.modalityId) {
            2 -> "megasena"; 3 -> "quina"; 7 -> "supersete"; 8 -> "lotofacil"
            9 -> "maismilionaria"; 11 -> "diadesorte"; 14 -> "lotogol"; 16 -> "lotomania"
            18 -> "duplasena"; 19 -> "loteca"; 20 -> "timemania"
            else -> bet.modality.lowercase().replace("_", "").replace("-", "").replace(" ", "")
        }
        val contests = (bet.contest until bet.contest + bet.recurringDraws.coerceAtLeast(1)).toList()
        remote.resultsForContests(modality, contests).map { it.toDomain() }
    }

    private fun DrawResultDto.toDomain(): DrawResult {
        val location = listOfNotNull(
            city?.name?.trim()?.takeIf(String::isNotEmpty),
            (city?.state?.abbreviation ?: state?.abbreviation)?.trim()?.takeIf(String::isNotEmpty),
        ).joinToString(" - ")
        return DrawResult(
            modality = contest?.modality.orEmpty(),
            contest = contest?.number ?: 0,
            drawDate = contest?.drawDate.orEmpty(),
            drawLocation = location,
            firstDrawNumbers = firstDrawNumbers.flattenNumbers(),
            secondDrawNumbers = secondDrawNumbers.orEmpty(),
            clovers = clovers.orEmpty(),
            accumulated = accumulated == true,
            accumulatedNextContest = accumulatedNextContest.orEmpty(),
            totalRevenue = totalRevenue.orEmpty(),
            notice = notice.orEmpty(),
            payouts = payouts.orEmpty().map { DrawPayout(it.description.orEmpty(), it.winners ?: 0L, it.value.orEmpty()) },
            firstDrawNumberColumns = firstDrawNumbers.numberColumns(),
            secondDrawPayouts = secondDrawPayouts.orEmpty().map { DrawPayout(it.description.orEmpty(), it.winners ?: 0L, it.value.orEmpty()) },
            lotecaMatches = lotecaMatches.orEmpty().map { it.toDomain() },
            lotogolMatches = lotogolMatches.orEmpty().map { it.toDomain() },
            favoriteTeamReward = favoriteTeamReward?.toDomain(),
            luckyMonthReward = luckyMonthReward?.toDomain(),
            firstDrawRegionalWinners = firstDrawRegionalWinners.orEmpty().map { it.toDomain() },
            secondDrawRegionalWinners = secondDrawRegionalWinners.orEmpty().map { it.toDomain() },
            nextContest = nextContest?.toUpcoming(),
            nextFinalZeroContest = nextFinalZeroContest?.toUpcoming(),
            nextFinalFiveContest = nextFinalFiveContest?.toUpcoming(),
            nextSpecialContest = nextSpecialContest?.toUpcoming(),
            specialContest = contest?.contestType.equals("ESPECIAL", true),
            specialName = contest?.modalityDetails?.specialName.orEmpty(),
        )
    }

    private fun DrawSportsTeamDto?.toDomain() = DrawSportsTeam(
        name = this?.name?.takeIf(String::isNotBlank) ?: this?.shortName?.takeIf(String::isNotBlank) ?: this?.parameterTeam?.name.orEmpty(),
        state = this?.state ?: this?.parameterTeam?.state.orEmpty(), score = this?.score.orEmpty(), won = this?.won,
    )
    private fun DrawSportsMatchDto.toDomain() = DrawSportsMatch(
        number, home.toDomain(), away.toDomain(), draw, homeScoreIndex, awayScoreIndex,
        legendLetter.orEmpty(), legendDescription.orEmpty(),
    )
    private fun DrawSelectionRewardDto.toDomain() = DrawSelectionReward(
        selection = month?.name ?: listOfNotNull(team?.name?.takeIf(String::isNotBlank),
            team?.state?.takeIf(String::isNotBlank)).joinToString("/"),
        winners = winners ?: 0, value = value.orEmpty(),
    )
    private fun DrawRegionalWinnerDto.toDomain() = DrawRegionalWinner(
        listOfNotNull(city?.name?.takeIf(String::isNotBlank),
            (state?.abbreviation ?: city?.state?.abbreviation)?.takeIf(String::isNotBlank)).joinToString(" - "),
        description.orEmpty(),
    )
    private fun DrawContestDto.toUpcoming() = number?.let {
        UpcomingDrawContest(it, drawDate.orEmpty(), estimatedPrize.orEmpty(), contestType.equals("ESPECIAL", true))
    }
    private fun JsonElement?.numberColumns(): List<List<Int>> = when {
        this == null || isJsonNull -> emptyList()
        isJsonArray && asJsonArray.any(JsonElement::isJsonArray) -> asJsonArray.map { it.flattenNumbers() }
        else -> listOf(flattenNumbers()).filter(List<Int>::isNotEmpty)
    }

    private fun JsonElement?.flattenNumbers(): List<Int> = when {
        this == null || isJsonNull -> emptyList()
        isJsonArray -> asJsonArray.flatMap { it.flattenNumbers() }
        isJsonPrimitive && asJsonPrimitive.isNumber -> listOf(asInt)
        else -> emptyList()
    }
}
