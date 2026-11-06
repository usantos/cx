package br.gov.caixa.loterias.core.network

import br.gov.caixa.loterias.core.keymanagement.model.HostPublicKeyPins
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.core.network.provider.BetsHistoryApiProvider
import java.util.Base64
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test

class NetworkClientFactoryTest {
    @Test fun `disabled inspection adds no request logger or inspector and emits no request logs`() {
        for (enabled in listOf(false, true)) {
            val logs = mutableListOf<String>()
            var inspections = 0
            val inspector = okhttp3.Interceptor { chain ->
                inspections++
                chain.proceed(chain.request())
            }
            val builder = OkHttpClient.Builder()
            NetworkClientFactory.create(
                "https://example.test",
                AuthTokenProvider { null },
                builder,
                debugOptions = br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions(
                    enabled,
                    br.gov.caixa.loterias.core.network.interceptor.NetworkLogSink {
                        logs.add(it)
                    },
                    true,
                    inspector
                )
            )
            assertEquals(
                enabled,
                builder.interceptors().any {
                    it is br.gov.caixa.loterias.core.network.interceptor.CurlLoggingInterceptor
                }
            )
            assertEquals(enabled, inspector in builder.interceptors())
            builder.addInterceptor { chain ->
                okhttp3.Response.Builder().request(chain.request()).protocol(okhttp3.Protocol.HTTP_1_1)
                    .code(HTTP_OK).message("OK").body(okhttp3.ResponseBody.create(null, "{}")).build()
            }
            builder.build().newCall(okhttp3.Request.Builder().url("https://example.test/test").build()).execute().close()
            assertEquals(if (enabled) 1 else 0, inspections)
            assertEquals(!enabled, logs.isEmpty())
        }
    }

    @Test
    fun `normalizes secure base urls`() {
        assertEquals("https://example.test/", NetworkClientFactory.normalizeBaseUrl(" https://example.test "))
        assertEquals("https://example.test/", NetworkClientFactory.normalizeBaseUrl("https://example.test/"))
    }

    @Test
    fun `rejects insecure base urls`() {
        assertThrows(IllegalArgumentException::class.java) {
            NetworkClientFactory.normalizeBaseUrl("http://example.test")
        }
    }

    @Test
    fun `creates retrofit api`() {
        val api = NetworkClientFactory.create(
            baseUrl = "https://example.test",
            tokenProvider = AuthTokenProvider { null },
            clientBuilder = OkHttpClient.Builder()
        )
        assertNotNull(api)
    }

    @Test
    fun `creates retrofit api with default client and certificate pins`() {
        val pin = "sha256/${Base64.getEncoder().encodeToString(ByteArray(SHA256_BYTE_COUNT) { SYNTHETIC_PIN_BYTE })}"
        val keySet = PublicKeySet(
            version = 1,
            issuedAtEpochSeconds = 1,
            expiresAtEpochSeconds = 2,
            hosts = listOf(HostPublicKeyPins("example.test", listOf(pin)))
        )

        val api = NetworkClientFactory.create(
            baseUrl = "https://example.test",
            tokenProvider = AuthTokenProvider { null },
            publicKeySet = keySet
        )

        assertNotNull(api)
        assertEquals(1, NetworkClientFactory.certificatePinner(keySet).pins.size)
    }

    @Test
    fun `creates every migrated backend contract`() {
        val token = AuthTokenProvider { "token" }
        val builder = OkHttpClient.Builder()

        assertNotNull(NetworkClientFactory.createLegacy("https://example.test", token, builder))
        assertNotNull(NetworkClientFactory.createPrizeRedemption("https://example.test", token))
        assertNotNull(NetworkClientFactory.createPurchaseBackend("https://example.test", token))
        assertNotNull(NetworkClientFactory.createBetsHistory("https://example.test", token, OkHttpClient.Builder()))
        assertNotNull(NetworkClientFactory.createMercadoPago("https://api.example.test", OkHttpClient.Builder()))
        assertNotNull(NetworkClientFactory.createRecargaPay("https://secure.example.test", OkHttpClient.Builder()))
    }

    @Test
    fun `history provider only creates and caches clients for currently pinned https hosts`() {
        val provider = BetsHistoryApiProvider()
        val fallback = NetworkClientFactory.createBetsHistory("https://silce.caixa.gov.br", AuthTokenProvider { null })
        var creations = 0
        provider.replace(fallback)
        provider.configureDynamicClients(setOf("history.caixa.gov.br")) { baseUrl ->
            creations++
            NetworkClientFactory.createBetsHistory(baseUrl, AuthTokenProvider { null })
        }

        val first = provider.current("https://history.caixa.gov.br/v1")
        val second = provider.current(" https://history.caixa.gov.br/v1/ ")

        assertNotNull(first)
        assertSame(first, second)
        assertSame(fallback, provider.current())
        assertEquals(1, creations)
        assertNull(provider.current("http://history.caixa.gov.br/v1"))
        assertNull(provider.current("https://evil.test/v1"))
        assertNull(provider.current("invalid"))
    }
    private companion object {
        const val HTTP_OK = 200
        const val SHA256_BYTE_COUNT = 32
        const val SYNTHETIC_PIN_BYTE: Byte = 7
    }
}
