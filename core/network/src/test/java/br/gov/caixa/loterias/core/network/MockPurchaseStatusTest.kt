package br.gov.caixa.loterias.core.network

import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Test

/** Entirely intercepted responses: no HTTP server, socket or actual payment. */
class MockPurchaseStatusTest {
    @Test fun `registration fixtures keep profile available and token envelope never exposes secrets in text`() {
        val gson = com.google.gson.Gson()
        assertNull(MercadoPagoRegistrationEnvelopeDto().payload)
        for (scenario in listOf(
            MockBackendScenario.PURCHASE_MP_REGISTRATION_REQUIRED,
            MockBackendScenario.PURCHASE_MP_REGISTRATION_TOKEN_FAILURE,
        )) {
            assertEquals(200, MockBackendFixtures.resolve("/rest/v1/usuarios", scenario).code)
            val response = MockBackendFixtures.resolve("/rest/v1/usuarios/gerar-token-cadastro-mercado-pago", scenario)
            if (scenario == MockBackendScenario.PURCHASE_MP_REGISTRATION_REQUIRED) {
                assertEquals(200, response.code)
                val envelope = gson.fromJson(response.body, MercadoPagoRegistrationEnvelopeDto::class.java)
                assertEquals("mock-registration-token", envelope.payload)
                assertNull(envelope.code)
                assertNull(envelope.redirect)
                assertFalse(envelope.toString().contains("mock-registration-token"))
            } else {
                assertEquals(500, response.code)
            }
        }
        val failure = gson.fromJson("""{"payload":"secret","codigo":"018003","redirect":"login"}""", MercadoPagoRegistrationEnvelopeDto::class.java)
        assertEquals("018003", failure.code)
        assertEquals("login", failure.redirect)
        assertFalse(failure.toString().contains("secret"))
    }

    @Test fun `notice fixtures are HTTP failures confined to purchase operations`() {
        for ((scenario, status, code) in listOf(
            Triple(MockBackendScenario.PURCHASE_MORE_BETS_REQUIRED, 400, "011010"),
            Triple(MockBackendScenario.PURCHASE_BUSINESS_RULE, 404, "MOCK_RULE"),
            Triple(MockBackendScenario.PURCHASE_SERVER_MESSAGE, 500, "MOCK_SERVER"),
            Triple(MockBackendScenario.PURCHASE_SERVER_FAILURE, 500, "MOCK_INTERNAL"),
            Triple(MockBackendScenario.PURCHASE_INVALID_IDENTIFICATION, 500, "2067"),
            Triple(MockBackendScenario.PURCHASE_MP_REGISTRATION_REQUIRED, 400, "018003"),
            Triple(MockBackendScenario.PURCHASE_MP_REGISTRATION_TOKEN_FAILURE, 400, "018003"),
        )) {
            for (path in listOf("registrar-apostas-async", "verifica-compra-processamento")) {
                val response = MockBackendFixtures.resolve("/rest/v1/logado/carrinhos/$path", scenario)
                assertEquals(status, response.code)
                assertEquals(code, JsonParser.parseString(response.body).asJsonObject["codigo"].asString)
            }
            assertTrue(payload(scenario, "parametros/configuraveis-app").has("mapa"))
            assertEquals(200, MockBackendFixtures.resolve("/rest/v1/logado/carrinhos", scenario).code)
        }
    }
    @Test fun `HTTP error fixtures preserve status and allow cart and favorite cart destinations to load`() {
        for ((scenario, expectedStatus, expectedRedirect) in listOf(
            Triple(MockBackendScenario.PURCHASE_HTTP_UNAUTHORIZED, 401, null),
            Triple(MockBackendScenario.PURCHASE_CART_REDIRECT, 400, "carrinho"),
            Triple(MockBackendScenario.PURCHASE_FAVORITE_CARTS_REDIRECT, 404, "carrinhos-favoritos"),
        )) {
            for (path in listOf("registrar-apostas-async", "verifica-compra-processamento")) {
                val response = MockBackendFixtures.resolve("/rest/v1/logado/carrinhos/$path", scenario)
                assertEquals(expectedStatus, response.code)
                val envelope = JsonParser.parseString(response.body).asJsonObject
                assertEquals(expectedRedirect, envelope["redirect"]?.asString)
            }
            for (path in listOf("/rest/v1/logado/carrinhos", "/rest/v1/logado/carrinhos-favoritos", "/rest/v1/parametros/configuraveis-app")) {
                assertEquals(200, MockBackendFixtures.resolve(path, scenario).code)
            }
        }
    }
    @Test fun `purchase redirect fixtures affect only purchase operations and preserve preparation`() {
        for ((scenario, destination) in mapOf(
            MockBackendScenario.PURCHASE_LOGIN_REQUIRED to "login",
            MockBackendScenario.PURCHASE_TERMS_REQUIRED to "aceitar-termo-de-uso",
            MockBackendScenario.PURCHASE_REGISTRATION_REQUIRED to "cadastrar-apostador",
            MockBackendScenario.PURCHASE_UNAVAILABLE to "indisponibilidade",
        )) {
            for (path in listOf("registrar-apostas-async", "verifica-compra-processamento")) {
                val response = MockBackendFixtures.resolve("/rest/v1/logado/carrinhos/$path", scenario)
                assertEquals(303, response.code)
                val envelope = JsonParser.parseString(response.body).asJsonObject
                assertEquals(destination, envelope["redirect"].asString)
                assertTrue(envelope["payload"].isJsonNull)
            }
            assertTrue(payload(scenario, "parametros/configuraveis-app").has("mapa"))
            assertTrue(payload(scenario, "logado/carrinhos/gerar-cobranca-pix", post = true).has("pixCopiaECola"))
        }
    }
    @Test fun `submission and polling use actual backend codes for each purchase scenario`() {
        for ((scenario, status) in mapOf(
            MockBackendScenario.HAPPY_PATH to 8,
            MockBackendScenario.PURCHASE_PROCESSING to 7,
            MockBackendScenario.PURCHASE_APPROVED to 8,
            MockBackendScenario.PURCHASE_REJECTED to 3,
        )) {
            assertEquals(status, payload(scenario, "logado/carrinhos/registrar-apostas-async", post = true)["idSituacaoCompra"].asInt)
            assertEquals(status, payload(scenario, "logado/carrinhos/verifica-compra-processamento")["idSituacaoCompra"].asInt)
        }
    }

    @Test fun `pending and expired Pix never fall through to happy payment approval`() {
        for ((scenario, status) in mapOf(MockBackendScenario.PIX_PENDING to 10, MockBackendScenario.PIX_EXPIRED to 12)) {
            assertEquals(status, payload(scenario, "logado/carrinhos/verifica-compra-processamento")["idSituacaoCompra"].asInt)
            assertTrue(payload(scenario, "logado/carrinhos/gerar-cobranca-pix", post = true)["pixCopiaECola"].asString.isNotBlank())
        }
    }

    @Test fun `purchase scenarios retain unrelated endpoint payloads`() {
        for (scenario in listOf(MockBackendScenario.PURCHASE_PROCESSING, MockBackendScenario.PURCHASE_APPROVED, MockBackendScenario.PURCHASE_REJECTED, MockBackendScenario.PIX_PENDING, MockBackendScenario.PIX_EXPIRED)) {
            assertTrue(payload(scenario, "parametros/configuraveis-app").has("mapa"))
        }
    }

    private fun payload(scenario: MockBackendScenario, path: String, post: Boolean = false) =
        OkHttpClient.Builder().addInterceptor(MockBackendInterceptor(MockBackendOptions(true, scenario))).build()
            .newCall(Request.Builder().url("https://mock.caixa.gov.br/rest/v1/$path").apply {
                if (post) post("{}".toRequestBody())
            }.build()).execute().use { response ->
                val processing = scenario in setOf(MockBackendScenario.PURCHASE_PROCESSING, MockBackendScenario.PURCHASE_REJECTED, MockBackendScenario.PIX_PENDING) &&
                    (path.contains("verifica-compra-processamento") || path.endsWith("registrar-apostas-async"))
                assertEquals(if (processing) 303 else 200, response.code)
                JsonParser.parseString(response.body.string()).asJsonObject["payload"].asJsonObject
            }
}
