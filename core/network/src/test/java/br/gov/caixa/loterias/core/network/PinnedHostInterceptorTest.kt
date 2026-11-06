package br.gov.caixa.loterias.core.network

import br.gov.caixa.loterias.core.keymanagement.model.HostPublicKeyPins
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import java.lang.reflect.Proxy
import javax.net.ssl.SSLPeerUnverifiedException
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory
import br.gov.caixa.loterias.core.network.interceptor.PinnedHostInterceptor

class PinnedHostInterceptorTest {
    private val keySet = PublicKeySet(1, 1, Long.MAX_VALUE, listOf(
        HostPublicKeyPins("api.caixa.gov.br", listOf("sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")),
        HostPublicKeyPins("empty.caixa.gov.br", emptyList()),
    ))

    @Test fun `existing client rejects pins at expiry including a redirect hop`() {
        var now = 10L
        var requests = 0
        val guard = PinnedHostInterceptor(keySet.copy(expiresAtEpochSeconds = 11)) { now }
        val request = Request.Builder().url("https://api.caixa.gov.br/").build()
        guard.intercept(chain(request) { requests++ }).close()
        now = 11
        val failure = assertThrows(SSLPeerUnverifiedException::class.java) {
            guard.intercept(chain(request) { requests++ })
        }
        assertEquals(1, requests)
        assertEquals("A configuração de pinagem TLS expirou. Atualize a configuração para continuar.", failure.message)
        now = 9
        assertThrows(SSLPeerUnverifiedException::class.java) { guard.intercept(chain(request) { requests++ }) }
        assertEquals(1, requests)
    }

    @Test fun `only explicitly pinned HTTPS host reaches the next interceptor`() {
        val request = Request.Builder().url("https://API.CAIXA.GOV.BR/resource").header("Authorization", "Bearer test-token").build()
        var forwarded: Request? = null
        PinnedHostInterceptor(keySet).intercept(chain(request) { forwarded = it }).close()
        assertSame(request, forwarded)
    }

    @Test fun `missing pins empty pin list subdomains and cleartext fail before sending data`() {
        listOf("https://other.caixa.gov.br", "https://empty.caixa.gov.br", "https://sub.api.caixa.gov.br",
            "https://api.caixa.gov.br.evil.test", "http://api.caixa.gov.br").forEach { url ->
            val request = Request.Builder().url("$url/?token=sensitive").build()
            var proceeded = false
            val failure = assertThrows(SSLPeerUnverifiedException::class.java) {
                PinnedHostInterceptor(keySet).intercept(chain(request) { proceeded = true })
            }
            assertFalse(proceeded)
            assertEquals("O destino não possui pinagem TLS configurada.", failure.message)
        }
        assertThrows(SSLPeerUnverifiedException::class.java) {
            PinnedHostInterceptor(keySet.copy(hosts = emptyList())).intercept(
                chain(Request.Builder().url("https://api.caixa.gov.br").build()) { fail("Unexpected network request") })
        }
    }

    @Test fun `all factories guard initial requests and redirect hops when pins are configured`() {
        val createClients: List<(OkHttpClient.Builder) -> Unit> = listOf(
            { NetworkClientFactory.create("https://api.caixa.gov.br", AuthTokenProvider { null }, it, publicKeySet = keySet) },
            { NetworkClientFactory.createLegacy("https://api.caixa.gov.br", AuthTokenProvider { null }, it, publicKeySet = keySet) },
            { NetworkClientFactory.createPrizeRedemption("https://api.caixa.gov.br", AuthTokenProvider { null }, it, publicKeySet = keySet) },
            { NetworkClientFactory.createPurchaseBackend("https://api.caixa.gov.br", AuthTokenProvider { null }, it, publicKeySet = keySet) },
            { NetworkClientFactory.createBetsHistory("https://api.caixa.gov.br", AuthTokenProvider { null }, it, publicKeySet = keySet) },
            { NetworkClientFactory.createMercadoPago("https://api.caixa.gov.br", it, publicKeySet = keySet) },
            { NetworkClientFactory.createRecargaPay("https://api.caixa.gov.br", it, publicKeySet = keySet) },
        )
        createClients.forEach { create ->
            val builder = OkHttpClient.Builder()
            create(builder)
            val client = builder.build()
            val guard = client.interceptors.filterIsInstance<PinnedHostInterceptor>().single()
            assertSame(guard, client.networkInterceptors.filterIsInstance<PinnedHostInterceptor>().single())
            val redirected = Request.Builder().url("https://unpinned.caixa.gov.br").build()
            assertThrows(SSLPeerUnverifiedException::class.java) {
                guard.intercept(chain(redirected) { fail("Redirect must not transmit application data") })
            }
        }
    }

    @Test fun `covered host still requires a matching peer certificate`() {
        val pinner = NetworkClientFactory.certificatePinner(keySet)
        assertThrows(SSLPeerUnverifiedException::class.java) { pinner.check("api.caixa.gov.br", emptyList()) }
        // Por isso, instalar CertificatePinner sem a proteção de hosts era insuficiente.
        pinner.check("unpinned.caixa.gov.br", emptyList())
    }

    @Test fun `mock remains local but disabled mock cannot call an unpinned real host`() {
        val request = Request.Builder().url("https://unpinned.caixa.gov.br/app/valida-versao/1.24.0").build()
        for (mockEnabled in listOf(false, true)) {
            val builder = OkHttpClient.Builder().dns { error("DNS must not be used") }
            NetworkClientFactory.create("https://unpinned.caixa.gov.br", AuthTokenProvider { null }, builder,
                publicKeySet = keySet, mockOptions = MockBackendOptions(enabled = mockEnabled))
            val client = builder.build()
            if (mockEnabled) client.newCall(request).execute().use { assertEquals(200, it.code) }
            else assertThrows(SSLPeerUnverifiedException::class.java) { client.newCall(request).execute() }
        }
    }

    private fun chain(request: Request, onProceed: (Request) -> Unit): Interceptor.Chain =
        Proxy.newProxyInstance(Interceptor.Chain::class.java.classLoader, arrayOf(Interceptor.Chain::class.java)) { _, method, args ->
            when (method.name) {
                "request" -> request
                "proceed" -> {
                    val forwarded = args!![0] as Request
                    onProceed(forwarded)
                    Response.Builder().request(forwarded).protocol(Protocol.HTTP_1_1).code(200).message("OK")
                        .body("".toResponseBody()).build()
                }
                else -> error("Unexpected chain method: ${method.name}")
            }
        } as Interceptor.Chain
}
