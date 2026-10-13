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

/** Fixture contracts are independent of the socket-backed interceptor delegation test. */
class MockBackendFixturesTest {
    @Test fun `all migrated operations retain JSON or the explicitly declared transport failure`() {
        val operations = listOf(LoteriasApi::class.java, LegacyBackendApi::class.java, BetsHistoryApi::class.java,
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

    private fun operation(method: java.lang.reflect.Method): Pair<String, String>? {
        method.getAnnotation(GET::class.java)?.let { return "GET" to it.value }
        method.getAnnotation(POST::class.java)?.let { return "POST" to it.value }
        method.getAnnotation(PUT::class.java)?.let { return "PUT" to it.value }
        method.getAnnotation(DELETE::class.java)?.let { return "DELETE" to it.value }
        method.getAnnotation(PATCH::class.java)?.let { return "PATCH" to it.value }
        return null
    }
}
