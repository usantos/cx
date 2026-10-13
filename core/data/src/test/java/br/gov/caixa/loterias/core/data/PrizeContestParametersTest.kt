package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.network.BettingParametersDto
import br.gov.caixa.loterias.core.network.LotteryContestDto
import br.gov.caixa.loterias.core.network.LotteryGameParameterDto
import br.gov.caixa.loterias.core.network.LotteryParameterDto
import com.google.gson.Gson
import br.gov.caixa.loterias.core.domain.betting.BetDraft
import br.gov.caixa.loterias.core.network.AddBetRequestDto
import br.gov.caixa.loterias.core.network.CartDto
import java.lang.reflect.Proxy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class PrizeContestParametersTest {
    @Test fun `normal and special draft types are preserved in cart wire requests`() = runTest {
        val captured = mutableListOf<AddBetRequestDto>()
        val source = Proxy.newProxyInstance(BettingRemoteDataSource::class.java.classLoader,
            arrayOf(BettingRemoteDataSource::class.java)) { _, method, arguments ->
            check(method.name == "addBet")
            captured += arguments!![0] as AddBetRequestDto
            CartDto(id = 1)
        } as BettingRemoteDataSource
        val repository = RemoteBettingRepository(source)
        listOf("NORMAL" to "N", "ESPECIAL" to "E").forEach { (type, code) ->
            repository.addBet(BetDraft(2, type, listOf(1, 2, 3, 4, 5, 6)))
            val json = Gson().toJsonTree(captured.last()).asJsonObject
            assertEquals(code, json.getAsJsonObject("tipoConcurso").get("valor").asString)
            assertEquals(type, json.getAsJsonObject("tipoConcurso").get("descricao").asString)
            assertEquals("MEGA_SENA", json.get("modalidade").asString)
            assertEquals(6, json.getAsJsonArray("numerosSelecionados").size())
        }
    }

    @Test fun `pool resale identity and dates survive wire decoding and domain mapping`() = runTest {
        val payload = Gson().fromJson("""{
            "modalidadesDisponiveisCotas":[
                {"modalidade":"MEGA_SENA","tipoConcurso":"ESPECIAL","concurso":3000,
                 "dataEncerramentoRevenda":"06/09/2026","horaEncerramentoRevenda":"19:00:00"},
                {"modalidade":"MEGA_SENA"}
            ]
        }""", BettingParametersDto::class.java)
        val source = Proxy.newProxyInstance(BettingRemoteDataSource::class.java.classLoader,
            arrayOf(BettingRemoteDataSource::class.java)) { _, method, _ ->
            check(method.name == "bettingParameters")
            payload
        } as BettingRemoteDataSource
        val pools = RemoteBettingRepository(source).parameters().availablePoolModalities
        assertEquals(2, pools[0].modalityId)
        assertEquals("ESPECIAL", pools[0].contestType)
        assertEquals(3000, pools[0].contestNumber)
        assertEquals("06/09/2026", pools[0].resaleClosingDate)
        assertEquals("19:00:00", pools[0].resaleClosingTime)
        assertNull(pools[1].contestNumber)
        assertEquals("", pools[1].resaleClosingDate)
        assertEquals("", pools[1].resaleClosingTime)
    }

    @Test fun `next contest flag survives mapping without replacing current contest summary`() = runTest {
        val current = LotteryGameParameterDto(contest = LotteryContestDto(
            modalityCode = "MEGA_SENA", number = 3000,
        ))
        val next = LotteryGameParameterDto(contest = LotteryContestDto(
            modalityCode = "MEGA_SENA", number = 3001,
        ))
        val payload = BettingParametersDto(parameters = listOf(
            LotteryParameterDto(game = current, nextGame = next),
            LotteryParameterDto(game = current),
            LotteryParameterDto(nextGame = next),
        ))
        val source = Proxy.newProxyInstance(BettingRemoteDataSource::class.java.classLoader,
            arrayOf(BettingRemoteDataSource::class.java)) { _, method, _ ->
            check(method.name == "bettingParameters")
            payload
        } as BettingRemoteDataSource
        val games = RemoteBettingRepository(source).parameters().games
        assertEquals(listOf(true, false, true), games.map { it.hasNextContest })
        assertEquals(listOf(3000, 3000, 3001), games.map { it.contestNumber })
        assertTrue(games.all { it.modality.id == 2 })
    }
}
