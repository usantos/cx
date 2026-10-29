package br.gov.caixa.loterias.core.network

import br.gov.caixa.loterias.core.keymanagement.model.HostPublicKeyPins
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import br.gov.caixa.loterias.core.network.adapter.ServerTimeAdapterFactory
import br.gov.caixa.loterias.core.network.api.PurchaseBackendApi
import br.gov.caixa.loterias.core.network.api.PurchaseBackendApiProvider
import br.gov.caixa.loterias.core.network.api.PurchaseBackendClient
import br.gov.caixa.loterias.core.network.api.PurchaseHttpException
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.core.network.interceptor.BearerTokenInterceptor
import br.gov.caixa.loterias.core.network.interceptor.CurlLoggingInterceptor
import br.gov.caixa.loterias.core.network.interceptor.MockBackendInterceptor
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.interceptor.PinnedHostInterceptor
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.mock.MockBackendScenario
import br.gov.caixa.loterias.feature.payment.data.dto.PurchaseEnvelopeDto
import br.gov.caixa.loterias.feature.payment.data.dto.PurchaseErrorEnvelopeDto
import br.gov.caixa.loterias.feature.payment.data.dto.PurchaseProcessingDto
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.io.IOException
import javax.net.ssl.SSLPeerUnverifiedException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.buffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

/** Esta suíte não usa sockets, credenciais reais ou chamadas a provedores de pagamento. */
class PurchaseBackendClientTest {
    @Test fun `lost response mock fails only the submission and keeps purchase review available`() = runTest {
        val client = NetworkClientFactory.createPurchaseBackend(
            "https://mock.caixa.gov.br/rest/v1/",
            AuthTokenProvider { null },
            mockOptions = MockBackendOptions(true, MockBackendScenario.PURCHASE_RESPONSE_LOST)
        )
        assertTrue(failure { client.submit(JsonParser.parseString("{}")) } is java.net.SocketTimeoutException)
        val progress = client.progress(91)
        assertFalse(progress.processing)
        assertEquals(8, progress.envelope.payload?.status)
        assertEquals(MockBackendScenario.PURCHASE_RESPONSE_LOST, MockBackendScenario.from("purchase_response_lost"))
    }

    @Test fun `200 and 303 preserve payload code redirect message and server clock`() = runTest {
        for (status in listOf(200, 303)) {
            val times = mutableListOf<String>()
            val gson = GsonBuilder().registerTypeAdapterFactory(ServerTimeAdapterFactory { times += it }).create()
            val requests = mutableListOf<Request>()
            val client = NetworkClientFactory.createPurchaseBackend(
                "https://example.invalid/rest/v1/",
                AuthTokenProvider { null },
                clientBuilder = OkHttpClient.Builder().addInterceptor { chain ->
                    requests += chain.request()
                    rawResponse(status, envelope, chain.request())
                },
                gson = gson
            )
            val response = client.submit(JsonParser.parseString("{}"))
            val progress = client.progress(91)
            assertEquals(response, progress)
            assertEquals(status == 303, response.processing)
            val submission = response.envelope
            assertEquals(PurchaseProcessingDto(91, 2345, 7, 2), submission.payload)
            assertEquals("E1", submission.code)
            assertEquals("/carrinhos/verifica-compra-processamento", submission.redirect)
            assertEquals("Mensagem", submission.message)
            assertEquals(listOf("2026-09-07T12:00:00", "2026-09-07T12:00:00"), times)
            assertEquals(listOf("POST", "GET"), requests.map { it.method })
            assertEquals("/rest/v1/logado/carrinhos/registrar-apostas-async", requests[0].url.encodedPath)
            assertEquals("91", requests[1].url.queryParameter("idCompraCarrinho"))
        }
    }

    @Test fun `303 Location header remains a business envelope without another request`() = runTest {
        var calls = 0
        val builder = OkHttpClient.Builder().addInterceptor { chain ->
            calls++
            rawResponse(303, envelope, chain.request()).newBuilder()
                .header("Location", "https://untrusted.invalid/collect").build()
        }
        val client = NetworkClientFactory.createPurchaseBackend("https://example.invalid/", AuthTokenProvider { null }, builder)
        assertEquals(91L, client.submit(JsonParser.parseString("{}")).envelope.payload?.id)
        assertEquals(1, calls)
        // Respostas interceptadas isoladamente não exercitam as continuações de rede do OkHttp.
        // Também verifica a política real do builder; TLS real continua sendo testado no Android.
        with(builder.build()) {
            assertFalse(followRedirects)
            assertFalse(followSslRedirects)
            assertFalse(retryOnConnectionFailure)
        }
    }

    @Test fun `JSON cannot override the processing signal supplied by the HTTP response`() = runTest {
        val dto = Gson().fromJson("""{"processing":true,"payload":{"id":1}}""", PurchaseEnvelopeDto::class.java)
        val success = PurchaseBackendClient(FakeApi(Response.success(dto)), Gson()).progress(1)
        assertFalse(success.processing)
        val continuation = PurchaseBackendClient(FakeApi(error(303, """{"processing":false,"payload":{"id":1}}""")), Gson()).progress(1)
        assertTrue(continuation.processing)
    }

    @Test fun `dedicated builder preserves bearer logging and both pin guards`() {
        val builder = OkHttpClient.Builder()
        val keys = PublicKeySet(
            1,
            1,
            Long.MAX_VALUE,
            listOf(
                HostPublicKeyPins("example.invalid", listOf("sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="))
            )
        )
        NetworkClientFactory.createPurchaseBackend(
            "https://example.invalid/",
            AuthTokenProvider { null },
            builder,
            publicKeySet = keys,
            debugOptions = NetworkDebugOptions(enabled = true)
        )
        with(builder.build()) {
            assertTrue(interceptors.any { it is BearerTokenInterceptor })
            assertTrue(interceptors.any { it is CurlLoggingInterceptor })
            assertTrue(interceptors.any { it is MockBackendInterceptor })
            assertEquals(1, interceptors.count { it is PinnedHostInterceptor })
            assertEquals(1, networkInterceptors.count { it is PinnedHostInterceptor })
            assertNotEquals(okhttp3.CertificatePinner.DEFAULT, certificatePinner)
        }
    }

    @Test fun `non 303 errors preserve business metadata and HTTP status without retry`() = runTest {
        for (status in listOf(301, 302, 307, 400, 401, 404, 500)) {
            val api = FakeApi(error(status, envelope))
            val failure = failure { PurchaseBackendClient(api, Gson()).progress(1) }
            assertTrue(failure is PurchaseHttpException)
            failure as PurchaseHttpException
            assertEquals(status, failure.statusCode)
            assertEquals("E1", failure.error?.code)
            assertEquals("/carrinhos/verifica-compra-processamento", failure.error?.redirect)
            assertEquals("Mensagem", failure.error?.message)
            assertEquals(1, api.calls)
        }
    }

    @Test fun `HTTP errors read metadata independently from payload and never expose it in exception text`() = runTest {
        val json =
            """{"payload":["arbitrary"],"statusCode":200,"codigo":"011010","redirect":"carrinho",""" +
                """"mensagem":"private response","tipo":"1"}"""
        val result = failure { PurchaseBackendClient(FakeApi(error(400, json)), Gson()).submit(JsonParser.parseString("{}")) }
        assertTrue(result is PurchaseHttpException)
        result as PurchaseHttpException
        assertEquals(400, result.statusCode)
        assertEquals(PurchaseErrorEnvelopeDto("011010", "carrinho", "private response", "1"), result.error)
        assertFalse(result.toString().contains("private response"))
        assertFalse(result.toString().contains("011010"))
        assertNull(result.cause)
    }

    @Test fun `invalid HTTP error bodies preserve status without accepting unparsed directives`() = runTest {
        for (body in listOf("", "null", "{", "[]", "{\"redirect\":{}}", " ".repeat(65_537))) {
            val result = failure { PurchaseBackendClient(FakeApi(error(401, body)), Gson()).progress(1) }
            assertTrue(result is PurchaseHttpException)
            result as PurchaseHttpException
            assertEquals(401, result.statusCode)
            assertNull(result.error)
        }
        val boundary = "{}" + " ".repeat(65_534)
        val result = failure { PurchaseBackendClient(FakeApi(error(400, boundary)), Gson()).progress(1) } as PurchaseHttpException
        assertEquals(PurchaseErrorEnvelopeDto(), result.error)
    }

    @Test fun `purchase client blocks unpinned hosts and expired configuration before network access`() = runTest {
        val host = HostPublicKeyPins("example.invalid", listOf("sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="))
        for ((url, keys) in listOf(
            "https://unknown.invalid/" to PublicKeySet(1, 1, Long.MAX_VALUE, listOf(host)),
            "https://example.invalid/" to PublicKeySet(1, 1, 2, listOf(host))
        )) {
            val client = NetworkClientFactory.createPurchaseBackend(url, AuthTokenProvider { null }, publicKeySet = keys)
            assertTrue(failure { client.progress(1) } is SSLPeerUnverifiedException)
        }
    }

    @Test fun `303 and HTTP error bodies close after successful malformed or failed decoding`() = runTest {
        for (status in listOf(303, 400, 401, 500)) {
            for ((text, readFailure) in listOf("{}" to false, "{" to false, "{}" to true, " ".repeat(65_537) to false)) {
                var closed = false
                val source = object : okio.ForwardingSource(okio.Buffer().writeUtf8(text)) {
                    override fun read(sink: okio.Buffer, byteCount: Long): Long {
                        if (readFailure) throw IOException("read failed")
                        return super.read(sink, byteCount)
                    }
                    override fun close() {
                        closed = true
                        super.close()
                    }
                }
                val buffered = source.buffer()
                val body = object : okhttp3.ResponseBody() {
                    override fun contentType() = "application/json".toMediaType()
                    override fun contentLength() = -1L
                    override fun source() = buffered
                }
                val response = Response.error<PurchaseEnvelopeDto>(body, rawResponse(status, ""))
                val client = PurchaseBackendClient(FakeApi(response), Gson())
                if (status == 303 && text == "{}" && !readFailure) client.progress(1) else failure { client.progress(1) }
                assertTrue(closed)
            }
        }
    }

    @Test fun `empty malformed and oversized 303 responses fail`() = runTest {
        for (body in listOf("", "null", "{", "[]", "{\"payload\":\"wrong\"}", " ".repeat(65_537))) {
            assertNotNull(failure { PurchaseBackendClient(FakeApi(error(303, body)), Gson()).progress(1) })
        }
        val boundary = "{}" + " ".repeat(65_534)
        assertNull(PurchaseBackendClient(FakeApi(error(303, boundary)), Gson()).progress(1).envelope.payload)
        assertTrue(failure { PurchaseBackendClient(FakeApi(Response.success(null)), Gson()).progress(1) } is IOException)
    }

    @Test fun `no payload and unknown business directives are preserved for domain validation`() = runTest {
        for (body in listOf("{}", "{\"redirect\":\"login\"}", "{\"codigo\":\"\",\"redirect\":\"https://untrusted.invalid\"}")) {
            val result = PurchaseBackendClient(FakeApi(error(303, body)), Gson()).progress(1)
            assertEquals(Gson().fromJson(body, PurchaseEnvelopeDto::class.java), result.envelope)
            assertTrue(result.processing)
            assertNull(result.envelope.payload)
        }
    }

    @Test fun `transport exceptions and cancellation propagate without another API call`() = runTest {
        for (expected in listOf(IOException("offline"), CancellationException("cancelled"))) {
            val api = FakeApi(Response.success(PurchaseEnvelopeDto()), expected)
            assertSame(expected, failure { PurchaseBackendClient(api, Gson()).submit(JsonParser.parseString("{}")) })
            assertEquals(1, api.calls)
        }
    }

    @Test fun `provider fails before preparation and uses replacement client after rotation`() = runTest {
        val provider = PurchaseBackendApiProvider()
        assertThrows(IllegalStateException::class.java) { provider.current() }
        for (id in listOf(1L, 2L)) {
            provider.replace(PurchaseBackendClient(FakeApi(Response.success(PurchaseEnvelopeDto(PurchaseProcessingDto(id = id)))), Gson()))
            assertEquals(id, provider.current().progress(1).envelope.payload?.id)
        }
    }

    private class FakeApi(val response: Response<PurchaseEnvelopeDto>, val throwable: Exception? = null) : PurchaseBackendApi {
        var calls = 0
        private fun answer(): Response<PurchaseEnvelopeDto> {
            calls++
            throwable?.let { throw it }
            return response
        }
        override suspend fun registerCartBetsAsync(body: JsonElement) = answer()
        override suspend fun purchaseProcessing(purchaseId: Long) = answer()
    }

    private suspend fun failure(block: suspend () -> Unit): Throwable {
        try {
            block()
        } catch (failure: Exception) {
            return failure
        }
        throw AssertionError("Expected a failure")
    }

    private fun error(code: Int, body: String): Response<PurchaseEnvelopeDto> =
        Response.error(body.toResponseBody("application/json".toMediaType()), rawResponse(code, ""))

    private fun rawResponse(code: Int, body: String, request: Request = Request.Builder().url("https://example.invalid/").build()) =
        okhttp3.Response.Builder().request(request).protocol(Protocol.HTTP_1_1).code(code).message("Test")
            .body(body.toResponseBody("application/json".toMediaType())).build()

    private val envelope =
        """{"payload":{"id":91,"periodicidade":2345,"repeticoes":7,"idSituacaoCompra":2},"codigo":"E1",""" +
            """"redirect":"/carrinhos/verifica-compra-processamento","mensagem":"Mensagem",""" +
            """"dataHoraServidor":"2026-09-07T12:00:00"}"""
}
