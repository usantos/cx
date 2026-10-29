package br.gov.caixa.loterias.core.network.interceptor

import java.io.IOException
import java.net.SocketTimeoutException
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.mock.MockBackendScenario
import br.gov.caixa.loterias.core.network.mock.fixture.MockBackendFixtures

/** Backend determinístico no processo. Nunca abre sockets quando habilitado. */
class MockBackendInterceptor(
    private val options: MockBackendOptions,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!options.enabled) return chain.proceed(chain.request())
        when (options.scenario) {
            MockBackendScenario.NETWORK_ERROR -> throw IOException("Mock network error")
            MockBackendScenario.TIMEOUT -> throw SocketTimeoutException("Mock timeout")
            MockBackendScenario.SLOW_SUCCESS -> Thread.sleep(options.slowDelayMillis.coerceAtLeast(0))
            else -> Unit
        }

        val request = chain.request()
        if (options.scenario == MockBackendScenario.PURCHASE_RESPONSE_LOST &&
            request.method == "POST" && request.url.encodedPath.endsWith("/logado/carrinhos/registrar-apostas-async")) {
            throw SocketTimeoutException("Mock purchase response lost")
        }
        val requestBody = runCatching {
            Buffer().use { buffer ->
                request.body?.writeTo(buffer)
                buffer.readUtf8()
            }
        }.getOrDefault("")
        val mocked = MockBackendFixtures.resolve(
            request.url.encodedPath, options.scenario, requestBody,
            purchaseId = request.url.queryParameter("idCompraCarrinho")?.toLongOrNull() ?: 99,
            contests = request.url.queryParameterValues("concursos").mapNotNull { it?.toIntOrNull() },
            marketplaceArea = request.url.queryParameter("tipoConsulta")?.toIntOrNull(),
        )
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(mocked.code)
            .message(mocked.message)
            .body(mocked.body.toResponseBody(JSON))
            .build()
    }

    private companion object {
        val JSON = "application/json; charset=utf-8".toMediaType()
    }
}
