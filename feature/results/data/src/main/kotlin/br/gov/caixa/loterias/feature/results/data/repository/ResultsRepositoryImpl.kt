package br.gov.caixa.loterias.feature.results.data.repository

import br.gov.caixa.loterias.feature.results.data.localizedMessage as messageText

import br.gov.caixa.loterias.core.network.mapper.flattenNumbers
import br.gov.caixa.loterias.feature.results.domain.model.DrawPayout
import br.gov.caixa.loterias.feature.results.domain.model.DrawRegionalWinner
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSelectionReward
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsMatch
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsTeam
import br.gov.caixa.loterias.feature.results.domain.repository.ResultsRepository
import br.gov.caixa.loterias.feature.results.domain.model.UpcomingDrawContest
import br.gov.caixa.loterias.feature.results.data.dto.DrawSportsMatchDto
import br.gov.caixa.loterias.feature.results.data.dto.DrawSportsTeamDto
import br.gov.caixa.loterias.feature.results.data.dto.DrawContestDto
import br.gov.caixa.loterias.feature.results.data.dto.DrawSelectionRewardDto
import br.gov.caixa.loterias.feature.results.data.dto.DrawRegionalWinnerDto
import br.gov.caixa.loterias.feature.results.data.dto.DrawResultDto
import com.google.gson.JsonElement
import br.gov.caixa.loterias.feature.results.data.datasource.remote.ResultsRemoteDataSource
import br.gov.caixa.loterias.core.data.mapper.serviceResultOf

class ResultsRepositoryImpl(private val remote: ResultsRemoteDataSource) : ResultsRepository {
    override suspend fun latest(modality: String): DrawResult {
        require(modality.isNotBlank()) { messageText("modalidade_obrigatoria_63c4e6") }
        return requireNotNull(remote.latestResult(modality)) { messageText("resultado_sem_payload_df1f51") }.toDomain()
    }

    override suspend fun byContest(modality: String, contest: Int): DrawResult {
        require(modality.isNotBlank()) { messageText("modalidade_obrigatoria_63c4e6") }
        require(contest > 0) { messageText("concurso_invalido_892893") }
        return requireNotNull(remote.resultByContest(modality, contest)) { messageText("resultado_sem_payload_df1f51") }.toDomain()
    }

    override suspend fun forConfirmedBet(bet: br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet) = serviceResultOf {
        require(bet.contest > 0) { messageText("concurso_invalido_892893") }
        val modality = when (bet.modalityId) {
            2 -> "megasena"
            3 -> "quina"
            7 -> "supersete"
            8 -> "lotofacil"
            9 -> "maismilionaria"
            11 -> "diadesorte"
            14 -> "lotogol"
            16 -> "lotomania"
            18 -> "duplasena"
            19 -> "loteca"
            20 -> "timemania"
            else -> bet.modality.lowercase().replace("_", "").replace("-", "").replace(" ", "")
        }
        val contests = (bet.contest until bet.contest + bet.recurringDraws.coerceAtLeast(1)).toList()
        remote.resultsForContests(modality, contests).map { it.toDomain() }
    }

    private suspend fun DrawResultDto.toDomain(): DrawResult {
        val shields = if (!lotecaMatches.isNullOrEmpty() || !lotogolMatches.isNullOrEmpty()) {
            try {
                remote.sportsTeamShields()
            } catch (failure: kotlinx.coroutines.CancellationException) {
                throw failure
            } catch (failure: java.io.IOException) {
                emptyMap()
            }
        } else {
            emptyMap()
        }
        val location = listOfNotNull(
            city?.name?.trim()?.takeIf(String::isNotEmpty),
            (city?.state?.abbreviation ?: state?.abbreviation)?.trim()?.takeIf(String::isNotEmpty)
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
            secondDrawPayouts = secondDrawPayouts.orEmpty().map {
                DrawPayout(it.description.orEmpty(), it.winners ?: 0L, it.value.orEmpty())
            },
            lotecaMatches = lotecaMatches.orEmpty().map { it.toDomain(shields) },
            lotogolMatches = lotogolMatches.orEmpty().map { it.toDomain(shields) },
            favoriteTeamReward = favoriteTeamReward?.toDomain(),
            luckyMonthReward = luckyMonthReward?.toDomain(),
            firstDrawRegionalWinners = firstDrawRegionalWinners.orEmpty().map { it.toDomain() },
            secondDrawRegionalWinners = secondDrawRegionalWinners.orEmpty().map { it.toDomain() },
            nextContest = nextContest?.toUpcoming(),
            nextFinalZeroContest = nextFinalZeroContest?.toUpcoming(),
            nextFinalFiveContest = nextFinalFiveContest?.toUpcoming(),
            nextSpecialContest = nextSpecialContest?.toUpcoming(),
            specialContest = contest?.contestType.equals("ESPECIAL", true),
            specialName = contest?.modalityDetails?.specialName.orEmpty()
        )
    }

    private fun DrawSportsTeamDto?.toDomain(shields: Map<Int, String>) = DrawSportsTeam(
        name = this?.name?.takeIf(String::isNotBlank) ?: this?.shortName?.takeIf(String::isNotBlank) ?: this?.parameterTeam?.name.orEmpty(),
        state = this?.state ?: this?.parameterTeam?.state.orEmpty(),
        score = this?.score.orEmpty(),
        won = this?.won,
        shieldBase64 = shields[this?.parameterTeam?.number],
        countryCode = this?.countryCode ?: this?.parameterTeam?.countryCode.orEmpty(),
        nationalTeam = this?.nationalTeam ?: this?.parameterTeam?.selectionIndicator
    )
    private fun DrawSportsMatchDto.toDomain(shields: Map<Int, String>) = DrawSportsMatch(
        number,
        home.toDomain(shields),
        away.toDomain(shields),
        draw,
        homeScoreIndex,
        awayScoreIndex,
        legendLetter.orEmpty(),
        legendDescription.orEmpty()
    )
    private fun DrawSelectionRewardDto.toDomain() = DrawSelectionReward(
        selection = month?.name ?: listOfNotNull(
            team?.name?.takeIf(String::isNotBlank),
            team?.state?.takeIf(String::isNotBlank)
        ).joinToString("/"),
        winners = winners ?: 0,
        value = value.orEmpty()
    )
    private fun DrawRegionalWinnerDto.toDomain() = DrawRegionalWinner(
        listOfNotNull(
            city?.name?.takeIf(String::isNotBlank),
            (state?.abbreviation ?: city?.state?.abbreviation)?.takeIf(String::isNotBlank)
        ).joinToString(" - "),
        description.orEmpty()
    )
    private fun DrawContestDto.toUpcoming() = number?.let {
        UpcomingDrawContest(it, drawDate.orEmpty(), estimatedPrize.orEmpty(), contestType.equals("ESPECIAL", true))
    }
    private fun JsonElement?.numberColumns(): List<List<Int>> = when {
        this == null || isJsonNull -> emptyList()
        isJsonArray && asJsonArray.any(JsonElement::isJsonArray) -> asJsonArray.map { it.flattenNumbers() }
        else -> listOf(flattenNumbers()).filter(List<Int>::isNotEmpty)
    }
}
