package br.gov.caixa.loterias.feature.betting.data.repository

import br.gov.caixa.loterias.core.network.error.BackendHttpFailure
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetDraft
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.FavoriteBetRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetCartOptions
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** O atalho exclusivo de concursos especiais omite inteiramente o parâmetro anulável espelho do legado. */
class FavoriteBetHttpParityTest {
    @Test fun `new favorite accepts native empty success responses`() = runTest {
        MockWebServer().use { server ->
            val retrofit = Retrofit.Builder().baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create()).build()
            val provider = BackendApiProvider().apply { replace(retrofit.create(BackendApi::class.java)) }
            val repository = FavoriteBetRepositoryImpl(FavoriteBetRemoteDataSourceImpl(provider))
            val draft = br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetDraft(2, "Mega-Sena", "novo", listOf((1..6).toList()))
            for (status in listOf(200, 204)) {
                server.enqueue(MockResponse().setResponseCode(status))
                assertEquals(
                    br.gov.caixa.loterias.core.common.result.AppResult.Success(Unit),
                    br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBet(repository)(draft)
                )
                server.enqueue(MockResponse().setResponseCode(status))
                assertEquals(
                    br.gov.caixa.loterias.core.common.result.AppResult.Success(Unit),
                    br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBet(repository)(draft)
                )
            }
            assertEquals(4, server.requestCount)
        }
    }

    @Test fun `duplicate bet name preserves business validation without saving`() = runTest {
        MockWebServer().use { server ->
            val retrofit = Retrofit.Builder().baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create()).build()
            val provider = BackendApiProvider().apply { replace(retrofit.create(BackendApi::class.java)) }
            server.enqueue(MockResponse().setResponseCode(400).setBody("""{"mensagem":"Aposta já cadastrada. Deseja substituir?"}"""))
            val repository = FavoriteBetRepositoryImpl(FavoriteBetRemoteDataSourceImpl(provider))
            val draft = br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetDraft(
                2,
                "Mega-Sena",
                "teste",
                listOf((1..6).toList())
            )
            val result = br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBet(repository)(draft)
            assertEquals(
                br.gov.caixa.loterias.core.common.result.AppResult.Failure(
                    br.gov.caixa.loterias.core.common.result.AppError.Validation("Aposta já cadastrada. Deseja substituir?")
                ),
                result
            )
            assertEquals("/logado/apostas-favoritas/validar", server.takeRequest().path)
            assertEquals(1, server.requestCount)
        }
    }

    @Test fun `filtered favorite page keeps native page size and requested offset`() = runTest {
        MockWebServer().use { server ->
            val retrofit = Retrofit.Builder().baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create()).build()
            val provider = BackendApiProvider().apply { replace(retrofit.create(BackendApi::class.java)) }
            server.enqueue(MockResponse().setBody("""{"payload":{"lista":[]}}"""))
            FavoriteBetRemoteDataSourceImpl(provider).byModality(2, 8)
            val request = server.takeRequest()
            assertEquals("GET", request.method)
            assertEquals("/logado/apostas-favoritas/modalidade", request.requestUrl?.encodedPath)
            assertEquals("2", request.requestUrl?.queryParameter("modalidade"))
            assertEquals("8", request.requestUrl?.queryParameter("offset"))
            assertEquals("8", request.requestUrl?.queryParameter("size"))
        }
    }

    @Test fun `special only favorite omits mirror while picker sends explicit false or true`() = runTest {
        MockWebServer().use { server ->
            val retrofit = Retrofit.Builder().baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create()).build()
            val provider = BackendApiProvider().apply { replace(retrofit.create(BackendApi::class.java)) }
            val source = FavoriteBetRemoteDataSourceImpl(provider)
            listOf<Boolean?>(null, false, true).forEach { mirror ->
                server.enqueue(MockResponse().setBody("""{"payload":{"id":12}}"""))
                source.addToCart(9, FavoriteBetCartOptions(2, 0, mirror))
                val request = server.takeRequest()
                assertEquals("POST", request.method)
                assertEquals("/logado/carrinhos/incluir-aposta-favorita", request.requestUrl?.encodedPath)
                assertEquals("9", request.requestUrl?.queryParameter("idApostaFavorita"))
                assertEquals("2", request.requestUrl?.queryParameter("valorTipoConcurso"))
                assertEquals("0", request.requestUrl?.queryParameter("qtdTeimosinhas"))
                assertEquals(mirror?.toString(), request.requestUrl?.queryParameter("espelho"))
                assertEquals(0L, request.body.size)
            }
        }
    }

    @Test fun `favorite save duplicate remains a business error with backend message`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(400).setBody("""{"mensagem":"Aposta já cadastrada"}"""))
            val draft = FavoriteBetDraft(2, "Mega-Sena", "teste", listOf((1..6).toList()))
            val failure = runCatching { source(server).save(draft) }.exceptionOrNull()
            assertTrue(failure is IllegalArgumentException)
            assertEquals("Aposta já cadastrada", failure?.message)
            assertEquals("/logado/apostas-favoritas", server.takeRequest().path)
        }
    }

    @Test fun `favorite validation and save preserve HTTP errors without a usable business message`() = runTest {
        val cases = listOf(
            Triple(400, "{}", null),
            Triple(400, "not-json", null),
            Triple(400, """{"mensagem":""}""", ""),
            Triple(400, """{"mensagem":" "}""", " "),
            Triple(503, """{"mensagem":"Manutenção"}""", "Manutenção")
        )
        MockWebServer().use { server ->
            val source = source(server)
            val draft = FavoriteBetDraft(2, "Mega-Sena", "teste", listOf((1..6).toList()))
            for (save in listOf(false, true)) {
                for ((status, body, message) in cases) {
                    server.enqueue(MockResponse().setResponseCode(status).setBody(body))
                    val failure = runCatching {
                        if (save) source.save(draft) else source.validate(draft)
                    }.exceptionOrNull()
                    assertTrue("HTTP $status must remain an infrastructure failure", failure is BackendHttpFailure)
                    failure as BackendHttpFailure
                    assertEquals(status, failure.statusCode)
                    assertEquals(message, failure.metadata?.message)
                    assertEquals(
                        if (save) "/logado/apostas-favoritas" else "/logado/apostas-favoritas/validar",
                        server.takeRequest().path
                    )
                }
            }
        }
    }

    private fun source(server: MockWebServer): FavoriteBetRemoteDataSourceImpl {
        val retrofit = Retrofit.Builder().baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create()).build()
        val provider = BackendApiProvider().apply { replace(retrofit.create(BackendApi::class.java)) }
        return FavoriteBetRemoteDataSourceImpl(provider)
    }
}
