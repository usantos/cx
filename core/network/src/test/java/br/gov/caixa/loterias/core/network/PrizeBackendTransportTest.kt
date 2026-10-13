package br.gov.caixa.loterias.core.network

import br.gov.caixa.loterias.core.keymanagement.HostPublicKeyPins
import br.gov.caixa.loterias.core.keymanagement.PublicKeySet
import com.google.gson.JsonObject
import java.io.IOException
import javax.net.ssl.SSLPeerUnverifiedException
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.HttpException

/** In-memory transport checks, not a substitute for socket/TLS and Android validation. */
class PrizeBackendTransportTest {
    @Test fun `redemption client disables redirects and retries without changing general client policy`() {
        val builder = OkHttpClient.Builder().followRedirects(true).followSslRedirects(true).retryOnConnectionFailure(true)
        NetworkClientFactory.createPrizeRedemption("https://example.invalid/", AuthTokenProvider { null }, builder)
        with(builder.build()) {
            assertFalse(followRedirects)
            assertFalse(followSslRedirects)
            assertFalse(retryOnConnectionFailure)
            assertEquals(1, interceptors.count { it is SingleAttemptTransactionInterceptor })
        }
        val general = OkHttpClient.Builder()
        NetworkClientFactory.createLegacy("https://example.invalid/", AuthTokenProvider { null }, general)
        assertTrue(general.build().followRedirects)
        assertTrue(general.build().retryOnConnectionFailure)
        assertTrue(general.build().interceptors.none { it is SingleAttemptTransactionInterceptor })
    }

    @Test fun `redirect metadata remains available and Location is not treated as redemption success`() = runTest {
        for (status in listOf(301, 302, 303, 307, 308, 400, 401, 408, 421, 500, 503)) {
            var calls = 0
            val api = NetworkClientFactory.createPrizeRedemption("https://example.invalid/rest/v1/", AuthTokenProvider { null },
                OkHttpClient.Builder().addInterceptor { chain ->
                    calls++
                    assertEquals("POST", chain.request().method)
                    assertEquals("/rest/v1/apostas/17/resgatar-premio", chain.request().url.encodedPath)
                    Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(status).message("Test")
                        .header("Location", "https://untrusted.invalid/collect")
                        .header("Retry-After", "0")
                        .body("""{"redirect":"login","codigo":"018003","mensagem":"private detail"}""".toResponseBody()).build()
                })
            val error = runCatching { api.redeemPrize(17, JsonObject()) }.exceptionOrNull() as HttpException
            val failure = error.toBackendHttpFailure()
            assertEquals(status, failure.statusCode)
            assertEquals("login", failure.metadata?.redirect)
            assertEquals("018003", failure.metadata?.code)
            assertFalse(failure.toString().contains("private detail"))
            assertEquals(1, calls)
        }
        // Application interceptors precede OkHttp follow-up handling: the policy test above
        // checks the configured flags independently; this is not proof of live HTTP behavior.
    }

    @Test fun `prize client retains authentication logging mocks pins and fails closed before sockets`() = runTest {
        val keys = PublicKeySet(1, 1, Long.MAX_VALUE, listOf(
            HostPublicKeyPins("example.invalid", listOf("sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")),
        ))
        val builder = OkHttpClient.Builder()
        NetworkClientFactory.createPrizeRedemption("https://example.invalid/", AuthTokenProvider { null }, builder, publicKeySet = keys)
        with(builder.build()) {
            assertTrue(interceptors.any { it is BearerTokenInterceptor })
            assertTrue(interceptors.any { it is CurlLoggingInterceptor })
            assertTrue(interceptors.any { it is MockBackendInterceptor })
            assertEquals(1, interceptors.count { it is PinnedHostInterceptor })
            assertEquals(1, networkInterceptors.count { it is PinnedHostInterceptor })
            assertNotEquals(okhttp3.CertificatePinner.DEFAULT, certificatePinner)
        }
        for ((url, pins) in listOf("https://unknown.invalid/" to keys, "https://example.invalid/" to keys.copy(expiresAtEpochSeconds = 2))) {
            val api = NetworkClientFactory.createPrizeRedemption(url, AuthTokenProvider { null }, publicKeySet = pins)
            assertTrue(runCatching { api.redeemPrize(1, JsonObject()) }.exceptionOrNull() is SSLPeerUnverifiedException)
        }
    }

    @Test fun `transport failure is not reissued and mock success retains redacted request logging`() = runTest {
        var calls = 0
        val lostResponse = IOException("response lost")
        val api = NetworkClientFactory.createPrizeRedemption("https://example.invalid/", AuthTokenProvider { null },
            OkHttpClient.Builder().addInterceptor { calls++; throw lostResponse })
        val failure = runCatching { api.redeemPrize(1, JsonObject()) }.exceptionOrNull()
        assertTrue(failure is IOException)
        // Coroutine stack recovery may copy the exception while retaining its original cause.
        assertTrue(generateSequence(failure) { it.cause }.any { it === lostResponse })
        assertEquals(1, calls)
        val logs = mutableListOf<String>()
        val requests = mutableListOf<okhttp3.Request>()
        val mock = NetworkClientFactory.createPrizeRedemption("https://mock.caixa.gov.br/", AuthTokenProvider { "secret-bearer" },
            OkHttpClient.Builder().addInterceptor { chain ->
                val response = chain.proceed(chain.request())
                requests += response.request
                response
            }, debugOptions = NetworkDebugOptions(true, NetworkLogSink { logs += it }),
            mockOptions = MockBackendOptions(true, MockBackendScenario.HAPPY_PATH))
        mock.redeemPrize(1, JsonObject().apply { addProperty("meioPagamento", 1); addProperty("EMAIL", "private@example.test") })
        assertEquals("Bearer secret-bearer", requests.single().header("Authorization"))
        assertTrue(requests.single().body!!.isOneShot())
        assertTrue(logs.isNotEmpty())
        assertTrue(logs.none { it.contains("secret-bearer") || it.contains("private@example.test") })
    }
}
