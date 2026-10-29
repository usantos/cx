package br.gov.caixa.loterias.core.network

import br.gov.caixa.loterias.feature.account.data.dto.AccountVerificationRequestDto

import br.gov.caixa.loterias.core.keymanagement.model.HostPublicKeyPins
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import com.google.gson.Gson
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response
import okio.Buffer
import okio.ForwardingSource
import okio.buffer
import br.gov.caixa.loterias.core.network.interceptor.BearerTokenInterceptor
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.interceptor.SingleAttemptTransactionInterceptor
import br.gov.caixa.loterias.core.network.interceptor.NetworkLogSink
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory
import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.core.network.api.AccountVerificationSessionExpiredException
import br.gov.caixa.loterias.core.network.api.AccountVerificationSessionInterceptor
import br.gov.caixa.loterias.core.network.api.AccountVerificationHttpException
import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendApi
import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendClient
import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendProvider
import br.gov.caixa.loterias.core.network.interceptor.PinnedHostInterceptor

/** Somente respostas no processo: sem apostas de usuários, credenciais reais ou gravações no backend. */
class AccountVerificationBackendTest {
    @Test fun `200 preserves business metadata without treating error or redirect as cart acceptance`() = runTest {
        val result = client(Response.success(envelope.toResponseBody(json))).verify(request, active())
        assertEquals("E1", result.code)
        assertEquals("Mensagem", result.message)
        assertEquals(2, result.type)
        assertEquals("/termos", result.redirect)
        assertEquals(listOf("Campo"), result.validationErrors)
        assertEquals("Pessoa", result.payload?.name)
        assertFalse(result.toString().contains("Pessoa"))
        assertFalse(result.toString().contains("Mensagem"))
        assertEquals(result, result.copy())
        assertEquals(result.hashCode(), result.copy().hashCode())
        assertEquals(listOf("E1", "Mensagem", 2, "/termos", listOf("Campo"), result.payload),
            result.let { (a, b, c, d, e, f) -> listOf(a, b, c, d, e, f) })
    }

    @Test fun `only 200 is accepted at transport level and errors retain envelopes`() = runTest {
        for (status in listOf(201, 202, 204, 301, 302, 303, 307, 308, 400, 401, 404, 500, 503)) {
            val response = if (status < 300) Response.success(status, envelope.toResponseBody(json))
                else Response.error(envelope.toResponseBody(json), okhttp3.Response.Builder()
                    .request(Request.Builder().url("https://example.invalid/").build())
                    .protocol(Protocol.HTTP_1_1).code(status).message("Error").build())
            val error = fail { client(response).verify(request, active()) } as AccountVerificationHttpException
            assertEquals(status, error.statusCode)
            assertEquals("E1", error.envelope?.code)
            assertEquals("/termos", error.envelope?.redirect)
            assertFalse(error.toString().contains("Mensagem"))
        }
    }

    @Test fun `empty malformed oversized and trailing documents are never valid envelopes`() = runTest {
        for (body in listOf("", "null", "[]", "false", "{", "{payload: {}}", "{} {}", "{\"mensagem\":\"" + "x".repeat(1_048_576) + "\"}")) {
            val error = fail { client(Response.success(body.toResponseBody(json))).verify(request, active()) }
            assertTrue(error is IOException)
            assertNull(error.cause)
            val http = fail { client(Response.error(400, body.toResponseBody(json))).verify(request, active()) } as AccountVerificationHttpException
            assertNull(http.envelope)
        }
        assertTrue(fail { client(Response.success(null)).verify(request, active()) } is IOException)
    }

    @Test fun `expired session prevents dispatch and expired response is discarded and closed`() = runTest {
        var calls = 0
        var current = false
        val session = AccountVerificationSession({ "old-secret" }, { current })
        var closed = false
        val source = object : ForwardingSource(Buffer().writeUtf8(envelope)) {
            override fun close() { closed = true; super.close() }
        }.buffer()
        val body = object : ResponseBody() {
            override fun contentType() = json
            override fun contentLength() = -1L
            override fun source() = source
        }
        val transport = object : AccountVerificationBackendApi {
            override suspend fun verify(request: AccountVerificationRequestDto, session: AccountVerificationSession): Response<ResponseBody> {
                calls++
                current = false
                return Response.success(body)
            }
        }
        val client = AccountVerificationBackendClient(transport, Gson())
        assertTrue(fail { client.verify(request, session) } is AccountVerificationSessionExpiredException)
        assertEquals(0, calls)
        current = true
        assertTrue(fail { client.verify(request, session) } is AccountVerificationSessionExpiredException)
        assertEquals(1, calls)
        assertTrue(closed)
        assertEquals("AccountVerificationSession(redacted)", session.toString())
    }

    @Test fun `factory sends exact tagged session body once with redacted curl and pinned policy`() = runTest {
        val requests = mutableListOf<Request>()
        val logs = mutableListOf<String>()
        val builder = OkHttpClient.Builder().addInterceptor { chain ->
            chain.proceed(chain.request()).also { requests += it.request }
        }
        val keys = PublicKeySet(1, 1, Long.MAX_VALUE, listOf(
            HostPublicKeyPins("mock.caixa.gov.br", listOf("sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")),
        ))
        val client = NetworkClientFactory.createAccountVerification("https://mock.caixa.gov.br/rest/v1/", builder,
            publicKeySet = keys, debugOptions = NetworkDebugOptions(true, NetworkLogSink { logs += it }),
            mockOptions = MockBackendOptions(true))
        val session = active()
        client.verify(request, session)
        assertEquals(1, requests.size)
        with(requests.single()) {
            assertEquals("POST", method)
            assertEquals("/rest/v1/logado/usuarios/verificar-cadastro", url.encodedPath)
            assertEquals("Bearer secret", header("Authorization"))
            assertSame(session, tag(AccountVerificationSession::class.java))
            assertTrue(body!!.isOneShot())
            assertEquals("{\"apostas\":[]}", okio.Buffer().also { body!!.writeTo(it) }.readUtf8())
        }
        assertTrue(logs.any { "curl" in it })
        assertTrue(logs.any { "[BODY REDACTED]" in it })
        assertTrue(logs.none { "secret" in it })
        with(builder.build()) {
            assertFalse(followRedirects)
            assertFalse(followSslRedirects)
            assertFalse(retryOnConnectionFailure)
            assertTrue(interceptors.any { it is AccountVerificationSessionInterceptor })
            assertTrue(interceptors.any { it is SingleAttemptTransactionInterceptor })
            assertTrue(interceptors.none { it is BearerTokenInterceptor })
            assertTrue(interceptors.any { it is PinnedHostInterceptor })
            assertTrue(networkInterceptors.any { it is PinnedHostInterceptor })
        }
    }

    @Test fun `no credential or missing tag fails closed without reaching terminal interceptor`() = runTest {
        var calls = 0
        val builder = OkHttpClient.Builder()
        val client = NetworkClientFactory.createAccountVerification("https://example.invalid/", builder)
        builder.addInterceptor { calls++; throw AssertionError("Must not send") }
        for (token in listOf(null, "", " ")) {
            val session = AccountVerificationSession({ token }, { true })
            assertTrue(fail { client.verify(request, session) } is AccountVerificationSessionExpiredException)
        }
        val missingTag = Request.Builder().url("https://example.invalid/").build()
        try {
            builder.build().newCall(missingTag).execute()
            fail("Expected missing lease failure")
        } catch (_: AccountVerificationSessionExpiredException) { }
        assertEquals(0, calls)
    }

    @Test fun `old lease cannot substitute new token and switching during response closes it`() = runTest {
        var current = true
        val builder = OkHttpClient.Builder()
        NetworkClientFactory.createAccountVerification("https://example.invalid/", builder)
        var calls = 0
        builder.addInterceptor { chain ->
            calls++
            assertEquals("Bearer old", chain.request().header("Authorization"))
            current = false
            okhttp3.Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(200)
                .message("OK").body(envelope.toResponseBody(json)).build()
        }
        val session = AccountVerificationSession({ if (current) "old" else null }, { current })
        val req = Request.Builder().url("https://example.invalid/")
            .header("Authorization", "Bearer wrong-login").tag(AccountVerificationSession::class.java, session).build()
        repeat(2) {
            try {
                builder.build().newCall(req).execute()
                fail("Expected expired lease")
            } catch (_: AccountVerificationSessionExpiredException) { }
        }
        assertEquals(1, calls)
        current = true
        val racing = AccountVerificationSession({ current = false; "old" }, { current })
        try {
            builder.build().newCall(req.newBuilder().tag(AccountVerificationSession::class.java, racing).build()).execute()
            fail("Expected expiry after atomic token read")
        } catch (_: AccountVerificationSessionExpiredException) { }
        assertEquals(1, calls)
    }

    @Test fun `provider has no fallback and cancellation or response loss are not retried`() = runTest {
        val provider = AccountVerificationBackendProvider()
        try { provider.current(); fail("Expected unconfigured provider") } catch (_: IllegalStateException) { }
        val first = client(Response.success(envelope.toResponseBody(json)))
        provider.replace(first)
        assertSame(first, provider.current())
        val second = client(Response.success(envelope.toResponseBody(json)))
        provider.replace(second)
        assertSame(second, provider.current())
        for (error in listOf(CancellationException("cancelled"), IOException("response lost"))) {
            var calls = 0
            val api = object : AccountVerificationBackendApi {
                override suspend fun verify(request: AccountVerificationRequestDto, session: AccountVerificationSession): Response<ResponseBody> {
                    calls++
                    throw error
                }
            }
            assertSame(error, fail { AccountVerificationBackendClient(api, Gson()).verify(request, active()) })
            assertEquals(1, calls)
        }
    }

    private fun client(response: Response<ResponseBody>) = AccountVerificationBackendClient(object : AccountVerificationBackendApi {
        override suspend fun verify(request: AccountVerificationRequestDto, session: AccountVerificationSession) = response
    }, Gson())

    private suspend fun fail(action: suspend () -> Unit): Exception {
        try { action() } catch (error: Exception) { return error }
        throw AssertionError("Expected failure")
    }

    private fun active() = AccountVerificationSession({ "secret" }, { true })
    private val request = AccountVerificationRequestDto()
    private val json = "application/json".toMediaType()
    private val envelope = """{"codigo":"E1","mensagem":"Mensagem","tipo":2,"redirect":"/termos","errosValidacao":["Campo"],"payload":{"nome":"Pessoa"}}"""
}
