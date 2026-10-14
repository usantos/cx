package br.gov.caixa.loterias.core.network

import java.io.IOException
import java.net.SocketTimeoutException
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

enum class MockBackendScenario {
    HAPPY_PATH,
    EMPTY,
    SLOW_SUCCESS,
    NETWORK_ERROR,
    TIMEOUT,
    UNAUTHENTICATED,
    FORBIDDEN,
    VALIDATION_ERROR,
    SERVER_ERROR,
    APP_UPDATE_REQUIRED,
    APP_UNAVAILABLE,
    REGISTRATION_REQUIRED,
    TERMS_REQUIRED,
    SELF_ASSESSMENT_REQUIRED,
    ACCOUNT_SUSPENDED,
    ACCOUNT_PARTIALLY_BLOCKED,
    POST_LOGIN_PAYMENT_NOT_IDENTIFIED,
    POST_LOGIN_NOTIFICATION,
    NOTIFICATION_READ_LOGIN_REQUIRED,
    NOTIFICATION_READ_TERMS_REQUIRED,
    NOTIFICATION_READ_CART_REDIRECT,
    NOTIFICATION_READ_BUSINESS_RULE,
    NOTIFICATION_READ_SERVER_MESSAGE,
    NOTIFICATION_READ_TIMEOUT,
    NOTIFICATION_READ_CONNECTION_FAILURE,
    POST_LOGIN_BLOCKED_CHECK_HTTP_ERROR,
    POST_LOGIN_BLOCKED_CHECK_REDIRECT,
    CART_EXPIRED_POOL,
    PURCHASE_PROCESSING,
    PURCHASE_APPROVED,
    PURCHASE_REJECTED,
    PURCHASE_RESPONSE_LOST,
    PURCHASE_LOGIN_REQUIRED,
    PURCHASE_TERMS_REQUIRED,
    PURCHASE_REGISTRATION_REQUIRED,
    PURCHASE_UNAVAILABLE,
    PURCHASE_HTTP_UNAUTHORIZED,
    PURCHASE_CART_REDIRECT,
    PURCHASE_FAVORITE_CARTS_REDIRECT,
    PURCHASE_MORE_BETS_REQUIRED,
    PURCHASE_BUSINESS_RULE,
    PURCHASE_SERVER_MESSAGE,
    PURCHASE_SERVER_FAILURE,
    PURCHASE_INVALID_IDENTIFICATION,
    PURCHASE_MP_REGISTRATION_REQUIRED,
    PURCHASE_MP_REGISTRATION_TOKEN_FAILURE,
    PAYMENT_NO_SAVED_CARDS,
    PIX_PENDING,
    PIX_EXPIRED,
    DAM_BLOCKED,
    TEIMOSINHA_RESCUED,
    PRIZE_AVAILABLE,
    PRIZE_MP_REGISTRATION_REQUIRED,
    PRIZE_MP_REGISTRATION_TOKEN_FAILURE,
    MP_TOKEN_LOGIN_REQUIRED,
    MP_TOKEN_LOGIN_REDIRECT,
    MP_TOKEN_TERMS_REQUIRED,
    MP_TOKEN_REPEAT_REGISTRATION,
    MP_TOKEN_BUSINESS_RULE,
    MP_TOKEN_UNCHANGED,
    MP_TOKEN_RESPONSE_TIMEOUT,
    MP_TOKEN_CONNECTION_FAILURE,
    PRIZE_RESPONSE_TIMEOUT,
    PRIZE_CONNECTION_FAILURE,
    PRIZE_LOGIN_REQUIRED,
    PRIZE_PROCESSING_REDIRECT,
    PRIZE_LOGIN_REDIRECT,
    PRIZE_TERMS_REQUIRED,
    PRIZE_ACCOUNT_REGISTRATION_REQUIRED,
    PRIZE_UNAVAILABLE,
    PRIZE_CART_REDIRECT,
    PRIZE_FAVORITE_CARTS_REDIRECT,
    PRIZE_MORE_BETS_REQUIRED,
    PRIZE_BUSINESS_RULE,
    PRIZE_SERVER_MESSAGE,
    PRIZE_SERVER_FAILURE,
    PRIZE_INVALID_IDENTIFICATION;

    companion object {
        fun from(value: String): MockBackendScenario = entries.firstOrNull {
            it.name.equals(value.trim(), ignoreCase = true)
        } ?: HAPPY_PATH
    }
}

data class MockBackendOptions(
    val enabled: Boolean = false,
    val scenario: MockBackendScenario = MockBackendScenario.HAPPY_PATH,
    val slowDelayMillis: Long = 2_500,
)

/** Deterministic in-process backend. It never opens a socket when enabled. */
class MockBackendInterceptor(
    private val options: MockBackendOptions,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!options.enabled) return chain.proceed(chain.request())
        when (options.scenario) {
            MockBackendScenario.NETWORK_ERROR -> throw IOException("Mock network error")
            MockBackendScenario.TIMEOUT -> throw SocketTimeoutException("Mock timeout")
            MockBackendScenario.SLOW_SUCCESS -> Thread.sleep(options.slowDelayMillis.coerceAtLeast(0))
            else -> Unit
        }

        val request = chain.request()
        if (options.scenario == MockBackendScenario.PURCHASE_RESPONSE_LOST &&
            request.method == "POST" && request.url.encodedPath.endsWith("/logado/carrinhos/registrar-apostas-async")) {
            throw SocketTimeoutException("Mock purchase response lost")
        }
        val requestBody = runCatching {
            Buffer().use { buffer ->
                request.body?.writeTo(buffer)
                buffer.readUtf8()
            }
        }.getOrDefault("")
        val mocked = MockBackendFixtures.resolve(
            request.url.encodedPath, options.scenario, requestBody,
            purchaseId = request.url.queryParameter("idCompraCarrinho")?.toLongOrNull() ?: 99,
            contests = request.url.queryParameterValues("concursos").mapNotNull { it?.toIntOrNull() },
        )
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(mocked.code)
            .message(mocked.message)
            .body(mocked.body.toResponseBody(JSON))
            .build()
    }

    private companion object {
        val JSON = "application/json; charset=utf-8".toMediaType()
    }
}

internal data class MockedHttpResponse(val code: Int, val message: String, val body: String)

/** Business fixtures are centralized so screenshots and tests use the same stable data. */
internal object MockBackendFixtures {
    fun resolve(
        encodedPath: String,
        scenario: MockBackendScenario,
        requestBody: String = "",
        purchaseId: Long = 99,
        contests: List<Int> = listOf(2800),
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
                MockBackendScenario.PURCHASE_MP_REGISTRATION_TOKEN_FAILURE ->
                    return MockedHttpResponse(400, "Bad Request", "{\"codigo\":\"018003\",\"mensagem\":\"Cadastro Mercado Pago necessário (simulação). Deseja iniciar o cadastro?\"}")
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
            MockBackendScenario.EMPTY -> emptyPayload(path)
            MockBackendScenario.APP_UPDATE_REQUIRED -> if (path.startsWith("app/valida-versao/")) "false" else successPayload(path, requestBody, purchaseId, contests)
            MockBackendScenario.APP_UNAVAILABLE -> successPayload(path, requestBody, purchaseId, contests)
            MockBackendScenario.REGISTRATION_REQUIRED -> if (path == "logado/usuarios/verificar-cadastro")
                "null,\"redirect\":\"cadastrar-apostador\"" else successPayload(path, requestBody, purchaseId, contests)
            MockBackendScenario.TERMS_REQUIRED -> if (path == "logado/usuarios/verificar-cadastro")
                "null,\"redirect\":\"aceitar-termo-de-uso\"" else successPayload(path, requestBody, purchaseId, contests)
            MockBackendScenario.SELF_ASSESSMENT_REQUIRED -> if (path == "logado/usuarios/verificar-cadastro")
                "{\"responderAutoavaliacao\":true}" else successPayload(path, requestBody, purchaseId, contests)
            MockBackendScenario.ACCOUNT_SUSPENDED -> if (path == "logado/usuarios/verificar-cadastro")
                "{\"suspensaoTemporariaApostador\":true,\"limiteDiario\":0}" else successPayload(path, requestBody, purchaseId, contests)
            MockBackendScenario.ACCOUNT_PARTIALLY_BLOCKED -> if (path == "logado/usuarios/verificar-cadastro")
                "{\"situacaoApostador\":{\"enum\":\"BLOQUEADO_PARCIAL\"},\"limiteDiario\":1000}" else successPayload(path, requestBody, purchaseId, contests)
            MockBackendScenario.POST_LOGIN_PAYMENT_NOT_IDENTIFIED -> if (path == "logado/usuarios/verificar-cadastro")
                """{"nome":"Pessoa Mock","limiteDiario":100,"carrinho":${cart()},"pagamentoNaoIdentificado":true,
                    "ultimaNotificacao":{"id":91,"titulo":"Notificação mock","conteudo":"O aviso de pagamento tem prioridade."}}"""
                else successPayload(path, requestBody, purchaseId, contests)
            MockBackendScenario.POST_LOGIN_NOTIFICATION,
            MockBackendScenario.NOTIFICATION_READ_LOGIN_REQUIRED,
            MockBackendScenario.NOTIFICATION_READ_TERMS_REQUIRED,
            MockBackendScenario.NOTIFICATION_READ_CART_REDIRECT,
            MockBackendScenario.NOTIFICATION_READ_BUSINESS_RULE,
            MockBackendScenario.NOTIFICATION_READ_SERVER_MESSAGE,
            MockBackendScenario.NOTIFICATION_READ_TIMEOUT,
            MockBackendScenario.NOTIFICATION_READ_CONNECTION_FAILURE -> if (path == "logado/usuarios/verificar-cadastro")
                """{"nome":"Pessoa Mock","limiteDiario":100,"carrinho":${cart()},"pagamentoNaoIdentificado":false,
                    "ultimaNotificacao":{"id":91,"titulo":"Notificação mock","conteudo":"Mensagem recebida após o login.","link":"https://www.loteriasonline.caixa.gov.br/"}}"""
                else successPayload(path, requestBody, purchaseId, contests)
            MockBackendScenario.CART_EXPIRED_POOL -> if (path == "logado/carrinhos") cart(expiredPool = true) else successPayload(path, requestBody, purchaseId, contests)
            MockBackendScenario.PURCHASE_PROCESSING -> if (path.isPurchaseOperation()) asyncPurchase(7, purchaseId) else successPayload(path, requestBody, purchaseId, contests)
            MockBackendScenario.PURCHASE_APPROVED -> if (path.isPurchaseOperation()) asyncPurchase(8, purchaseId) else successPayload(path, requestBody, purchaseId, contests)
            MockBackendScenario.PURCHASE_REJECTED -> if (path.isPurchaseOperation()) asyncPurchase(3, purchaseId) else successPayload(path, requestBody, purchaseId, contests)
            MockBackendScenario.PAYMENT_NO_SAVED_CARDS -> if (path == "usuarios/cartoes") "[]" else successPayload(path, requestBody, purchaseId, contests)
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
            MockBackendScenario.DAM_BLOCKED -> if (path == "parametros/valida-represa")
                "{\"payload\":true,\"mensagem\":\"As apostas estão temporariamente indisponíveis neste horário.\"}" else successPayload(path, requestBody, purchaseId, contests)
            MockBackendScenario.TEIMOSINHA_RESCUED -> when {
                path == "apostas" || path == "consultar" -> teimosinhaBet()
                path.endsWith("/resgatar-premio") -> "\"MI053\""
                else -> successPayload(path, requestBody, purchaseId, contests)
            }
            MockBackendScenario.PRIZE_AVAILABLE -> if (path == "apostas" || path == "consultar") prizeBet() else successPayload(path, requestBody, purchaseId, contests)
            else -> successPayload(path, requestBody, purchaseId, contests)
        }
        val body = if (path.startsWith("v1/") || path == "cards" || path.startsWith("cards/")) {
            payload
        } else {
            "{\"payload\":$payload}"
        }
        if (path.isPurchaseOperation() && scenario == MockBackendScenario.PURCHASE_REJECTED) {
            return MockedHttpResponse(303, "See Other", "{\"codigo\":\"MOCK_PURCHASE_REJECTED\",\"mensagem\":\"Pagamento não autorizado.\",\"payload\":$payload}")
        }
        if (path.isPurchaseOperation() && scenario in setOf(MockBackendScenario.PURCHASE_PROCESSING, MockBackendScenario.PIX_PENDING)) {
            return MockedHttpResponse(303, "See Other", body)
        }
        return MockedHttpResponse(200, "OK", body)
    }

    private fun errorResponse(path: String, scenario: MockBackendScenario): MockedHttpResponse? {
        // Authentication/business HTTP errors must not prevent the mock app from passing startup.
        if (path.startsWith("app/valida-versao/") || path == "parametros/configuraveis-app") return null
        return when (scenario) {
        MockBackendScenario.UNAUTHENTICATED -> error(401, "Não autenticado")
        MockBackendScenario.FORBIDDEN -> error(403, "Acesso negado")
        MockBackendScenario.VALIDATION_ERROR -> error(422, "Dados inválidos")
        MockBackendScenario.SERVER_ERROR -> error(500, "Serviço indisponível")
        else -> null
        }
    }

    private fun error(code: Int, message: String) = MockedHttpResponse(
        code,
        message,
        "{\"codigo\":\"MOCK_$code\",\"mensagem\":\"$message\",\"payload\":null}",
    )

    private fun emptyPayload(path: String): String = when {
        path in listOf(
            "publico/modalidades", "logado/combos", "duvidas/secoes", "duvidas",
            "publico/equipes-esportivas/listaEquipes",
            "municipios", "bairros", "logado/carrinhos-favoritos",
            "logado/apostas-favoritas", "logado/apostas-favoritas/",
            "usuarios/cartoes", "meios-pagamento", "compras", "compras/meses", "compras/situacoes-compra",
        ) -> "[]"
        path == "boloes/recuperar-boloes-disponiveis" -> "{\"totalRegistros\":0,\"paginaAtual\":0,\"ultimaPagina\":0,\"cotas\":[]}"
        path == "autoavaliacoes/recuperar-perguntas" -> "{\"retornoPerguntaResposta\":[]}"
        path == "logado/carrinhos" -> cart(empty = true)
        else -> "null"
    }

    private fun successPayload(path: String, requestBody: String = "", purchaseId: Long = 99, contests: List<Int> = listOf(2800)): String = when {
        path.startsWith("app/valida-versao/") -> "true"
        path == "parametros/configuraveis-app" -> appParameters()
        path == "publico/modalidades" -> modalities()
        path == "publico/parametros-simulacao" -> bettingParameters()
        path == "publico/equipes-esportivas/listaEquipes" -> sportsTeamShields()
        path == "logado/combos" -> combos()
        path == "logado/rapidoes" -> rapidConfiguration()
        path == "logado/rapidoes/gerar-apostas" -> cart()
        path == "logado/apostas-favoritas" || path == "logado/apostas-favoritas/" -> groupedFavoriteBets()
        path == "logado/apostas-favoritas/modalidade" || path == "logado/apostas-favoritas/lista-unica" -> favoriteBetPage()
        path == "logado/carrinhos-favoritos" -> "[{\"id\":21,\"nome\":\"Meu carrinho favorito\"}]"
        path.contains("carrinhos-favoritos/") && path.endsWith("/apostas") -> favoriteCartBets()
        path.endsWith("gerar-cobranca-pix") -> pix(false)
        path.isPurchaseOperation() -> asyncPurchase(8, purchaseId)
        path == "logado/carrinhos/incluir-aposta" && requestBody.contains("\"indicadorCotaBolao\":true") -> poolCart()
        path.startsWith("logado/carrinhos") -> cart()
        path == "duvidas/secoes" || path == "duvidas" -> support()
        path == "termos-de-uso/termo-vigente" -> "{\"conteudo\":\"<h1>Termos de uso</h1><p>Conteúdo mock.</p>\"}"
        path == "boloes/recuperar-boloes-disponiveis" -> marketplace()
        path == "boloes/detalhar-bolao" -> marketplaceDetail()
        path == "lotericas/nome-loterico" -> retailers()
        path == "boloes/consultar-loterica-favorita" -> favoriteRetailers()
        path.endsWith("/detalhes-premio") -> prizeDetails()
        path.endsWith("/app/gerar-codigo-resgate-loterica") -> lotteryRedemptionCode()
        path.endsWith("/consulta-dados-chave") -> pixBeneficiary()
        path.endsWith("/resgatar-premio") || path.endsWith("/cancelar-resgate-pix") -> "{}"
        path == "usuarios/gerar-token-cadastro-mercado-pago" -> "\"mock-registration-token\""
        path == "usuarios/gerar-token-apostador-recarga-pay" -> "\"mock-recarga-customer-token\""
        path == "resultados" -> contests.joinToString(prefix = "[", postfix = "]") { contest ->
            result().replace("\"numero\":2800", "\"numero\":$contest")
        }
        path.startsWith("resultados/") -> result()
        path.endsWith("/conferir") -> """{"valor":100,"descricao":"PREMIADA"}"""
        path.endsWith("/comprovante") || path.endsWith("/comprovante-premio") -> betReceipt(path)
        path.matches(Regex("[0-9]+/recuperar")) -> betReceipt("apostas/$path")
        path == "autoavaliacoes/recuperar-perguntas" -> assessment()
        path == "repassometro" -> socialTransfers()
        path == "usuarios/recuperar-dados" || path == "usuarios/recalcular-limite-autorizado" -> profile()
        path == "municipios" -> municipalities()
        path == "bairros" -> neighborhoods()
        path == "logado/usuarios/verificar-cadastro" -> session()
        path == "usuarios/suspender-conta-apostador" -> "{\"apostadorSuspenso\":true,\"prazoHorasDias\":\"24 horas\"}"
        path == "meios-pagamento" -> paymentMethods()
        path.startsWith("meios-pagamento/") -> paymentMethod(path.substringAfterLast('/').toIntOrNull())
        path == "apostas/config-consulta" -> confirmedBetsConfiguration()
        path == "compras/meses" -> purchaseMonths()
        path == "compras/situacoes-compra" -> purchaseStatuses()
        path == "compras" -> purchases()
        path.startsWith("compras/") && path.endsWith("/apostas") -> purchaseDetails()
        path == "usuarios/cartoes" -> cards()
        path.matches(Regex("historico-notificacoes/[0-9]+/visualiza")) -> "{}"
        path == "apostas" || path == "consultar" -> confirmedBets()
        path == "v1/payment_methods/installments" -> "[{\"payment_method_id\":\"visa\"}]"
        path == "v1/card_tokens" -> "{\"id\":\"mock-card-token\",\"luhn_validation\":true}"
        path == "cards" || path.startsWith("cards/") -> "{\"customerCardToken\":\"mock-customer-card\",\"brand\":\"VISA\"}"
        else -> "{}"
    }

    private fun appParameters() = """{
        "mapa":{"qtdeMinimaApostaBolaoFiltro":1,"qtdeMaximaApostaBolaoFiltro":15,
        "habilitaRevendaBolaoApp":true,"comboApostasHabilitado":true,"rapidaoHabilitado":true,
        "microServico":false,"grupoCpfNovaApi":0}}
    """.trimIndent()

    private fun modalities() = """[
        {"valor":2,"descricao":"Mega-Sena","descricaoEspecial":"Mega da Virada","descricaoGestor":"MEGA_SENA"},
        {"valor":3,"descricao":"Quina","descricaoEspecial":"Quina de São João","descricaoGestor":"QUINA"},
        {"valor":7,"descricao":"Super Sete","descricaoGestor":"SUPER_7"},
        {"valor":8,"descricao":"Lotofácil","descricaoEspecial":"Lotofácil da Independência","descricaoGestor":"LOTOFACIL"},
        {"valor":11,"descricao":"Dia de Sorte","descricaoGestor":"DIA_DE_SORTE"},
        {"valor":14,"descricao":"Lotogol","descricaoGestor":"LOTOGOL"},
        {"valor":16,"descricao":"Lotomania","descricaoGestor":"LOTOMANIA"},
        {"valor":19,"descricao":"Loteca","descricaoGestor":"LOTECA"},
        {"valor":20,"descricao":"Timemania","descricaoGestor":"TIMEMANIA"}
    ]"""

    private fun retailers() = """[{"id":101,"codigo":"000101","numeroFormatado":"0001-01",
        "nomeFantasia":"Lotérica Centro","nome":"Centro Loterias Ltda","logradouro":"SCS Quadra 1",
        "bairro":"Asa Sul","nomeMunicipio":"Brasília","nomeUF":"DF"},{"id":102,"codigo":"000102",
        "nomeFantasia":"Lotérica da Praça","logradouro":"Praça Central","bairro":"Centro",
        "nomeMunicipio":"Brasília","nomeUF":"DF"}]"""

    private fun favoriteRetailers() = """[{"codigo":101,"polo":1,"dv":0,"nomeFantasia":"Lotérica Centro",
        "nomeMunicipio":"Brasília","siglaUf":"DF"}]"""

    private fun prizeDetails() = """{"aposta":{"id":100,"modalidade":"MEGA_SENA","concursoInicial":2800,"concursoAlvo":2800},"premio":{"valorBruto":1250,"valorLiquido":1250},
        "nsbi":"MOCK-NSBI","mensagemInformativaIR":"Valores sujeitos às regras fiscais vigentes.",
        "meiosResgate":[{"id":6,"descricao":"PIX"},{"id":1,"descricao":"Mercado Pago"},
        {"id":9660,"descricao":"Lotérica"},{"id":9770,"descricao":"Agência Caixa","canal":"SISPL"}]}"""

    private fun lotteryRedemptionCode() = """{"nsb":"MOCK-NSB","codigo":"123456",
        "validade":15,"qrCode":"CAIXA|LOTERIAS|RESGATE|MOCK-100"}"""

    private fun pixBeneficiary() = """{"cpfMascarado":"***.456.789-**","nome":"PESSOA MOCK",
        "chave":"mock@caixa.gov.br","nomeBanco":"CAIXA ECONÔMICA FEDERAL"}"""

    private fun bettingParameters() = """{"valorLimiteDiario":500,"valorMinimoCarrinho":30,
        "quantidadeMaximaApostasCarrinho":50,"modalidadesDisponiveisCotas":[{
        "modalidade":"MEGA_SENA","modalidadeDetalhada":{"valor":2,"descricao":"Mega-Sena"},
        "tipoConcurso":"NORMAL"}],"parametros":[{"parametroJogo":{"concurso":{
        "modalidade":"MEGA_SENA","modalidadeDetalhada":{"valor":2,"descricao":"Mega-Sena"},
        "tipoConcurso":"NORMAL","numero":2900,"dataHoraSorteio":"10/09/2026 20:00:00",
        "dataFechamento":"10/09/2099 19:00:00","estimativa":35000000,"aberto":true},
        "prognosticoMaximo":60,"quantidadeMinima":6,"quantidadeMaxima":20,"quantidadeSurpresinhas":15,
        "teimosinhas":[2,3,4],"valoresAposta":[{"valor":5,"numeroPrognosticos":6,"numeroTrevos":0}],
        "concursoDisponivelSimulacao":true,"concursoCanalAberto":true}},
        {"parametroJogo":{"concurso":{"modalidade":"TIMEMANIA","modalidadeDetalhada":{"valor":20,"descricao":"Timemania"},
        "tipoConcurso":"NORMAL","numero":2300,"dataHoraSorteio":"10/09/2026 20:00:00","aberto":true},
        "prognosticoMaximo":80,"quantidadeMinima":10,"quantidadeMaxima":10,"quantidadeSurpresinhas":15,
        "teimosinhas":[2,4],"valoresAposta":[{"valor":3.50,"numeroPrognosticos":10}],"equipes":[
        {"numero":101,"nome":"Time Azul","uf":"DF","descricaoCurta":"AZU"},
        {"numero":102,"nome":"Time Verde","uf":"SP","descricaoCurta":"VRD"},
        {"numero":103,"nome":"Time Branco","uf":"RJ","descricaoCurta":"BRA"}],
        "concursoDisponivelSimulacao":true,"concursoCanalAberto":true}},
        {"parametroJogo":{"concurso":{"modalidade":"LOTECA","modalidadeDetalhada":{"valor":19,"descricao":"Loteca"},
        "tipoConcurso":"NORMAL","numero":1200,"dataHoraSorteio":"10/09/2026 16:00:00","aberto":true},
        "prognosticoMaximo":0,"quantidadeMinima":0,"quantidadeMaxima":0,"quantidadeSurpresinhas":0,
        "teimosinhas":[0],"valoresAposta":[{"valor":3.00,"quantidadeDuplos":0,"quantidadeTriplos":0},
        {"valor":6.00,"quantidadeDuplos":1,"quantidadeTriplos":0}],"partidas":[
        {"numero":1,"equipe1":{"numero":101,"nome":"Time Azul","uf":"DF"},"equipe2":{"numero":102,"nome":"Time Verde","uf":"SP"}},
        {"numero":2,"equipe1":{"numero":103,"nome":"Time Branco","uf":"RJ"},"equipe2":{"numero":101,"nome":"Time Azul","uf":"DF"}}],
        "concursoDisponivelSimulacao":true,"concursoCanalAberto":true}},
        {"parametroJogo":{"concurso":{"modalidade":"LOTOGOL","modalidadeDetalhada":{"valor":14,"descricao":"Lotogol"},
        "tipoConcurso":"NORMAL","numero":1100,"dataHoraSorteio":"10/09/2026 16:00:00","aberto":true},
        "prognosticoMaximo":0,"quantidadeMinima":0,"quantidadeMaxima":0,"quantidadeSurpresinhas":0,
        "teimosinhas":[0],"valoresAposta":[{"valor":2.00,"numeroPrognosticos":0}],"partidas":[
        {"numero":1,"equipe1":{"numero":101,"nome":"Time Azul","uf":"DF"},"equipe2":{"numero":102,"nome":"Time Verde","uf":"SP"}},
        {"numero":2,"equipe1":{"numero":103,"nome":"Time Branco","uf":"RJ"},"equipe2":{"numero":101,"nome":"Time Azul","uf":"DF"}}],
        "concursoDisponivelSimulacao":true,"concursoCanalAberto":true}},
        {"parametroJogo":{"concurso":{"modalidade":"SUPER_7","modalidadeDetalhada":{"valor":7,"descricao":"Super Sete"},
        "tipoConcurso":"NORMAL","numero":750,"dataHoraSorteio":"10/09/2026 20:00:00","aberto":true},
        "prognosticoMaximo":9,"quantidadeMinima":7,"quantidadeMaxima":21,"quantidadeSurpresinhas":15,
        "teimosinhas":[2,3],"valoresAposta":[{"valor":2.50,"numeroPrognosticos":7},
        {"valor":5.00,"numeroPrognosticos":8},{"valor":1250.00,"numeroPrognosticos":15},
        {"valor":2500000.00,"numeroPrognosticos":21}],"concursoDisponivelSimulacao":true,"concursoCanalAberto":true}},
        {"parametroJogo":{"concurso":{"modalidade":"DIA_DE_SORTE","modalidadeDetalhada":{"valor":11,"descricao":"Dia de Sorte"},
        "tipoConcurso":"NORMAL","numero":1100,"dataHoraSorteio":"10/09/2026 20:00:00","aberto":true},
        "prognosticoMaximo":31,"quantidadeMinima":7,"quantidadeMaxima":15,"quantidadeSurpresinhas":15,
        "teimosinhas":[2,3],"valoresAposta":[{"valor":2.50,"numeroPrognosticos":7}],"meses":[
        {"numero":1,"nome":"Janeiro","abreviacao":"JAN"},{"numero":2,"nome":"Fevereiro","abreviacao":"FEV"},
        {"numero":3,"nome":"Março","abreviacao":"MAR"},{"numero":4,"nome":"Abril","abreviacao":"ABR"},
        {"numero":5,"nome":"Maio","abreviacao":"MAI"},{"numero":6,"nome":"Junho","abreviacao":"JUN"},
        {"numero":7,"nome":"Julho","abreviacao":"JUL"},{"numero":8,"nome":"Agosto","abreviacao":"AGO"},
        {"numero":9,"nome":"Setembro","abreviacao":"SET"},{"numero":10,"nome":"Outubro","abreviacao":"OUT"},
        {"numero":11,"nome":"Novembro","abreviacao":"NOV"},{"numero":12,"nome":"Dezembro","abreviacao":"DEZ"}],
        "concursoDisponivelSimulacao":true,"concursoCanalAberto":true}},
        {"parametroJogo":{"concurso":{"modalidade":"LOTOMANIA","modalidadeDetalhada":{"valor":16,"descricao":"Lotomania"},
        "tipoConcurso":"NORMAL","numero":2800,"dataHoraSorteio":"10/09/2026 20:00:00","aberto":true},
        "aceitaEspelho":true,"prognosticoMaximo":100,"quantidadeMinima":50,"quantidadeMaxima":50,
        "quantidadeSurpresinhas":15,"teimosinhas":[2,3],"valoresAposta":[{"valor":3.00,"numeroPrognosticos":50}],
        "concursoDisponivelSimulacao":true,"concursoCanalAberto":true}}]}"""

    private fun sportsTeamShields() = """[
        {"id":1,"modalidades":["TIMEMANIA","LOTECA","LOTOGOL"],"equipe":{"id":101},"imagem":null},
        {"id":2,"modalidades":["TIMEMANIA","LOTECA","LOTOGOL"],"equipe":{"id":102},"imagem":null},
        {"id":3,"modalidades":["TIMEMANIA","LOTECA","LOTOGOL"],"equipe":{"id":103},"imagem":null}
    ]"""

    private fun combos() = """[{"tipoCombo":{"codigo":7,"nome":"Combo da Sorte","descricao":"8 apostas","novo":true},
        "modalidadesCombo":[{"modalidade":{"valor":2,"descricao":"Mega-Sena"},"tipoConcurso":"NORMAL",
        "quantidadeApostas":2,"quantidadeTeimosinhas":0,"quantidadeDezenas":6,"numero":2900}],"valor":20}]"""

    private fun rapidConfiguration() = """{"valorMinimo":30,"valorMaximo":100,
        "valorMinimoPremioPrincipal":0,"prognosticosObrigatorios":[],"prognosticosProibidos":[],
        "modalidades":[{"valor":2,"descricao":"Mega-Sena","descricaoGestor":"MEGA_SENA"}],
        "apostasFavoritas":[]}"""

    private fun cart(empty: Boolean = false, expiredPool: Boolean = false) = if (empty) {
        "{\"id\":9,\"valorTotal\":0,\"apostasIndividuais\":[],\"cotas\":[],\"combos\":[]}"
    } else """{"id":9,"valorTotal":35,"msgCotaExcluida":$expiredPool,"apostasIndividuais":[
        {"id":12,"modalidade":"MEGA_SENA","numerosSelecionados":[1,2,3,4,5,6],"valor":5,"quantidadeApostas":1}],
        "cotas":[],"combos":[]}"""

    private fun poolCart(): String {
        val expiresAt = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))
            .plusMinutes(15)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        return """{"id":9,"valorTotal":25,"apostasIndividuais":[],"combos":[],"cotas":[{
            "id":31,"modalidade":"MEGA_SENA","valor":25,"concursoAlvo":2900,
            "reservaCotaBolao":{"codigoBolaoReserva":"B1","numeroCotaReservada":1,
            "vrTotalCota":25,"qtdCotaTotalBolao":10,"dataHoraExpiracaoReserva":"$expiresAt"}}]}"""
    }

    private fun support() = """[{"id":10,"nome":"Geral","duvidas":[{"id":20,
        "pergunta":"Como apostar?","resposta":"Escolha uma modalidade e seus números."}]}]"""

    private fun marketplace() = """{"totalRegistros":1,"paginaAtual":0,"ultimaPagina":0,"cotas":[{
        "codigoBolao":"B1","modalidade":"Mega-Sena","concurso":2900,"nomeFantasia":"Loterias Centro",
        "dataSorteio":"10/09/2026","valorCota":"12.50","qtdCotaDisponivel":4,"qtdApostas":3,
        "qtdNumeros":6,"lotericaFavorita":true,"loterica":99,"municipio":{"nome":"Brasília","uf":{"sigla":"DF"}},
        "qtdCotaTotal":10,"tipoConcurso":1,"vrPremioEstimado":"1000000","vrCotaComTarifa":10.00,
        "valorTarifaServico":2.50,"vrUltimaCotaComTarifa":12.50}]}"""

    private fun marketplaceDetail() = """{"qtdCotaTotal":10,"qtdCotaDisponivel":4,"loterica":99,
        "nomeFantasia":"Loterias Centro","concurso":2900,"tipoConcurso":1,"modalidade":"MEGA_SENA","dataSorteio":"10/09/2026",
        "horaSorteio":"20:00","vrPremioEstimado":"1000000","qtdApostas":1,"qtdNumeros":6,
        "vrCotaSemTarifa":"10","vrTarifaServicoCota":2.50,"vrUltimaCotaComTarifa":"12.50","vrTarifaServicoUltimaCota":"2.50",
        "apostas":[{"indicadorSurpresinha":false,"dezenas":[1,2,3,4,5,6],"trevos":[]}]}"""

    private fun result() = """{"aviso":"Resultado mock","acumulou":false,"arrecadacaoTotal":"0.00",
        "numerosSorteadosPrimeiroSorteio":[5,10,23,34,42,58],"concurso":{"modalidade":"Mega-Sena",
        "numero":2800,"dataSorteio":"10/09/2026"},"premiacoesPrimeiroSorteio":[]}"""

    private fun assessment() = """{"retornoPerguntaResposta":[
        {"id":1,"descricaoPergunta":"Você aposta mais do que planeja?","ordemPergunta":1,"ativo":true},
        {"id":2,"descricaoPergunta":"Você já tentou apostar menos?","ordemPergunta":2,"ativo":true},
        {"id":3,"descricaoPergunta":"Apostar já causou problemas para você?","ordemPergunta":3,"ativo":true}]}"""

    private fun socialTransfers() = """{"arrecadacaoAcumulada":1000000,"arrecadacaoUltimaSemana":100000,
        "dataBase":"04/09/2026","percentualProgramasSociais":48,"percentualEducacao":30,
        "percentualCultura":10,"percentualEsporte":5,"percentualSeguranca":2,
        "percentualSeguridade":1,"percentualOutros":4}"""

    private fun profile() = """{"cpf":"12345678901","email":"mock@caixa.gov.br","nome":"Pessoa Mock",
        "dataNascimento":"1990-01-01","telefone":{"ddd":"61","numero":"999999999"},
        "cep":"70000000","aceitaReceberNoticias":true,"municipioId":{"idUF":7,"numero":1,
        "digitoVerificador":0},"sexo":"F","limiteMinimoDiario":10,"limiteDiario":100,
        "limiteDiarioAutorizado":200}"""

    private fun municipalities() = """[{"id":{"idUF":7,"numero":1,"digitoVerificador":0},
        "nome":"Brasília","uf":{"id":7,"sigla":"DF","nome":"Distrito Federal"}}]"""

    private fun neighborhoods() = """[{"id":1,"nome":"Centro","municipio":{"id":{"idUF":7,
        "numero":1,"digitoVerificador":0},"nome":"Brasília","uf":{"id":7,"sigla":"DF"}}}]"""

    private fun session() = """{"nome":"Pessoa Mock","email":"mock@caixa.gov.br","primeiroNome":"Pessoa",
        "limiteDiario":100,"suspensaoTemporariaApostador":false,"responderAutoavaliacao":false}"""

    private fun favoriteCartBets() = """[{"id":31,"modalidade":{"valor":2,"descricao":"Mega-Sena",
        "descricaoEspecial":"Mega da Virada"},"numerosSelecionados":[1,2,3,4,5,6],
        "quantidadeTeimosinhas":2,"espelho":false,"indicadorSurpresinha":"NAO_SURPRESINHA"}]"""

    private fun favoriteBetPage() = """{"rowCount":2,"lista":[
        {"id":41,"modalidade":{"valor":2,"descricao":"Mega-Sena"},"nome":"Minha Mega",
        "numerosSelecionados":[1,7,13,22,35,49]},
        {"id":42,"modalidade":{"valor":2,"descricao":"Mega-Sena"},"nome":"Aniversários",
        "numerosSelecionados":[3,9,12,18,27,31]}],"listaSize":2}"""

    private fun groupedFavoriteBets() = """{"resultado":{"MEGA_SENA":${favoriteBetPage()}},"pageSize":1}"""

    private fun paymentMethods() = "[${paymentMethod(1)},${paymentMethod(5)},${paymentMethod(6)}]"

    private fun paymentMethod(id: Int?) = when (id) {
        1 -> """{"id":1,"nomePagamentoCompra":"Mercado Pago","padrao":true,"publicKey":"mock-public-key"}"""
        5 -> """{"id":5,"nomePagamentoCompra":"RecargaPay","padrao":false}"""
        6 -> """{"id":6,"nomePagamentoCompra":"Pix","padrao":false}"""
        else -> "{}"
    }

    private fun cards() = """[{"idCartao":10,"mesValidade":12,"anoValidade":2029,"primeirosDigitos":"458900",
        "ultimosDigitos":"4589","tamanhoCodigoSeguranca":3,"nomeMetodoPagamento":"Visa",
        "idMetodoPagamento":"visa","operacaoExecutadaComSucesso":true,"tokenCartao":"mock-card-token"}]"""

    private fun pix(expired: Boolean): String {
        val now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        val expiration = (if (expired) now.minusMinutes(1) else now.plusMinutes(36))
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return """{"pixCopiaECola":"00020101021226850010014BR.GOV.BCB.PIX0136mock.caixa.gov.br/pix/checkout/99","txId":"MOCK-TX",
            "qrcodeArrByte":"","dataHoraExpiracao":"$expiration",
            "dataHoraCriacao":"${now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)}","idCompra":99,"valorCobranca":25}"""
    }

    private fun String.isPurchaseOperation(): Boolean = contains("verifica-compra-processamento") || endsWith("registrar-apostas-async")

    private fun asyncPurchase(status: Int, purchaseId: Long) = "{\"id\":$purchaseId,\"periodicidade\":1,\"repeticoes\":1,\"idSituacaoCompra\":$status}"

    private fun betReceipt(path: String): String {
        val id = path.split('/').firstOrNull { it.toLongOrNull() != null }?.toLongOrNull() ?: 100L
        val paid = path.endsWith("/comprovante-premio")
        return """{"nsbi":"MOCK-$id","aposta":{"id":$id,"modalidade":"MEGA_SENA",
            "concursoInicial":2900,"situacao":{"valor":${if (paid) 11 else 100},"descricao":"${if (paid) "PAGA" else "PREMIADA"}"},
            "dataEfetivacao":"13/10/2026","numerosSelecionados":[[5,10,23,34,42,58]]},
            "premio":{"valorLiquido":1250,"dataPagamento":"${if (paid) "13/10/2026" else ""}","localPagamento":"${if (paid) "Lotérica" else ""}"}}"""
    }

    private fun confirmedBets() = """{"totalRegistros":1,"resultado":[{"id":100,"modalidade":"MEGA_SENA",
        "numeroConcurso":2900,"situacao":{"valor":11,"descricao":"PAGA"},"teimosinha":false}]}"""

    private fun confirmedBetsConfiguration() = """{
        "ordenacaoPadrao":{"valor":3,"descricao":"Mais recentes"},
        "situacaoPadrao":{"valor":1,"descricao":"Todas"},
        "periodoPadrao":{"mes":{"valor":45,"descricao":"Últimos 45 dias"}},
        "situacoes":[{"valor":1,"descricao":"Todas"},{"valor":2,"descricao":"Pagas"},{"valor":3,"descricao":"Prescritas"}],
        "ordenacoes":[{"valor":3,"descricao":"Mais recentes"},{"valor":2,"descricao":"Mais antigas"}],
        "periodos":[{"mes":{"valor":45,"descricao":"Últimos 45 dias"}}],
        "listaTipoAposta":[{"valor":1,"descricao":"Todas"},{"valor":2,"descricao":"Simples"}],
        "listaTipoConcurso":[{"valor":1,"descricao":"Regular"},{"valor":2,"descricao":"Especial"}]
    }""".trimIndent()

    private fun purchaseMonths() = """[
        {"mes":{"valor":9,"descricao":"Setembro"},"ano":2026},
        {"mes":{"valor":8,"descricao":"Agosto"},"ano":2026}
    ]""".trimIndent()

    private fun purchaseStatuses() = """[
        {"id":2,"descricao":"Pagamento confirmado"},
        {"id":9,"descricao":"Pendente - pagamento"},
        {"id":12,"descricao":"Pagamento - cancelado"}
    ]""".trimIndent()

    private fun purchases() = """{"lista":[{
        "id":99,"dataInicioCompra":"04/09/2026","dataFinalizacaoCompra":"04/09/2026",
        "horaFinalizacaoCompra":"14:35","situacao":{"valor":8,"descricao":"Finalizada"},
        "meioPagamento":{"valor":6,"nome":"PIX"},"valorTotal":35.00,"e2eId":"E29001101202609041435MOCK"
    }]}""".trimIndent()

    private fun purchaseDetails() = """{"dataHoraServidor":"2026-09-04T14:35:00.000","compra":{
        "id":99,"dataInicioCompra":"04/09/2026","dataFinalizacaoCompra":"04/09/2026",
        "horaFinalizacaoCompra":"14:35","situacao":{"valor":8,"descricao":"Finalizada"},
        "meioPagamento":{"valor":6,"nome":"PIX"},"valorTotal":35.00,
        "e2eId":"E29001101202609041435MOCK"},"apostas":[
        {"id":100,"modalidade":{"valor":2,"descricao":"Mega-Sena"},"numeroConcurso":2900,
         "tipoConcurso":{"valor":1,"descricao":"Regular"},"situacao":{"valor":2,"descricao":"Paga"},
         "dataEfetivacao":"04/09/2026","numerosSelecionados":[1,7,13,22,35,49],
         "indicadorCotaBolao":false,"indicadorSurpresinha":"NAO_SURPRESINHA"}
    ]}""".trimIndent()

    private fun teimosinhaBet() = """{"totalRegistros":1,"resultado":[{"id":100,"modalidade":"MEGA_SENA",
        "numeroConcurso":2900,"situacao":"RESGATADA","teimosinha":true,"apostaOriginal":true,
        "houveResgate":true,"idApostaTroca":101}]}"""

    private fun prizeBet() = """{"totalRegistros":1,"resultado":[{"id":100,"modalidade":"MEGA_SENA",
        "numeroConcurso":2900,"situacao":"PREMIADA","valorPremio":1000}]}"""
}
