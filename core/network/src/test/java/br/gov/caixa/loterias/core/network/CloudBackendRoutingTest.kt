package br.gov.caixa.loterias.core.network

import com.google.gson.Gson
import java.io.IOException
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import br.gov.caixa.loterias.core.network.interceptor.NetworkLogSink
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.interceptor.CurlLoggingInterceptor
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.routing.CloudBackendRouting
import br.gov.caixa.loterias.core.network.model.AppParametersDto

class CloudBackendRoutingTest {
    private val source = "https://mobileloterias.caixa.gov.br/silce-servico-rest/rest/v1/".toHttpUrl()

    @Test
    fun `routes cloud paths using the actual configuration wire names`() {
        val parameters = Gson().fromJson(
            """{"hostMicroServicoCarrinhoProducao":"https://silce.carrinho.caixa.gov.br/loterias-web/carrinho/v1", "hostMicroServicoConferenciaAposta":"https://mobileloterias.caixa.gov.br/silce-servico-rest/rest/v2", "habilitarConferenciaAutomaticaApostas":true, "nuvemApikey":"test-key"}""",
            AppParametersDto::class.java,
        )
        val routing = CloudBackendRouting(source, production = true)
        routing.update(parameters)
        val request = capture(routing, source.resolve("publico/modalidades?especial=true")!!.toString())
        assertEquals("https://silce.carrinho.caixa.gov.br/loterias-web/carrinho/v1/publico/modalidades?especial=true", request.url.toString())
        assertEquals("test-key", request.header("ocp-apim-subscription-key"))
        assertEquals("3", request.header("Subcanal"))
        assertEquals(true, parameters.microserviceEnabled)
        assertEquals("https://mobileloterias.caixa.gov.br/silce-servico-rest/rest/v2", parameters.betsHistoryBaseUrl)
        val logs = mutableListOf<String>()
        val curl = CurlLoggingInterceptor(NetworkDebugOptions(true, NetworkLogSink(logs::add))).toCurl(request)
        assertFalse(curl.contains("test-key"))
    }

    @Test
    fun `pilot routing and replacement preserve original relative path`() {
        val routing = CloudBackendRouting(source, production = false)
        routing.update(AppParametersDto(pilotCartBaseUrl = "https://pilot.caixa.gov.br/cart/", cloudSubscriptionKey = "pilot"))
        val url = source.resolve("logado/combos")!!.toString()
        assertEquals("https://pilot.caixa.gov.br/cart/logado/combos", capture(routing, url).url.toString())
        routing.update(AppParametersDto(pilotCartBaseUrl = "https://next.caixa.gov.br/v1/", cloudSubscriptionKey = "next"))
        assertEquals("next", capture(routing, url).header("ocp-apim-subscription-key"))
    }

    @Test
    fun `missing or malformed configuration fails before a cloud request can be sent`() {
        for (base in listOf(null, "", "http://cloud.caixa.gov.br/", "https://user:pass@cloud.caixa.gov.br/", "https://cloud.caixa.gov.br/?query=1", "https://cloud.caixa.gov.br/#fragment")) {
            val routing = CloudBackendRouting(source, production = true)
            routing.update(AppParametersDto(productionCartBaseUrl = base, cloudSubscriptionKey = "key"))
            assertThrows(IOException::class.java) { capture(routing, source.resolve("publico/modalidades")!!.toString()) }
        }
        val routing = CloudBackendRouting(source, production = true)
        routing.update(AppParametersDto(productionCartBaseUrl = "https://cloud.caixa.gov.br/"))
        assertThrows(IOException::class.java) { capture(routing, source.resolve("logado/combos")!!.toString()) }
    }

    @Test
    fun `main service external urls and unrelated paths never receive a subscription key`() {
        val routing = CloudBackendRouting(source, production = true)
        routing.update(AppParametersDto(productionCartBaseUrl = "https://cloud.caixa.gov.br/", cloudSubscriptionKey = "key"))
        for (url in listOf(source.resolve("resultados/megasena")!!.toString(), "https://example.test/publico/modalidades", "https://mobileloterias.caixa.gov.br/other/publico/modalidades")) {
            val request = capture(routing, url)
            assertEquals(url, request.url.toString())
            assertNull(request.header("ocp-apim-subscription-key"))
        }
    }

    @Test
    fun `configuration callback updates the shared client when parameters load`() {
        val provider = LoteriasApiProvider()
        val parameters = AppParametersDto(minimumMarketplaceBetQuantity = 2)
        provider.updateParameters(parameters)
        var received: AppParametersDto? = null
        provider.onParametersLoaded { received = it }
        provider.updateParameters(parameters)
        assertEquals(parameters, received)
    }

    private fun capture(routing: CloudBackendRouting, url: String): Request {
        var captured: Request? = null
        val client = OkHttpClient.Builder().addInterceptor(routing).addInterceptor { chain ->
            captured = chain.request()
            Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1)
                .code(200).message("OK").body("{}".toResponseBody()).build()
        }.build()
        client.newCall(Request.Builder().url(url).build()).execute().close()
        return checkNotNull(captured)
    }
}
