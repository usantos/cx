package br.gov.caixa.loterias.core.network

import java.io.IOException
import java.net.SocketTimeoutException
import java.time.Instant
import java.time.OffsetDateTime
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.mock.MockBackendScenario
import br.gov.caixa.loterias.core.network.interceptor.MockBackendInterceptor
import br.gov.caixa.loterias.core.network.mock.fixture.MockBackendFixtures

class MockBackendInterceptorTest {
    @Test
    fun `disabled mock delegates to the real chain`() = MockWebServer().use { server ->
        server.enqueue(MockResponse().setBody("real"))
        val client = OkHttpClient.Builder()
            .addInterceptor(MockBackendInterceptor(MockBackendOptions()))
            .build()

        client.newCall(Request.Builder().url(server.url("/real")).build()).execute().use { response ->
            assertEquals("real", response.body.string())
        }
    }

    @Test
    fun `enabled mock returns deterministic response without a server`() {
        val response = call(MockBackendScenario.HAPPY_PATH, "/rest/v1/app/valida-versao/1.24.0")

        assertEquals(200, response.first)
        assertTrue(response.second.contains("true"))
    }

    @Test
    fun `mock exposes every HTTP failure situation`() {
        val expected = mapOf(
            MockBackendScenario.UNAUTHENTICATED to 401,
            MockBackendScenario.FORBIDDEN to 403,
            MockBackendScenario.VALIDATION_ERROR to 422,
            MockBackendScenario.SERVER_ERROR to 500,
        )
        expected.forEach { (scenario, code) ->
            assertEquals(code, call(scenario, "/rest/v1/logado/carrinhos").first)
        }
    }

    @Test
    fun `mock exposes network timeout and slow success`() {
        assertThrows(IOException::class.java) { call(MockBackendScenario.NETWORK_ERROR, "/rest/v1/logado/carrinhos") }
        assertThrows(SocketTimeoutException::class.java) { call(MockBackendScenario.TIMEOUT, "/rest/v1/logado/carrinhos") }
        assertEquals(
            200,
            call(MockBackendScenario.SLOW_SUCCESS, "/rest/v1/logado/carrinhos", slowDelayMillis = 0).first,
        )
    }

    @Test
    fun `scenario names are case insensitive and unknown names are safe`() {
        assertEquals(MockBackendScenario.PIX_EXPIRED, MockBackendScenario.from(" pix_expired "))
        assertEquals(MockBackendScenario.HAPPY_PATH, MockBackendScenario.from("unknown"))
    }

    @Test
    fun `empty and business scenarios change only their intended payloads`() {
        assertTrue(call(MockBackendScenario.EMPTY, "/rest/v1/publico/modalidades").second.contains("[]"))
        assertTrue(call(MockBackendScenario.CART_EXPIRED_POOL, "/rest/v1/logado/carrinhos").second.contains("true"))
        assertTrue(call(MockBackendScenario.TEIMOSINHA_RESCUED, "/rest/v1/apostas").second.contains("idApostaTroca"))
        assertTrue(call(MockBackendScenario.TEIMOSINHA_RESCUED, "/rest/v1/apostas/100/resgatar-premio").second.contains("MI053"))
        assertTrue(call(MockBackendScenario.PRIZE_AVAILABLE, "/rest/v1/consultar").second.contains("PREMIADA"))
        val expiredPix = pixExpiration(call(MockBackendScenario.PIX_EXPIRED, "/rest/v1/logado/carrinhos/gerar-cobranca-pix").second)
        val pendingPix = pixExpiration(call(MockBackendScenario.PIX_PENDING, "/rest/v1/logado/carrinhos/gerar-cobranca-pix").second)
        assertTrue(expiredPix.isBefore(Instant.now()))
        assertTrue(pendingPix.isAfter(Instant.now().plusSeconds(35 * 60)))
        assertTrue(call(MockBackendScenario.DAM_BLOCKED, "/rest/v1/parametros/valida-represa").second.contains("temporariamente indisponíveis"))
        assertTrue(call(MockBackendScenario.PAYMENT_NO_SAVED_CARDS, "/rest/v1/meios-pagamento").second.contains("Mercado Pago"))
        assertTrue(call(MockBackendScenario.PAYMENT_NO_SAVED_CARDS, "/rest/v1/usuarios/cartoes").second.contains("[]"))
        assertTrue(call(MockBackendScenario.HAPPY_PATH, "/rest/v1/lotericas/nome-loterico").second.contains("Lotérica Centro"))
        assertTrue(call(MockBackendScenario.HAPPY_PATH, "/rest/v1/boloes/consultar-loterica-favorita").second.contains("101"))
        assertTrue(call(MockBackendScenario.HAPPY_PATH, "/rest/v1/apostas/100/detalhes-premio").second.contains("meiosResgate"))
        assertTrue(call(MockBackendScenario.HAPPY_PATH, "/rest/v1/apostas/100/app/gerar-codigo-resgate-loterica").second.contains("qrCode"))
        assertTrue(call(MockBackendScenario.HAPPY_PATH, "/rest/v1/apostas/100/consulta-dados-chave").second.contains("cpfMascarado"))
        assertFalse(call(MockBackendScenario.APP_UPDATE_REQUIRED, "/rest/v1/app/valida-versao/1").second.contains("true"))
        assertEquals(503, call(MockBackendScenario.APP_UNAVAILABLE, "/rest/v1/app/valida-versao/1").first)
    }

    @Test
    fun `pool inclusion mock returns the backend reservation only for pool body`() {
        val pool = MockBackendFixtures.resolve(
            "/rest/v1/logado/carrinhos/incluir-aposta",
            MockBackendScenario.HAPPY_PATH,
            """{"indicadorCotaBolao":true,"idBolao":"B1"}""",
        )
        val regular = MockBackendFixtures.resolve(
            "/rest/v1/logado/carrinhos/incluir-aposta",
            MockBackendScenario.HAPPY_PATH,
            """{"indicadorCotaBolao":false}""",
        )

        assertTrue(pool.body.contains("codigoBolaoReserva"))
        assertTrue(pool.body.contains("dataHoraExpiracaoReserva"))
        assertFalse(regular.body.contains("codigoBolaoReserva"))
    }

    private fun call(
        scenario: MockBackendScenario,
        path: String,
        slowDelayMillis: Long = 1,
    ): Pair<Int, String> {
        val client = OkHttpClient.Builder().addInterceptor(
            MockBackendInterceptor(MockBackendOptions(true, scenario, slowDelayMillis)),
        ).build()
        return client.newCall(Request.Builder().url("https://mock.caixa.gov.br$path").build()).execute().use {
            it.code to it.body.string()
        }
    }

    private fun pixExpiration(body: String): Instant = OffsetDateTime.parse(
        Regex("\\\"dataHoraExpiracao\\\":\\\"([^\\\"]+)").find(body)!!.groupValues[1],
    ).toInstant()

}
