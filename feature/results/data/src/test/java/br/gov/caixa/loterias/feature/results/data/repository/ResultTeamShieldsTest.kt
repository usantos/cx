package br.gov.caixa.loterias.feature.results.data.repository

import br.gov.caixa.loterias.feature.results.data.datasource.remote.ResultsRemoteDataSource
import br.gov.caixa.loterias.feature.results.data.dto.DrawResultDto
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class ResultTeamShieldsTest {
    private val sportsDto = Gson().fromJson("""{"concurso":{"numero":1,"modalidade":"LOTECA"},
        "partidasLoteca":[{"equipe1":{"nome":"Casa","siglaPais":"BRA","indicadorSelecao":true,"parametroEquipe":{"numero":42}},"equipe2":{"nome":"Fora","parametroEquipe":{"numero":43}}}],
        "premiacaoTimeDoCoracao":{"equipe":{"numero":42,"nome":"Casa","uf":"SP"},"quantidadeGanhadores":1,"valor":"8"}}""", DrawResultDto::class.java)

    @Test fun `sports teams reuse real shield catalog by team number`() = runTest {
        val result = ResultsRepositoryImpl(remote { mapOf(42 to "encoded-shield") }).latest("loteca")
        assertEquals("encoded-shield", result.lotecaMatches.single().home.shieldBase64)
        assertNull(result.lotecaMatches.single().away.shieldBase64)
        assertEquals("BRA", result.lotecaMatches.single().home.countryCode)
        assertEquals(true, result.lotecaMatches.single().home.nationalTeam)
    }

    @Test fun `shield network failure preserves draw and default crest`() = runTest {
        val result = ResultsRepositoryImpl(remote { throw java.io.IOException("offline") }).latest("loteca")
        assertEquals("Casa", result.lotecaMatches.single().home.name)
        assertNull(result.lotecaMatches.single().home.shieldBase64)
    }

    @Test fun `shield cancellation propagates`() = runTest {
        try {
            ResultsRepositoryImpl(remote { throw CancellationException("cancelled") }).latest("loteca")
            fail("Cancellation must propagate")
        } catch (_: CancellationException) { }
    }

    @Test fun `numeric draws do not fetch sports catalog`() = runTest {
        val dto = DrawResultDto()
        val source = object : ResultsRemoteDataSource {
            override suspend fun latestResult(modality: String) = dto
            override suspend fun resultByContest(modality: String, contest: Int) = dto
            override suspend fun sportsTeamShields(): Map<Int, String> = error("Unexpected shield request")
        }
        assertTrue(ResultsRepositoryImpl(source).latest("megasena").lotecaMatches.isEmpty())
    }

    private fun remote(shields: suspend () -> Map<Int, String>) = object : ResultsRemoteDataSource {
        override suspend fun latestResult(modality: String) = sportsDto
        override suspend fun resultByContest(modality: String, contest: Int) = sportsDto
        override suspend fun sportsTeamShields() = shields()
    }
}
