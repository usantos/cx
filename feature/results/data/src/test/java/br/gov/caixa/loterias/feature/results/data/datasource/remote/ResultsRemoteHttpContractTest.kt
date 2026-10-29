package br.gov.caixa.loterias.feature.results.data.datasource.remote

import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ResultsRemoteHttpContractTest {
    @Test fun `shield catalog uses team identity and drops unusable images`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(response("""[
                {"id":900,"equipe":{"id":42},"imagem":"shield-42"},
                {"id":901,"equipe":{"id":43},"imagem":"shield-43"},
                {"id":902,"imagem":"missing-team"},
                {"id":903,"equipe":{},"imagem":"missing-team-id"},
                {"id":904,"equipe":{"id":44},"imagem":"  "},
                {"id":905,"equipe":{"id":45}}
            ]"""))

            assertEquals(mapOf(42 to "shield-42", 43 to "shield-43"), source(server).sportsTeamShields())
            assertEquals("/publico/equipes-esportivas/listaEquipes", server.takeRequest().path)
        }
    }

    @Test fun `optional shield catalog without a backend provider is empty`() = runTest {
        val source = ResultsRemoteDataSourceImpl(LoteriasApiProvider())
        assertTrue(source.sportsTeamShields().isEmpty())
    }

    @Test fun `empty shield payload is an empty catalog`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(response("null"))
            assertTrue(source(server).sportsTeamShields().isEmpty())
        }
    }

    @Test fun `latest and requested contest preserve their endpoint and payload`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(response("""{"concurso":{"numero":2801,"modalidade":"MEGA_SENA"}}"""))
            server.enqueue(response("""{"concurso":{"numero":2799,"modalidade":"MEGA_SENA"}}"""))
            val source = source(server)

            assertEquals(2801, source.latestResult("megasena")?.contest?.number)
            assertEquals("/resultados/megasena", server.takeRequest().path)
            assertEquals(2799, source.resultByContest("megasena", 2799)?.contest?.number)
            assertEquals("/resultados/megasena/2799", server.takeRequest().path)
        }
    }

    @Test fun `range request repeats contest keys and returns every draw`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(response("""[
                {"concurso":{"numero":2800,"modalidade":"MEGA_SENA"}},
                {"concurso":{"numero":2801,"modalidade":"MEGA_SENA"}}
            ]"""))

            val results = source(server).resultsForContests("megasena", listOf(2800, 2801))

            assertEquals(listOf(2800, 2801), results.map { it.contest?.number })
            val request = server.takeRequest()
            assertEquals("/resultados", request.requestUrl?.encodedPath)
            assertEquals("megasena", request.requestUrl?.queryParameter("modalidade"))
            assertEquals(listOf("2800", "2801"), request.requestUrl?.queryParameterValues("concursos"))
        }
    }

    @Test fun `missing range payload fails instead of claiming no results`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(response("null"))
            try {
                source(server).resultsForContests("megasena", listOf(2800))
                fail("A missing payload cannot be interpreted as an empty draw range")
            } catch (failure: IllegalArgumentException) {
                assertEquals("Resultados sem payload", failure.message)
            }
        }
    }

    private fun source(server: MockWebServer): ResultsRemoteDataSourceImpl {
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return ResultsRemoteDataSourceImpl(
            LoteriasApiProvider().apply { replace(retrofit.create(LoteriasApi::class.java)) },
            BackendApiProvider().apply { replace(retrofit.create(BackendApi::class.java)) },
        )
    }

    private fun response(payload: String) = MockResponse()
        .setHeader("Content-Type", "application/json")
        .setBody("{\"payload\":$payload}")
}
