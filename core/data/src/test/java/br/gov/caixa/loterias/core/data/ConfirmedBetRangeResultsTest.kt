package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.results.*
import br.gov.caixa.loterias.core.domain.service.ServiceResult
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet
import br.gov.caixa.loterias.core.network.DrawResultDto
import br.gov.caixa.loterias.core.network.DrawContestDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class ConfirmedBetRangeResultsTest {
    @Test fun `teimosinha fetches every contest once from initial contest`() = runTest {
        var requested = emptyList<Int>()
        var modality = ""
        val source = object : ResultsRemoteDataSource {
            override suspend fun latestResult(modality: String): DrawResultDto? = error("not latest")
            override suspend fun resultByContest(modality: String, contest: Int): DrawResultDto? = error("not individual")
            override suspend fun resultsForContests(code: String, contests: List<Int>): List<DrawResultDto> {
                modality = code; requested = contests
                return contests.map { DrawResultDto(contest = DrawContestDto(number = it, modality = "MEGA_SENA")) }
            }
        }
        val bet = ConfirmedBet(7, "Mega Sena", 2800, "", "Não premiada", listOf(1,2,3), modalityId = 2, recurringDraws = 3)
        val result = GetConfirmedBetResults(RemoteResultsRepository(source))(bet) as ServiceResult.Success
        assertEquals("megasena", modality)
        assertEquals(listOf(2800,2801,2802), requested)
        assertEquals(requested, result.value.map { it.contest })
    }
}
