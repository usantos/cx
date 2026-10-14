package br.gov.caixa.loterias.core.domain.results

interface ResultsRepository {
    suspend fun latest(modality: String): DrawResult
    suspend fun byContest(modality: String, contest: Int): DrawResult
    suspend fun forConfirmedBet(bet: br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet): br.gov.caixa.loterias.core.domain.service.ServiceResult<List<DrawResult>> =
        br.gov.caixa.loterias.core.domain.service.ServiceResult.Success(
            (bet.contest until bet.contest + bet.recurringDraws.coerceAtLeast(1)).map { byContest(bet.modality, it) },
        )
}

data class DrawResult(
    val modality: String,
    val contest: Int,
    val drawDate: String,
    val drawLocation: String,
    val firstDrawNumbers: List<Int>,
    val secondDrawNumbers: List<Int>,
    val clovers: List<Int>,
    val accumulated: Boolean,
    val accumulatedNextContest: String,
    val totalRevenue: String,
    val notice: String,
    val payouts: List<DrawPayout>,
    val firstDrawNumberColumns: List<List<Int>> = if (firstDrawNumbers.isEmpty()) emptyList() else listOf(firstDrawNumbers),
    val secondDrawPayouts: List<DrawPayout> = emptyList(),
    val lotecaMatches: List<DrawSportsMatch> = emptyList(),
    val lotogolMatches: List<DrawSportsMatch> = emptyList(),
    val favoriteTeamReward: DrawSelectionReward? = null,
    val luckyMonthReward: DrawSelectionReward? = null,
    val firstDrawRegionalWinners: List<DrawRegionalWinner> = emptyList(),
    val secondDrawRegionalWinners: List<DrawRegionalWinner> = emptyList(),
    val nextContest: UpcomingDrawContest? = null,
    val nextFinalZeroContest: UpcomingDrawContest? = null,
    val nextFinalFiveContest: UpcomingDrawContest? = null,
    val nextSpecialContest: UpcomingDrawContest? = null,
    val specialContest: Boolean = false,
    val specialName: String = "",
)

data class DrawPayout(
    val description: String,
    val winners: Long,
    val value: String,
)

data class DrawSportsTeam(val name: String, val state: String = "", val score: String = "", val won: Boolean? = null)
data class DrawSportsMatch(
    val number: Int?, val home: DrawSportsTeam, val away: DrawSportsTeam,
    val draw: Boolean? = null, val homeScoreIndex: Int? = null, val awayScoreIndex: Int? = null,
    val legendLetter: String = "", val legendDescription: String = "",
)
data class DrawSelectionReward(val selection: String, val winners: Long, val value: String)
data class DrawRegionalWinner(val location: String, val description: String)
data class UpcomingDrawContest(val number: Int, val date: String, val estimatedPrize: String, val special: Boolean = false)
