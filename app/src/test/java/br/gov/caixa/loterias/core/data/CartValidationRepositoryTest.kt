package br.gov.caixa.loterias.core.data

import org.junit.Assert.*
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceNotice
import br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory
import br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartRemoteDataSource
import br.gov.caixa.loterias.feature.cart.data.repository.CartRepositoryImpl
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCart
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test

class CartValidationRepositoryTest {
    @Test fun `real Retrofit validation uses legacy GET and success is distinct from rejected outcome`() = runTest {
        val calls = mutableListOf<String>()
        val repository = http(200, "{\"payload\":null}", calls)
        assertEquals(AppResult.Success(CartValidationResult.Accepted), ValidateCart(repository)())
        assertEquals(listOf("GET /rest/v1/logado/carrinhos/validar"), calls)
    }

    @Test fun `HTTP errors preserve legacy notice kind code message and precedence`() = runTest {
        data class Case(val status: Int, val json: String, val expected: ServiceFailure)
        val cases = listOf(
            Case(401, "{}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.LOGIN_REQUIRED))),
            Case(400, "{\"codigo\":\"011010\",\"mensagem\":\"Aposte mais\"}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.MORE_BETS_REQUIRED, "Aposte mais", "011010"))),
            Case(400, "{\"codigo\":\"123\",\"mensagem\":\"Carrinho inválido\"}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.BUSINESS_RULE, "Carrinho inválido", "123"))),
            Case(500, "{\"codigo\":\"2067\"}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.INVALID_IDENTIFICATION, code = "2067"))),
            Case(500, "{\"tipo\":1,\"mensagem\":\"Falha informada\"}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.SERVER_MESSAGE, "Falha informada"))),
            Case(500, "{}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.SERVER_FAILURE))),
            Case(408, "{}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.BUSINESS_RULE, "Tempo de resposta esgotou"))),
            Case(404, "{\"codigo\":\"018003\"}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.OPERATION_FAILED))),
            Case(400, "{\"redirect\":\"home\"}", ServiceFailure.Unchanged),
            Case(400, "{\"redirect\":\"desconhecido\"}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.OPERATION_FAILED))),
        )
        for (case in cases) {
            val result = ValidateCart(http(case.status, case.json))()
            assertEquals(case.json, AppResult.Success(CartValidationResult.Rejected(case.expected)), result)
        }
    }

    @Test fun `redirect metadata precedes 401 and all recognized destinations survive the data boundary`() = runTest {
        val routes = mapOf("login" to ServiceRedirect.LOGIN, "aceitar-termo-de-uso" to ServiceRedirect.ACCEPT_TERMS,
            "cadastrar-apostador" to ServiceRedirect.REGISTER_ACCOUNT, "indisponibilidade" to ServiceRedirect.UNAVAILABLE,
            "carrinho" to ServiceRedirect.CART, "carrinhos-favoritos" to ServiceRedirect.FAVORITE_CARTS)
        for ((wire, destination) in routes) assertEquals(
            CartValidationResult.Rejected(ServiceFailure.RedirectRequired(destination)),
            http(401, "{\"redirect\":\"$wire\"}").validate(),
        )
    }

    @Test fun `malformed or absent error metadata never become payment acceptance`() = runTest {
        for (body in listOf("broken", "null", "[]", "x".repeat(65_537))) {
            val result = ValidateCart(http(400, body))()
            assertEquals(AppResult.Success(CartValidationResult.Rejected(ServiceFailure.NoticeRequired(
                ServiceNotice(ServiceNoticeReason.BUSINESS_RULE)))), result)
        }
        assertEquals(AppResult.Success(CartValidationResult.Rejected(ServiceFailure.ConnectionFailed(ServiceConnectionFailure.INVALID_RESPONSE))),
            ValidateCart(http(200, "broken"))())
        assertEquals(CartValidationResult.Rejected(ServiceFailure.ConnectionFailed(ServiceConnectionFailure.INVALID_RESPONSE)),
            failing { throw com.google.gson.JsonParseException("fixture") }.validate())
    }

    @Test fun `timeouts and connection errors stay typed while coroutine cancellation propagates`() = runTest {
        assertEquals(CartValidationResult.Rejected(ServiceFailure.ConnectionFailed(ServiceConnectionFailure.TIMEOUT)),
            failing { throw SocketTimeoutException("fixture") }.validate())
        assertEquals(CartValidationResult.Rejected(ServiceFailure.ConnectionFailed(ServiceConnectionFailure.CONNECTION)),
            failing { throw IOException("fixture") }.validate())
        val cancellation = CancellationException("cancelled")
        assertSame(cancellation, runCatching { ValidateCart(failing { throw cancellation })() }.exceptionOrNull())
    }

    private fun http(status: Int, json: String, calls: MutableList<String> = mutableListOf()): CartRepositoryImpl {
        val api = NetworkClientFactory.create("https://example.invalid/rest/v1/", AuthTokenProvider { "fixture" },
            clientBuilder = OkHttpClient.Builder().addInterceptor { chain ->
                val request = chain.request()
                calls += "${request.method} ${request.url.encodedPath}"
                okhttp3.Response.Builder().request(request).protocol(Protocol.HTTP_1_1).code(status)
                    .message("fixture").body(json.toResponseBody()).build()
            })
        return CartRepositoryImpl(
            br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartRemoteDataSourceImpl(
                LoteriasApiProvider().apply { replace(api) },
            ),
        )
    }

    private fun failing(block: suspend () -> Unit) = CartRepositoryImpl(object : CartRemoteDataSource {
        override suspend fun validateCart() = block()
        override suspend fun currentCart(): CartDto? = error("Unexpected cart read")
        override suspend fun removeCartBet(id: Long): CartDto? = error("Unexpected mutation")
        override suspend fun removeCartCombo(id: Long): CartDto? = error("Unexpected mutation")
        override suspend fun clearCart(): CartDto? = error("Unexpected mutation")
        override suspend fun validateFavoriteCartName(name: String) = error("Unexpected validation")
        override suspend fun saveCurrentCartFavorite(name: String) = error("Unexpected mutation")
    })
}
