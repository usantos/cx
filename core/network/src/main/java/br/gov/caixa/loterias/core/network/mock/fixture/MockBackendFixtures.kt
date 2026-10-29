package br.gov.caixa.loterias.core.network.mock.fixture

import java.io.IOException
import java.net.SocketTimeoutException
import br.gov.caixa.loterias.core.network.mock.response.MockResponseErrors.errorResponse
import br.gov.caixa.loterias.core.network.mock.response.MockResponseErrors.error
import br.gov.caixa.loterias.core.network.mock.response.MockResponsePayloads.emptyPayload
import br.gov.caixa.loterias.core.network.mock.response.MockResponsePayloads.successPayload
import br.gov.caixa.loterias.core.network.mock.fixture.MockConfigurationFixtures.appParameters
import br.gov.caixa.loterias.core.network.mock.fixture.MockMarketplaceFixtures.marketplaceParityPayload
import br.gov.caixa.loterias.core.network.mock.fixture.MockCartFixtures.cart
import br.gov.caixa.loterias.core.network.mock.fixture.MockPaymentFixtures.pix
import br.gov.caixa.loterias.core.network.mock.fixture.MockPaymentFixtures.isPurchaseOperation
import br.gov.caixa.loterias.core.network.mock.fixture.MockPaymentFixtures.asyncPurchase
import br.gov.caixa.loterias.core.network.mock.fixture.MockTicketsFixtures.teimosinhaBet
import br.gov.caixa.loterias.core.network.mock.fixture.MockTicketsFixtures.prizeBet
import br.gov.caixa.loterias.core.network.mock.response.MockedHttpResponse
import br.gov.caixa.loterias.core.network.mock.MockBackendScenario

internal object MockBackendFixtures {
    fun resolve(
        encodedPath: String,
        scenario: MockBackendScenario,
        requestBody: String = "",
        purchaseId: Long = 99,
        contests: List<Int> = listOf(2800),
        marketplaceArea: Int? = null,
    ): MockedHttpResponse {
        val path = encodedPath.substringAfter("/rest/v1/", encodedPath.trimStart('/'))
        if (path.matches(Regex("historico-notificacoes/[0-9]+/visualiza"))) {
            when (scenario) {
                MockBackendScenario.NOTIFICATION_READ_LOGIN_REQUIRED -> return MockedHttpResponse(401, "Unauthorized", "{}")
                MockBackendScenario.NOTIFICATION_READ_TERMS_REQUIRED -> return MockedHttpResponse(303, "See Other", "{\"redirect\":\"aceitar-termo-de-uso\"}")
                MockBackendScenario.NOTIFICATION_READ_CART_REDIRECT -> return MockedHttpResponse(400, "Bad Request", "{\"redirect\":\"carrinho\"}")
                MockBackendScenario.NOTIFICATION_READ_BUSINESS_RULE -> return MockedHttpResponse(400, "Bad Request", "{\"codigo\":\"mock-notification\",\"mensagem\":\"Não foi possível confirmar a leitura.\"}")
                MockBackendScenario.NOTIFICATION_READ_SERVER_MESSAGE -> return MockedHttpResponse(500, "Server Error", "{\"codigo\":\"mock-notification\",\"tipo\":1,\"mensagem\":\"Leitura temporariamente indisponível.\"}")
                MockBackendScenario.NOTIFICATION_READ_TIMEOUT -> throw SocketTimeoutException("Timeout de leitura simulado.")
                MockBackendScenario.NOTIFICATION_READ_CONNECTION_FAILURE -> throw IOException("Falha de conexão de leitura simulada.")
                else -> Unit
            }
        }
        if (path == "usuarios/verificar-apostador-bloqueado") {
            when (scenario) {
                MockBackendScenario.POST_LOGIN_BLOCKED_CHECK_HTTP_ERROR ->
                    return MockedHttpResponse(403, "Forbidden", "{\"codigo\":\"mock-blocked-check\"}")
                MockBackendScenario.POST_LOGIN_BLOCKED_CHECK_REDIRECT ->
                    return MockedHttpResponse(200, "OK", "{\"redirect\":\"login\"}")
                else -> Unit
            }
        }
        errorResponse(path, scenario)?.let { return it }
        if (path.startsWith("apostas/") && path.endsWith("/resgatar-premio")) {
            if (scenario == MockBackendScenario.PRIZE_RESPONSE_TIMEOUT) throw SocketTimeoutException("Timeout de resgate simulado.")
            if (scenario == MockBackendScenario.PRIZE_CONNECTION_FAILURE) throw java.net.UnknownHostException("Falha de conexão de resgate simulada.")
        }
        if (path == "usuarios/gerar-token-cadastro-mercado-pago") {
            if (scenario == MockBackendScenario.MP_TOKEN_RESPONSE_TIMEOUT) throw SocketTimeoutException("Timeout de cadastro simulado.")
            if (scenario == MockBackendScenario.MP_TOKEN_CONNECTION_FAILURE) throw IOException("Falha de conexão de cadastro simulada.")
        }
        val tokenConnectionScenario = scenario in setOf(MockBackendScenario.MP_TOKEN_RESPONSE_TIMEOUT, MockBackendScenario.MP_TOKEN_CONNECTION_FAILURE)
        val tokenFailure = when (scenario) {
            MockBackendScenario.MP_TOKEN_LOGIN_REQUIRED -> MockedHttpResponse(401, "Unauthorized", "{}")
            MockBackendScenario.MP_TOKEN_LOGIN_REDIRECT -> MockedHttpResponse(303, "See Other", "{\"redirect\":\"login\"}")
            MockBackendScenario.MP_TOKEN_TERMS_REQUIRED -> MockedHttpResponse(303, "See Other", "{\"redirect\":\"aceitar-termo-de-uso\"}")
            MockBackendScenario.MP_TOKEN_REPEAT_REGISTRATION -> MockedHttpResponse(400, "Bad Request", "{\"codigo\":\"018003\",\"mensagem\":\"Confirme novamente o cadastro (simulação).\"}")
            MockBackendScenario.MP_TOKEN_BUSINESS_RULE -> MockedHttpResponse(400, "Bad Request", "{\"codigo\":\"MOCK_TOKEN_RULE\",\"mensagem\":\"Regra de cadastro simulada.\"}")
            MockBackendScenario.MP_TOKEN_UNCHANGED -> MockedHttpResponse(303, "See Other", "{\"redirect\":\"home\"}")
            else -> null
        }
        if (tokenFailure != null && path == "usuarios/gerar-token-cadastro-mercado-pago") return tokenFailure
        if ((tokenFailure != null || tokenConnectionScenario) && (path.isPurchaseOperation() ||
            (path.startsWith("apostas/") && path.endsWith("/resgatar-premio") &&
                runCatching { com.google.gson.JsonParser.parseString(requestBody).asJsonObject["meioPagamento"].asInt == 1 }.getOrDefault(false)))) {
            return MockedHttpResponse(400, "Bad Request", "{\"codigo\":\"018003\",\"mensagem\":\"Cadastro Mercado Pago necessário (simulação). Deseja iniciar o cadastro?\"}")
        }
        if (path.startsWith("apostas/") && path.endsWith("/resgatar-premio")) {
            val prizeFailure = when (scenario) {
                MockBackendScenario.PRIZE_LOGIN_REQUIRED -> MockedHttpResponse(401, "Unauthorized", "{}")
                MockBackendScenario.PRIZE_PROCESSING_REDIRECT -> MockedHttpResponse(303, "See Other", "{\"redirect\":\"/carrinhos/verifica-compra-processamento\"}")
                MockBackendScenario.PRIZE_LOGIN_REDIRECT -> MockedHttpResponse(303, "See Other", "{\"redirect\":\"login\"}")
                MockBackendScenario.PRIZE_TERMS_REQUIRED -> MockedHttpResponse(303, "See Other", "{\"redirect\":\"aceitar-termo-de-uso\"}")
                MockBackendScenario.PRIZE_ACCOUNT_REGISTRATION_REQUIRED -> MockedHttpResponse(303, "See Other", "{\"redirect\":\"cadastrar-apostador\"}")
                MockBackendScenario.PRIZE_UNAVAILABLE -> MockedHttpResponse(303, "See Other", "{\"redirect\":\"indisponibilidade\"}")
                MockBackendScenario.PRIZE_CART_REDIRECT -> MockedHttpResponse(400, "Bad Request", "{\"redirect\":\"carrinho\"}")
                MockBackendScenario.PRIZE_FAVORITE_CARTS_REDIRECT -> MockedHttpResponse(404, "Not Found", "{\"redirect\":\"carrinhos-favoritos\"}")
                MockBackendScenario.PRIZE_MORE_BETS_REQUIRED -> MockedHttpResponse(400, "Bad Request", "{\"codigo\":\"011010\",\"mensagem\":\"Revise suas apostas (simulação).\"}")
                MockBackendScenario.PRIZE_BUSINESS_RULE -> MockedHttpResponse(400, "Bad Request", "{\"codigo\":\"MOCK_RULE\",\"mensagem\":\"Regra de resgate simulada.\"}")
                MockBackendScenario.PRIZE_SERVER_MESSAGE -> MockedHttpResponse(500, "Server Error", "{\"codigo\":\"MOCK_SERVER\",\"tipo\":1,\"mensagem\":\"Mensagem negocial de resgate simulada.\"}")
                MockBackendScenario.PRIZE_SERVER_FAILURE -> MockedHttpResponse(500, "Server Error", "{\"codigo\":\"MOCK_INTERNAL\",\"tipo\":2,\"mensagem\":\"Detalhe técnico oculto.\"}")
                MockBackendScenario.PRIZE_INVALID_IDENTIFICATION -> MockedHttpResponse(500, "Server Error", "{\"codigo\":\"2067\",\"tipo\":1}")
                else -> null
            }
            if (prizeFailure != null) return prizeFailure
        }
        if (scenario in setOf(MockBackendScenario.PURCHASE_MP_REGISTRATION_TOKEN_FAILURE, MockBackendScenario.PRIZE_MP_REGISTRATION_TOKEN_FAILURE) &&
            path == "usuarios/gerar-token-cadastro-mercado-pago") {
            return error(500, "Falha simulada ao obter token de cadastro.")
        }
        if (scenario in setOf(MockBackendScenario.PRIZE_MP_REGISTRATION_REQUIRED, MockBackendScenario.PRIZE_MP_REGISTRATION_TOKEN_FAILURE) &&
            path.startsWith("apostas/") && path.endsWith("/resgatar-premio") &&
            runCatching { com.google.gson.JsonParser.parseString(requestBody).asJsonObject["meioPagamento"].asInt == 1 }.getOrDefault(false)) {
            return MockedHttpResponse(400, "Bad Request", "{\"codigo\":\"018003\",\"mensagem\":\"Cadastro Mercado Pago necessário para resgate (simulação). Deseja iniciar o cadastro?\"}")
        }
        if (path.isPurchaseOperation()) {
            when (scenario) {
                MockBackendScenario.PURCHASE_HTTP_UNAUTHORIZED ->
                    return MockedHttpResponse(401, "Unauthorized", "{\"codigo\":\"MOCK_SESSION_EXPIRED\"}")
                MockBackendScenario.PURCHASE_CART_REDIRECT ->
                    return MockedHttpResponse(400, "Bad Request", "{\"redirect\":\"carrinho\"}")
                MockBackendScenario.PURCHASE_FAVORITE_CARTS_REDIRECT ->
                    return MockedHttpResponse(404, "Not Found", "{\"redirect\":\"carrinhos-favoritos\"}")
                MockBackendScenario.PURCHASE_MORE_BETS_REQUIRED ->
                    return MockedHttpResponse(400, "Bad Request", "{\"codigo\":\"011010\",\"mensagem\":\"Mensagem negocial simulada: revise suas apostas.\"}")
                MockBackendScenario.PURCHASE_BUSINESS_RULE ->
                    return MockedHttpResponse(404, "Not Found", "{\"codigo\":\"MOCK_RULE\",\"mensagem\":\"Mensagem negocial simulada.\"}")
                MockBackendScenario.PURCHASE_SERVER_MESSAGE ->
                    return MockedHttpResponse(500, "Server Error", "{\"codigo\":\"MOCK_SERVER\",\"tipo\":1,\"mensagem\":\"Mensagem de servidor simulada.\"}")
                MockBackendScenario.PURCHASE_SERVER_FAILURE ->
                    return MockedHttpResponse(500, "Server Error", "{\"codigo\":\"MOCK_INTERNAL\",\"tipo\":2,\"mensagem\":\"Detalhe técnico que não deve aparecer no alerta.\"}")
                MockBackendScenario.PURCHASE_INVALID_IDENTIFICATION ->
                    return MockedHttpResponse(500, "Server Error", "{\"codigo\":\"2067\",\"tipo\":1}")
                MockBackendScenario.PURCHASE_MP_REGISTRATION_REQUIRED,
                MockBackendScenario.PURCHASE_MP_REGISTRATION_TOKEN_FAILURE,
                ->
                    return MockedHttpResponse(
                        400,
                        "Bad Request",
                        "{\"codigo\":\"018003\",\"mensagem\":\"Cadastro Mercado Pago necessário (simulação). Deseja iniciar o cadastro?\"}"
                    )

                else -> Unit
            }
            val redirect = when (scenario) {
                MockBackendScenario.PURCHASE_LOGIN_REQUIRED -> "login"
                MockBackendScenario.PURCHASE_TERMS_REQUIRED -> "aceitar-termo-de-uso"
                MockBackendScenario.PURCHASE_REGISTRATION_REQUIRED -> "cadastrar-apostador"
                MockBackendScenario.PURCHASE_UNAVAILABLE -> "indisponibilidade"
                else -> null
            }
            if (redirect != null) {
                return MockedHttpResponse(303, "See Other", "{\"payload\":null,\"redirect\":\"$redirect\"}")
            }
        }
        if (
            scenario == MockBackendScenario.APP_UNAVAILABLE &&
            (path.startsWith("app/valida-versao/") || path == "parametros/configuraveis-app")
        ) {
            return error(503, "Aplicativo temporariamente indisponível")
        }
        val payload = when (scenario) {
            MockBackendScenario.MARKETPLACE_PARITY, MockBackendScenario.MARKETPLACE_PARITY_OCTOBER ->
                when (path) {
                    "parametros/configuraveis-app" -> appParameters(scenario)

                    "publico/parametros-simulacao" -> marketplaceParityPayload("parameters")

                    "boloes/recuperar-boloes-disponiveis" -> if (marketplaceArea == 1 || marketplaceArea == 2) {
                        """{"totalRegistros":0,"paginaAtual":1,"ultimaPagina":0,"cotas":[]}"""
                    } else {
                        marketplaceParityPayload("pools")
                    }

                    "usuarios/recuperar-dados" -> marketplaceParityPayload("profile")

                    "boloes/consultar-loterica-favorita" -> marketplaceParityPayload("retailers")

                    else -> successPayload(path, requestBody, purchaseId, contests)
                }

            MockBackendScenario.CAMPAIGN_OCTOBER_ROSE, MockBackendScenario.CAMPAIGN_SEPTEMBER_BLUE ->
                if (path == "parametros/configuraveis-app") {
                    appParameters(scenario)
                } else {
                    successPayload(path, requestBody, purchaseId, contests)
                }

            MockBackendScenario.EMPTY -> emptyPayload(path)

            MockBackendScenario.APP_UPDATE_REQUIRED -> if (path.startsWith(
                    "app/valida-versao/"
                )
            ) {
                "false"
            } else {
                successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.APP_UNAVAILABLE -> successPayload(path, requestBody, purchaseId, contests)

            MockBackendScenario.REGISTRATION_REQUIRED -> if (path == "logado/usuarios/verificar-cadastro") {
                "null,\"redirect\":\"cadastrar-apostador\""
            } else {
                successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.TERMS_REQUIRED -> if (path == "logado/usuarios/verificar-cadastro") {
                "null,\"redirect\":\"aceitar-termo-de-uso\""
            } else {
                successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.SELF_ASSESSMENT_REQUIRED -> if (path == "logado/usuarios/verificar-cadastro") {
                "{\"responderAutoavaliacao\":true}"
            } else {
                successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.ACCOUNT_SUSPENDED -> if (path == "logado/usuarios/verificar-cadastro") {
                "{\"suspensaoTemporariaApostador\":true,\"limiteDiario\":0}"
            } else {
                successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.ACCOUNT_PARTIALLY_BLOCKED -> if (path == "logado/usuarios/verificar-cadastro") {
                "{\"situacaoApostador\":{\"enum\":\"BLOQUEADO_PARCIAL\"},\"limiteDiario\":1000}"
            } else {
                successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.POST_LOGIN_PAYMENT_NOT_IDENTIFIED -> if (path == "logado/usuarios/verificar-cadastro") {
                """{"nome":"Pessoa Mock","limiteDiario":100,"carrinho":${cart()},"pagamentoNaoIdentificado":true,
                    "ultimaNotificacao":{"id":91,"titulo":"Notificação mock","conteudo":"O aviso de pagamento tem prioridade."}}"""
            } else {
                successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.POST_LOGIN_NOTIFICATION,
            MockBackendScenario.NOTIFICATION_READ_LOGIN_REQUIRED,
            MockBackendScenario.NOTIFICATION_READ_TERMS_REQUIRED,
            MockBackendScenario.NOTIFICATION_READ_CART_REDIRECT,
            MockBackendScenario.NOTIFICATION_READ_BUSINESS_RULE,
            MockBackendScenario.NOTIFICATION_READ_SERVER_MESSAGE,
            MockBackendScenario.NOTIFICATION_READ_TIMEOUT,
            MockBackendScenario.NOTIFICATION_READ_CONNECTION_FAILURE,
            -> if (path == "logado/usuarios/verificar-cadastro") {
                """{"nome":"Pessoa Mock","limiteDiario":100,"carrinho":${cart()},"pagamentoNaoIdentificado":false,
                    "ultimaNotificacao":{"id":91,"titulo":"Notificação mock","conteudo":"Mensagem recebida após o login.","link":"https://www.loteriasonline.caixa.gov.br/"}}"""
            } else {
                successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.CART_EXPIRED_POOL -> if (path ==
                "logado/carrinhos"
            ) {
                cart(expiredPool = true)
            } else {
                successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.PURCHASE_PROCESSING -> if (path.isPurchaseOperation()) {
                asyncPurchase(
                    7,
                    purchaseId
                )
            } else {
                successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.PURCHASE_APPROVED -> if (path.isPurchaseOperation()) {
                asyncPurchase(
                    8,
                    purchaseId
                )
            } else {
                successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.PURCHASE_REJECTED -> if (path.isPurchaseOperation()) {
                asyncPurchase(
                    3,
                    purchaseId
                )
            } else {
                successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.PAYMENT_NO_SAVED_CARDS -> if (path ==
                "usuarios/cartoes"
            ) {
                "[]"
            } else {
                successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.PIX_PENDING -> when {
                path.endsWith("gerar-cobranca-pix") -> pix(expired = false)
                path.contains("verifica-compra-processamento") -> asyncPurchase(10, purchaseId)
                else -> successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.PIX_EXPIRED -> when {
                path.endsWith("gerar-cobranca-pix") -> pix(expired = true)
                path.contains("verifica-compra-processamento") -> asyncPurchase(12, purchaseId)
                else -> successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.DAM_BLOCKED -> if (path == "parametros/valida-represa") {
                "{\"payload\":true,\"mensagem\":\"As apostas estão temporariamente indisponíveis neste horário.\"}"
            } else {
                successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.TEIMOSINHA_RESCUED -> when {
                path == "apostas" || path == "consultar" -> teimosinhaBet()
                path.endsWith("/resgatar-premio") -> "\"MI053\""
                else -> successPayload(path, requestBody, purchaseId, contests)
            }

            MockBackendScenario.PRIZE_AVAILABLE -> if (path == "apostas" ||
                path == "consultar"
            ) {
                prizeBet()
            } else {
                successPayload(path, requestBody, purchaseId, contests)
            }

            else -> successPayload(path, requestBody, purchaseId, contests)
        }
        val body = if (path.startsWith("v1/") || path == "cards" || path.startsWith("cards/")) {
            payload
        } else {
            "{\"payload\":$payload}"
        }
        if (path.isPurchaseOperation() && scenario == MockBackendScenario.PURCHASE_REJECTED) {
            return MockedHttpResponse(
                303,
                "See Other",
                "{\"codigo\":\"MOCK_PURCHASE_REJECTED\",\"mensagem\":\"Pagamento não autorizado.\",\"payload\":$payload}"
            )
        }
        if (path.isPurchaseOperation() && scenario in setOf(MockBackendScenario.PURCHASE_PROCESSING, MockBackendScenario.PIX_PENDING)) {
            return MockedHttpResponse(303, "See Other", body)
        }
        return MockedHttpResponse(200, "OK", body)
    }
}
