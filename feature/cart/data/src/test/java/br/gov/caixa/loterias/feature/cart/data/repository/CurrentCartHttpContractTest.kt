package br.gov.caixa.loterias.feature.cart.data.repository

import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.core.network.error.BackendHttpFailure
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Caminho completo de requisição/resposta em localhost; nunca contata serviços de conta ou pagamento. */
class CurrentCartHttpContractTest {
    @Test fun `grouped removal ids use native endpoints and replace totals from each response`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(response(filled))
            server.enqueue(response(empty))
            server.enqueue(response(empty))
            server.enqueue(response(empty))
            val repository = repository(server)
            val cart = repository.current()
            assertEquals(BigDecimal("17.50"), cart.total)
            assertEquals(3, cart.items.size)
            assertEquals(3, cart.betCount)
            cart.items.forEach { item ->
                val updated = repository.remove(item)
                assertTrue(updated.items.isEmpty())
                assertEquals(BigDecimal.ZERO, updated.total)
            }
            val requests = List(4) { server.takeRequest() }
            assertEquals(listOf("GET", "DELETE", "DELETE", "DELETE"), requests.map { it.method })
            assertEquals(
                listOf(
                    "/rest/v1/logado/carrinhos",
                    "/rest/v1/logado/carrinhos/apostas/22",
                    "/rest/v1/logado/carrinhos/apostas/11",
                    "/rest/v1/logado/carrinhos/combos/33"
                ),
                requests.map { it.path }
            )
            assertTrue(requests.all { it.bodySize == 0L })
        }
    }

    @Test fun `clear uses native POST and validation uses GET without registering a purchase`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(response(empty))
            server.enqueue(response("{}"))
            val repository = repository(server)
            assertTrue(repository.clear().items.isEmpty())
            assertEquals(CartValidationResult.Accepted, repository.validate())
            val clear = server.takeRequest()
            val validation = server.takeRequest()
            assertEquals("POST", clear.method)
            assertEquals("/rest/v1/logado/carrinhos/limpar", clear.path)
            assertEquals("GET", validation.method)
            assertEquals("/rest/v1/logado/carrinhos/validar", validation.path)
            assertEquals(0L, validation.bodySize)
            assertEquals(2, server.requestCount)
        }
    }

    @Test fun `duplicate favorite name is a business validation without saving`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(
                MockResponse().setResponseCode(400).setBody("""{"mensagem":"Já existe um carrinho com esse nome. Deseja substituir?"}""")
            )
            val result = br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartName(repository(server))("teste")
            assertEquals(
                br.gov.caixa.loterias.core.common.result.AppResult.Failure(
                    br.gov.caixa.loterias.core.common.result.AppError.Validation("Já existe um carrinho com esse nome. Deseja substituir?")
                ),
                result
            )
            assertEquals("/rest/v1/logado/carrinhos-favoritos/validar", server.takeRequest().path)
            assertEquals(1, server.requestCount)
        }
    }

    @Test fun `bound cart removal uses captured session rather than generic token`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(response(empty))
            val result = source(server).removeBoundCartBet(91, AccountVerificationSession({ "bound-login" }, { true }))
            assertEquals(1L, result?.id)
            val request = server.takeRequest()
            assertEquals("DELETE", request.method)
            assertEquals("/rest/v1/logado/carrinhos/apostas/91", request.path)
            assertEquals("Bearer bound-login", request.getHeader("Authorization"))
            assertEquals(0L, request.bodySize)
        }
    }

    @Test fun `favorite save duplicate remains a business error with backend message`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(400).setBody("""{"mensagem":"Nome já utilizado"}"""))
            val failure = runCatching { source(server).saveCurrentCartFavorite("teste") }.exceptionOrNull()
            assertTrue(failure is IllegalArgumentException)
            assertEquals("Nome já utilizado", failure?.message)
            assertEquals("/rest/v1/logado/carrinhos/salvar-carrinho-favorito", server.takeRequest().path)
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
            for (save in listOf(false, true)) {
                for ((status, body, message) in cases) {
                    server.enqueue(MockResponse().setResponseCode(status).setBody(body))
                    val failure = runCatching {
                        if (save) source.saveCurrentCartFavorite("teste") else source.validateFavoriteCartName("teste")
                    }.exceptionOrNull()
                    assertTrue("HTTP $status must remain an infrastructure failure", failure is BackendHttpFailure)
                    failure as BackendHttpFailure
                    assertEquals(status, failure.statusCode)
                    assertEquals(message, failure.metadata?.message)
                    assertEquals(
                        if (save) "/rest/v1/logado/carrinhos/salvar-carrinho-favorito" else "/rest/v1/logado/carrinhos-favoritos/validar",
                        server.takeRequest().path
                    )
                }
            }
        }
    }

    private fun repository(server: MockWebServer) = CartRepositoryImpl(source(server))

    private fun source(server: MockWebServer): CartRemoteDataSourceImpl {
        val baseUrl = server.url("/rest/v1/").newBuilder().scheme("https").build().toString()
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request()
            chain.proceed(request.newBuilder().url(request.url.newBuilder().scheme("http").build()).build())
        }
        val api = NetworkClientFactory.create(baseUrl, AuthTokenProvider { "fixture" }, client)
        val provider = LoteriasApiProvider().apply { replace(api) }
        return CartRemoteDataSourceImpl(provider)
    }

    private fun response(payload: String) = MockResponse().setHeader("Content-Type", "application/json")
        .setBody("{\"payload\":$payload}")

    private val empty = """{"id":1,"apostas":[],"apostasIndividuais":[],"cotas":[],"combos":[]}"""
    private val filled = """{
        "id":1,"valorTotal":999,
        "apostas":[{"id":11,"valor":6},{"id":22,"reservaCotaBolao":{"vrTotalCota":7.50}},{"id":44,"valor":4}],
        "apostasIndividuais":[{"id":11,"modalidade":"MEGA_SENA","valor":6}],
        "cotas":[{"id":22,"modalidade":"MEGA_SENA","reservaCotaBolao":{"codigoBolaoReserva":"fixture","vrTotalCota":7.50}}],
        "combos":[{"id":33,"tipoCombo":{"nome":"Combo"},"apostas":[{"id":44,"valor":4}]}]
    }"""
}
