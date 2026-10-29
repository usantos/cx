package br.gov.caixa.loterias.feature.results.data.mapper

import br.gov.caixa.loterias.feature.results.data.dto.DrawResultDto
import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.feature.results.data.repository.ResultsRepositoryImpl
import br.gov.caixa.loterias.feature.results.data.datasource.remote.ResultsRemoteDataSource

class SpecialDrawResultsMappingTest {
    private suspend fun mapped(json: String) = ResultsRepositoryImpl(object : ResultsRemoteDataSource {
        val dto = Gson().fromJson(json, DrawResultDto::class.java)
        override suspend fun latestResult(modality: String) = dto
        override suspend fun resultByContest(modality: String, contest: Int) = dto
    }).latest("loteca")

    @Test fun `sports winner draw score and legends remain distinct from wager selections`() = runTest {
        val result = mapped("""{"concurso":{"modalidade":"LOTECA","numero":1200},
          "partidasLoteca":[{"numero":1,"equipe1":{"nome":"Casa","placar":"2","vitoria":true,"uf":"SP"},
            "equipe2":{"nome":"Fora","placar":"1","vitoria":false},"empate":false,"letraLegenda":"A","descricaoLegenda":"Jogo antecipado"}],
          "partidasLotogol":[{"equipe1":{"nome":"Casa"},"equipe2":{"nome":"Fora"},"indexPlacarEquipe1":4,"indexPlacarEquipe2":0}]}""")
        val loteca = result.lotecaMatches.single()
        assertEquals("Casa", loteca.home.name)
        assertEquals("2", loteca.home.score)
        assertEquals(true, loteca.home.won)
        assertEquals(false, loteca.draw)
        assertEquals("Jogo antecipado", loteca.legendDescription)
        assertEquals(4, result.lotogolMatches.single().homeScoreIndex)
        assertTrue(result.firstDrawNumbers.isEmpty())
    }
    @Test fun `second payouts rewards regions and upcoming contest metadata survive mapping`() = runTest {
        val result = mapped("""{"concurso":{"numero":2800,"tipoConcurso":"ESPECIAL","modalidadeDetalhada":{"descricaoEspecial":"Especial do serviço"}},
          "numerosSorteadosPrimeiroSorteio":[[1],[2,3]],"numerosSorteadosSegundoSorteio":[4,5],
          "premiacoesSegundoSorteio":[{"descricao":"6 acertos","quantidadeGanhadores":2,"valor":125.50}],
          "premiacaoTimeDoCoracao":{"equipe":{"nome":"Time","uf":"RJ"},"quantidadeGanhadores":3,"valor":8},
          "premiacaoMesDeSorte":{"mesDeSorte":{"numero":10,"nome":"Outubro"},"quantidadeGanhadores":4,"valor":5},
          "ganhadoresPrimeiroSorteio":[{"municipioSorteio":{"nome":"Cidade"},"ufSorteio":{"sigla":"SP"},"descricao":"1 aposta"}],
          "ganhadoresSegundoSorteio":[{"municipioSorteio":{"nome":"Outra"},"ufSorteio":{"sigla":"RJ"},"descricao":"2 apostas"}],
          "proximoConcurso":{"numero":2801,"dataSorteio":"15/10/2026","estimativa":1000},
          "proximoConcursoFinalZero":{"numero":2810,"estimativa":2000},"proximoConcursoFinalCinco":{"numero":2805},
          "proximoConcursoEspecial":{"numero":2900,"tipoConcurso":"ESPECIAL"}}""")
        assertEquals(listOf(listOf(1), listOf(2,3)), result.firstDrawNumberColumns)
        assertEquals("125.50", result.secondDrawPayouts.single().value)
        assertEquals("Time/RJ", result.favoriteTeamReward!!.selection)
        assertEquals("Outubro", result.luckyMonthReward!!.selection)
        assertEquals("Cidade - SP", result.firstDrawRegionalWinners.single().location)
        assertEquals("Outra - RJ", result.secondDrawRegionalWinners.single().location)
        assertEquals(2801, result.nextContest!!.number)
        assertEquals(2810, result.nextFinalZeroContest!!.number)
        assertEquals(2805, result.nextFinalFiveContest!!.number)
        assertEquals(2900, result.nextSpecialContest!!.number)
        assertTrue(result.specialContest)
        assertEquals("Especial do serviço", result.specialName)
    }
}
