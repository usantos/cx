package br.gov.caixa.loterias.core.network

import com.google.gson.Gson
import com.google.gson.JsonParser
import java.math.BigDecimal
import org.junit.Assert.*
import org.junit.Test

class LegacyCartUploadDtoTest {
    private val gson = Gson()

    @Test fun `empty verification remains explicit and redacted`() {
        val request = AccountVerificationRequestDto()
        assertEquals("{\"apostas\":[]}", gson.toJson(request))
        assertEquals("AccountVerificationRequestDto(redacted)", request.toString())
    }

    @Test fun `Gson retains nested columns teams and wire annotations across round trip`() {
        val parameter = LegacyCartTeamDto(80,"Time",55,"Brasil","BR","DF","team",true,"T","Time teste")
        val first = parameter.copy(parameter = parameter, score = "2", winner = true)
        val second = parameter.copy(number = 81, parameter = LegacyCartTeamDto(), score = "0", winner = false)
        val dto = LegacyCartUploadDto(
            99, "SUPER_7", LegacyCartCodeDto("E", "Especial"), JsonParser.parseString("[[0,1],[],[2],[3],[4],[5],[6]]").asJsonArray,
            listOf(1,3), LegacyCartCodeDto(2,"Parcial"), BigDecimal("35.50"), 10,5,3,true,true,8,
            LegacyCartCodeDto(2L,"teste"), true,"2026-09-07","10:00",90,6,2,true,parameter,
            LegacyCartMonthDto(12,"Dezembro","Dez"), listOf(LegacyCartLotecaDto(first, second, true)),
            listOf(LegacyCartLotogolDto(first, second, 2, 0)),
        )
        val request = AccountVerificationRequestDto(listOf(dto))
        val json = gson.toJsonTree(request)
        val restored = gson.fromJson(json, AccountVerificationRequestDto::class.java)
        assertEquals(json, gson.toJsonTree(restored))
        assertEquals(dto, restored.bets.single())
        val bet = json.asJsonObject["apostas"].asJsonArray.single().asJsonObject
        assertEquals(JsonParser.parseString("[[0,1],[],[2],[3],[4],[5],[6]]"), bet["numerosSelecionados"])
        assertEquals(99L, bet["id"].asLong)
        assertEquals("SUPER_7", bet["modalidade"].asString)
        assertEquals(JsonParser.parseString("[1,3]"), bet["numerosSelecionadosMaisMilionariaTrevo"])
        assertEquals(2, bet["quantidadeNumerosMaisMilionariaTrevo"].asInt)
        assertEquals(3, bet["quantidadeApostas"].asInt)
        assertFalse(bet["indicadorCotaBolao"].asBoolean)
        assertEquals("2", bet["partidasLoteca"].asJsonArray.single().asJsonObject["equipe1"].asJsonObject["placar"].asString)
        assertEquals(80, bet["timeDoCoracao"].asJsonObject["numero"].asInt)
        assertEquals("Dez", bet["mesDeSorte"].asJsonObject["abreviacao"].asString)
        assertEquals("LegacyCartUploadDto(redacted)", dto.toString())
        assertEquals("LegacyCartTeamDto(redacted)", first.toString())
        assertFalse(gson.toJson(request).contains("idDB"))
        assertFalse(gson.toJson(request).contains("localId"))
    }
}
