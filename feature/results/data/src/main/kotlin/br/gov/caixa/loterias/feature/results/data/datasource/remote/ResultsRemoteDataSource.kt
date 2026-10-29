package br.gov.caixa.loterias.feature.results.data.datasource.remote

import br.gov.caixa.loterias.feature.results.data.dto.DrawResultDto

interface ResultsRemoteDataSource {
    suspend fun sportsTeamShields(): Map<Int, String> = emptyMap()
    suspend fun latestResult(modality: String): DrawResultDto?
    suspend fun resultByContest(modality: String, contest: Int): DrawResultDto?
    suspend fun resultsForContests(code: String, contests: List<Int>): List<DrawResultDto> =
        contests.mapNotNull { resultByContest(code, it) }
}
