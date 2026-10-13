package br.gov.caixa.loterias.core.network

import br.gov.caixa.loterias.core.keymanagement.HostPublicKeyPins
import br.gov.caixa.loterias.core.keymanagement.PublicKeySet
import java.util.Base64
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test

class NetworkClientFactoryTest {
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
            clientBuilder = OkHttpClient.Builder(),
        )
        assertNotNull(api)
    }

    @Test
    fun `creates retrofit api with default client and certificate pins`() {
        val pin = "sha256/${Base64.getEncoder().encodeToString(ByteArray(32) { 7 })}"
        val keySet = PublicKeySet(
            version = 1,
            issuedAtEpochSeconds = 1,
            expiresAtEpochSeconds = 2,
            hosts = listOf(HostPublicKeyPins("example.test", listOf(pin))),
        )

        val api = NetworkClientFactory.create(
            baseUrl = "https://example.test",
            tokenProvider = AuthTokenProvider { null },
            publicKeySet = keySet,
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
}
