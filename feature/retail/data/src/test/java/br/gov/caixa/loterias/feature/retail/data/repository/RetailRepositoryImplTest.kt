package br.gov.caixa.loterias.feature.retail.data.repository

import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.provider.PrizeBackendApiProvider
import br.gov.caixa.loterias.feature.retail.data.dto.MercadoPagoRegistrationEnvelopeDto
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.interceptor.NetworkLogSink
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.mock.MockBackendScenario
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.payment.domain.usecase.PrepareMercadoPagoRegistration
import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistrationResult
import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistrationToken
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import com.google.gson.JsonObject
import org.junit.Assert.assertSame
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonParser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Test
import okhttp3.ResponseBody.Companion.toResponseBody
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionResult
import br.gov.caixa.loterias.feature.retail.domain.usecase.RedeemPrize
import br.gov.caixa.loterias.core.domain.service.model.ServiceNotice
import br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.feature.retail.data.datasource.remote.PrizeRedemptionRemoteDataSource
import br.gov.caixa.loterias.feature.retail.data.repository.RetailRepositoryImpl
import br.gov.caixa.loterias.feature.retail.data.datasource.remote.RetailRemoteDataSource
import br.gov.caixa.loterias.feature.retail.data.datasource.remote.RetailRemoteDataSourceImpl

class RetailRepositoryImplTest {
    @Test fun `transport failures remain typed through Retrofit for token and both redemption channels`() = runTest {
        for ((failure, expected) in listOf(
            java.net.SocketTimeoutException("private-timeout") to br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure.TIMEOUT,
            java.io.IOException("wrapper", java.net.SocketTimeoutException("private")) to br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure.TIMEOUT,
            java.net.UnknownHostException("private-host") to br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure.CONNECTION,
            javax.net.ssl.SSLPeerUnverifiedException("private-pin") to br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure.CONNECTION,
            javax.net.ssl.SSLHandshakeException("private-handshake") to br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure.CONNECTION,
            java.io.IOException("private") to br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure.CONNECTION,
        )) {
            var calls = 0
            val api = NetworkClientFactory.createPrizeRedemption("https://example.invalid/", AuthTokenProvider { null },
                okhttp3.OkHttpClient.Builder().addInterceptor { calls++; throw failure })
            val repository = RetailRepositoryImpl(sourceFor(api))
            for (channel in listOf(PrizeRedemptionChannel.MERCADO_PAGO, PrizeRedemptionChannel.PIX)) {
                val outcome = RedeemPrize(repository)(1, channel, "selected@example.test")
                assertEquals(AppResult.Success(PrizeRedemptionResult.ConnectionFailed(expected)), outcome)
                assertFalse(outcome.toString().contains("private"))
            }
            val token = PrepareMercadoPagoRegistration(repository)("selected@example.test")
            assertEquals(AppResult.Success(MercadoPagoRegistrationResult.Rejected(ServiceFailure.ConnectionFailed(expected))), token)
            assertFalse(token.toString().contains("private"))
            assertEquals(3, calls)
        }
    }

    @Test fun `cancellation is not converted to a connectivity notice or retried`() = runTest {
        var calls = 0
        val cancellation = kotlinx.coroutines.CancellationException("cancelled")
        val remote = object : PrizeRedemptionRemoteDataSource by FakePrizeSource() {
            override suspend fun redeemPrize(betId: Long, body: com.google.gson.JsonObject): JsonElement? { calls++; throw cancellation }
            override suspend fun mercadoPagoToken(email: String): String { calls++; throw cancellation }
        }
        val repository = RetailRepositoryImpl(FakeSource(), remote)
        for (channel in listOf(PrizeRedemptionChannel.MERCADO_PAGO, PrizeRedemptionChannel.PIX)) {
            assertTrue(runCatching { RedeemPrize(repository)(1, channel, "selected@example.test") }.exceptionOrNull() === cancellation)
        }
        assertTrue(runCatching { PrepareMercadoPagoRegistration(repository)("selected@example.test") }.exceptionOrNull() === cancellation)
        assertEquals(3, calls)
    }

    @Test fun `connection mocks affect only their transaction stage and keep preparation available`() = runTest {
        val cases = mapOf(
            MockBackendScenario.PRIZE_RESPONSE_TIMEOUT to br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure.TIMEOUT,
            MockBackendScenario.PRIZE_CONNECTION_FAILURE to br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure.CONNECTION,
            MockBackendScenario.MP_TOKEN_RESPONSE_TIMEOUT to br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure.TIMEOUT,
            MockBackendScenario.MP_TOKEN_CONNECTION_FAILURE to br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure.CONNECTION,
        )
        for ((scenario, reason) in cases) {
            val api = NetworkClientFactory.createPrizeRedemption("https://mock.caixa.gov.br/rest/v1/", AuthTokenProvider { null },
                mockOptions = MockBackendOptions(true, scenario))
            val repository = RetailRepositoryImpl(sourceFor(api))
            assertTrue(repository.details(1).options.isNotEmpty())
            val result = repository.redeemMercadoPago(1, "selected@example.test")
            val token = PrepareMercadoPagoRegistration(repository)("selected@example.test")
            if (scenario.name.startsWith("MP_TOKEN")) {
                assertTrue(result is PrizeRedemptionResult.RegistrationRequired)
                assertTrue(repository.redeemPix(1, "key") is PrizeRedemptionResult.Redeemed)
                assertEquals(AppResult.Success(MercadoPagoRegistrationResult.Rejected(ServiceFailure.ConnectionFailed(reason))), token)
            } else {
                assertEquals(PrizeRedemptionResult.ConnectionFailed(reason), result)
                assertEquals(PrizeRedemptionResult.ConnectionFailed(reason), repository.redeemPix(1, "key"))
                assertTrue((token as AppResult.Success).value is MercadoPagoRegistrationResult.Ready)
            }
            assertEquals(scenario, MockBackendScenario.from(scenario.name.lowercase()))
        }
    }

    @Test fun `token HTTP errors preserve generic handler decisions instead of losing them as unexpected errors`() = runTest {
        val cases = listOf(
            Triple(401, "{}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.LOGIN_REQUIRED))),
            Triple(400, "{\"codigo\":\"011010\",\"mensagem\":\"Regra\"}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.MORE_BETS_REQUIRED, "Regra", "011010"))),
            Triple(404, "{\"codigo\":\"011010\",\"mensagem\":\"Regra\"}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.BUSINESS_RULE, "Regra", "011010"))),
            Triple(408, "{}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.BUSINESS_RULE, "Tempo de resposta esgotou"))),
            Triple(500, "{\"codigo\":\"2067\",\"tipo\":1}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.INVALID_IDENTIFICATION, code = "2067"))),
            Triple(500, "{\"codigo\":\"E1\",\"tipo\":1,\"mensagem\":\"Mensagem\"}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.SERVER_MESSAGE, "Mensagem", "E1"))),
            Triple(500, "{\"codigo\":\"018003\",\"tipo\":2}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.SERVER_FAILURE, code = "018003"))),
            Triple(400, "{\"codigo\":\"018003\",\"mensagem\":\"Cadastrar?\"}", ServiceFailure.RegistrationRequired("Cadastrar?")),
            Triple(404, "{\"codigo\":\"018003\",\"mensagem\":\"Cadastrar?\"}", ServiceFailure.RegistrationRequired("Cadastrar?")),
            Triple(400, "{", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.BUSINESS_RULE))),
            Triple(303, "{\"redirect\":\"unknown\"}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.OPERATION_FAILED))),
            Triple(303, "{\"redirect\":\"\"}", ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.OPERATION_FAILED))),
            Triple(303, "{\"redirect\":\"home\"}", ServiceFailure.Unchanged),
            Triple(303, "{\"redirect\":\"/carrinhos/verifica-compra-processamento\"}", ServiceFailure.Unchanged),
        ) + mapOf("login" to ServiceRedirect.LOGIN, "aceitar-termo-de-uso" to ServiceRedirect.ACCEPT_TERMS,
            "cadastrar-apostador" to ServiceRedirect.REGISTER_ACCOUNT, "indisponibilidade" to ServiceRedirect.UNAVAILABLE,
            "carrinho" to ServiceRedirect.CART, "carrinhos-favoritos" to ServiceRedirect.FAVORITE_CARTS).flatMap { (wire, redirect) ->
            listOf(303, 400, 401, 500).map { status -> Triple(status,
                "{\"redirect\":\"$wire\",\"codigo\":\"018003\"}", ServiceFailure.RedirectRequired(redirect)) }
        }
        for ((status, body, expected) in cases) {
            val repository = redemptionRepository(status, body)
            assertEquals("HTTP $status $body", AppResult.Success(MercadoPagoRegistrationResult.Rejected(expected)),
                PrepareMercadoPagoRegistration(repository)("selected@example.test"))
        }
    }

    @Test fun `token mocks retain preparation and require explicit token request after registration consent`() = runTest {
        val cases = mapOf(
            MockBackendScenario.MP_TOKEN_LOGIN_REQUIRED to ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.LOGIN_REQUIRED)),
            MockBackendScenario.MP_TOKEN_LOGIN_REDIRECT to ServiceFailure.RedirectRequired(ServiceRedirect.LOGIN),
            MockBackendScenario.MP_TOKEN_TERMS_REQUIRED to ServiceFailure.RedirectRequired(ServiceRedirect.ACCEPT_TERMS),
            MockBackendScenario.MP_TOKEN_REPEAT_REGISTRATION to ServiceFailure.RegistrationRequired("Confirme novamente o cadastro (simulação)."),
            MockBackendScenario.MP_TOKEN_BUSINESS_RULE to ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.BUSINESS_RULE, "Regra de cadastro simulada.", "MOCK_TOKEN_RULE")),
            MockBackendScenario.MP_TOKEN_UNCHANGED to ServiceFailure.Unchanged,
        )
        for ((scenario, expected) in cases) {
            val requests = mutableListOf<okhttp3.Request>()
            val api = NetworkClientFactory.createPrizeRedemption("https://mock.caixa.gov.br/rest/v1/", AuthTokenProvider { null },
                okhttp3.OkHttpClient.Builder().addInterceptor { chain -> requests += chain.request(); chain.proceed(chain.request()) },
                mockOptions = MockBackendOptions(true, scenario))
            val repository = RetailRepositoryImpl(sourceFor(api))
            assertTrue(repository.details(1).options.isNotEmpty())
            assertTrue(repository.redeemMercadoPago(1, "selected@example.test") is PrizeRedemptionResult.RegistrationRequired)
            assertTrue(repository.redeemPix(1, "key") is PrizeRedemptionResult.Redeemed)
            assertTrue(requests.none { it.url.encodedPath.endsWith("gerar-token-cadastro-mercado-pago") })
            assertEquals(AppResult.Success(MercadoPagoRegistrationResult.Rejected(expected)), PrepareMercadoPagoRegistration(repository)("selected@example.test"))
            assertEquals(listOf("GET", "POST", "POST", "GET"), requests.map { it.method })
            assertEquals("selected@example.test", requests.last().url.queryParameter("email"))
            assertEquals(scenario, MockBackendScenario.from(scenario.name.lowercase()))
        }
    }

    @Test fun `prize submission and token use dedicated client and pin rotation affects the next request`() = runTest {
        val calls = mutableListOf<String>()
        fun api(label: String) = java.lang.reflect.Proxy.newProxyInstance(
            BackendApi::class.java.classLoader, arrayOf(BackendApi::class.java),
        ) { _, method, _ ->
            calls += "$label:${method.name}"
            when (method.name) {
                "redeemPrize" -> br.gov.caixa.loterias.feature.retail.data.dto.PrizeRedemptionEnvelopeDto(JsonNull.INSTANCE)
                "mercadoPagoRegistrationToken" -> MercadoPagoRegistrationEnvelopeDto("token")
                else -> ApiEnvelope(JsonNull.INSTANCE)
            }
        } as BackendApi
        val general = BackendApiProvider().apply { replace(api("general")) }
        val transaction = PrizeBackendApiProvider()
        val source = RetailRemoteDataSourceImpl(general, transaction)
        assertTrue(runCatching { source.redeemPrize(1, com.google.gson.JsonObject()) }.exceptionOrNull() is IllegalStateException)
        assertTrue(calls.isEmpty()) // A ausência de cliente de transação nunca leva ao uso da API geral como alternativa.
        transaction.replace(api("pins-v1"))
        source.prizeDetails(1)
        source.consultPixKey(1, com.google.gson.JsonObject())
        source.mercadoPagoToken("person@example.test")
        source.redeemPrize(1, com.google.gson.JsonObject())
        transaction.replace(api("pins-v2"))
        assertEquals(4, calls.size) // Substituir um cliente não repete a transação.
        source.redeemPrize(1, com.google.gson.JsonObject())
        assertEquals(listOf("general:prizeDetails", "general:consultPixKey", "pins-v1:mercadoPagoRegistrationToken",
            "pins-v1:redeemPrize", "pins-v2:redeemPrize"), calls)
    }

    @Test fun `all prize error mocks keep preparation available and match typed outcomes through Retrofit`() = runTest {
        val redirects = mapOf(
            MockBackendScenario.PRIZE_LOGIN_REDIRECT to ServiceRedirect.LOGIN,
            MockBackendScenario.PRIZE_TERMS_REQUIRED to ServiceRedirect.ACCEPT_TERMS,
            MockBackendScenario.PRIZE_ACCOUNT_REGISTRATION_REQUIRED to ServiceRedirect.REGISTER_ACCOUNT,
            MockBackendScenario.PRIZE_UNAVAILABLE to ServiceRedirect.UNAVAILABLE,
            MockBackendScenario.PRIZE_CART_REDIRECT to ServiceRedirect.CART,
            MockBackendScenario.PRIZE_FAVORITE_CARTS_REDIRECT to ServiceRedirect.FAVORITE_CARTS,
        )
        val notices = mapOf(
            MockBackendScenario.PRIZE_LOGIN_REQUIRED to ServiceNoticeReason.LOGIN_REQUIRED,
            MockBackendScenario.PRIZE_MORE_BETS_REQUIRED to ServiceNoticeReason.MORE_BETS_REQUIRED,
            MockBackendScenario.PRIZE_BUSINESS_RULE to ServiceNoticeReason.BUSINESS_RULE,
            MockBackendScenario.PRIZE_SERVER_MESSAGE to ServiceNoticeReason.SERVER_MESSAGE,
            MockBackendScenario.PRIZE_SERVER_FAILURE to ServiceNoticeReason.SERVER_FAILURE,
            MockBackendScenario.PRIZE_INVALID_IDENTIFICATION to ServiceNoticeReason.INVALID_IDENTIFICATION,
        )
        for (scenario in redirects.keys + notices.keys + MockBackendScenario.PRIZE_PROCESSING_REDIRECT) {
            val api = NetworkClientFactory.createPrizeRedemption("https://mock.caixa.gov.br/rest/v1/", AuthTokenProvider { null }, mockOptions = MockBackendOptions(true, scenario))
            val repository = RetailRepositoryImpl(sourceFor(api))
            assertTrue(repository.details(7).options.isNotEmpty())
            for (channel in listOf(PrizeRedemptionChannel.MERCADO_PAGO, PrizeRedemptionChannel.PIX)) {
                val outcome = (RedeemPrize(repository)(7, channel, "selected@example.test") as AppResult.Success).value
                if (scenario == MockBackendScenario.PRIZE_PROCESSING_REDIRECT) assertEquals(PrizeRedemptionResult.Unchanged, outcome)
                else if (scenario in redirects) assertEquals(PrizeRedemptionResult.RedirectRequired(redirects.getValue(scenario)), outcome)
                else assertEquals(notices.getValue(scenario), (outcome as PrizeRedemptionResult.NoticeRequired).notice.reason)
            }
        }
    }

    @Test fun `redemption redirects take priority over status code and registration for both online channels`() = runTest {
        for ((wire, destination) in mapOf(
            "login" to ServiceRedirect.LOGIN, "aceitar-termo-de-uso" to ServiceRedirect.ACCEPT_TERMS,
            "cadastrar-apostador" to ServiceRedirect.REGISTER_ACCOUNT, "indisponibilidade" to ServiceRedirect.UNAVAILABLE,
            "carrinho" to ServiceRedirect.CART, "carrinhos-favoritos" to ServiceRedirect.FAVORITE_CARTS,
        )) {
            for (status in listOf(303, 400, 401, 500)) {
                val repository = redemptionRepository(status, """{"redirect":"$wire","codigo":"018003"}""")
                for (channel in listOf(PrizeRedemptionChannel.MERCADO_PAGO, PrizeRedemptionChannel.PIX)) {
                    assertEquals(AppResult.Success(PrizeRedemptionResult.RedirectRequired(destination)), RedeemPrize(repository)(7, channel, "selected@example.test"))
                }
            }
        }
        for (wire in listOf("", "https://untrusted.invalid", "verifica-compra-processamento", "/carrinhos/verifica-compra-processamento-unknown")) {
            val result = RedeemPrize(redemptionRepository(401, """{"redirect":"$wire","codigo":"018003"}"""))(7, PrizeRedemptionChannel.MERCADO_PAGO, "selected@example.test")
            assertEquals(AppResult.Success(PrizeRedemptionResult.NoticeRequired(ServiceNotice(ServiceNoticeReason.OPERATION_FAILED))), result)
        }
        for (wire in listOf(
            "home", "ativar-cadastro", "mega-sena", "surpresinhaMegaSena", "quina", "surpresinhaQuina",
            "dupla-sena", "surpresinhaDuplaSena", "lotomania", "surpresinhaLotomania",
            "lotofacil", "surpresinhaLotofacil", "timemania", "surpresinhaTimemania", "lotogol", "loteca",
            "aposta", "validar-cpf", "selecionar-loterica", "/carrinhos/verifica-compra-processamento",
        )) {
            for (channel in listOf(PrizeRedemptionChannel.MERCADO_PAGO, PrizeRedemptionChannel.PIX)) {
                val result = RedeemPrize(redemptionRepository(303, """{"redirect":"$wire","codigo":"018003"}"""))(7, channel, "selected@example.test")
                assertEquals(AppResult.Success(PrizeRedemptionResult.Unchanged), result)
            }
        }
    }

    @Test fun `redemption errors preserve login consent business rules server type and CPF priority`() = runTest {
        data class Case(val status: Int, val code: String?, val type: String?, val reason: ServiceNoticeReason)
        for (case in listOf(
            Case(401, null, null, ServiceNoticeReason.LOGIN_REQUIRED),
            Case(400, "011010", null, ServiceNoticeReason.MORE_BETS_REQUIRED),
            Case(400, "011011", null, ServiceNoticeReason.MORE_BETS_REQUIRED),
            Case(400, "011012", null, ServiceNoticeReason.MORE_BETS_REQUIRED),
            Case(404, "011010", null, ServiceNoticeReason.BUSINESS_RULE),
            Case(400, "E1", null, ServiceNoticeReason.BUSINESS_RULE),
            Case(408, "E1", null, ServiceNoticeReason.BUSINESS_RULE),
            Case(422, null, null, ServiceNoticeReason.BUSINESS_RULE),
            Case(500, "2067", "1", ServiceNoticeReason.INVALID_IDENTIFICATION),
            Case(500, "018003", "1", ServiceNoticeReason.SERVER_MESSAGE),
            Case(500, "E1", "2", ServiceNoticeReason.SERVER_FAILURE),
            Case(500, null, null, ServiceNoticeReason.SERVER_FAILURE),
        )) {
            val body = com.google.gson.JsonObject().apply {
                addProperty("codigo", case.code); addProperty("tipo", case.type); addProperty("mensagem", "Mensagem")
            }
            for (channel in listOf(PrizeRedemptionChannel.MERCADO_PAGO, PrizeRedemptionChannel.PIX)) {
                val result = RedeemPrize(redemptionRepository(case.status, body.toString()))(7, channel, "selected@example.test") as AppResult.Success
                val notice = (result.value as PrizeRedemptionResult.NoticeRequired).notice
                assertEquals(case.reason, notice.reason)
                assertEquals(if (case.status == 401) null else case.code, notice.code)
                assertEquals(when (case.status) { 401 -> null; 408 -> "Tempo de resposta esgotou"; else -> "Mensagem" }, notice.message)
            }
        }
    }

    @Test fun `prize details preserve each supported lottery modality and only server enabled redemption channels`() = runTest {
        val source = FakePrizeSource()
        val repository = RetailRepositoryImpl(FakeSource(), source)
        for ((code, id, name) in listOf(
            Triple("MEGA_SENA", 2, "Mega-Sena"), Triple("QUINA", 3, "Quina"),
            Triple("SUPER_7", 7, "Super Sete"), Triple("LOTOFACIL", 8, "Lotofácil"),
            Triple("MAIS_MILIONARIA", 9, "+Milionária"), Triple("DIA_DE_SORTE", 11, "Dia de Sorte"),
            Triple("LOTOGOL", 14, "Lotogol"), Triple("LOTOMANIA", 16, "Lotomania"),
            Triple("DUPLA_SENA", 18, "Dupla Sena"), Triple("LOTECA", 19, "Loteca"),
            Triple("TIMEMANIA", 20, "Timemania"),
        )) {
            source.detailsValue = JsonParser.parseString("""{"aposta":{"modalidade":"$code"},"meiosResgate":[{"id":9770}]}""")
            val details = repository.details(7)
            assertEquals(id, details.modalityId)
            assertEquals(name, details.modalityName)
            assertEquals(PrizeRedemptionChannel.AGENCY, details.options.single().channel)
            assertEquals("Agência Caixa", details.options.single().title)
            assertEquals("Valores acima da isenção do IR", details.options.single().description)
        }
    }

    @Test fun `unrecognized notice payload has no recurring success notice`() = runTest {
        val source = FakePrizeSource().apply { redeemValue = JsonParser.parseString("[]") }
        val result = RetailRepositoryImpl(FakeSource(), source).redeemPix(7, "key")
        assertNull((result as PrizeRedemptionResult.Redeemed).noticeCode)
    }

    @Test fun `only eligible Mercado Pago HTTP errors request registration never a Pix redemption`() = runTest {
        for (status in listOf(303, 400, 404, 408, 422, 503)) {
            val repository = redemptionRepository(status, """{"codigo":"018003","mensagem":"Cadastrar?"}""")
            val result = RedeemPrize(repository)(7, PrizeRedemptionChannel.MERCADO_PAGO, "pessoa@example.test")
            assertEquals(AppResult.Success(PrizeRedemptionResult.RegistrationRequired(
                if (status == 408) "Tempo de resposta esgotou" else "Cadastrar?",
            )), result)
            assertEquals(AppResult.Success(PrizeRedemptionResult.NoticeRequired(ServiceNotice(ServiceNoticeReason.OPERATION_FAILED))),
                RedeemPrize(repository)(7, PrizeRedemptionChannel.PIX, "key"))
        }
        for (message in listOf("null", "\"\"")) {
            val result = RedeemPrize(redemptionRepository(400, """{"codigo":"018003","mensagem":$message}"""))(
                7, PrizeRedemptionChannel.MERCADO_PAGO, "pessoa@example.test",
            )
            assertEquals(AppResult.Success(PrizeRedemptionResult.RegistrationRequired(
                "Não foi possível concluir esta operação. Por favor, tente novamente.",
            )), result)
        }
    }

    @Test fun `other errors malformed bodies and 200 error metadata never become prize success or registration`() = runTest {
        for ((status, body) in listOf(
            401 to """{"codigo":"018003"}""", 500 to """{"codigo":"018003"}""",
            400 to "{}", 400 to "not-json", 400 to "x".repeat(65_537),
            400 to """{"codigo":"unknown"}""",
            400 to """{"codigo":"018003","redirect":"login"}""",
            400 to """{"codigo":"018003","redirect":""}""",
            200 to """{"codigo":"018003","payload":"MI053"}""",
            200 to """{"redirect":"login","payload":"MI053"}""",
        )) {
            val result = RedeemPrize(redemptionRepository(status, body))(7, PrizeRedemptionChannel.MERCADO_PAGO, "pessoa@example.test")
            if (status == 200) assertTrue(result is AppResult.Failure)
            else {
                val outcome = (result as AppResult.Success).value
                assertTrue("HTTP $status must not approve redemption", outcome is PrizeRedemptionResult.NoticeRequired || outcome is PrizeRedemptionResult.RedirectRequired)
            }
        }
        for (payload in listOf("null", "\"MI053\"", "\"MIO53\"")) {
            val result = RedeemPrize(redemptionRepository(200, "{\"payload\":$payload}"))(7, PrizeRedemptionChannel.MERCADO_PAGO, "pessoa@example.test")
            assertTrue((result as AppResult.Success).value is PrizeRedemptionResult.Redeemed)
        }
    }

    @Test fun `redemption mocks request registration without fetching token or affecting Pix`() = runTest {
        for (scenario in listOf(MockBackendScenario.PRIZE_MP_REGISTRATION_REQUIRED, MockBackendScenario.PRIZE_MP_REGISTRATION_TOKEN_FAILURE)) {
            val requests = mutableListOf<okhttp3.Request>()
            val api = NetworkClientFactory.createPrizeRedemption(
                "https://mock.caixa.gov.br/rest/v1/", AuthTokenProvider { null },
                clientBuilder = okhttp3.OkHttpClient.Builder().addInterceptor { chain ->
                    requests += chain.request(); chain.proceed(chain.request())
                }, mockOptions = MockBackendOptions(true, scenario),
            )
            val repository = RetailRepositoryImpl(sourceFor(api))
            val result = RedeemPrize(repository)(7, PrizeRedemptionChannel.MERCADO_PAGO, "selected@example.test")
            assertTrue((result as AppResult.Success).value is PrizeRedemptionResult.RegistrationRequired)
            assertEquals("POST", requests.single().method)
            assertEquals("/rest/v1/apostas/7/resgatar-premio", requests.single().url.encodedPath)
            val body = okio.Buffer().also { requests.single().body!!.writeTo(it) }.readUtf8()
            assertEquals("selected@example.test", JsonParser.parseString(body).asJsonObject["parametrosPagamento"].asJsonObject["EMAIL"].asString)
            assertTrue((RedeemPrize(repository)(7, PrizeRedemptionChannel.PIX, "key") as AppResult.Success).value is PrizeRedemptionResult.Redeemed)
            assertTrue(requests.none { it.url.encodedPath.contains("gerar-token") })
            val tokenResult = PrepareMercadoPagoRegistration(repository)("selected@example.test")
            val preparation = (tokenResult as AppResult.Success).value
            if (scenario == MockBackendScenario.PRIZE_MP_REGISTRATION_REQUIRED) assertTrue(preparation is MercadoPagoRegistrationResult.Ready)
            else assertTrue(preparation is MercadoPagoRegistrationResult.Rejected)
        }
    }

    private fun redemptionRepository(status: Int, body: String): RetailRepositoryImpl {
        val api = NetworkClientFactory.createPrizeRedemption(
            "https://mock.caixa.gov.br/rest/v1/", AuthTokenProvider { null },
            clientBuilder = okhttp3.OkHttpClient.Builder().addInterceptor { chain ->
                okhttp3.Response.Builder().request(chain.request()).protocol(okhttp3.Protocol.HTTP_1_1)
                    .code(status).message("Test response").body(body.toResponseBody()).build()
            },
        )
        return RetailRepositoryImpl(sourceFor(api))
    }

    @Test fun `registration source rejects error metadata redirects and absent tokens`() = runTest {
        var response = MercadoPagoRegistrationEnvelopeDto("private-token")
        val calls = mutableListOf<String>()
        val api = java.lang.reflect.Proxy.newProxyInstance(
            BackendApi::class.java.classLoader, arrayOf(BackendApi::class.java),
        ) { _, method, args ->
            assertEquals("mercadoPagoRegistrationToken", method.name)
            calls += args[0] as String
            response
        } as BackendApi
        val source = sourceFor(api)
        assertEquals("private-token", source.mercadoPagoToken("pessoa@example.test"))
        for (invalid in listOf(
            MercadoPagoRegistrationEnvelopeDto("private-token", code = "018003"),
            MercadoPagoRegistrationEnvelopeDto("private-token", code = ""),
            MercadoPagoRegistrationEnvelopeDto("private-token", redirect = "login"),
            MercadoPagoRegistrationEnvelopeDto("private-token", redirect = ""),
            MercadoPagoRegistrationEnvelopeDto(),
        )) {
            response = invalid
            val failure = runCatching { source.mercadoPagoToken("pessoa@example.test") }.exceptionOrNull()
            assertTrue(failure is IllegalArgumentException)
            assertFalse(failure.toString().contains("private-token"))
            assertFalse(failure.toString().contains("pessoa@example.test"))
        }
        assertEquals(List(6) { "pessoa@example.test" }, calls)
    }

    @Test fun `registration Retrofit chain preserves token and redacts logs including failure scenario`() = runTest {
        for (scenario in listOf(
            MockBackendScenario.PURCHASE_MP_REGISTRATION_REQUIRED,
            MockBackendScenario.PURCHASE_MP_REGISTRATION_TOKEN_FAILURE,
        )) {
            val logs = mutableListOf<String>()
            val requests = mutableListOf<okhttp3.Request>()
            val api = NetworkClientFactory.createPrizeRedemption(
                "https://mock.caixa.gov.br/rest/v1/",
                AuthTokenProvider { "private-authorization" },
                clientBuilder = okhttp3.OkHttpClient.Builder().addInterceptor { chain ->
                    requests += chain.request()
                    chain.proceed(chain.request())
                },
                debugOptions = NetworkDebugOptions(true, NetworkLogSink { logs += it }),
                mockOptions = MockBackendOptions(true, scenario),
            )
            val repository = RetailRepositoryImpl(sourceFor(api))
            val result = PrepareMercadoPagoRegistration(repository)(" pessoa+teste@example.test ")
            assertEquals("GET", requests.single().method)
            assertEquals("/rest/v1/usuarios/gerar-token-cadastro-mercado-pago", requests.single().url.encodedPath)
            assertEquals("pessoa+teste@example.test", requests.single().url.queryParameter("email"))
            if (scenario == MockBackendScenario.PURCHASE_MP_REGISTRATION_REQUIRED) {
                assertTrue(result is AppResult.Success)
                assertEquals(
                    "https://registration-br.mercadopago.com/registration-mp?mode=mp&external_registration_token=mock-registration-token",
                    ((result as AppResult.Success).value as MercadoPagoRegistrationResult.Ready).request.url,
                )
            } else {
                assertTrue((result as AppResult.Success).value is MercadoPagoRegistrationResult.Rejected)
            }
            assertTrue(logs.any { it.startsWith("curl ") })
            assertTrue(logs.any { "HTTP GET" in it })
            for (secret in listOf("pessoa", "example.test", "private-authorization", "mock-registration-token")) {
                assertFalse("Sensitive data in logs", logs.any { secret in it })
                assertFalse("Sensitive data in result representation", secret in result.toString())
            }
        }
    }

    @Test fun `search maps nested retailers and marks backend favorites`() = runTest {
        val source = FakeSource()
        val repository = RetailRepositoryImpl(source)
        val retailers = repository.search("Centro")
        assertEquals("Centro", source.query)
        assertEquals(2, retailers.size)
        with(retailers.first()) {
            assertEquals(101L, id)
            assertEquals("0001-01", code)
            assertEquals("Lotérica Centro", fantasyName)
            assertEquals("Centro Ltda", legalName)
            assertEquals("Rua A", address)
            assertEquals("Centro", district)
            assertEquals("Brasília", city)
            assertEquals("DF", state)
            assertTrue(favorite)
        }
        with(retailers.last()) {
            assertEquals(102L, id)
            assertEquals("102", code)
            assertFalse(favorite)
        }
    }

    @Test fun `favorite list and mutations preserve legacy operations`() = runTest {
        val source = FakeSource()
        val repository = RetailRepositoryImpl(source)
        assertTrue(repository.favorites().single().favorite)
        repository.setFavorite(101, true)
        repository.setFavorite(102, false)
        assertEquals(listOf("add:101", "delete:102"), source.mutations)
        source.searchValue = JsonNull.INSTANCE
        source.favoritesValue = JsonParser.parseString("{\"payload\":[null,1,{}]}")
        assertTrue(repository.search("nada").isEmpty())
        assertTrue(repository.favorites().isEmpty())
    }

    @Test fun `retrofit source delegates all retail calls`() = runTest {
        val calls = mutableListOf<String>()
        val api = java.lang.reflect.Proxy.newProxyInstance(
            BackendApi::class.java.classLoader,
            arrayOf(BackendApi::class.java),
        ) { _, method, _ ->
            calls += method.name
            ApiEnvelope(if (method.name == "retailersByNameOrCode") JsonParser.parseString("[]") else JsonNull.INSTANCE)
        } as BackendApi
        val provider = BackendApiProvider().apply { replace(api) }
        val source = RetailRemoteDataSourceImpl(provider, PrizeBackendApiProvider())
        source.search("Centro")
        source.favorites()
        source.addFavorite(1)
        source.deleteFavorite(2)
        assertEquals(listOf("retailersByNameOrCode", "favoriteRetailers", "addFavoriteRetailer", "deleteFavoriteRetailer"), calls)
    }

    @Test fun `prize details codes pix and redemptions map legacy contracts`() = runTest {
        val source = FakePrizeSource()
        val repository = RetailRepositoryImpl(FakeSource(), source)
        val details = repository.details(7)
        assertEquals(8L, details.betId)
        assertEquals("90", details.netValue.toPlainString())
        assertEquals("NSBI", details.nsbi)
        assertEquals(listOf(PrizeRedemptionChannel.PIX, PrizeRedemptionChannel.LOTTERY), details.options.map { it.channel })
        assertEquals("IR", details.incomeTaxMessage)
        assertEquals(20, details.modalityId)
        assertEquals("Timemania", details.modalityName)
        assertEquals(1455, details.initialContest)
        assertEquals(1456, details.targetContest)
        assertEquals("3 acertos (2 vezes)", details.awards.single().description)
        assertEquals(2, details.awards.single().hitCount)
        assertEquals("25", details.awards.single().amount!!.toPlainString())
        assertFalse(details.isPool)

        val code = repository.lotteryCode(7)
        assertEquals("QR", code.qrCode)
        assertEquals("CODE", code.code)
        assertEquals(15, code.validityMinutes)
        val pix = repository.consultPixKey(7, "key")
        assertEquals("Pessoa", pix.name)
        assertEquals(6L, source.lastBody!!.get("meioPagamento").asLong)
        assertEquals("key", source.lastBody!!.get("chavePix").asString)
        source.redeemValue = JsonParser.parseString("\"MI053\"")
        assertEquals("MI053", (repository.redeemPix(7, "pix") as PrizeRedemptionResult.Redeemed).noticeCode)
        source.redeemValue = JsonParser.parseString("{\"codigo\":\"MIO53\"}")
        assertEquals("MIO53", (repository.redeemMercadoPago(7, "mail@caixa.gov.br") as br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionResult.Redeemed).noticeCode)
        assertEquals("mail@caixa.gov.br", source.lastBody!!.getAsJsonObject("parametrosPagamento").get("EMAIL").asString)
        repository.cancelPix(7)
        assertEquals("token", (repository.mercadoPagoRegistrationToken("mail") as MercadoPagoRegistrationToken.Available).value)
        assertEquals(1, repository.betReceiptPdf(7).single().toInt())
        assertEquals(2, repository.prizeReceiptPdf(7).single().toInt())
        assertEquals(listOf("details", "code", "consult", "redeem", "redeem", "cancel", "token", "betPdf", "prizePdf"), source.calls)
    }

    @Test fun `prize fallbacks preserve absent qr for agency and ignore unknown channels`() = runTest {
        val source = FakePrizeSource().apply {
            detailsValue = JsonParser.parseString("""{"aposta":{},"premio":{"valorBruto":12},"meiosResgate":[{"id":999},{"id":1}]}""")
            codeValue = JsonParser.parseString("""{"qrCode":"","validade":0}""")
        }
        val repository = RetailRepositoryImpl(FakeSource(), source)
        val details = repository.details(9)
        assertEquals(9L, details.betId)
        assertEquals("0", details.netValue.toPlainString())
        assertEquals("Mercado Pago", details.options.single().title)
        assertEquals("", repository.lotteryCode(9).qrCode)
        assertEquals(1, repository.lotteryCode(9).validityMinutes)
        assertTrue(runCatching { RetailRepositoryImpl(FakeSource()).details(1) }.isFailure)
    }

    @Test fun `zero net prize must not become gross prize in redemption`() = runTest {
        val source = FakePrizeSource().apply {
            detailsValue = JsonParser.parseString("""{"aposta":{"id":7},"premio":{"valorLiquido":0,"valorBruto":1200}}""")
        }
        assertEquals(0, RetailRepositoryImpl(FakeSource(), source).details(7).netValue.signum())
    }

    @Test fun `installed result uses hit description occurrence count and Dupla draw prefix`() = runTest {
        val source = FakePrizeSource().apply {
            detailsValue = JsonParser.parseString("""{
              "aposta":{"id":9002,"modalidade":"DUPLA_SENA"},
              "premio":{"valorLiquido":7,"concursos":[{"concurso":{"numero":3797},"faixas":[
                {"nome":"Quadra","descricaoAcerto":"4 acertos","quantidadeAcertos":2,"sorteio":1,"valorPremioCota":7},
                {"nome":"Terno","descricaoAcerto":"3 acertos","quantidadeAcertos":1,"sorteio":2,"valorPremioCota":0}
              ]}]}
            }""")
        }
        val details = RetailRepositoryImpl(FakeSource(), source).details(9002)
        assertEquals(listOf("1º Sorteio - 4 acertos (2 vezes)", "2º Sorteio - 3 acertos"), details.awards.map { it.description })
        assertEquals(listOf(2, 1), details.awards.map { it.hitCount })
        assertEquals(listOf(1, 2), details.awards.map { it.draw })
        assertEquals(listOf("7", "0"), details.awards.map { it.amount?.toPlainString() })
        assertEquals(listOf("details"), source.calls)
    }

    @Test fun `installed Loteca removes parenthesized placement while retaining occurrences`() = runTest {
        val source = FakePrizeSource().apply {
            detailsValue = JsonParser.parseString("""{
              "aposta":{"id":9002,"modalidade":"LOTECA"},
              "premio":{"valorLiquido":7,"concursos":[{"concurso":{"numero":3797},"faixas":[
                {"nome":"Primeira faixa","descricaoAcerto":"1º (14 acertos)","quantidadeAcertos":3,"valorPremioCota":7},
                {"nome":"Segunda faixa","descricaoAcerto":"2º (13 acertos)","quantidadeAcertos":1,"valorPremioCota":0}
              ]}]}
            }""")
        }
        val awards = RetailRepositoryImpl(FakeSource(), source).details(9002).awards
        assertEquals(listOf("14 acertos (3 vezes)", "13 acertos"), awards.map { it.description })
        assertEquals(listOf("7", "0"), awards.map { it.amount?.toPlainString() })
        assertEquals(listOf("details"), source.calls)
    }

    @Test fun `installed one winning Lotofacil range describes hits without multiplying by hit number`() = runTest {
        val source = FakePrizeSource().apply {
            detailsValue = JsonParser.parseString("""{
              "aposta":{"id":9002,"modalidade":"LOTOFACIL"},
              "premio":{"valorLiquido":7,"concursos":[{"concurso":{"numero":3797},"faixas":[
                {"nome":"Quinta faixa","descricaoAcerto":"11 acertos","quantidadeAcertos":1,"valorPremioCota":7}
              ]}]}
            }""")
        }
        val award = RetailRepositoryImpl(FakeSource(), source).details(9002).awards.single()
        assertEquals("11 acertos", award.description)
        assertEquals(1, award.hitCount)
        assertEquals("7", award.amount?.toPlainString())
        assertEquals(listOf("details"), source.calls)
    }

    @Test fun `pool preserves range names zero quota prizes and missing values`() = runTest {
        val source = FakePrizeSource().apply {
            detailsValue = JsonParser.parseString("""{
              "aposta":{"id":7,"indicadorCotaBolao":true},
              "premio":{"valorLiquido":100,"concursos":[
                {"concurso":{"numero":3100},"situacao":"Premiada","faixas":[
                  {"nome":"Quina","descricaoAcerto":"5 acertos","quantidadeAcertos":2,"sorteio":1,"valorPremioCota":0,"valorLiquido":500},
                  {"nome":"Quadra","valorPremioCota":null,"valorLiquido":100},
                  {"nome":"Terno"}
                ]}
              ]}
            }""")
        }
        val details = RetailRepositoryImpl(FakeSource(), source).details(7)
        assertTrue(details.isPool)
        assertEquals(listOf("5 acertos (2 vezes)", "Quadra", "Terno"), details.awards.map { it.description })
        assertEquals(listOf("0", "100", null), details.awards.map { it.amount?.toPlainString() })
        assertEquals("Premiada", details.awards.first().status)
        assertEquals(3100, details.awards.first().contest)
        assertEquals(2, details.awards.first().hitCount)
        assertEquals(1, details.awards.first().draw)
    }

    @Test fun `prize bet attributes come from the selected bet without inferred recurrence`() = runTest {
        val source = FakePrizeSource().apply {
            detailsValue = JsonParser.parseString("""{"aposta":{
                "troca":true,"combo":true,"espelho":true,"surpresinha":true,
                "indicadorCotaBolao":true,"quantidadeTeimosinhas":1
            }}""")
        }
        val repository = RetailRepositoryImpl(FakeSource(), source)
        val bet = repository.details(7)
        assertTrue(bet.exchange && bet.combo && bet.mirror && bet.surprise && bet.isPool)
        assertEquals(1, bet.recurringDraws)
        source.detailsValue = JsonParser.parseString("""{"aposta":{
            "troca":false,"combo":null,"espelho":false,"surpresinha":null,
            "concursoInicial":3100,"concursoAlvo":3110
        }}""")
        val ordinary = repository.details(7)
        assertFalse(ordinary.exchange || ordinary.combo || ordinary.mirror || ordinary.surprise || ordinary.isPool)
        assertEquals(0, ordinary.recurringDraws)
    }

    @Test fun `recurring prize keeps non winning contests after prize ranges and ignores invalid contests`() = runTest {
        val source = FakePrizeSource().apply {
            detailsValue = JsonParser.parseString("""{
              "aposta":{"indicadorCotaBolao":false},"premio":{
                "concursos":[
                  {"faixas":[{"nome":"Invalid"}]},
                  {"concurso":{"numero":3101},"situacao":"Resgatada","faixas":[{"nome":"Quadra","quantidadeAcertos":1}]}
                ],
                "concursosNaoPremiado":[
                  {"concurso":{"numero":3100},"situacao":"Não premiada"},
                  {"concurso":{"numero":3102},"situacao":"Aguardando sorteio"},{}
                ]
              }
            }""")
        }
        val details = RetailRepositoryImpl(FakeSource(), source).details(7)
        assertFalse(details.isPool)
        assertEquals(listOf(3101, 3100, 3102), details.awards.map { it.contest })
        assertEquals(listOf("Resgatada", "Não premiada", "Aguardando sorteio"), details.awards.map { it.status })
        assertEquals(listOf(1, null, null), details.awards.map { it.hitCount })
        assertTrue(details.awards.drop(1).all { it.description.isEmpty() && it.amount == null && it.draw == null })
        assertEquals(listOf(true, false, false), details.awards.map { it.isAwarded })
    }

    @Test fun `retrofit prize source delegates request and response body operations`() = runTest {
        val calls = mutableListOf<String>()
        val api = java.lang.reflect.Proxy.newProxyInstance(
            BackendApi::class.java.classLoader,
            arrayOf(BackendApi::class.java),
        ) { _, method, _ ->
            calls += method.name
            when (method.name) {
                "betReceiptPdf", "prizeReceiptPdf" -> "%PDF-1.7\n".toResponseBody()
                "mercadoPagoRegistrationToken" -> br.gov.caixa.loterias.feature.retail.data.dto.MercadoPagoRegistrationEnvelopeDto("token")
                "redeemPrize" -> br.gov.caixa.loterias.feature.retail.data.dto.PrizeRedemptionEnvelopeDto(JsonNull.INSTANCE)
                else -> ApiEnvelope(JsonNull.INSTANCE)
            }
        } as BackendApi
        val source = sourceFor(api)
        source.prizeDetails(1); source.lotteryCode(1); source.consultPixKey(1, com.google.gson.JsonObject())
        source.redeemPrize(1, com.google.gson.JsonObject()); source.cancelPix(1); source.mercadoPagoToken("mail")
        assertEquals("%PDF-1.7\n", source.betReceiptPdf(1).decodeToString())
        assertEquals("%PDF-1.7\n", source.prizeReceiptPdf(1).decodeToString())
        assertEquals(listOf("prizeDetails", "retailerRedemptionCode", "consultPixKey", "redeemPrize", "cancelPixRedemption", "mercadoPagoRegistrationToken", "betReceiptPdf", "prizeReceiptPdf"), calls)
    }

    @Test fun `every search refreshes favorites first and keeps backend identifier priority`() = runTest {
        val fake = FakeSource().apply {
            favoritesValue = JsonParser.parseString("""{"nested":[{"id":999,"codigo":101,"nomeFantasia":"Centro"}]}""")
            searchValue = JsonParser.parseString("""[{"id":101,"nomeFantasia":"Com ID"},{"codigo":101,"nomeFantasia":"Sem ID"}]""")
        }
        val calls = mutableListOf<String>()
        val source = object : RetailRemoteDataSource by fake {
            override suspend fun favorites() = fake.favorites().also { calls += "favorites" }
            override suspend fun search(query: String) = fake.search(query).also { calls += "search:$query" }
        }
        val repository = RetailRepositoryImpl(source)
        val first = repository.search("  Centro  ")
        assertEquals(listOf(101L, 101L), first.map { it.id })
        assertEquals(listOf(true, false), first.map { it.favorite })
        assertEquals("  Centro  ", fake.query)
        fake.favoritesValue = JsonParser.parseString("[]")
        assertEquals(listOf(false, false), repository.search("Centro").map { it.favorite })
        assertEquals(listOf("favorites", "search:  Centro  ", "favorites", "search:Centro"), calls)
    }

    @Test fun `missing prize source fails only when a prize operation is requested`() = runTest {
        val repository = RetailRepositoryImpl(FakeSource())
        assertEquals(2, repository.search("Centro").size)
        assertEquals(1, repository.favorites().size)
        repository.setFavorite(101, true)
        val operations = listOf<suspend () -> Unit>(
            { repository.details(1) }, { repository.lotteryCode(1) }, { repository.consultPixKey(1, "key") },
            { repository.redeemPix(1, "key") }, { repository.cancelPix(1) }, { repository.redeemMercadoPago(1, "email") },
            { repository.mercadoPagoRegistrationToken("email") }, { repository.betReceiptPdf(1) }, { repository.prizeReceiptPdf(1) },
        )
        for (operation in operations) {
            val failure = runCatching { operation() }.exceptionOrNull()
            assertTrue(failure is IllegalArgumentException)
            assertEquals("Fonte de resgate de prêmio não configurada", failure?.message)
        }
    }

    @Test fun `implicit prize source preserves raw payloads identifiers tokens and receipt bytes`() = runTest {
        val calls = mutableListOf<String>()
        val bodies = mutableListOf<JsonObject>()
        val betPdf = byteArrayOf(0, 37, 80, 68, 70, -1)
        val prizePdf = byteArrayOf(37, 80, 68, 70, 45, 0, 1)
        val source = object : RetailRemoteDataSource by FakeSource(), PrizeRedemptionRemoteDataSource by FakePrizeSource() {
            override suspend fun consultPixKey(betId: Long, body: JsonObject): JsonElement? {
                calls += "consult:$betId"; bodies += body
                return JsonParser.parseString("""{"nome":"Pessoa","chave":"retornada","nomeBanco":"Banco","cpfMascarado":"***"}""")
            }
            override suspend fun redeemPrize(betId: Long, body: JsonObject): JsonElement? {
                calls += "redeem:$betId"; bodies += body
                return JsonParser.parseString("\"  MI053  \"")
            }
            override suspend fun cancelPix(betId: Long) { calls += "cancel:$betId" }
            override suspend fun mercadoPagoToken(email: String): String { calls += "token:$email"; return "  raw-token  " }
            override suspend fun betReceiptPdf(betId: Long) = betPdf.also { calls += "betPdf:$betId" }
            override suspend fun prizeReceiptPdf(betId: Long) = prizePdf.also { calls += "prizePdf:$betId" }
        }
        val repository = RetailRepositoryImpl(source)
        val beneficiary = repository.consultPixKey(91, "  chave+pix@exemplo.test  ")
        assertEquals("retornada", beneficiary.key)
        assertEquals("Pessoa", beneficiary.name)
        assertEquals("Banco", beneficiary.bankName)
        assertEquals("***", beneficiary.maskedCpf)
        assertEquals(PrizeRedemptionResult.Redeemed("MI053"), repository.redeemPix(92, "  chave+pix@exemplo.test  "))
        assertEquals(PrizeRedemptionResult.Redeemed("MI053"), repository.redeemMercadoPago(93, "  Pessoa+resgate@exemplo.test  "))
        repository.cancelPix(94)
        assertEquals("  raw-token  ", (repository.mercadoPagoRegistrationToken("  Pessoa+resgate@exemplo.test  ") as MercadoPagoRegistrationToken.Available).value)
        assertSame(betPdf, repository.betReceiptPdf(95))
        assertSame(prizePdf, repository.prizeReceiptPdf(96))
        val pix = JsonParser.parseString("""{"meioPagamento":6,"chavePix":"  chave+pix@exemplo.test  "}""")
        assertEquals(pix, bodies[0]); assertEquals(pix, bodies[1])
        assertEquals(JsonParser.parseString("""{"meioPagamento":1,"parametrosPagamento":{"EMAIL":"  Pessoa+resgate@exemplo.test  "}}"""), bodies[2])
        assertEquals(listOf("consult:91", "redeem:92", "redeem:93", "cancel:94",
            "token:  Pessoa+resgate@exemplo.test  ", "betPdf:95", "prizePdf:96"), calls)
    }

    @Test fun `cancellation propagates by identity through every repository operation without retry`() = runTest {
        val cancellation = kotlinx.coroutines.CancellationException("cancelled")
        var calls = 0
        fun cancelled(): Nothing { calls++; throw cancellation }
        val retail = object : RetailRemoteDataSource {
            override suspend fun search(query: String): JsonElement? = cancelled()
            override suspend fun favorites(): JsonElement? = cancelled()
            override suspend fun addFavorite(retailerId: Long) { cancelled() }
            override suspend fun deleteFavorite(retailerId: Long) { cancelled() }
        }
        val prize = object : PrizeRedemptionRemoteDataSource {
            override suspend fun prizeDetails(betId: Long): JsonElement? = cancelled()
            override suspend fun lotteryCode(betId: Long): JsonElement? = cancelled()
            override suspend fun consultPixKey(betId: Long, body: JsonObject): JsonElement? = cancelled()
            override suspend fun redeemPrize(betId: Long, body: JsonObject): JsonElement? = cancelled()
            override suspend fun cancelPix(betId: Long) { cancelled() }
            override suspend fun mercadoPagoToken(email: String): String = cancelled()
            override suspend fun betReceiptPdf(betId: Long): ByteArray = cancelled()
            override suspend fun prizeReceiptPdf(betId: Long): ByteArray = cancelled()
        }
        val repository = RetailRepositoryImpl(retail, prize)
        val operations = listOf<suspend () -> Unit>(
            { repository.search("Centro") }, { repository.favorites() },
            { repository.setFavorite(101, true) }, { repository.setFavorite(101, false) },
            { repository.details(1) }, { repository.lotteryCode(1) }, { repository.consultPixKey(1, "key") },
            { repository.redeemPix(1, "key") }, { repository.cancelPix(1) }, { repository.redeemMercadoPago(1, "email") },
            { repository.mercadoPagoRegistrationToken("email") }, { repository.betReceiptPdf(1) }, { repository.prizeReceiptPdf(1) },
        )
        for (operation in operations) assertSame(cancellation, runCatching { operation() }.exceptionOrNull())
        assertEquals(13, calls)
    }

    @Test fun `malformed optional fields keep fallbacks and exact zero quota decimal values`() = runTest {
        val source = FakePrizeSource().apply {
            detailsValue = JsonParser.parseString("""{
                "aposta":{"id":"invalid","modalidade":"UNKNOWN","concursoInicial":"invalid","concursoAlvo":22},
                "premio":{"valorLiquido":"0.0100","concursos":[{"concurso":{"numero":22},"faixas":[
                    {"nome":"Zero","valorPremioCota":"0.0000","valorLiquido":99},
                    {"nome":"Fallback","valorPremioCota":"invalid","valorLiquido":"1.2300"},
                    {"nome":"Absent","valorPremioCota":null,"valorLiquido":"invalid"}
                ]}]},"meiosResgate":[null,7,{}, {"id":999}, {"id":9770}]
            }""")
            codeValue = JsonParser.parseString("""{"qrCode":null,"codigo":[],"nsb":{},"validade":"invalid"}""")
        }
        val repository = RetailRepositoryImpl(FakeSource(), source)
        val details = repository.details(91)
        assertEquals(91L, details.betId)
        assertEquals(2, details.modalityId)
        assertEquals("Mega-Sena", details.modalityName)
        assertEquals(22, details.initialContest)
        assertEquals(22, details.targetContest)
        assertEquals("0.0100", details.netValue.toPlainString())
        assertEquals(listOf("0.0000", "1.2300", null), details.awards.map { it.amount?.toPlainString() })
        assertEquals(listOf(PrizeRedemptionChannel.AGENCY), details.options.map { it.channel })
        assertEquals("Agência Caixa", details.options.single().title)
        assertEquals("Valores acima da isenção do IR", details.options.single().description)
        assertEquals(br.gov.caixa.loterias.feature.retail.domain.model.LotteryRedemptionCode("", "", "", 15), repository.lotteryCode(91))
    }

    private fun sourceFor(api: BackendApi) = RetailRemoteDataSourceImpl(
        BackendApiProvider().apply { replace(api) },
        PrizeBackendApiProvider().apply { replace(api) },
    )

    private class FakeSource : RetailRemoteDataSource {
        var searchValue: JsonElement? = JsonParser.parseString("""{"items":[
            {"id":101,"numeroFormatado":"0001-01","nomeFantasia":"Lotérica Centro","nome":"Centro Ltda","logradouro":"Rua A","bairro":"Centro","nomeMunicipio":"Brasília","nomeUF":"DF"},
            {"id":102,"codigo":102,"nomeFantasia":"Praça","siglaUf":"GO"}]}
        """)
        var favoritesValue: JsonElement? = JsonParser.parseString("[{\"codigo\":101,\"nomeFantasia\":\"Lotérica Centro\"}]")
        var query = ""
        val mutations = mutableListOf<String>()
        override suspend fun search(query: String) = searchValue.also { this.query = query }
        override suspend fun favorites() = favoritesValue
        override suspend fun addFavorite(retailerId: Long) { mutations += "add:$retailerId" }
        override suspend fun deleteFavorite(retailerId: Long) { mutations += "delete:$retailerId" }
    }

    private class FakePrizeSource : PrizeRedemptionRemoteDataSource {
        val calls = mutableListOf<String>()
        var lastBody: com.google.gson.JsonObject? = null
        var detailsValue: JsonElement? = JsonParser.parseString("""{"aposta":{"id":8,"modalidade":"TIMEMANIA","concursoInicial":1455,"concursoAlvo":1456},"premio":{"valorLiquido":90,"valorBruto":100,"concursos":[{"concurso":{"numero":1456},"faixas":[{"descricaoAcerto":"3 acertos","quantidadeAcertos":2,"valorPremioCota":25}]}]},"nsbi":"NSBI","mensagemInformativaIR":"IR","meiosResgate":[{"id":6,"descricao":"Pix"},{"id":9660}]}""")
        var codeValue: JsonElement? = JsonParser.parseString("""{"qrCode":"QR","codigo":"CODE","nsb":"NSB"}""")
        var redeemValue: JsonElement? = null
        override suspend fun prizeDetails(betId: Long) = detailsValue.also { calls += "details" }
        override suspend fun lotteryCode(betId: Long) = codeValue.also { calls += "code" }
        override suspend fun consultPixKey(betId: Long, body: com.google.gson.JsonObject) =
            JsonParser.parseString("""{"cpfMascarado":"cpf","nome":"Pessoa","chave":"key","nomeBanco":"Banco"}""").also { calls += "consult"; lastBody = body }
        override suspend fun redeemPrize(betId: Long, body: com.google.gson.JsonObject) = redeemValue.also { calls += "redeem"; lastBody = body }
        override suspend fun cancelPix(betId: Long) { calls += "cancel" }
        override suspend fun mercadoPagoToken(email: String) = "token".also { calls += "token" }
        override suspend fun betReceiptPdf(betId: Long) = byteArrayOf(1).also { calls += "betPdf" }
        override suspend fun prizeReceiptPdf(betId: Long) = byteArrayOf(2).also { calls += "prizePdf" }
    }
}
