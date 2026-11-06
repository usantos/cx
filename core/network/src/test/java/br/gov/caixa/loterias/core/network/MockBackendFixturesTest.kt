package br.gov.caixa.loterias.core.network

import com.google.gson.JsonParser
import java.io.IOException
import java.net.SocketTimeoutException
import org.junit.Assert.*
import org.junit.Test
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.api.BetsHistoryApi
import br.gov.caixa.loterias.core.network.api.MercadoPagoApi
import br.gov.caixa.loterias.core.network.api.RecargaPayApi
import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.core.network.mock.MockBackendScenario
import br.gov.caixa.loterias.core.network.mock.fixture.MockBackendFixtures

/** Contratos das fixtures são independentes do teste de delegação do interceptor com sockets. */
class MockBackendFixturesTest {
    @Test fun `all migrated operations retain JSON or the explicitly declared transport failure`() {
        val operations = listOf(LoteriasApi::class.java, BackendApi::class.java, BetsHistoryApi::class.java,
            MercadoPagoApi::class.java, RecargaPayApi::class.java)
            .flatMap { type -> type.declaredMethods.mapNotNull(::operation) }.toSet()
        assertEquals(106, operations.size)
        MockBackendScenario.entries
            .filterNot { it == MockBackendScenario.NETWORK_ERROR || it == MockBackendScenario.TIMEOUT }
            .forEach { scenario ->
                operations.forEach { (_, template) ->
                    val path = template.replace(Regex("\\{[^}]+}"), "1")
                    val prize = path.startsWith("apostas/") && path.endsWith("/resgatar-premio")
                    val token = path == "usuarios/gerar-token-cadastro-mercado-pago"
                    val notification = path.matches(Regex("historico-notificacoes/[0-9]+/visualiza"))
                    when {
                        (prize && scenario == MockBackendScenario.PRIZE_RESPONSE_TIMEOUT) ||
                            (token && scenario == MockBackendScenario.MP_TOKEN_RESPONSE_TIMEOUT) ||
                            (notification && scenario == MockBackendScenario.NOTIFICATION_READ_TIMEOUT) ->
                            assertThrows(SocketTimeoutException::class.java) { MockBackendFixtures.resolve(path, scenario) }
                        (prize && scenario == MockBackendScenario.PRIZE_CONNECTION_FAILURE) ||
                            (token && scenario == MockBackendScenario.MP_TOKEN_CONNECTION_FAILURE) ||
                            (notification && scenario == MockBackendScenario.NOTIFICATION_READ_CONNECTION_FAILURE) ->
                            assertThrows(IOException::class.java) { MockBackendFixtures.resolve(path, scenario) }
                        else -> {
                            val response = MockBackendFixtures.resolve(path, scenario)
                            assertTrue("$scenario $path", response.code in 200..599)
                            JsonParser.parseString(response.body)
                        }
                    }
                }
            }
    }

    @Test fun `campaign fixtures expose legacy and optional contracts through configuration endpoint`() {
        val october = JsonParser.parseString(MockBackendFixtures.resolve(
            "parametros/configuraveis-app", MockBackendScenario.CAMPAIGN_OCTOBER_ROSE).body)
            .asJsonObject.getAsJsonObject("payload").getAsJsonObject("mapa")
        assertTrue(october.get("vigenciaMesOutubroRosa").asBoolean)
        assertTrue(october.get("vigenciaOutubroRosaMegaSena").asBoolean)
        val september = JsonParser.parseString(MockBackendFixtures.resolve(
            "parametros/configuraveis-app", MockBackendScenario.CAMPAIGN_SEPTEMBER_BLUE).body)
            .asJsonObject.getAsJsonObject("payload").getAsJsonObject("mapa")
            .getAsJsonArray("campanhasComemorativas")[0].asJsonObject
        assertEquals("SETEMBRO_AZUL", september.get("codigo").asString)
        assertTrue(september.get("ativa").asBoolean)
    }

    private fun operation(method: java.lang.reflect.Method): Pair<String, String>? {
        method.getAnnotation(GET::class.java)?.let { return "GET" to it.value }
        method.getAnnotation(POST::class.java)?.let { return "POST" to it.value }
        method.getAnnotation(PUT::class.java)?.let { return "PUT" to it.value }
        method.getAnnotation(DELETE::class.java)?.let { return "DELETE" to it.value }
        method.getAnnotation(PATCH::class.java)?.let { return "PATCH" to it.value }
        return null
    }
}
