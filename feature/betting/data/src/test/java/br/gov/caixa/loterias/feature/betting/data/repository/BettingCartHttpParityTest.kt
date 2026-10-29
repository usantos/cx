package br.gov.caixa.loterias.feature.betting.data.repository

import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.BettingRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCart
import com.google.gson.JsonParser
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Requisições reais do Retrofit apenas para loopback, nunca para um endpoint da CAIXA. */
class BettingCartHttpParityTest {
    @Test fun `manual inclusion sends the native numeric contest domain and target contest`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("""{"payload":{"id":12}}"""))
            repository(
                server
            ).addBet(BetDraft(2, numbers = listOf(1, 2, 3, 4, 5, 6), contestNumber = 2900, amount = java.math.BigDecimal("5.00")))
            val request = server.takeRequest()
            assertEquals("/logado/carrinhos/incluir-aposta", request.path)
            val body = JsonParser.parseString(request.body.readUtf8()).asJsonObject
            assertEquals("1", body.getAsJsonObject("tipoConcurso").get("valor").asString)
            assertEquals(2900, body.get("concursoAlvo")?.asInt)
            assertEquals(java.math.BigDecimal("5.00"), body.get("valor")?.asBigDecimal)
            assertEquals("", body.getAsJsonObject("indicadorSurpresinha").get("descricao").asString)
            assertEquals(1, body.getAsJsonObject("indicadorSurpresinha").get("valor").asInt)
        }
    }

    @Test fun `surprise inclusion uses native endpoint quantity target and mirror field`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("""{"payload":{"id":12}}"""))
            repository(server).addBet(
                BetDraft(
                    16,
                    numbers = emptyList(),
                    surprise = true,
                    quantity = 3,
                    numberQuantity = 50,
                    contestNumber = 2901,
                    generateMirror = true
                )
            )
            val request = server.takeRequest()
            assertEquals("/logado/carrinhos/incluir-surpresinha", request.path)
            val body = JsonParser.parseString(request.body.readUtf8()).asJsonObject
            assertEquals(2, body.getAsJsonObject("indicadorSurpresinha").get("valor").asInt)
            assertEquals(3, body.get("quantidadeSurpresinhas")?.asInt)
            assertEquals(1, body.get("quantidadeApostas")?.asInt)
            assertEquals(2901, body.get("concursoAlvo")?.asInt)
            assertEquals(true, body.get("gerarEspelho")?.asBoolean)
            assertFalse(body.has("numerosSelecionados"))
            assertFalse(body.has("geraEspelho"))
        }
    }

    @Test fun `service rejection message survives instead of becoming a generic loading error`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(400).setBody("""{"mensagem":"O concurso foi encerrado."}"""))
            val result = AddBetToCart(repository(server))(BetDraft(2, numbers = listOf(1, 2, 3, 4, 5, 6), contestNumber = 2900))
            assertEquals(AppResult.Failure(AppError.Validation("O concurso foi encerrado.")), result)
        }
    }

    @Test fun `Timemania explicit team keeps numeric surprise indicator while random team stays ordinary`() = runTest {
        MockWebServer().use { server ->
            listOf(false to 2, true to 3).forEach { (selected, indicator) ->
                server.enqueue(MockResponse().setBody("""{"payload":{"id":12}}"""))
                repository(server).addBet(
                    BetDraft(
                        20,
                        numbers = emptyList(),
                        surprise = true,
                        numberQuantity = 10,
                        favoriteTeam = br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam(1, "Time", "DF"),
                        contestNumber = 2900,
                        surpriseTeamSelected = selected
                    )
                )
                val body = JsonParser.parseString(server.takeRequest().body.readUtf8()).asJsonObject
                assertEquals(indicator, body.getAsJsonObject("indicadorSurpresinha").get("valor").asInt)
            }
        }
    }

    private fun repository(server: MockWebServer): BettingRepositoryImpl {
        val retrofit = Retrofit.Builder().baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create()).build()
        val api = LoteriasApiProvider().apply { replace(retrofit.create(LoteriasApi::class.java)) }
        val backend = BackendApiProvider().apply { replace(retrofit.create(BackendApi::class.java)) }
        return BettingRepositoryImpl(BettingRemoteDataSourceImpl(api, backend))
    }
}
