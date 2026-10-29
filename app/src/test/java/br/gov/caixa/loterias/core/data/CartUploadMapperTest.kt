package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendApi
import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendClient
import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendProvider
import br.gov.caixa.loterias.feature.account.data.dto.AccountVerificationRequestDto
import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.core.network.api.AccountVerificationSessionExpiredException
import br.gov.caixa.loterias.feature.cart.data.dto.CartTeamDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartUploadDto
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding
import br.gov.caixa.loterias.feature.account.data.datasource.remote.PostLoginAccountDataSourceImpl
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.cart.data.mapper.toUploadDto
import br.gov.caixa.loterias.feature.cart.domain.model.CartCode
import br.gov.caixa.loterias.feature.cart.domain.model.CartLotecaMatch
import br.gov.caixa.loterias.feature.cart.domain.model.CartLotogolMatch
import br.gov.caixa.loterias.feature.cart.domain.model.CartMatchTeam
import br.gov.caixa.loterias.feature.cart.domain.model.CartModality
import br.gov.caixa.loterias.feature.cart.domain.model.CartMonth
import br.gov.caixa.loterias.feature.cart.domain.model.CartNumbers
import br.gov.caixa.loterias.feature.cart.domain.model.CartTeam
import br.gov.caixa.loterias.feature.cart.domain.model.LocalCartBet
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

class CartUploadMapperTest {
    @Test fun `post login data source binds original identity and sends typed snapshot without local id`() = runTest {
        val identity = AuthenticatedSessionIdentity("a".repeat(64))
        val boundSession = AccountVerificationSession({ "fixture" }, { true })
        val requests = mutableListOf<AccountVerificationRequestDto>()
        val provider = AccountVerificationBackendProvider()
        provider.replace(AccountVerificationBackendClient(object : AccountVerificationBackendApi {
            override suspend fun verify(request: AccountVerificationRequestDto, session: AccountVerificationSession): Response<ResponseBody> {
                assertSame(boundSession, session)
                requests += request
                return Response.success("""{"codigo":"review","redirect":"/termos","payload":{"nome":"Pessoa"}}"""
                    .toResponseBody("application/json".toMediaType()))
            }
        }, gson))
        val dataSource = PostLoginAccountDataSourceImpl(provider, AccountVerificationSessionBinding {
            assertSame(identity, it)
            boundSession
        })
        val response = dataSource.verify(identity, listOf(bet(), bet().copy(serverId = 123)))
        assertEquals("review", response.code)
        assertEquals("/termos", response.redirect)
        assertEquals("Pessoa", response.payload?.name)
        assertEquals(listOf(null, 123L), requests.single().bets.map { it.id })
        assertEquals(gson.toJsonTree(bet().toUploadDto()), gson.toJsonTree(requests.single().bets.first()))
        dataSource.verify(identity, emptyList())
        assertTrue(requests.last().bets.isEmpty())
    }

    @Test fun `post login data source does not fall back or retry when transport is unavailable`() = runTest {
        val identity = AuthenticatedSessionIdentity("b".repeat(64))
        var bindings = 0
        val provider = AccountVerificationBackendProvider()
        val dataSource = PostLoginAccountDataSourceImpl(provider, AccountVerificationSessionBinding {
            bindings++
            AccountVerificationSession({ null }, { false })
        })
        try { dataSource.verify(identity, listOf(bet())); fail("Expected missing client") }
        catch (_: IllegalStateException) { }
        assertEquals(0, bindings)
        var calls = 0
        provider.replace(AccountVerificationBackendClient(object : AccountVerificationBackendApi {
            override suspend fun verify(request: AccountVerificationRequestDto, session: AccountVerificationSession): Response<ResponseBody> {
                calls++
                throw AssertionError("Expired session must not dispatch")
            }
        }, gson))
        try { dataSource.verify(identity, listOf(bet())); fail("Expected expired session") }
        catch (_: AccountVerificationSessionExpiredException) { }
        assertEquals(1, bindings)
        assertEquals(0, calls)
    }

    private val gson = Gson()
    private fun bet() = LocalCartBet(
        localId = 777, serverId = null, modality = CartModality.MAIS_MILIONARIA,
        contestType = CartCode("E", "Especial"), numbers = CartNumbers.Flat(listOf(1,2,3,4,5,6)),
        clovers = listOf(1,3), quickPickIndicator = CartCode(2, "Parcial"), amount = BigDecimal("35.50"),
        targetContest = 10, repeatCount = 5, betCount = 3, mirrored = true, generateMirror = true,
        initialContest = 8, status = CartCode(2L, "teste"), replacement = true,
        effectiveDate = "2026-09-07", effectiveTime = "10:00", mirrorLink = 90, numberCount = 6,
        cloverCount = 2, quickPick = true, quickPickCount = 99, favoriteTeam = null, luckyMonth = null,
        loteca = emptyList(), lotogol = emptyList(),
    )

    @Test fun `request serializes all bet fields with exact wire names and no local metadata`() {
        val dto = bet().toUploadDto()
        val request = AccountVerificationRequestDto(listOf(dto))
        val actual = gson.toJsonTree(request)
        val expected = JsonParser.parseString("""{
          "apostas": [{"modalidade":"MAIS_MILIONARIA", "tipoConcurso":{"valor":"E","descricao":"Especial"},
            "numerosSelecionados":[1,2,3,4,5,6], "numerosSelecionadosMaisMilionariaTrevo":[1,3],
            "indicadorSurpresinha":{"valor":2,"descricao":"Parcial"}, "valor":35.50,
            "concursoAlvo":10,"quantidadeTeimosinhas":5,"quantidadeApostas":3,"espelho":true,"geraEspelho":true,
            "concursoInicial":8,"situacao":{"valor":2,"descricao":"teste"},"troca":true,
            "dataEfetivacao":"2026-09-07","horaEfetivacao":"10:00","vinculoEspelho":90,
            "quantidadeNumeros":6,"quantidadeNumerosMaisMilionariaTrevo":2,"surpresinha":true,
            "timeDoCoracao":{},"mesDeSorte":{},"partidasLoteca":[],"partidasLotogol":[],"indicadorCotaBolao":false}]
        }""")
        assertEquals(expected, actual)
        assertEquals("AccountVerificationRequestDto(redacted)", request.toString())
        assertEquals("CartUploadDto(redacted)", dto.toString())
        assertEquals(777L, bet().localId)
    }

    @Test fun `columns remain nested nulls absent and server identity independent of local identity`() {
        val columns = bet().copy(modality = CartModality.SUPER_7, serverId = 12,
            numbers = CartNumbers.Columns(listOf(listOf(0,1), emptyList(), listOf(2), listOf(3), listOf(4), listOf(5), listOf(6))))
        val json = gson.toJsonTree(columns.toUploadDto()).asJsonObject
        assertEquals(JsonParser.parseString("[[0,1],[],[2],[3],[4],[5],[6]]"), json["numerosSelecionados"])
        assertEquals(12L, json["id"].asLong)
        assertEquals("SUPER_7", json["modalidade"].asString)
        val absent = gson.toJsonTree(bet().copy(serverId = 0, numbers = null, clovers = null,
            effectiveDate = null, effectiveTime = null).toUploadDto()).asJsonObject
        for (field in listOf("id", "numerosSelecionados", "numerosSelecionadosMaisMilionariaTrevo", "dataEfetivacao", "horaEfetivacao")) {
            assertFalse(field, absent.has(field))
        }
        for (modality in CartModality.entries) {
            assertEquals(modality.name, bet().copy(modality = modality).toUploadDto().modality)
        }
    }

    @Test fun `sports team fields are flat with nested parameter and separate score indexes`() {
        val team = CartTeam(80, "Time", 55, "Brasil", "BR", "DF", "team", true, "T", "Time teste")
        val first = CartMatchTeam(team, team, "2", true)
        val second = CartMatchTeam(team.copy(number = 81, isNationalTeam = false), null, "0", false)
        val dto = bet().copy(favoriteTeam = team, luckyMonth = CartMonth(12,"Dezembro","Dez"),
            loteca = listOf(CartLotecaMatch(first, second, true)),
            lotogol = listOf(CartLotogolMatch(first, second, 2, 0))).toUploadDto()
        val json = gson.toJsonTree(dto).asJsonObject
        val parameter = JsonParser.parseString("""{"numero":80,"nome":"Time","numeroPais":55,"pais":"Brasil",
            "siglaPais":"BR","uf":"DF","nomeClass":"team","indicadorSelecao":true,"descricaoCurta":"T","descricaoLonga":"Time teste"}""")
        assertEquals(parameter, json["timeDoCoracao"])
        assertEquals(JsonParser.parseString("""{"numero":12,"nome":"Dezembro","abreviacao":"Dez"}"""), json["mesDeSorte"])
        val loteca = json["partidasLoteca"].asJsonArray.single().asJsonObject
        val lotogol = json["partidasLotogol"].asJsonArray.single().asJsonObject
        assertTrue(loteca["empate"].asBoolean)
        assertEquals(loteca["equipe1"], lotogol["equipe1"])
        assertEquals(loteca["equipe2"], lotogol["equipe2"])
        assertEquals(2, lotogol["indexPlacarEquipe1"].asInt); assertEquals(0, lotogol["indexPlacarEquipe2"].asInt)
        val firstJson = loteca["equipe1"].asJsonObject
        assertEquals(parameter, firstJson["parametroEquipe"])
        assertEquals("2", firstJson["placar"].asString); assertTrue(firstJson["vitoria"].asBoolean)
        for ((key, value) in parameter.asJsonObject.entrySet()) assertEquals(value, firstJson[key])
        val secondJson = loteca["equipe2"].asJsonObject
        assertEquals(JsonParser.parseString("{}"), secondJson["parametroEquipe"])
        assertFalse(secondJson["vitoria"].asBoolean); assertFalse(secondJson["indicadorSelecao"].asBoolean)
        assertEquals("CartTeamDto(redacted)", dto.favoriteTeam.toString())
    }

    @Test fun `upload owns its selection collections and cannot mutate the migration snapshot`() {
        val numbers = mutableListOf(1,2)
        val clovers = mutableListOf(1,3)
        val source = bet().copy(numbers = CartNumbers.Flat(numbers), clovers = clovers)
        val dto = source.toUploadDto()
        numbers += 3; clovers += 4
        assertEquals(JsonParser.parseString("[1,2]"), dto.numbers)
        assertEquals(listOf(1,3), dto.clovers)
        dto.numbers!!.add(7)
        assertEquals(listOf(1,2,3), numbers)
    }
}
